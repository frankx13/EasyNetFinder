package com.studio.neopanda.easynetfinder

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity() {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SHOW IF NETWORKS ARE AVAILABLE
//        show_networks_btn.setOnClickListener {
//            val connectivityManager =
//                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//            val networksList = ArrayList<Network>()
//            val availableNetworks = connectivityManager.allNetworks
//
//            if (availableNetworks.isNotEmpty()){
//                Toast.makeText(this, "There are networks available in your zone", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this, "No networks available in your zone", Toast.LENGTH_LONG).show()
//            }
//
//            Log.e("ListNetworks", networksList.toString())
//        }

        //SHOW IF WE ARE CONNECTED TO NETWORK
        test_connectivity_btn.setOnClickListener {
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                // connected to the internet
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    Toast.makeText(this, "You're connected to a wifi network !", Toast.LENGTH_LONG).show()
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    Toast.makeText(this, "You're connected to a mobile network !", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "You're not connected to any network !", Toast.LENGTH_LONG).show()
            }
        }

        show_networks_btn.setOnClickListener {

        }





        //Init Connectivity Tester
//        test_connectivity_btn.setOnClickListener {
//            val connectivityManager =
//                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//            val netAllInfo = connectivityManager.allNetworkInfo
//            var connectionStatusNumber = 0
//            var connectionStatusName = ""
//
//                if (netAllInfo[0].state == NetworkInfo.State.CONNECTED
//                    || netAllInfo[0].state == NetworkInfo.State.CONNECTING
//                    || netAllInfo[1].state == NetworkInfo.State.CONNECTED
//                    || netAllInfo[1].state == NetworkInfo.State.CONNECTING
//                ) {
//                    connectionStatusNumber = 1
//                    connectionStatusName = "connected"
//                } else if (netAllInfo[0].state == NetworkInfo.State.DISCONNECTED
//                    || netAllInfo[0].state == NetworkInfo.State.DISCONNECTING
//                    || netAllInfo[1].state == NetworkInfo.State.DISCONNECTED
//                    || netAllInfo[1].state == NetworkInfo.State.DISCONNECTING
//                ) {
//                    connectionStatusNumber = 2
//                    connectionStatusName = "not connected"
//                } else if (netAllInfo[0].state == NetworkInfo.State.UNKNOWN
//                    || netAllInfo[1].state == NetworkInfo.State.UNKNOWN){
//                    connectionStatusNumber = 3
//                    connectionStatusName = "unknown"
//                } else if (netAllInfo[0].state == NetworkInfo.State.UNKNOWN
//                    || netAllInfo[1].state == NetworkInfo.State.UNKNOWN){
//                    connectionStatusNumber = 4
//                    connectionStatusName = "suspended"
//                }
//
//            when (connectionStatusNumber) {
//                1 -> Toast.makeText(this, "The network is " + connectionStatusName, Toast.LENGTH_LONG).show()
//                2 -> Toast.makeText(this, "The network is " + connectionStatusName, Toast.LENGTH_LONG).show()
//                3 -> Toast.makeText(this, "The network is " + connectionStatusName, Toast.LENGTH_LONG).show()
//                4 -> Toast.makeText(this, "The network is " + connectionStatusName, Toast.LENGTH_LONG).show()
//                else -> Toast.makeText(this, "Device is bugged", Toast.LENGTH_LONG).show()
//            }
//        }






        //Init Type of Network
//        test_type_btn.setOnClickListener {
//            val connectivityManager =
//                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//            var isWifiConn = false
//            var isMobileConn = false
//
//            connectivityManager.allNetworks.forEach { network ->
//                connectivityManager.getNetworkInfo(network).apply {
//                    if (type == ConnectivityManager.TYPE_WIFI) {
//                        isWifiConn = isWifiConn or isConnected
//                    }
//                    if (type == ConnectivityManager.TYPE_MOBILE) {
//                        isMobileConn = isMobileConn or isConnected
//                    }
//                }
//            }
//            when {
//                isWifiConn -> Toast.makeText(this, "Device is connected in Wifi", Toast.LENGTH_LONG).show()
//                isMobileConn -> Toast.makeText(this, "Device is connected in Mobile", Toast.LENGTH_LONG).show()
//                else -> Toast.makeText(this, "Device is not connected to any Network", Toast.LENGTH_LONG).show()
//            }
//        }
    }
}
