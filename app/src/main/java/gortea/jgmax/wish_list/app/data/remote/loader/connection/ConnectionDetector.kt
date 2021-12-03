package gortea.jgmax.wish_list.app.data.remote.loader.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConnectionDetector(context: Context) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean>
        get() = _isConnected
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val validNetworks: MutableSet<Network> = HashSet()

    fun detect() {
        if (networkCallback == null) {
            networkCallback = createNetworkCallback()
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            networkCallback?.let { connectivityManager.registerNetworkCallback(networkRequest, it) }
        }
    }

    fun stopDetection() {
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        networkCallback = null
    }

    private fun checkNetwork() {
        _isConnected.value = validNetworks.isNotEmpty()
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isInternet =
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (isInternet == true) {
                validNetworks.add(network)
            }
            checkNetwork()
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkNetwork()
        }
    }
}
