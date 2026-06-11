package com.example.moviemachine.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * EN: Tests device internet connectivity logic using Mockito.
 * TR: Cihazın internet bağlantı mantığını Mockito kullanarak test eder.
 */
public class NetworkUtilsTest {

    @Mock
    Context mockContext;
    @Mock
    ConnectivityManager mockConnectivityManager;
    @Mock
    NetworkInfo mockNetworkInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsNetworkAvailable_Connected() {
        // EN: Scenario - Internet is available.
        // TR: İnternet bağlantısının olduğu senaryo.
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(true);

        assertTrue(NetworkUtils.isNetworkAvailable(mockContext));
    }

    @Test
    public void testIsNetworkAvailable_NotConnected() {
        // EN: Scenario - Internet is not available.
        // TR: İnternet bağlantısının olmadığı senaryo.
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);

        assertFalse(NetworkUtils.isNetworkAvailable(mockContext));
    }
}
