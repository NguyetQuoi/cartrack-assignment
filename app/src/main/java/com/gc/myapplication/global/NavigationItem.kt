package com.gc.myapplication.global

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import com.gc.myapplication.base.BaseActivity
import com.gc.myapplication.base.BaseDialogFragment
import com.gc.myapplication.base.BaseFragment
import com.gc.myapplication.extention.showToast

/**
 * @author n.quoi
 * @date 10.18.2021
 */

abstract class NavigationItem {

    /**
     * Abstract for navigation
     * @param activity activity
     */
    abstract fun navigate(activity: BaseActivity<*, *>)

    /**
     * Start an activity with data is class of destination (eg: MainActivity.class)
     */
    class StartActivity private constructor(
        @VisibleForTesting val clazz: Class<*>?,
        private val intent: Intent?,
        private val bundle: Bundle?,
        private val requestCode: Int,
        private val clearTask: Boolean,
        private val finish: Boolean,
        private val transactionAnimation: TransactionAnimation? =
            TransactionAnimation.NONE
    ) : NavigationItem() {
        constructor(
            clazz: Class<*>,
            data: Bundle? = null,
            clearTask: Boolean = false,
            requestCode: Int = -1,
            finish: Boolean = false,
            transactionAnimation: TransactionAnimation?
        )
                : this(clazz, null, data, requestCode, clearTask, finish, transactionAnimation)

        constructor(
            intent: Intent, data: Bundle? = null, requestCode: Int = -1,
            finish: Boolean = false, transactionAnimation: TransactionAnimation?
        )
                : this(null, intent, data, requestCode, false, finish, transactionAnimation)

        override fun navigate(activity: BaseActivity<*, *>) {
            val launcherIntent = intent ?: if (clazz != null) Intent(activity, clazz) else null
            launcherIntent?.let { intent ->
                intent.putExtra(BaseActivity.BUNDLE_EXTRA, bundle)
                if (clearTask) {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (requestCode >= 0) {
                    activity.startActivityForResult(intent, requestCode)
                } else {
                    activity.startActivity(intent)
                }
                transactionAnimation?.let {
                    activity.overridePendingTransition(it.enter, it.exit)
                }
            }
            if (finish) activity.finish()
        }
    }

    /**
     * Show toast
     */
    class ShowToast private constructor(
        @VisibleForTesting val message: String?,
        private val messageId: Int,
        private val argParams: Array<out Any>
    ) : NavigationItem() {
        constructor(message: String) : this(message, 0, emptyArray())
        constructor(messageId: Int, vararg argParams: Any) : this(null, messageId, argParams)

        /**
         * Navigate function
         * @param activity component where show toast
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            val mess = message
                ?: if (argParams.isNotEmpty()) activity.getString(
                    messageId,
                    argParams
                ) else activity.getString(messageId)
            activity.showToast(mess)
        }
    }

    /**
     * Finish an intent with activity RESULT_OK
     * @param intent [Intent]
     */
    class Finish(
        private val intent: Intent? = null,
        private val transactionAnimation: TransactionAnimation? = TransactionAnimation.NONE
    ) : NavigationItem() {
        override fun navigate(activity: BaseActivity<*, *>) {
            intent?.let { activity.setResult(Activity.RESULT_OK, it) }
            activity.finish()
            transactionAnimation?.let {
                activity.overridePendingTransition(it.popEnter, it.popExit)
            }
        }
    }

    /**
     * Change fragment
     * @param fragment which will be shown
     * @param addToTask add new fragment to back stack
     * @param clearTask clear old fragment from back stack
     * @param transactionAnimation animation for new fragment
     */
    class ChangeFragment(
        @VisibleForTesting val fragment: BaseFragment<*, *>,
        private val clearTask: Boolean = false,
        private val addToTask: Boolean = false,
        private val transactionAnimation: TransactionAnimation = TransactionAnimation.NONE
    ) : NavigationItem() {
        override fun navigate(activity: BaseActivity<*, *>) {
            activity.changeFragment(fragment, clearTask, addToTask, transactionAnimation)
        }
    }

    /**
     * Pop back fragment class
     */
    class PopFragmentBack : NavigationItem() {
        /**
         * Navigate function
         * @param activity which component will be popped back
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            activity.onBackPressed()
        }
    }

    /**
     * Show dialog
     * @param dialog type of BaseDialogFragment
     * @param tag tag string of fragment
     * @param positiveClick listener for positive click
     * @param negativeClick listener for negative click
     */
    class ShowDialog(
        @VisibleForTesting val dialog: BaseDialogFragment<*>,
        private val tag: String = DEFAULT_DIALOG_TAG,
        @VisibleForTesting val positiveClick: DialogInterface.OnClickListener? = null,
        @VisibleForTesting val negativeClick: DialogInterface.OnClickListener? = null
    ) : NavigationItem() {
        /**
         * Navigate function
         * @param activity component where show dialog
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            dialog.show(activity.supportFragmentManager, tag, positiveClick, negativeClick)
        }
    }

    /**
     * Dismiss dialog
     * @param dialog type of BaseDialogFragment
     * @param tag tag string of fragment
     */
    class DismissDialog(
        private val dialog: BaseDialogFragment<*>? = null,
        private val tag: String = DEFAULT_DIALOG_TAG
    ) : NavigationItem() {
        /**
         * Navigation function
         * @param activity component where dismiss dialog
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            val d = dialog
                ?: activity.supportFragmentManager.findFragmentByTag(tag) as? DialogFragment
            d?.dismiss()
        }
    }

    /**
     * Show loading dialog
     */
    class ShowLoadingDialog : NavigationItem() {
        /**
         * Navigation function
         * @param activity component where show loading dialog
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            activity.showLoadingDialog(true)
        }
    }

    /**
     * Dismiss loading dialog
     */
    class DismissLoadingDialog : NavigationItem() {
        /**
         * Navigation function
         * @param activity component where dismiss loading dialog
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            activity.showLoadingDialog(false)
        }
    }

    /**
     * Start service
     * @param clazz class name of service
     */
    class StartService(private val clazz: Class<*>?) : NavigationItem() {
        /**
         * Navigation function
         * @param activity component where start the service
         */
        override fun navigate(activity: BaseActivity<*, *>) {
            val intent = Intent(activity, clazz)
            activity.startService(intent)
        }
    }

    companion object {
        private const val DEFAULT_DIALOG_TAG = "dialog"
    }
}