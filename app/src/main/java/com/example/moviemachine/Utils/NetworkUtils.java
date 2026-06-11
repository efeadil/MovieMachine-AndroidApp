package com.example.moviemachine.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class for network connectivity checks.
 * EN: Provides methods to check the device's internet connection status.
 * TR: Cihazın internet bağlantısını kontrol eden yardımcı metodları içerir.
 */
public class NetworkUtils {

    /**
     * EN: Checks if the device is currently connected to the internet.
     * TR: Cihazın internete bağlı olup olmadığını kontrol eder.
     * @param context Application context
     * @return True if connected, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
