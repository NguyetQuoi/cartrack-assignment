/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gc.myapplication.util

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.gc.myapplication.R
import com.gc.myapplication.widget.dialog.GeneralAlertDialog

/**
 * Utility class for access to runtime permissions
 * @date 05.12.2019
 */
object PermissionUtils {

    /**
     * A dialog that displays a permission denied message.
     */
    class PermissionDeniedDialog : GeneralAlertDialog() {

        private var mFinishActivity = false
        private var permissionRequiredToast: String? = null

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val arguments = arguments
            arguments?.let { argument ->
                mFinishActivity = argument.getBoolean(ARGUMENT_FINISH_ACTIVITY)
                permissionRequiredToast = argument.getString(ARGUMENT_TOAST) ?: ""
            }
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (mFinishActivity) {
                Toast.makeText(activity, permissionRequiredToast,
                        Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }

        companion object {

            private const val ARGUMENT_FINISH_ACTIVITY = "finish"
            private const val ARGUMENT_MESSAGE = "message"
            private const val ARGUMENT_TOAST = "toast"

            /**
             * Creates a new instance of this dialog and optionally finishes the calling Activity
             * when the 'Ok' button is clicked.
             */
            fun newInstance(finishActivity: Boolean, message: Int, permissionRequiredToast: Int): PermissionDeniedDialog {
                val arguments = Bundle()
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                arguments.putInt(ARGUMENT_MESSAGE, message)
                arguments.putInt(ARGUMENT_TOAST, permissionRequiredToast)
                arguments.putInt(POSITIVE_EXTRA, R.string.ok)

                arguments.putInt(MESSAGE_EXTRA, message)
                arguments.putInt(TITLE_EXTRA, permissionRequiredToast)

                val dialog = PermissionDeniedDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     *
     *
     * The activity should implement
     * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback]
     * to handle permit or denial of this permission request.
     */
    class RationaleDialog : GeneralAlertDialog() {

        private var mFinishActivity = false
        private var permissionRequiredToast: Int = 0
        private var smsResId: Int = 0

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (mFinishActivity) {
                Toast.makeText(activity,
                        permissionRequiredToast,
                        Toast.LENGTH_SHORT)
                        .show()
                activity?.finish()
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val arguments = arguments
            arguments?.let { argument ->
                val requestCode = argument.getInt(ARGUMENT_PERMISSION_REQUEST_CODE)
                mFinishActivity = argument.getBoolean(ARGUMENT_FINISH_ACTIVITY)
                smsResId = argument.getInt(ARGUMENT_MESSAGE)
                permissionRequiredToast = argument.getInt(ARGUMENT_TOAST)

                positiveClick = DialogInterface.OnClickListener { _, _ ->
                    activity?.let {
                        ActivityCompat.requestPermissions(it,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                requestCode)
                        // Do not finish the Activity while requesting permission.
                        mFinishActivity = false
                    }
                }
            }
        }

        companion object {

            private const val ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode"

            private const val ARGUMENT_FINISH_ACTIVITY = "finish"

            private const val ARGUMENT_MESSAGE = "message"
            private const val ARGUMENT_TOAST = "toast"

            /**
             * Creates a new instance of a dialog displaying the rationale for the use of the location
             * permission.
             *
             *
             * The permission is requested after clicking 'ok'.
             *
             * @param requestCode    Id of the request that is used to request the permission. It is
             * returned to the
             * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback].
             * @param finishActivity Whether the calling Activity should be finished if the dialog is
             * cancelled.
             */
            fun newInstance(requestCode: Int, finishActivity: Boolean, message: Int, permissionRequiredToast: Int): RationaleDialog {
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode)
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                arguments.putInt(ARGUMENT_MESSAGE, message)
                arguments.putInt(ARGUMENT_TOAST, permissionRequiredToast)
                arguments.putInt(MESSAGE_EXTRA, message)
                arguments.putBoolean(INT_TYPE_EXTRA, true)
                arguments.putInt(TITLE_EXTRA, permissionRequiredToast)

                arguments.putInt(NEGATIVE_EXTRA, R.string.no)
                arguments.putInt(POSITIVE_EXTRA, R.string.ok)

                val dialog = RationaleDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }
}
