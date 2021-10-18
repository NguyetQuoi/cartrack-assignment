package com.gc.myapplication.extention

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gc.myapplication.widget.PermissionUtils

/**
 * Function to hide soft keyboard
 */
fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Function to check whether app has permission or not
 * @param permission permission which want to be checked
 */
fun AppCompatActivity.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
}

/**
 * Checks if the result contains a [PackageManager.PERMISSION_GRANTED] result for a
 * permission from a runtime permissions request.
 *
 * @see androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
 * @param grantPermissions list permission which want to be checked
 * @param grantResults the list result
 */
fun AppCompatActivity.isPermissionGranted(grantPermissions: Array<String>, grantResults: IntArray,
                                          permission: String): Boolean {
    for (i in grantPermissions.indices) {
        if (permission == grantPermissions[i]) {
            return grantResults[i] == PackageManager.PERMISSION_GRANTED
        }
    }
    return false
}

/**
 * Requests permission. If a rationale with an additional explanation should
 * be shown to the user, displays a dialog that triggers the request.
 * @param permission permission which want to request
 * @param requestCode
 * @param dialogMessage sms
 * @param toastMessage toast sms
 */
@TargetApi(Build.VERSION_CODES.M)
fun AppCompatActivity.requestPermissionsSafe(permission: String, requestCode: Int, dialogMessage: Int, toastMessage: Int) {

    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
        // Display a dialog with rationale.
        PermissionUtils.RationaleDialog.newInstance(requestCode, false, dialogMessage, toastMessage)
                .show(supportFragmentManager, "dialog")
    } else {
        // Location permission has not been granted yet, request it.
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

    }
}

/**
 * Function help update title of toolbar
 * @param heading new title of toolbar
 */
fun AppCompatActivity.updateToolbarTitle(heading: String) {
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
    supportActionBar?.title = heading
}

/**
 * Function help show/hide home button
 * @param show boolean value stand for state of home button
 */
fun AppCompatActivity.showHomeButton(show: Boolean) {
    this.supportActionBar?.setDisplayHomeAsUpEnabled(show)
}

/**
 * Function help hide/show toolbar
 * @param isHide boolean value stand for visible state of toolbar
 */
fun AppCompatActivity.hideToolbar(isHide: Boolean) {
    if (isHide)
        supportActionBar?.hide()
    else
        supportActionBar?.show()
}

/**
 * Function help show toast
 * @param message the sms which need to be shown
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Function help show toast
 * @param messageId the string resource id stand for which sms need to be shown
 */
fun Context.showToast(@StringRes messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
}