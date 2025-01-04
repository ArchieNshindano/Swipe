package com.plcoding.bluetoothchat.data.chat

import com.plcoding.bluetoothchat.domain.chat.BluetoothMessage
import org.json.JSONObject

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    try {
        // Parse the JSON string into a JSONObject
        val jsonObject = JSONObject(this)
        return BluetoothMessage(
            gesture = jsonObject.getString("gesture"),
            xCoordinate = jsonObject.getInt("xCoordinate"),
            yCoordinate = jsonObject.getInt("yCoordinate"),
            text = jsonObject.getString("text"),
            isFromLocalUser = isFromLocalUser
        )
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid JSON string for BluetoothMessage: $this")
    }
}

fun BluetoothMessage.toByteArray(): ByteArray {
    // Create a JSONObject and populate it with BluetoothMessage fields
    val jsonObject = JSONObject().apply {
        put("gesture", gesture)
        put("xCoordinate", xCoordinate)
        put("yCoordinate", yCoordinate)
        put("text", text)
        put("is_from_local_user", isFromLocalUser)
    }

    // Convert the JSON object to a UTF-8 encoded byte array
    return jsonObject.toString().toByteArray(Charsets.UTF_8)
}
