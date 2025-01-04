package com.plcoding.bluetoothchat.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.bluetoothchat.domain.chat.BluetoothDevice
import com.plcoding.bluetoothchat.presentation.BluetoothUiState
@Composable
fun DeviceScreen(
    state: BluetoothUiState = BluetoothUiState(),
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    onStartServer: () -> Unit = {},
    onDeviceClick: (BluetoothDevice) -> Unit = {},
    onGetStartedClick: () -> Unit = {}
) {
    var showInstructions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { showInstructions = true }) {
                    Text(text = "Instructions")
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = com.example.swipe.R.mipmap.swipe), // Replace with your drawable resource
                contentDescription = "Bluetooth Icon",
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Welcome to Swipe!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Easily connect and control your laptop with your phone via Bluetooth",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            BluetoothDeviceList(
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onClick = onDeviceClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp) // Adjust the height as necessary
            )

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    if (showInstructions) {
        InstructionDialog(onDismiss = { showInstructions = false })
    }
}


@Composable
fun InstructionDialog(onDismiss: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Instructions")
        },
        text = {
            Box(modifier = Modifier.height(300.dp)) { // Restrict height to make it scrollable
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()) // Enable scrolling
                ) {
                    Text(
                        text = "Usage Instructions\n\n" +
                                "Basic Setup\n" +
                                "1. Ensure Bluetooth is enabled on both devices.\n" +
                                "2. Pair the devices through system settings or the app.\n" +
                                "3. Select the target device from the list to establish a connection.\n\n" +
                                "TrackPad Area\n" +
                                "1. The central gray box represents the TrackPad.\n" +
                                "2. Use gestures and controls here to interact with the paired device.\n\n" +
                                "Gesture Controls\n\n" +
                                "Single Tap\n" +
                                "1. Action: Triggers a standard click (left-click equivalent).\n" +
                                "2. Example: Selecting a file.\n\n" +
                                "Double Tap\n" +
                                "1. Action: Triggers a double-click action.\n" +
                                "2. Example: Opening a file or folder.\n\n" +
                                "Single Tap and Drag\n" +
                                "1. Action: Drag an item across the screen while holding down the \"left click.\"\n" +
                                "2. Example: Moving an icon on the desktop.\n\n" +
                                "Double Tap and Drag\n" +
                                "1. Action: Enables dragging items with a double-click gesture.\n" +
                                "2. Example: Resizing or rearranging windows.\n\n" +
                                "Two-Finger Scroll\n" +
                                "1. Action: Scroll vertically or horizontally using two fingers.\n" +
                                "2. Example: Navigating through documents or web pages.\n\n" +
                                "Button Controls\n\n" +
                                "Directional Keys\n" +
                                "1. Up Key: Scrolls or moves the selection upward.\n" +
                                "2. Down Key: Scrolls or moves the selection downward.\n" +
                                "3. Left Key: Moves selection to the left.\n" +
                                "4. Right Key: Moves selection to the right.\n\n" +
                                "Click Button\n" +
                                "1. Located at the center of the directional keys.\n" +
                                "2. Supports single-tap and double-tap actions for standard clicks.\n\n" +
                                "Keyboard Icon\n" +
                                "1. Toggles the on-screen keyboard.\n" +
                                "2. Use the text field for typing input to the connected device.\n\n" +
                                "Windows Key\n" +
                                "1. Simulates the Windows key (or Command key on macOS).\n" +
                                "2. Example: Open the Start Menu or Spotlight search.\n\n" +
                                "Special Features\n\n" +
                                "Dynamic Typing\n" +
                                "1. Typing is enabled in the text field.\n" +
                                "2. The app detects typing, including special gestures for backspace or input clearing.\n\n" +
                                "Back Handling\n" +
                                "1. Pressing the back button on Android devices hides the keyboard if visible.\n" +
                                "2. If the keyboard is not visible, pressing back disconnects from the paired device.\n"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It")
            }
        }
    )
}




@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(
                text = "Scroll then Click your Device to Connect \n \n" +
                        "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }

        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IntroScreenPreview() {
    DeviceScreen(
        state = BluetoothUiState(
            pairedDevices = listOf(
                BluetoothDevice(name = "Device 1", address = "00:11:22:33:44:55"),
                BluetoothDevice(name = "Device 2", address = "66:77:88:99:AA:BB")
            ),
            scannedDevices = listOf(
                BluetoothDevice(name = "Device A", address = "CC:DD:EE:FF:00:11"),
                BluetoothDevice(name = "Device B", address = "22:33:44:55:66:77")
            )
        )
    )
}
