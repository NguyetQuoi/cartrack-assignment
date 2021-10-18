package com.gc.myapplication.base

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.gc.myapplication.R
import com.gc.myapplication.global.*
import com.gc.myapplication.manager.UserManager
import com.gc.myapplication.util.FetcherListener
import com.gc.myapplication.util.StringUtils
import com.gc.myapplication.widget.dialog.ErrorDialog
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import timber.log.Timber

/**
 * An open class for ViewModel which are bound with UI component (Activity, Fragment)
 * @author n.quoi
 * @date 10.18.2021
 */

open class BindingViewModel(protected val userManager: UserManager) : BaseViewModel(), Navigable {

    /**
     * Live data is used to send navigate event (start Activity, change Fragment, show Dialog, ...)
     * to UI component that bound with this ViewModel
     */
    var navigationEvent = MutableLiveData<Event<NavigationItem>>()

    var fetcherListener: FetcherListener? = null

    var isUnitTest = false

    /**
     * Common onNext consumer of [Observable.subscribe] with dismiss loading dialog and show Toast "Successful"
     */
    protected var commonOnNextConsumer = {
        dismissLoadingDialog()
        showToast(R.string.successful)
    }

    /**
     * Common onError consumer of [Observable.subscribe] with dismiss loading dialog and show Toast use [handleError]
     */
    protected var commonOnErrorConsumer = Consumer<Throwable> { throwable ->
        dismissLoadingDialog()
        handleError(throwable)
    }

    /**
     * onError consumer of [Observable.subscribe] with log [Throwable]
     */
    protected var onErrorLogConsumer: Consumer<in Throwable> = Consumer { Timber.e(it) }

    val userSignedIn = RxProperty(false)

//    init {
//        userManager.userState().observeForever {
//            Timber.d("UserState change: ${it.userState}")
//            handleUserState(it.userState)
//        }
//    }
//
//    protected open fun handleUserState(userState: UserState) {
//        when (userState) {
//            UserState.SIGNED_IN -> {
//                userSignedIn.set(true)
//            }
//            UserState.SIGNED_OUT_FEDERATED_TOKENS_INVALID,
//            UserState.SIGNED_OUT_USER_POOLS_TOKENS_INVALID -> {
//                handleSessionExpired()
//                userSignedIn.set(false)
//            }
//            UserState.SIGNED_OUT -> {
//                userSignedIn.set(false)
//            }
//            else -> Timber.i("unsupported: $userState")
//        }
//    }

    /**
     * Handle network error. If want to handle session expired, should override handleSessionExpired methods, eg: logout,..
     * @param error network error
     */
    protected fun handleError(error: Throwable?) {
        error?.let { throwable ->
            Timber.d(throwable)
            when (throwable) {
                is RetrofitException -> {
                    when (throwable.kind) {
                        RetrofitException.Kind.HTTP -> {
                            val messages = throwable.errorData?.errorMessage ?: error.message
                            messages?.let { showToast(it) }
                        }

                        RetrofitException.Kind.HTTP_403 -> {
                            showToast(R.string.session_expired)
                            handleSessionExpired()
                        }

                        RetrofitException.Kind.NETWORK, RetrofitException.Kind.JSON_SYNTAX, RetrofitException.Kind.UNEXPECTED ->
                            showToast(R.string.time_out)

                        else -> showToast(R.string.time_out)
                    }
                }
                is UserThrowable -> {
                    throwable.message?.let { showToast(it) }
                }
                else -> showToast(R.string.unknown_error)
            }
        }
    }

    protected open fun handleSessionExpired() {
        Timber.d("Token is expired")
        showToast(R.string.session_expired)
        //startActivity(WelcomeActivity::class.java, clearTask = true, finish = true)
    }

    private fun navigateTo(item: NavigationItem) {
        navigationEvent.value = Event(item)
    }

    override fun startActivity(clazz: Class<*>, data: Bundle?, clearTask: Boolean, finish: Boolean,
                               animation: TransactionAnimation?) {
        navigateTo(NavigationItem.StartActivity(clazz, data, clearTask, finish = finish, transactionAnimation = animation))
    }

    override fun startActivity(intent: Intent, data: Bundle?, animation: TransactionAnimation?) {
        navigateTo(NavigationItem.StartActivity(intent, data, transactionAnimation = animation))
    }

    override fun startActivityForResult(clazz: Class<*>, requestCode: Int, data: Bundle?, animation: TransactionAnimation?) {
        navigateTo(NavigationItem.StartActivity(clazz, data, false, requestCode, transactionAnimation = animation))
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int, data: Bundle?, animation: TransactionAnimation?) {
        navigateTo(NavigationItem.StartActivity(intent, data, requestCode, transactionAnimation = animation))
    }

    override fun finishActivity(data: Intent?, animation: TransactionAnimation?) {
        navigateTo(NavigationItem.Finish(data, transactionAnimation = animation))
    }

    override fun startService(clazz: Class<*>?) {
        navigateTo(NavigationItem.StartService(clazz))
    }

    override fun showDialog(dialog: BaseDialogFragment<*>, tag: String, positiveClickListener: DialogInterface.OnClickListener?, negativeClickListener: DialogInterface.OnClickListener?) {
        navigateTo(NavigationItem.ShowDialog(dialog, tag, positiveClickListener, negativeClickListener))
    }

    override fun dismissDialog(dialog: BaseDialogFragment<*>?) {
        navigateTo(NavigationItem.DismissDialog(dialog))
    }

    override fun dismissDialog(tag: String) {
        navigateTo(NavigationItem.DismissDialog(tag = tag))
    }

    override fun showToast(messageId: Int, vararg argParams: Any) {
        navigateTo(NavigationItem.ShowToast(messageId, argParams))
    }

    override fun showToast(message: String) {
        navigateTo(NavigationItem.ShowToast(message))
    }

    override fun changeFragment(fragment: BaseFragment<*, *>, clearTask: Boolean, addToTask: Boolean, animation: TransactionAnimation) {
        navigateTo(NavigationItem.ChangeFragment(fragment, clearTask, addToTask, animation))
    }

    override fun popFragmentBack() {
        navigateTo(NavigationItem.PopFragmentBack())
    }

    override fun showLoadingDialog() {
        if (!isUnitTest) navigateTo(NavigationItem.ShowLoadingDialog())
    }

    override fun dismissLoadingDialog() {
        if (!isUnitTest) navigateTo(NavigationItem.DismissLoadingDialog())
    }

    /**
     * On activity result
     * @param requestCode the request code help to check identity of response
     * @param resultCode result code: RESULT_OK, RESULT_CANCEL
     * @param data response of request
     * @return true if consume this callback and Activity/Fragment will doesn't run [BaseActivity.onActivityResult]
     */
    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    /**
     * On back press
     * @return true if consume this callback and Activity/Fragment will doesn't run [BaseActivity.onBackPressed]
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    /**
     * Show error as dialog
     * @param errorMessage error sms
     * @param sampleError if error is null, then will show sample error
     */
    open fun showErrorDialog(errorMessage: String, sampleError: String) {
        val error = StringUtils.splitString(errorMessage, "(")
        if (error.isNotEmpty()) {
            showDialog(ErrorDialog.newInstance(error[0], 0))
        } else {
            showDialog(ErrorDialog.newInstance(sampleError, 0))
        }
    }
}
