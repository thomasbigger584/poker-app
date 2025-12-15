/*
 * Copyright 2016 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twb.pokerapp.data.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twb.pokerapp.R;

import net.openid.appauth.connectivity.ConnectionBuilder;
import net.openid.appauth.connectivity.DefaultConnectionBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;


/**
 * Reads and validates the demo app configuration from `res/raw/auth_config.json`. Configuration
 * changes are detected by comparing the hash of the last known configuration to the read
 * configuration. When a configuration change is detected, the app state is reset.
 */
public final class AuthConfiguration {
    private static final String PREFS_NAME = "com.twb.pokerapp.auth.config";
    private static final String KEY_LAST_HASH = "com.twb.pokerapp.auth.config.lastHash";
    private final Context context;
    private final SharedPreferences prefs;
    private final Resources resources;

    private JSONObject configJson;
    private String configHash;
    private String configError;

    private String clientId;
    private String scope;
    private Uri redirectUri;
    private Uri endSessionRedirectUri;
    private Uri discoveryUri;
    private Uri authEndpointUri;
    private Uri tokenEndpointUri;
    private Uri endSessionEndpoint;
    private Uri registrationEndpointUri;
    private Uri userInfoEndpointUri;
    private boolean httpsRequired;

    public AuthConfiguration(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.resources = context.getResources();

        try {
            readConfiguration();
        } catch (InvalidConfigurationException ex) {
            configError = ex.getMessage();
        }
    }

    /**
     * Indicates whether the configuration has changed from the last known valid state.
     */
    public boolean hasConfigurationChanged() {
        var lastHash = getLastKnownConfigHash();
        return !configHash.equals(lastHash);
    }

    /**
     * Indicates whether the current configuration is valid.
     */
    public boolean isValid() {
        return configError == null;
    }

    /**
     * Returns a description of the configuration error, if the configuration is invalid.
     */
    @Nullable
    public String getConfigurationError() {
        return configError;
    }

    /**
     * Indicates that the current configuration should be accepted as the "last known valid"
     * configuration.
     */
    public void acceptConfiguration() {
        prefs.edit().putString(KEY_LAST_HASH, configHash).apply();
    }

    @Nullable
    public String getClientId() {
        return clientId;
    }

    @NonNull
    public String getScope() {
        return scope;
    }

    @NonNull
    public Uri getRedirectUri() {
        return redirectUri;
    }

    @Nullable
    public Uri getDiscoveryUri() {
        return discoveryUri;
    }

    @Nullable
    public Uri getEndSessionRedirectUri() {
        return endSessionRedirectUri;
    }

    @Nullable
    public Uri getAuthEndpointUri() {
        return authEndpointUri;
    }

    @Nullable
    public Uri getTokenEndpointUri() {
        return tokenEndpointUri;
    }

    @Nullable
    public Uri getEndSessionEndpoint() {
        return endSessionEndpoint;
    }

    @Nullable
    public Uri getRegistrationEndpointUri() {
        return registrationEndpointUri;
    }

    @Nullable
    public Uri getUserInfoEndpointUri() {
        return userInfoEndpointUri;
    }

    public boolean isHttpsRequired() {
        return httpsRequired;
    }

    public ConnectionBuilder getConnectionBuilder() {
        if (isHttpsRequired()) {
            return DefaultConnectionBuilder.INSTANCE;
        }
        return ConnectionBuilderForTesting.INSTANCE;
    }

    private String getLastKnownConfigHash() {
        return prefs.getString(KEY_LAST_HASH, null);
    }

    private void readConfiguration() throws InvalidConfigurationException {
        var configSource =
                Okio.buffer(Okio.source(resources.openRawResource(R.raw.auth_config)));
        var configData = new Buffer();
        try {
            configSource.readAll(configData);
            configJson = new JSONObject(configData.readString(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new InvalidConfigurationException(
                    "Failed to read configuration: " + ex.getMessage());
        } catch (JSONException ex) {
            throw new InvalidConfigurationException(
                    "Unable to parse configuration: " + ex.getMessage());
        }

        configHash = configData.sha256().base64();
        clientId = getConfigString("client_id");
        scope = getRequiredConfigString("authorization_scope");
        redirectUri = getRequiredConfigUri("redirect_uri");
        endSessionRedirectUri = getRequiredConfigUri("end_session_redirect_uri");

        if (!isRedirectUriRegistered()) {
            throw new InvalidConfigurationException(
                    "redirect_uri is not handled by any activity in this app! "
                            + "Ensure that the appAuthRedirectScheme in your build.gradle file "
                            + "is correctly configured, or that an appropriate intent filter "
                            + "exists in your app manifest.");
        }

        if (getConfigString("discovery_uri") == null) {
            authEndpointUri = getRequiredConfigWebUri("authorization_endpoint_uri");

            tokenEndpointUri = getRequiredConfigWebUri("token_endpoint_uri");
            userInfoEndpointUri = getRequiredConfigWebUri("user_info_endpoint_uri");
            endSessionEndpoint = getRequiredConfigUri("end_session_endpoint");

            if (clientId == null) {
                registrationEndpointUri = getRequiredConfigWebUri("registration_endpoint_uri");
            }
        } else {
            discoveryUri = getRequiredConfigWebUri("discovery_uri");
        }
        httpsRequired = configJson.optBoolean("https_required", true);
    }

    @Nullable
    String getConfigString(String propName) {
        var value = configJson.optString(propName);

        value = value.trim();
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        return value;
    }

    @NonNull
    private String getRequiredConfigString(String propName)
            throws InvalidConfigurationException {
        var value = getConfigString(propName);
        if (value == null) {
            throw new InvalidConfigurationException(
                    propName + " is required but not specified in the configuration");
        }

        return value;
    }

    @NonNull
    Uri getRequiredConfigUri(String propName)
            throws InvalidConfigurationException {
        var uriStr = getRequiredConfigString(propName);
        Uri uri;
        try {
            uri = Uri.parse(uriStr);
        } catch (Throwable ex) {
            throw new InvalidConfigurationException(propName + " could not be parsed", ex);
        }

        if (!uri.isHierarchical() || !uri.isAbsolute()) {
            throw new InvalidConfigurationException(
                    propName + " must be hierarchical and absolute");
        }

        if (!TextUtils.isEmpty(uri.getEncodedUserInfo())) {
            throw new InvalidConfigurationException(propName + " must not have user info");
        }

        if (!TextUtils.isEmpty(uri.getEncodedQuery())) {
            throw new InvalidConfigurationException(propName + " must not have query parameters");
        }

        if (!TextUtils.isEmpty(uri.getEncodedFragment())) {
            throw new InvalidConfigurationException(propName + " must not have a fragment");
        }

        return uri;
    }

    Uri getRequiredConfigWebUri(String propName)
            throws InvalidConfigurationException {
        var uri = getRequiredConfigUri(propName);
        var scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme) || !("http".equals(scheme) || "https".equals(scheme))) {
            throw new InvalidConfigurationException(
                    propName + " must have an http or https scheme");
        }

        return uri;
    }

    private boolean isRedirectUriRegistered() {
        // ensure that the redirect URI declared in the configuration is handled by some activity
        // in the app, by querying the package manager speculatively
        var redirectIntent = new Intent();
        redirectIntent.setPackage(context.getPackageName());
        redirectIntent.setAction(Intent.ACTION_VIEW);
        redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        redirectIntent.setData(redirectUri);

        return !context.getPackageManager().queryIntentActivities(redirectIntent, 0).isEmpty();
    }

    public static final class InvalidConfigurationException extends Exception {
        InvalidConfigurationException(String reason) {
            super(reason);
        }

        InvalidConfigurationException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }
}
