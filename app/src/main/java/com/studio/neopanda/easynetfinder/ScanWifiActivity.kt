package com.studio.neopanda.easynetfinder

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.ListAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scan_wifi.*


class ScanWifiActivity : Activity(), View.OnClickListener {
    internal lateinit var wifi: WifiManager
    internal var size = 0
    internal lateinit var results: List<ScanResult>

    private var ITEM_KEY = "key"
    private var arraylist = ArrayList<HashMap<String, String>>()
    private lateinit var adapter: SimpleAdapter

    /* Called when the activity is first created. */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_wifi)

        buttonScan.setOnClickListener(this)
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifi.isWifiEnabled) {
            Toast.makeText(
                applicationContext,
                "wifi is disabled..making it enabled",
                Toast.LENGTH_LONG
            ).show()
            wifi.isWifiEnabled = true
        }
        this.adapter = SimpleAdapter(
            this@ScanWifiActivity,
            arraylist,
            R.layout.row,
            arrayOf(ITEM_KEY),
            intArrayOf(R.id.list_value)
        )
        list.adapter = this.adapter

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                results = wifi.scanResults
                size = results.size
            }
        }, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    override fun onClick(view: View) {
        arraylist.clear()
        wifi.startScan()

        Toast.makeText(this, "Scanning....$size", Toast.LENGTH_SHORT).show()
        try {
            size -= 1
            while (size >= 0) {
                val item = HashMap<String, String>()
                item[ITEM_KEY] = results[size].SSID + "  " + results[size].capabilities

                arraylist.add(item)
                size--
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
        }

    }
}