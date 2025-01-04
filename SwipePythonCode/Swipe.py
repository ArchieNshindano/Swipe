import os
import socket
import sys
import threading
import time
from tkinter import Tk, Button, Label
import tkinter as tk
from tkinter import ttk

from PIL import Image, ImageTk
from MouseGestures import Mouse
from connection import BluetoothConnection
import asyncio
import os
import sys
import ctypes


def resource_path(relative_path):
    """ Get the absolute path to a resource, works for both development and when frozen into a bundled executable. """
    try:
        # PyInstaller creates a temp folder and stores path in _MEIPASS
        base_path = sys._MEIPASS
    except Exception:
        base_path = os.path.abspath(".")

    return os.path.join(base_path, relative_path)

class BluetoothApp:
    def __init__(self, root):
        self.root: Tk = root
        self.root.title("Swipe")
        root.iconbitmap(resource_path("swipe.ico"))

        self.root.geometry("650x600")
        self.root.configure(bg="white")  # Set background to white


        self.mouse = Mouse()
        self.connection = BluetoothConnection()
        self.turn_on_bluetooth()
        self.sock = None
        self.connecting = False
        self.connected = False


        # UI Elements
        self.image_label = Label(self.root)
        self.image_label.pack(pady=20)

        self.status_label = Label(self.root, text="", font=("Helvetica", 14), bg="white")
        self.status_label.pack(pady=10)
        self.status_label.config(text=f"Check if bluetooth is On")


        # Create a style object
        self.style = ttk.Style()

        # Configure the button style
        self.style.configure(
            "Custom.TButton",
            font=("Helvetica", 14),
            foreground="blue",
            background="#0000ff",  # Material 3-inspired blue
            borderwidth=1,
            padding=10,
            relief="flat"
        )

        # Change hover behavior
        self.style.map(
            "Custom.TButton",
            foreground=[("active", "blue")],
            background=[("active", "#1E70FF")]  # Slightly darker blue on hover
        )

        # Create a styled button
        self.connect_button = ttk.Button(
            self.root,
            text="Connect",
            style="Custom.TButton",
            command=self.on_connect_press
        )
        self.connect_button.pack(pady=50)

        self.image_cache = {}  # Cache images to prevent garbage collection
        self.update_ui("resources/connect.jpg", "Connect")

    def finalize_connection(self):
        """Finalize the connection."""
        if not self.connecting:
            print("Connection was canceled before finalizing.")
            return

        self.update_ui("resources/connected.jpg", "Connected")
        self.status_label.config(text="Connected")
        self.connect_button.config(state=tk.DISABLED)  # Disable the button
        self.connecting = False
        threading.Thread(target=self.listen_for_gestures, daemon=True).start()

    def update_ui(self, image_path, button_text):
        """Update the UI with a new image and button text."""


        try:
            img_path = resource_path(image_path)
            img = Image.open(img_path)
            img = img.resize((650, 350), Image.Resampling.LANCZOS)
            self.image_cache[image_path] = ImageTk.PhotoImage(img)
            self.image_label.config(image=self.image_cache[image_path])
        except Exception as e:
            print(f"Error loading image: {e}")
            self.status_label.config(text=f"Error loading image: {image_path}")

        self.connect_button.config(text=button_text, state='normal')  # Enable by default

    def on_connect_press(self):
      """Handle Connect button press."""

      if not self.connected:

        if self.connecting:
            self.connecting = False
            self.update_ui("resources/connect.jpg", "Connect")
            self.status_label.config(text="Connection canceled.")
            self.connect_button.config(state='normal')  # Ensure button is enabled

        else:
            self.connecting = True
            self.update_ui("resources/connecting.jpg", "Connecting... (Click to cancel)")
            self.status_label.config(text=f"On your device click your Computer's bluetooth name")
            self.connect_button.config(state='normal')  # Keep button enabled for cancellation
            threading.Thread(target=self.start_connection, daemon=True).start()

    def start_connection(self):
        """Start the Bluetooth connection."""
        try:
            self.sock = self.connection.start_server()

            if self.sock:
                print("Socket established. Proceeding to establish connection.")
                self.root.after(0, self.establish_connection)
            elif not self.connecting:
                print("Connection was canceled.")
        except Exception as e:
            print(f"Error during connection: {e}")
            self.root.after(0, lambda: self.update_ui("resources/connect.jpg", "Connect"))
        finally:
            if not self.sock and self.connecting:
                print("Resetting UI after failed connection attempt.")
                self.root.after(0, lambda: self.update_ui("resources/connect.jpg", "Connect"))

    def establish_connection(self):
        """Handle the connection process."""
        if not self.connecting:
            print("Connection was canceled during establishment.")
            return

        self.status_label.config(text="Establishing connection...")
        self.root.after(2000, self.finalize_connection)

    def finalize_connection(self):
        """Finalize the connection."""
        if not self.connecting:
            print("Connection was canceled before finalizing.")
            return


        self.connected = True

        self.update_ui("resources/connected.jpg", "Connected")
        self.status_label.config(text="Connected")
        self.connecting = False
        threading.Thread(target=self.listen_for_gestures, daemon=True).start()

    def listen_for_gestures(self):
        """Start listening for Bluetooth gestures."""
        try:
            while self.connected and self.sock:  # Check connection status
                bluetooth_message = self.connection.receive_message(self.sock)

                if bluetooth_message:
                    print(f"Received gesture message: {bluetooth_message}")
                    self.mouse.handle_gesture(bluetooth_message)
                elif self.connection.connection_status == "Disconnected":
                    print("Connection lost.")
                    self.root.after(0, self.handle_disconnection)  # Schedule reset on the main thread
                    break  # Exit the loop
        except Exception as e:
            print(f"Error in listen_for_gestures: {e}")
            self.root.after(0, self.handle_disconnection)  # Schedule reset on the main thread

    def handle_disconnection(self):
        """Handle disconnection and reset the UI."""
        print("Handling disconnection...")
        self.connected = False
        self.connecting = False

        # Close the existing socket, if any
        try:
            self.connection.close_connection(self.sock)
        except Exception as e:
            print(f"Error while closing socket: {e}")
        finally:
            self.sock = None

        # Reset the UI and notify the user
        self.update_ui("resources/connect.jpg", "Connect")
        self.status_label.config(text="Disconnected. Try again")
        self.turn_off_bluetooth()
        self.root.after(1000, self.turn_on_bluetooth)


    def turn_on_bluetooth(self):
      asyncio.run(self.connection.bluetooth_power(True))

    def turn_off_bluetooth(self):
      asyncio.run(self.connection.bluetooth_power(False))

    def keep_alive(self):

        """Prevent the application from pausing when minimized."""
        self.root.update_idletasks()
        self.root.update()
        self.root.after(100, self.keep_alive)


def is_admin():
    """Check if the script is running with admin privileges."""
    try:
        return ctypes.windll.shell32.IsUserAnAdmin()
    except:
        return False


def run_as_admin():
    """Restart the script with admin privileges."""
    if not is_admin():
        try:
            # Re-run the script with admin rights
            ctypes.windll.shell32.ShellExecuteW(
                None,
                "runas",  # Run as administrator
                sys.executable,
                " ".join(sys.argv),
                None,
                1  # Show the window
            )
        except Exception as e:
            print(f"Failed to request admin privileges: {e}")
            sys.exit(1)



def get_local_address_name():
    try:
        # Get the local hostname
        hostname = socket.gethostname()
        # Get the local IP address
        local_ip = socket.gethostbyname(hostname)
        return hostname, local_ip
    except Exception as e:
        return f"Error retrieving local address name: {e}"



if __name__ == "__main__":

    hostname, local_ip = get_local_address_name()
    print(f"Hostname: {hostname}")
    print(f"Local IP Address: {local_ip}")

    if not is_admin():
        print("This script needs to be run as an administrator.")
        run_as_admin()
        sys.exit(0)  # Exit as the elevated script will be a new process



    root = Tk()
    app = BluetoothApp(root)
    root.after(100, app.keep_alive)
    root.mainloop()

    app.turn_off_bluetooth()

