package com.plcoding.bluetoothchat.domain.chat

data class BluetoothMessage(

    val gesture: String,
    val xCoordinate: Int,
    val yCoordinate: Int,
    val text: String,
    val isFromLocalUser: Boolean,




    )
