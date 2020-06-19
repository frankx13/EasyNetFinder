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
import android.net.NetworkRequest
import android.net.NetworkCapabilities
import androidx.annotation.RequiresApi
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        //GET ALL NETWORKS
        show_networks_btn.setOnClickListener {
            getAvailableNetwoks(this)
        }

        //TODO: GET ACTIVE NETWORK & INFOS
        //TODO: VPN CASE
        //TODO: CREATE SHAPES & THEME FOR THE UI
        //TODO: GET DHCP INFOS
        //TODO: PING FUNCTIONNALITY
        //TODO: TRACERT FUNCTIONNALITY
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getAvailableNetwoks(context: Context): Map<String, Network> {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val setNetworkName = HashMap<String, Network>()
        if (null != connManager) {

            // Set MOBILE network for checking is it existing
            activateMobileNetwork(context, connManager)

            val networks = connManager.allNetworks
            if (networks != null) {
                var nwInfo: NetworkInfo?
                for (nw in networks) {
                    nwInfo = connManager.getNetworkInfo(nw)
                    setNetworkName[nwInfo!!.typeName] = nw
                }
            }
        }

        Log.e("gorteijigire", setNetworkName.toString())
        Toast.makeText(context, "list of networks : " + setNetworkName.toString(), Toast.LENGTH_LONG).show()

        return setNetworkName
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun activateMobileNetwork(context: Context, connManager: ConnectivityManager) {
        val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)

        val networkRequest = builder.build()
        connManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Toast.makeText(context, "MOBILE connect", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
