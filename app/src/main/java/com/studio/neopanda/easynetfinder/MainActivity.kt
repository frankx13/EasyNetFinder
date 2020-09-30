package com.studio.neopanda.easynetfinder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
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
                type_connection_result.text = getString(R.string.type_connection_search) + "Wifi"
                ipv4_current_result.visibility = View.VISIBLE
            } else if (typeConnection == 2) {
                currentIPv4 = getMobileIPAddress()
                ipv4_current_result.text = "Current IP : $currentIPv4"
                type_connection_result.text = getString(R.string.type_connection_search) + "Mobile"
                ipv4_current_result.visibility = View.VISIBLE
            } else {
                ipv4_current_result.visibility = View.GONE
                type_connection_result.text =
                    getString(R.string.type_connection_search) + "Disconnected"
            }

            type_connection_result.visibility = View.VISIBLE

            getDHCPInfo()
        }

        //TODO: VPN CASE
        //TODO: CREATE SHAPES FOR THE UI
        //TODO: PING FUNCTIONALITY
        //TODO: TRACERT FUNCTIONALITY
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
        //TODO: Handle exceptions
        return "No way!"
    }

    fun getDHCPInfo() {
        val sDns1: String
        val sDns2: String
        val sGateway: String
        val sIpaddress: String
        val sLeaseduration: String
        val sNetmask: String
        val sServeraddress: String
        val d: DhcpInfo
        val wifii: WifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        d = wifii.dhcpInfo
        sDns1 = intToIp(d.dns1)
        sDns2 = intToIp(d.dns2)
        sGateway = intToIp(d.gateway)
        sIpaddress = intToIp(d.ipAddress)
        sLeaseduration = d.leaseDuration.toString()
        sNetmask = intToIp(d.netmask)
        sServeraddress = intToIp(d.serverAddress)

        dns_1_result.text = sDns1
        dns_1_result.visibility = View.VISIBLE
        dns_2_result.text = sDns2
        dns_2_result.visibility = View.VISIBLE
        default_gateway_result.text = sGateway
        default_gateway_result.visibility = View.VISIBLE
        ip_address_result.text = sIpaddress
        ip_address_result.visibility = View.VISIBLE
        lease_time_result.text = sLeaseduration
        lease_time_result.visibility = View.VISIBLE
        subnet_mask_result.text = sNetmask
        subnet_mask_result.visibility = View.VISIBLE
        serveur_address_result.text = sServeraddress
        serveur_address_result.visibility = View.VISIBLE
    }

    fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)
    }
}
