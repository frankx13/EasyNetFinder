package com.studio.neopanda.easynetfinder

import android.R.attr.key
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wifi_scanner.*
import java.util.*


class WifiScannerActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiList: ListView
    private lateinit var scanBtn: Button
    private var resultList = ArrayList<ScanResult>()
    private var arrayList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scanner)

        //TODO : Add permission asked to user  for location, the functionality isn't working otherwise

        scanBtn = scan_btn
        scanBtn.setOnClickListener {
            scanWifi()
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        wifiList = wifi_list
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "Wifi is disabled... You need to enable it", Toast.LENGTH_LONG)
                .show()
            wifiManager.isWifiEnabled = true
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        wifiList.adapter = adapter
        scanWifi()


    }

    private fun scanWifi() {
        arrayList.clear()
        adapter.clear()
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()
        Toast.makeText(this, "Scanning Wifi...", Toast.LENGTH_SHORT).show()
        connectToWifi()
    }

    private fun connectToWifi() {
        wifiList.setOnItemClickListener { parent, view, position, id ->
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Wifi Password")
            alertDialogBuilder.setMessage("Please enter this Wifi password : ")

            // Set up the input
            var input_password = ""
            val passwordInput = EditText(this)
            passwordInput.inputType =
                InputType.TYPE_CLASS_TEXT; InputType.TYPE_TEXT_VARIATION_PASSWORD

            alertDialogBuilder.setView(passwordInput)

            // Set up the buttons
            alertDialogBuilder.setPositiveButton(
                "Enter"
            ) { dialog, which ->
                input_password = passwordInput.text.toString()
            }

            alertDialogBuilder.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
                dialog.cancel()
            }
            alertDialogBuilder.show()

            val ssid = resultList[position].SSID

            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = String.format("\"%s\"", ssid)
            wifiConfig.preSharedKey = String.format("\"%s\"", input_password)

            //remember id
            val netId = wifiManager.addNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
        }
    }

    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            resultList = wifiManager.scanResults as ArrayList<ScanResult>
            unregisterReceiver(this)

            for (scanResult in resultList) {
                arrayList.add(
                    scanResult.SSID + " - " +
                            scanResult.capabilities + " - " +
                            scanResult.level + " - " +
                            scanResult.BSSID + " - " +
                            scanResult.frequency
                )
                adapter.notifyDataSetChanged()
            }
            Log.d("TESTING", "onReceive Called")
        }
    }

    private fun freezeUI() {

    }
}
