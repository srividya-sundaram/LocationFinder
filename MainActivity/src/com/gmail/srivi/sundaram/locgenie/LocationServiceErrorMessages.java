/*******************************************************************************
 * /**************************************************************************************
 * LocGenie – An open source Android application that suggests users places of their preferred activity within their 
 * preferred distance in Map View along with their address.
 *
 * Copyright (C) 2014 Srividya Sundaram
 *
 * This program is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. 
 * If not, see http://www.gnu.org/licenses/.
 *
 * Following is the link for the repository: https://github.com/Srividya2212/LocationFinder
 *
 * Please, see the file license in this distribution for license terms. Link is
 * https://github.com/Srividya2212/LocationFinder/blob/master/LICENSE.md
 *
 * References:
 * https://developers.google.com/maps/documentation/android/start#getting_the_google_maps_android_api_v2
 * https://developers.google.com/maps/documentation/android/
 * https://developers.google.com/places/documentation/
 * https://developers.google.com/places/documentation/search
 * http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android
 *
 * Author - Srividya Sundaram
 * email: srividya@pdx.edu
 *
 *  ******************************************************************************************/

package com.gmail.srivi.sundaram.locgenie;

import com.google.android.gms.common.ConnectionResult;
import android.content.Context;
import android.content.res.Resources;

import com.gmail.srivi.sundaram.locgenie.R;

/**
 * Map error codes to error messages.
 */
public class LocationServiceErrorMessages {

    // Don't allow instantiation
    private LocationServiceErrorMessages() {}

    public static String getErrorString(Context context, int errorCode) {

        // Get a handle to resources, to allow the method to retrieve messages.
        Resources mResources = context.getResources();

        // Define a string to contain the error message
        String errorString;

        // Decide which error message to get, based on the error code.
        switch (errorCode) {
            case ConnectionResult.DEVELOPER_ERROR:
                errorString = mResources.getString(R.string.connection_error_misconfigured);
                break;

            case ConnectionResult.INTERNAL_ERROR:
                errorString = mResources.getString(R.string.connection_error_internal);
                break;

            case ConnectionResult.INVALID_ACCOUNT:
                errorString = mResources.getString(R.string.connection_error_invalid_account);
                break;

            case ConnectionResult.LICENSE_CHECK_FAILED:
                errorString = mResources.getString(R.string.connection_error_license_check_failed);
                break;

            case ConnectionResult.NETWORK_ERROR:
                errorString = mResources.getString(R.string.connection_error_network);
                break;

            case ConnectionResult.RESOLUTION_REQUIRED:
                errorString = mResources.getString(R.string.connection_error_needs_resolution);
                break;

            case ConnectionResult.SERVICE_DISABLED:
                errorString = mResources.getString(R.string.connection_error_disabled);
                break;

            case ConnectionResult.SERVICE_INVALID:
                errorString = mResources.getString(R.string.connection_error_invalid);
                break;

            case ConnectionResult.SERVICE_MISSING:
                errorString = mResources.getString(R.string.connection_error_missing);
                break;

            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                errorString = mResources.getString(R.string.connection_error_outdated);
                break;

            case ConnectionResult.SIGN_IN_REQUIRED:
                errorString = mResources.getString(
                        R.string.connection_error_sign_in_required);
                break;

            default:
                errorString = mResources.getString(R.string.connection_error_unknown);
                break;
        }

        // Return the error message
        return errorString;
    }
}
