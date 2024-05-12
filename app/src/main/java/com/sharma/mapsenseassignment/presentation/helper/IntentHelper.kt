package com.sharma.mapsenseassignment.presentation.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings


/**
 * Open settings page to ask user to allow location permission
 */
fun openAppSettings(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    context.startActivity(intent)
}