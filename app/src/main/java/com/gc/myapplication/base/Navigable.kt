package com.gc.myapplication.base

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import com.gc.myapplication.global.TransactionAnimation

/**
 * Navigation interface
 * @author n.quoi
 * @date 10.18.2021
 */

interface Navigable {
    /**
     * Start an explicit activity
     * @param clazz class name of activity
     * @param data data pass to new activity
     * @param clearTask start new activity and clear current task
     * @param animation [TransactionAnimation]
     */
    fun startActivity(
        clazz: Class<*>, data: Bundle? = null, clearTask: Boolean = false, finish: Boolean = false,
        animation: TransactionAnimation? = TransactionAnimation.NONE
    )

    /**
     * Start an implicit activity
     * @param intent intent of activity
     * @param data data pass to new activity
     * @param animation [TransactionAnimation]
     */
    fun startActivity(
        intent: Intent,
        data: Bundle? = null,
        animation: TransactionAnimation? = TransactionAnimation.NONE
    )

    /**
     * Start an explicit activity for the result
     * @param clazz class name of activity
     * @param data which data pass to new activity
     * @param requestCode the request code to identity
     * @param animation [TransactionAnimation]
     */
    fun startActivityForResult(
        clazz: Class<*>, requestCode: Int, data: Bundle? = null,
        animation: TransactionAnimation? = TransactionAnimation.NONE
    )

    /**
     * Start an implicit activity for the result
     * @param intent intent of new activity
     * @param requestCode the request code to identity
     * @param data data pass to new activity
     * @param animation [TransactionAnimation]
     */
    fun startActivityForResult(
        intent: Intent, requestCode: Int, data: Bundle? = null,
        animation: TransactionAnimation? = TransactionAnimation.NONE
    )

    /**
     * Finish activity
     * @param data intent which will return to previous component
     */
    fun finishActivity(
        data: Intent? = null,
        animation: TransactionAnimation? = TransactionAnimation.NONE
    )

    /**
     * Start a Service
     * @param clazz class name of service which will be started
     */
    fun startService(clazz: Class<*>?)

    /**
     * Show a toast with string resource
     * @param messageId resource id of message
     */
    fun showToast(@StringRes messageId: Int, vararg argParams: Any)

    /**
     * Show a toast with string
     * @param message which message want to be shown
     */
    fun showToast(message: String)

    /**
     * Change to a fragment
     * @param fragment fragment which navigate to
     * @param addToTask add new fragment to task or not
     * @param clearTask clear task for current fragment or not
     * @param animation type of transaction animation which apply to
     */
    fun changeFragment(
        fragment: BaseFragment<*, *>,
        clearTask: Boolean = false,
        addToTask: Boolean = false,
        animation: TransactionAnimation = TransactionAnimation.NONE
    )

    /**
     * Pop back current fragment
     */
    fun popFragmentBack()

    /**
     * Show loading dialog
     */
    fun showLoadingDialog()

    /**
     * Dismiss the loading dialog
     */
    fun dismissLoadingDialog()

    /**
     * Show a dialog
     * @param dialog which dialog need to be shown
     * @param tag
     * @param positiveClickListener listener for on positive button click
     * @param negativeClickListener listener for on negative button click
     */
    fun showDialog(
        dialog: BaseDialogFragment<*>,
        tag: String = "dialog",
        positiveClickListener: DialogInterface.OnClickListener? = null,
        negativeClickListener: DialogInterface.OnClickListener? = null
    )

    /**
     * Dismiss the dialog
     * @param dialog which dialog need to be shown
     */
    fun dismissDialog(dialog: BaseDialogFragment<*>? = null)

    /**
     * Dismiss a dialog with tag
     * @param tag
     */
    fun dismissDialog(tag: String = "dialog")
}