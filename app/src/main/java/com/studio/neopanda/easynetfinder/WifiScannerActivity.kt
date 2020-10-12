package com.studio.neopanda.easynetfinder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wifi_scanner.*
import java.security.Permission
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
