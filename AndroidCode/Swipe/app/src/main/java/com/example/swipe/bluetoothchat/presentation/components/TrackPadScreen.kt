package com.plcoding.bluetoothchat.presentation.components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.bluetoothchat.enums.Gestures
import com.plcoding.bluetoothchat.presentation.BluetoothUiState
import kotlin.math.abs
import kotlin.math.roundToInt


import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key

import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp

class TriangleShapeLeft : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Generic(Path().apply {
            moveTo(size.width, 0f) // Top right
            lineTo(0f, size.height / 2f) // Middle left
            lineTo(size.width, size.height) // Bottom right
            close()
        })
}

class TriangleShapeRight : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Generic(Path().apply {
            moveTo(0f, 0f) // Top left
            lineTo(size.width, size.height / 2f) // Middle right
            lineTo(0f, size.height) // Bottom left
            close()
        })
}


class TriangleShapeUp : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Generic(Path().apply {
            moveTo(size.width / 2f, 0f) // Top center
            lineTo(0f, size.height)    // Bottom left
            lineTo(size.width, size.height) // Bottom right
            close()
        })
}

class TriangleShapeDown : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Generic(Path().apply {
            moveTo(0f, 0f) // Top left
            lineTo(size.width, 0f) // Top right
            lineTo(size.width / 2f, size.height) // Bottom center
            close()
        })
}



@Composable
fun MultiGestureDetector(

    state: BluetoothUiState = BluetoothUiState(),
    onDisconnect: () -> Unit,
    onSendMessage: (String, Int,Int, String) -> Unit,
    modifier: Modifier = Modifier,
    onSingleFingerDrag: (delta: Offset) -> Unit,
    onDoubleTap: () -> Unit,
    onTap: () -> Unit,
    onTwoFingerScroll: (delta: Offset) -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit


) {
    var swipeStartOffset by remember { mutableStateOf<Offset?>(null) }
    var lastTapTime by remember { mutableStateOf(0L) } // To track double-tap timing
    var isSingleTapConfirmed by remember { mutableStateOf(false) } // Single tap flag
    var isUsingTwoFingers by remember { mutableStateOf(false) } // Single tap flag
    var isSingleTapAndDragConfirmed by remember { mutableStateOf(false) } // Single tap flag




    Box(
        modifier = modifier
            .pointerInput(Unit)
            {
                awaitPointerEventScope {

                    while (true) {
                        val event = awaitPointerEvent()
                        val pointers = event.changes.filter { it.pressed }

                        when (pointers.size) {
                            1 -> {

                                val pointer = pointers[0]

                                if (event.type == PointerEventType.Press) {
                                    val currentTime = System.currentTimeMillis()

                                    // Check for Double Tap
                                    if (currentTime - lastTapTime < 300) { // Double-tap detected
                                        isSingleTapConfirmed = false // Reset single tap
                                        onSendMessage(Gestures.DoubleTap.name,0,0, "")
                                        lastTapTime = 0L // Reset timing after double-tap

                                    }

                                    else {


                                        isSingleTapConfirmed = true // Mark single tap for now
                                        lastTapTime = currentTime // Update last tap time

                                    }
                                }

                                if (event.type == PointerEventType.Release) {
                                    // Delay to verify single tap after release
                                    if (isSingleTapConfirmed) {
                                        isSingleTapConfirmed = false
                                        onTap() // Trigger single tap
                                    }
                                }

                                // Drag Detection
                                if (pointer.pressed) {



                                    val delta = pointer.position - pointer.previousPosition



                                    onSendMessage( if (isSingleTapConfirmed) Gestures.SingleTapAndDrag.name else Gestures.DoubleTapAndDrag.name,delta.x.roundToInt(), delta.y.roundToInt(), "")
                                    onSingleFingerDrag(delta)
                                }

                                // Swipe Detection
                                if (event.type == PointerEventType.Release) {
                                    swipeStartOffset?.let { startOffset ->
                                        val swipeDelta = pointer.position.x - startOffset.x

                                        if (abs(swipeDelta) > 250)
                                            onSendMessage( if (swipeDelta > 0) Gestures.SwipeRight.name else Gestures.SwipeLeft.name,0, 0,"")


                                    }
                                    swipeStartOffset = null

                                }

                                else
                                    swipeStartOffset = pointer.position


                                pointer.consume()
                            }

                            2 -> {

                                val pointer1 = pointers[0]
                                val pointer2 = pointers[1]

                                val delta1 = pointer1.position - pointer1.previousPosition
                                val delta2 = pointer2.position - pointer2.previousPosition
                                val averageDelta = (delta1 + delta2) / 2f



                                if( pointer1.pressed && pointer2.pressed) {

                                    isUsingTwoFingers = true


                                }

                                else
                                    isUsingTwoFingers = false

                                onSendMessage(Gestures.Scroll.name, delta2.x.roundToInt(), delta1.y.roundToInt() , "")
                                onTwoFingerScroll(averageDelta)
                                pointers.forEach { it.consume() }
                            }
                        }
                    }
                }
            }
    ){


        content()


    }
}
@Composable
fun TrackPadScreen(
    state: BluetoothUiState,
    onDisconnect: () -> Unit,
    onSendMessage: (String, Int, Int, String) -> Unit
) {
    var singleFingerOffset by remember { mutableStateOf(Offset.Zero) }
    var doubleTapCount by remember { mutableStateOf(0) }
    var tapCount by remember { mutableStateOf(0) }
    var twoFingerScrollOffset by remember { mutableStateOf(Offset.Zero) }
    var swipeMessage by remember { mutableStateOf("") }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    var isKeyboardVisible by rememberSaveable { mutableStateOf(false) }
    var textFieldValue by rememberSaveable { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val color = ButtonDefaults.buttonColors().containerColor
    val size = 70.dp


    BackHandler {

    if (isKeyboardVisible) {
        keyboardController?.hide()
        isKeyboardVisible = false


    }

    else
        onDisconnect()




    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .transformable(state = transformableState)
        ) {
            MultiGestureDetector(
                state = state,
                onDisconnect = onDisconnect,
                onSendMessage = onSendMessage,
                onSingleFingerDrag = { delta ->
                    singleFingerOffset += delta
                },
                onDoubleTap = {
                    doubleTapCount += 1
                },
                onTap = {
                    tapCount += 1
                },
                onTwoFingerScroll = { delta ->
                    twoFingerScrollOffset += delta
                },
                onSwipeLeft = {
                    swipeMessage = "Swiped Left!"
                },
                onSwipeRight = {
                    swipeMessage = "Swiped Right!"
                }
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }
        }

        if (isKeyboardVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        if (newValue.length < textFieldValue.length) {
                            // Backspace detected
                            Log.d("Gesture", "Backspace detected")
                            onSendMessage(Gestures.Backspace.name, 0, 0, "")
                        }

                        else
                            onSendMessage(Gestures.Typing.name,0,0,newValue.last().toString())
                        Log.d("Text", newValue)

                        textFieldValue = newValue
                    },
                    modifier = Modifier.weight(1f)
                        .onKeyEvent { event ->


                            if (event.type == KeyEventType.KeyUp && event.key == Key.Backspace && textFieldValue.isNullOrEmpty()) {

                                onSendMessage(Gestures.Backspace.name, 0, 0, "")
                                // TODO remove from list
                                return@onKeyEvent true
                            }

                            false


                        },
                    placeholder = { Text(text = "Type here...") }
                )

                IconButton(onClick = {
                    onSendMessage(Gestures.Typing.name, 0, 0, textFieldValue)
                    textFieldValue = ""
                    keyboardController?.hide()
                    isKeyboardVisible = false
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = "Send Text"
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onBackground)
                .height(200.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(


                modifier = Modifier.size(size)
                    .background(shape = TriangleShapeLeft(), color = color )
                    .pointerInput(Unit) {
                        detectTapGestures(

                            onPress = {
                                onSendMessage(Gestures.LeftKey.name, 0, 0, "")
                            },

                            onTap = {
                                onSendMessage(Gestures.LeftKey.name, 0, 0, "")
                            },


                            onDoubleTap = {
                                onSendMessage(Gestures.LeftKey.name, 0, 0, "")
                            }


                        )
                    }
            )



            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(


                        modifier = Modifier.size(size)
                            .background(shape = TriangleShapeUp(), color = color )
                            .pointerInput(Unit) {
                                detectTapGestures(


                                    onPress = {
                                        onSendMessage(Gestures.UpKey.name, 0, 0, "")
                                    },


                                    onTap = {
                                        onSendMessage(Gestures.UpKey.name, 0, 0, "")
                                    },

                                    onDoubleTap = {
                                        onSendMessage(Gestures.UpKey.name, 0, 0, "")
                                    }


                                )
                            }
                    )


                    Box(


                        modifier = Modifier.size(size)
                            .background(shape = CircleShape, color = color )
                        .pointerInput(Unit) {
                            detectTapGestures(


                                onPress = {
                                    onSendMessage(Gestures.RightClick.name, 0, 0, "")
                                },

                                onTap = {
                                    onSendMessage(Gestures.SingleTap.name, 0, 0, "")
                                },

                                onDoubleTap = {
                                    onSendMessage(Gestures.SingleTap.name, 0, 0, "")
                                }


                            )
                        }
                    ) {

                       Column(

                           modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                       ) {
                           Text(text = "Click", fontSize = 12.sp, color = Color.Black)

                       }

                    }
                    Box(


                        modifier = Modifier.size(size)
                            .background(shape = TriangleShapeDown(), color = color )
                            .pointerInput(Unit) {
                                detectTapGestures(


                                    onPress = {
                                        onSendMessage(Gestures.DownKey.name, 0, 0, "")
                                    },

                                    onTap = {
                                        onSendMessage(Gestures.DownKey.name, 0, 0, "")
                                    },


                                    onDoubleTap = {
                                        onSendMessage(Gestures.DownKey.name, 0, 0, "")
                                    }


                                )
                            }
                    )
                }
            }

            Box(


                modifier = Modifier.size(size)
                    .background(shape = TriangleShapeRight(), color = color )
                    .pointerInput(Unit) {
                        detectTapGestures(


                            onPress = {
                                onSendMessage(Gestures.RightKey.name, 0, 0, "")
                            },

                            onTap = {
                                onSendMessage(Gestures.RightKey.name, 0, 0, "")
                            },


                            onDoubleTap = {
                                onSendMessage(Gestures.RightKey.name, 0, 0, "")
                            }


                        )
                    }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(

                    onClick = {

                        isKeyboardVisible = !isKeyboardVisible

                }

                ) {
                    Icon(
                        painter = painterResource(com.example.swipe.R.drawable.keyboard_foreground), // Replace with desired icon
                        contentDescription = "Keyboard Icon",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { onSendMessage(Gestures.WindowsKey.name, 0, 0, "") }) {
                    Icon(
                        painter = painterResource(com.example.swipe.R.drawable.windows_foreground), // Replace with desired icon
                        contentDescription = "Extra Icon",
                        tint = Color.White
                    )
                }
            }
        }
        // End of Row
    }  // End of Column
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

   TrackPadScreen(BluetoothUiState(), {}, { _,_,_,_-> })
}

