import json
from dataclasses import dataclass
import bluetooth
from enum import Enum
import threading
from winrt.windows.devices import radios
import subprocess
import socket
from uuid import getnode as get_mac

# UUID that matches the Android Bluetooth service
SERVICE_UUID = "27b7d1da-08c7-4505-a6d1-2459987e5e2d"


@dataclass
class BluetoothMessage:
    gesture: str
    xCoordinate: int
    yCoordinate: int
    text: str
    isFromLocalUser: bool



class BluetoothConnection:
    def __init__(self):
        self.connection_status = "Disconnected"  # Initial connection status
        self.sock = None  # Bluetooth socket
        self.client_name = None

    def create_bluetooth_message(self, gesture: str, xCoordinate: int, yCoordinate: int, text: str,
                                 is_from_local_user: bool) -> dict:
        """Create a BluetoothMessage as a dictionary for JSON serialization."""
        return {
            "gesture": gesture,
            "xCoordinate": xCoordinate,
            "yCoordinate": yCoordinate,
            "text": text,
            "is_from_local_user": is_from_local_user,
        }

    def serialize_bluetooth_message(self, bluetooth_message):
        """Serialize the BluetoothMessage into a JSON string."""
        return json.dumps(bluetooth_message)  # Convert dictionary to JSON string

    def send_message(self, sock, bluetooth_message):
        """Send a BluetoothMessage as JSON over the Bluetooth socket."""
        try:
            message_data = self.serialize_bluetooth_message(bluetooth_message)
            sock.send(message_data.encode('utf-8'))  # Encode as UTF-8 byte array
            print(f"Sent: {message_data}")
        except Exception as e:
            print(f"Error sending message: {e}")

    def receive_message(self, sock):
        """Receive a BluetoothMessage object over the Bluetooth socket."""
        try:
            data = sock.recv(1024)  # Read up to 1024 bytes of data
            if not data:
                print("No data received. Connection may have been lost.")
                self.update_connection_status("Disconnected")
                return None

            message_str = data.decode('utf-8')
            print(f"Raw received data: {message_str}")

            # Parse the JSON string into a dictionary
            try:
                bluetooth_message = json.loads(message_str)  # Deserialize JSON
                return BluetoothMessage(
                    bluetooth_message['gesture'],
                    bluetooth_message['xCoordinate'],
                    bluetooth_message['yCoordinate'],
                    bluetooth_message['text'],
                    False
                )
            except json.JSONDecodeError:
                print("Error: Received data is not valid JSON.")
                return None
        except Exception as e:
            print(f"Error receiving message: {e}")
            return None

    def start_server(self):
        """Start the Bluetooth server to accept incoming connections."""
        server_socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)


        try:
            server_socket.bind(("", bluetooth.PORT_ANY))
            server_socket.listen(1)
            port = server_socket.getsockname()[1]

            print(f"Server started on device {self.get_server_name()}")

            bluetooth.advertise_service(
                server_socket,
                "BluetoothServer",
                service_id=SERVICE_UUID,
                service_classes=[SERVICE_UUID, bluetooth.SERIAL_PORT_CLASS],
                profiles=[bluetooth.SERIAL_PORT_PROFILE]
            )
            print(f"Waiting for a connection on RFCOMM channel {port}")

            client_socket, client_address = server_socket.accept()

            self.client_name = bluetooth.lookup_name(client_address[0])

            print(f"Accepted connection from {client_address}")

            # Update connection status when the connection is successful
            self.update_connection_status("Connected")

            self.sock = client_socket  # Save the socket for future use
            return client_socket
        except Exception as e:
            print(f"Error setting up server: {e}")
            server_socket.close()
            self.update_connection_status("Disconnected")
            return None

    def close_connection(self, sock):
        """Close the Bluetooth socket."""
        try:
            sock.close()
            print("Connection closed.")
            self.update_connection_status("Disconnected")
        except Exception as e:
            print(f"Error closing connection: {e}")




    def update_connection_status(self, status):
        """Update the Bluetooth connection status."""
        self.connection_status = status
        print(f"Connection Status: {self.connection_status}")

    def get_connection_status(self):
        """Get the current Bluetooth connection status."""
        return self.connection_status

    async def bluetooth_power(self, turn_on):
        all_radios = await radios.Radio.get_radios_async()
        for this_radio in all_radios:
            if this_radio.kind == radios.RadioKind.BLUETOOTH:
                if turn_on:
                    result = await this_radio.set_state_async(radios.RadioState.ON)
                else:
                    result = await this_radio.set_state_async(radios.RadioState.OFF)

    def get_server_name(self):
        """Retrieve the name of the server device."""
        try:
            # Fetch the local Bluetooth address

            print("MAC: ", get_mac())

            return bluetooth.lookup_name(get_mac())
        except Exception as e:
            print(f"Error retrieving server name: {e}")
            return None


if __name__ == "__main__":
    connection = BluetoothConnection()




    # server_sock = connection.start_server()
    # if server_sock:
    #     try:
    #         while True:
    #             bluetooth_message = connection.receive_message(server_sock)
    #             if bluetooth_message:
    #                 print(f"Received: {bluetooth_message}")
    #     except KeyboardInterrupt:
    #         print("Server shutting down.")
    #     finally:
    #         connection.close_connection(server_sock)
