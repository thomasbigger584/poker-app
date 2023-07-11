package com.twb.pokergame.ui.activity.login.auth;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.openid.appauth.Preconditions;
import net.openid.appauth.connectivity.ConnectionBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * DO NOT USE THIS IN PRODUCTION
 * TESTING INSTANCE ALLOWS HTTP AND HTTPS WITH ANY CERTIFICATE
 */
public class CustomConnectionBuilder implements ConnectionBuilder {
    public static final CustomConnectionBuilder INSTANCE = new CustomConnectionBuilder();
    private static final String TAG = "ConnectionBuilder";

    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    @Nullable
    private static final SSLContext TRUSTING_CONTEXT;


    @SuppressLint("CustomX509TrustManager")
    private static final TrustManager[] ANY_CERT_MANAGER = new TrustManager[]{
            new X509TrustManager() {

                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                }


                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
    };

    private static final HostnameVerifier ANY_HOSTNAME_VERIFIER = (hostname, session) -> true;

    static {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sslContext = null;
        }

        SSLContext initializedContext = null;
        if (sslContext != null) {
            try {
                sslContext.init(null, ANY_CERT_MANAGER, new java.security.SecureRandom());
                initializedContext = sslContext;
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        TRUSTING_CONTEXT = initializedContext;
    }

    private CustomConnectionBuilder() {
    }

    @NonNull
    @Override
    public HttpURLConnection openConnection(@NonNull Uri uri) throws IOException {
        Preconditions.checkNotNull(uri, "url must not be null");
        Preconditions.checkArgument(HTTP.equals(uri.getScheme()) || HTTPS.equals(uri.getScheme()),
                "scheme or uri must be http or https");
        HttpURLConnection connection = (HttpURLConnection) new URL(String.valueOf(uri)).openConnection();
        connection.setInstanceFollowRedirects(false);

        if (connection instanceof HttpsURLConnection && TRUSTING_CONTEXT != null) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
            httpsConn.setSSLSocketFactory(TRUSTING_CONTEXT.getSocketFactory());
            httpsConn.setHostnameVerifier(ANY_HOSTNAME_VERIFIER);
        }
        return connection;
    }
}