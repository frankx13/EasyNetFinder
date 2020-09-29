package com.studio.neopanda.easynetfinder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.format.Formatter.formatIpAddress
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface.getNetworkInterfaces
import java.util.*


class MainActivity : AppCompatActivity() {

    private var typeConnection: Int = 0
    private var currentIPv4: String = ""

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Allow use and fetch of network data on the current device
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        show_net_inf_btn.setOnClickListener {
            checkTypeConnection()
            getLocalHostIp()
            getLoopbackAddress()
            if (typeConnection == 1) {
                currentIPv4 = getWifiIPAddress()
                ipv4_current_result.text = getString(R.string.ipv4_current_search) + currentIPv4
            } else if (typeConnection == 2) {
                currentIPv4 = getMobileIPAddress()
                ipv4_current_result.text = "Current IP : $currentIPv4"
            }

            ipv4_current_result.visibility = View.VISIBLE
        }

        //TODO: GET ACTIVE NETWORK & INFOS OK
        //TODO: VPN CASE
        //TODO: CREATE SHAPES FOR THE UI
        //TODO: GET DHCP INFOS
        //TODO: PING FUNCTIONNALITY
        //TODO: TRACERT FUNCTIONNALITY
    }

    fun checkTypeConnection(): Int {
        //SHOW IF WE ARE CONNECTED TO NETWORK
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                typeConnection = 1
                Toast.makeText(this, "You're connected to a wifi network !", Toast.LENGTH_LONG)
                    .show()
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                typeConnection = 2
                Toast.makeText(
                    this,
                    "You're connected to a mobile network !",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            typeConnection = 3
            Toast.makeText(this, "You're not connected to any network !", Toast.LENGTH_LONG)
                .show()
        }
        return typeConnection
    }

    @SuppressLint("SetTextI18n")
    fun getLocalHostIp() {
        val hostIPv4 = Inet4Address.getLocalHost().hostAddress
        ipv4_localhost_result.text = getString(R.string.ipv4_localhost_search) + hostIPv4
        ipv4_localhost_result.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun getLoopbackAddress() {
        val loopbackIPv4 = Inet4Address.getLoopbackAddress().address
        ipv4_loopback_result.text =
            getString(R.string.ipv4_looback_search) + loopbackIPv4.toString()
        ipv4_loopback_result.visibility = View.VISIBLE
    }

    fun getWifiIPAddress(): String {
        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val ip = wifiInfo.ipAddress
        return formatIpAddress(ip)
    }

    fun getMobileIPAddress(): String {
        try {
            val interfaces = Collections.list(getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return "No way!"
    }

}
