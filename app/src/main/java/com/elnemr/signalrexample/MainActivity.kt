package com.elnemr.signalrexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.microsoft.signalr.TransportEnum
import io.reactivex.Single
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {


    private lateinit var hubConnection: HubConnection

    private lateinit var tv_message: TextView
    private lateinit var et_message: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initHubConnection()

        tv_message = findViewById(R.id.message)
        et_message = findViewById(R.id.et_message)

        findViewById<Button>(R.id.btn_send).setOnClickListener {
            sendMessage(et_message.text.toString())
        }

        listenRNotification()
    }

    private fun listenRNotification() {
//        hubConnection.on("Alert", {
        hubConnection.on("RecieveNewMessage", { message ->
            tv_message.text = message
        }, String::class.java)
    }

    private fun initHubConnection() {
        hubConnection =
//            HubConnectionBuilder.create("http://test.boniantech.com/LMS/signalr?connectionToken=uMbWFiTS5Y7sRjMREA0udSe9jZ67cZz3PYDqHOG4yW/Y7GXNEXUg3AvuCqQ2rK6jRkQC8BtGUTcvBWH/ldmD0BJOIHMKVcjrcfbNrHo5K38r06ATsbiqQaMoSbx81Lnm")
//                .withTransport(TransportEnum.WEBSOCKETS)
//                .build()
            HubConnectionBuilder.create("http://192.168.1.15:50001/movehub").build()
//            HubConnectionBuilder.create("wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self").build()
//            HubConnectionBuilder.create("http://127.0.0.1:5000/movehub").build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                hubConnection.start()
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    tv_message.text = "$e"
                }
            }
        }

        Handler(Looper.myLooper()!!).postDelayed({
            Toast.makeText(this, "${hubConnection.connectionState}", Toast.LENGTH_SHORT).show()

        }, 10000)
    }

    private fun sendMessage(message: String) {
        if (hubConnection.connectionState == HubConnectionState.CONNECTED){
            hubConnection.send("MoveViewFromServer", message)
            Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()
        }
        if (hubConnection.connectionState == HubConnectionState.DISCONNECTED){
            tv_message.text = "Disconnected"
            hubConnection.start()
        }
    }

    override fun onDestroy() {
        hubConnection.stop()
        super.onDestroy()
    }
}