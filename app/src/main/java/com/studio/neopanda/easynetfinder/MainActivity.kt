package com.studio.neopanda.easynetfinder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.text.format.Formatter.formatIpAddress
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface.getNetworkInterfaces
import java.util.*


class MainActivity : AppCompatActivity() {

    private var typeConnection: Int = 0
    private var currentIPv4: String = ""
    private var isNetInfoOn: Boolean = false
    private var isPingFuncOn: Boolean = false
    private var inputIPv4: String = ""
    private val FINE_LOCATION_REQUEST = 101
    private val CAMERA_REQUEST = 102
    private var isNameOrIP = true
    private var isIterationValid = true

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Allow use and fetch of network data on the current device
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        showNetInfosClick()
        pingIPClick()
        wifiScanClick()

        //TODO: VPN CASE
        //TODO: TRACERT FUNCTIONALITY
        //TODO: IMPLEMENT NETWORK THREAD
    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        applicationContext,
                        "$name permission granted",
                        Toast.LENGTH_SHORT
                    ).show()
                    wifiScan()
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT)
                    .show()
                wifiScan()
            }
        }

        when (requestCode) {
            FINE_LOCATION_REQUEST -> innerCheck("location")
            CAMERA_REQUEST -> innerCheck("camera")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showNetInfosClick() {
        show_net_inf_btn.setOnClickListener {
            isNetInfoOn = !isNetInfoOn
            if (isNetInfoOn) {
                network_results.visibility = View.VISIBLE
                checkTypeConnection()
                getLocalHostIp()
                getLoopbackAddress()
                when (typeConnection) {
                    1 -> {
                        currentIPv4 = getWifiIPAddress()
                        ipv4_current_result.text =
                            getString(R.string.ipv4_current_search) + currentIPv4
                        type_connection_result.text =
                            getString(R.string.type_connection_search) + "Wifi"
                        ipv4_current_result.visibility = View.VISIBLE
                    }
                    2 -> {
                        currentIPv4 = getMobileIPAddress()
                        ipv4_current_result.text = "Current IP : $currentIPv4"
                        type_connection_result.text =
                            getString(R.string.type_connection_search) + "Mobile"
                        ipv4_current_result.visibility = View.VISIBLE
                    }
                    else -> {
                        ipv4_current_result.visibility = View.GONE
                        type_connection_result.text =
                            getString(R.string.type_connection_search) + "Disconnected"
                    }
                }

                type_connection_result.visibility = View.VISIBLE

                getDHCPInfo()
            } else {
                network_results.visibility = View.GONE
            }
        }
    }

    private fun checkTypeConnection(): Int {
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

    private fun getWifiIPAddress(): String {
        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val ip = wifiInfo.ipAddress
        return formatIpAddress(ip)
    }

    private fun getMobileIPAddress(): String {
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

    @SuppressLint("SetTextI18n")
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
        sDns1 = "DNS1: " + intToIp(d.dns1)
        sDns2 = "DNS2: " + intToIp(d.dns2)
        sGateway = "Gateway: " + intToIp(d.gateway)
        sIpaddress = "IPV4: " + intToIp(d.ipAddress)
        sLeaseduration = "Lease: " + d.leaseDuration.toString()
        sNetmask = "Netmask: " + intToIp(d.netmask)
        sServeraddress = "Srv address: " + intToIp(d.serverAddress)

        dns_1_result.text = sDns1
        dns_1_result.visibility = View.VISIBLE
        if (sDns2 != "DNS2: 0.0.0.0") {
            dns_2_result.text = sDns2
        } else {
            dns_2_result.text = "DNS2: None"
        }
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

    private fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)
    }

    private fun pingIP(ipInput: String, numberIteration: Int): ArrayList<Int> {
        val inet = InetAddress.getByName(ipInput)
        val reachable = inet.isReachable(5000)
        var packetsLost = 0
        var packetsReceived = 0
        val packetsFlux = arrayListOf<Int>()


        for (i in 0 until numberIteration) {
            if (!reachable) {
                packetsLost += 1
            } else {
                packetsReceived += 1
            }
        }
        packetsFlux.add(0, packetsReceived)
        packetsFlux.add(1, packetsLost)

        return packetsFlux
    }

    private fun wifiScanClick() {
        scan_wifi_btn.setOnClickListener {
            checkForPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                "location",
                FINE_LOCATION_REQUEST
            )
        }
    }

    private fun wifiScan() {
        val intent = Intent(this, WifiScannerActivity::class.java)
        startActivity(intent)
    }

    private fun pingIPClick() {
        ping_ip_btn.setOnClickListener {
            isPingFuncOn = !isPingFuncOn
            if (isPingFuncOn) {
                ping_input.visibility = View.VISIBLE
                ping_parameters.visibility = View.VISIBLE
                ping_results.visibility = View.VISIBLE

                pingParametersToggle()

                launch_ping.setOnClickListener {
                    if (ping_input.text.toString() != "") {
                        var iterationPing = 4
                        val lostPing: Int
                        val receivedPing: Int
                        val packetsFlux: ArrayList<Int>

                        if (ping_parameters.isChecked) {
                            if (parameter_number_iterations.text.toString() != "" &&
                                parameter_number_iterations.text.toString().toInt() > 0
                            ) {
                                isIterationValid = true
                                val iterationCustom: Int =
                                    parameter_number_iterations.text.toString().toInt()
                                iterationPing = iterationCustom
                            } else if (parameter_number_iterations.text.toString().toInt() <= 0) {
                                isIterationValid = false
                                parameter_number_iterations.setTextColor(resources.getColor(R.color.colorExit))
                                Toast.makeText(
                                    this,
                                    "The iteration number must be positive",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isIterationValid = true
                                iterationPing = 4
                            }

                            //Check for ping on IP or name
                            if (parameter_ping_ip.isChecked && !parameter_ping_name.isChecked) {
                                isNameOrIP = true
                                ping_input.inputType = InputType.TYPE_CLASS_DATETIME
                            } else if (parameter_ping_ip.isChecked && parameter_ping_name.isChecked) {
                                isNameOrIP = false
                                Toast.makeText(
                                    this,
                                    "You cannot ping both an IP and a domain name",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (!parameter_ping_ip.isChecked && parameter_ping_name.isChecked) {
                                isNameOrIP = true
                                ping_input.inputType = InputType.TYPE_CLASS_TEXT
                            } else {
                                isNameOrIP = false
                                Toast.makeText(
                                    this,
                                    "You need to chose what to ping : an IP or a name",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        if (isNameOrIP && isIterationValid) {
                            inputIPv4 = ping_input.text.toString()
                            packetsFlux = pingIP(inputIPv4, iterationPing)
                            receivedPing = packetsFlux[0]
                            lostPing = packetsFlux[1]
                            Toast.makeText(
                                this,
                                "$iterationPing packets sent, $receivedPing received, $lostPing lost",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "You need to provide an IPv4 to ping !",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                exitPing()
            } else {
                isPingFuncOn = false
                ping_input.visibility = View.GONE
                ping_parameters.visibility = View.GONE
                ping_results.visibility = View.GONE
                parameter_ping_ip.visibility = View.GONE
                parameter_ping_name.visibility = View.GONE
                parameter_number_iterations.visibility = View.GONE
            }
        }
    }

    private fun pingParametersToggle() {
        ping_parameters.setOnClickListener {
            if (ping_parameters.isChecked) {
                parameter_ping_ip.visibility = View.VISIBLE
                parameter_ping_name.visibility = View.VISIBLE
                parameter_number_iterations.visibility = View.VISIBLE
            } else {
                parameter_ping_ip.visibility = View.GONE
                parameter_ping_name.visibility = View.GONE
                parameter_number_iterations.visibility = View.GONE
                parameter_number_iterations.setTextColor(resources.getColor(R.color.colorTextMain))
                parameter_number_iterations.setText("")
            }
        }
    }

    private fun exitPing() {
        exit_ping.setOnClickListener {
            ping_input.visibility = View.GONE
            ping_parameters.visibility = View.GONE
            ping_results.visibility = View.GONE
            parameter_ping_ip.visibility = View.GONE
            parameter_ping_name.visibility = View.GONE
            parameter_number_iterations.visibility = View.GONE
        }
    }
}
