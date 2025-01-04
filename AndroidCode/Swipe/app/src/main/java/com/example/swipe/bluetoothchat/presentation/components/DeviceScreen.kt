package com.plcoding.bluetoothchat.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = com.example.swipe.R.mipmap.swipe), // Replace with your drawable resource
                contentDescription = "Bluetooth Icon",
                modifier = Modifier

                    .size(70.dp),
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
                text = "Easily connect and Control your laptop with your phone via Bluetooth",
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
