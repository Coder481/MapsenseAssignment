package com.sharma.mapsenseassignment.presentation.helper

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast


fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
fun Context.showMessageDialog(msg: String) {
    val dialog = AlertDialog.Builder(this)
    dialog.apply {
        setTitle("Message for you!")
        setMessage(msg)
        setPositiveButton("Sure"){a,b -> a.dismiss() }
        show()
    }
}
fun Context.askForPermissionDialog() {
    val dialog = AlertDialog.Builder(this)
    dialog.apply {
        setTitle("Need permission!")
        setMessage("Location permission denied. Please give the app the location permission from settings to access your state.")
        setPositiveButton("Open Settings"){a,b -> openAppSettings(this@askForPermissionDialog, packageName) }
        setNegativeButton("Close") { dialogInterface,b -> dialogInterface.dismiss() }
        show()
    }
}