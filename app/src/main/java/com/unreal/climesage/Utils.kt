package com.unreal.climesage

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.app.AlertDialog

class Utils {
    companion object {
        var BASE_URL: String = "https://api.openweathermap.org/data/2.5/"
        var API_KEY: String = "2b735dc113eb7adffb91d09604767927"
        const val PERMISSION_REQUEST_CODE = 123

        const val LOCATION_PERMISSION_REQUEST_CODE = 1001

        fun isConnectedToInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                } else {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    return activeNetworkInfo != null && activeNetworkInfo.isConnected
                }
            }

            return false
        }

        fun showAlertDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String = "OK",
            negativeButtonText: String? = null,
            positiveAction: (() -> Unit)? = null,
            negativeAction: (() -> Unit)? = null
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText) { _, _ ->
                    positiveAction?.invoke()
                }

            if (negativeButtonText != null) {
                builder.setNegativeButton(negativeButtonText) { _, _ ->
                    negativeAction?.invoke()
                }
            }

            builder.create().show()
        }
    }
}
