# Description: This file contains the main logic for the swipe gestures. It receives the swipe gestures from the Android device and performs the corresponding actions on the computer.
import msvcrt
import threading
from enum import Enum

import pyautogui



pyautogui.FAILSAFE = False


increaseBy = 8


class Gestures(Enum):
    DoubleTap = 1
    SingleTap = 2
    SwipeLeft = 3
    SwipeRight = 4
    Scroll = 5
    DoubleTapAndDrag = 6
    SingleTapAndDrag = 7
    LeftKey = 8
    RightKey = 9
    Typing = 10
    Backspace = 11
    UpKey = 12
    DownKey = 13
    WindowsKey = 14
    RightClick = 15

class Mouse:

 def simulate_tap(self,doubleTap = False):

   try:


    if doubleTap:
        pyautogui.doubleClick()

    else:
      pyautogui.click()

   except Exception as e:

       print(e)



 def simulate_scroll(self,deltaY):
    pyautogui.scroll(deltaY*20)


 def dragTo(self, deltaX, deltaY):

    try:
     x, y = pyautogui.position()


     print(f"Dragging to {x+deltaX}, {y+deltaY}")

     pyautogui.mouseDown(button="left")
     pyautogui.moveTo(x + deltaX*increaseBy, y + deltaY*increaseBy, duration= 1)
     pyautogui.mouseUp(button="left")


    except Exception as e:

        print(e)


 def moveCursor(self, deltaX, deltaY):
    try:
     x,y = pyautogui.position()

     print(f"Moving to {x+deltaX}, {y+deltaY}")


     pyautogui.moveTo(x+deltaX*increaseBy, y+deltaY*increaseBy)


    except Exception as e:

        print(e)


 def simulate_zoom_in(self):


    try:
        pyautogui.keyDown('ctrl')
        pyautogui.scroll(600)
        pyautogui.keyUp('ctrl')


    except Exception as e:
        print(e)

 def simulate_zoom_out(self):
    try:
        pyautogui.keyDown('ctrl')
        pyautogui.scroll(-600)
        pyautogui.keyUp('ctrl')


    except Exception as e:
        print(e)


 def rightKey(self):
    try:
        pyautogui.press('right')


    except Exception as e:
        print(e)

 def leftKey(self):

    try:
        pyautogui.press('left')


    except Exception as e:
        print(e)



 def typing(self, text: str):

    try:

     pyautogui.typewrite(text)



    except Exception as e:
        print(e)


 def backSpace(self):

    try:

     pyautogui.press('backspace')


    except Exception as e:
        print(e)



 def upKey(self):

     try:
        pyautogui.press('up')


     except Exception as e:
        print(e)

 def downKey(self):

     try:
        pyautogui.press('down')


     except Exception as e:
        print(e)

 def windowsKey(self):

     try:

        pyautogui.press('win')



     except Exception as e:
        print(e)


 def rightClick(self):

     try:

        pyautogui.rightClick()

     except Exception as e:
        print(e)

 def handle_gesture(self, bluetooth_message):
     """Handle the received gesture and perform corresponding mouse actions."""
     gestures = Gestures

     if gestures.DoubleTapAndDrag == bluetooth_message.gesture:
         self.dragTo(bluetooth_message.xCoordinate, bluetooth_message.yCoordinate)

     elif gestures.DoubleTap.name == bluetooth_message.gesture:
         self.simulate_tap(True)

     elif gestures.SingleTap.name == bluetooth_message.gesture:
         self.simulate_tap(False)

     elif gestures.SingleTapAndDrag.name == bluetooth_message.gesture:
         self.moveCursor(bluetooth_message.xCoordinate, bluetooth_message.yCoordinate)

     elif gestures.Scroll.name == bluetooth_message.gesture:
         self.simulate_scroll(bluetooth_message.yCoordinate)

     elif gestures.SwipeRight.name == bluetooth_message.gesture:
         self.simulate_zoom_in()

     elif gestures.SwipeLeft.name == bluetooth_message.gesture:
         self.simulate_zoom_out()

     elif gestures.LeftKey.name == bluetooth_message.gesture:
         self.leftKey()

     elif gestures.RightKey.name == bluetooth_message.gesture:
         self.rightKey()

     elif gestures.Typing.name == bluetooth_message.gesture:
         self.typing(bluetooth_message.text)

     elif gestures.Backspace.name == bluetooth_message.gesture:
         self.backSpace()

     elif gestures.WindowsKey.name == bluetooth_message.gesture:
         self.windowsKey()

     elif gestures.UpKey.name == bluetooth_message.gesture:
         self.upKey()

     elif gestures.DownKey.name == bluetooth_message.gesture:
         self.downKey()

     elif gestures.RightClick.name == bluetooth_message.gesture:
         self.rightClick()





    
    







if __name__ == "__main__":


    pyautogui.moveTo(45,288)
    Mouse().rightClick()



    while True:

      print(pyautogui.position())

    # mouse = Mouse()
    #
    #
    # connection = BluetoothConnection()
    #
    # sock = connection.start_server()
    # gestures = Gestures
    #
    #
    # try:
    #     while True:
    #         # Receive message from Android device
    #
    #         bluetooth_message = connection.receive_message(sock)
    #         print("MAIN  ", bluetooth_message)
    #
    #
    #         if bluetooth_message:
    #
    #
    #             if gestures.DoubleTapAndDrag == bluetooth_message.gesture:
    #                 mouse.dragTo(bluetooth_message.xCoordinate, bluetooth_message.yCoordinate)
    #
    #             elif gestures.DoubleTap.name == bluetooth_message.gesture:
    #                 mouse.simulate_tap(True)
    #
    #
    #             elif gestures.SingleTap.name == bluetooth_message.gesture:
    #                 mouse.simulate_tap(False)
    #
    #             elif gestures.SingleTapAndDrag.name == bluetooth_message.gesture:
    #                 mouse.moveCursor(bluetooth_message.xCoordinate, bluetooth_message.yCoordinate)
    #
    #
    #
    #             elif gestures.Scroll.name == bluetooth_message.gesture:
    #                 mouse.simulate_scroll(bluetooth_message.yCoordinate)
    #
    #
    #             elif gestures.SwipeRight.name == bluetooth_message.gesture:
    #                 mouse.simulate_zoom_in()
    #
    #
    #             elif gestures.SwipeLeft.name == bluetooth_message.gesture:
    #                mouse.simulate_zoom_out()
    #
    #             elif gestures.LeftKey.name == bluetooth_message.gesture:
    #                   mouse.leftKey()
    #
    #
    #             elif gestures.RightKey.name == bluetooth_message.gesture:
    #                 mouse.rightKey()
    #
    #
    #             elif gestures.Typing.name == bluetooth_message.gesture:
    #                 mouse.typing(bluetooth_message.text)
    #
    #             elif gestures.Backspace.name == bluetooth_message.gesture:
    #                 mouse.backSpace()
    #
    #
    #             elif gestures.WindowsKey.name == bluetooth_message.gesture:
    #                 mouse.windowsKey()
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #         # Send a reply message to the Android device
    #         # reply_message = input("Enter Message: ")
    #         # sender_name = input("Enter Name: ")
    #         # message_to_send = create_bluetooth_message(reply_message, sender_name, True)
    #         # send_message(sock, message_to_send)
    #
    #
    # except KeyboardInterrupt:
    #     print("Connection closed by user.")
    # finally:
    #     sock.close()
    #
    #





