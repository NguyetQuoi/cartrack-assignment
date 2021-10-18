package com.gc.myapplication.base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.gc.myapplication.BR
import com.gc.myapplication.extention.hideKeyboard
import com.gc.myapplication.global.NavigationItem
import com.gc.myapplication.global.TransactionAnimation
import com.gc.myapplication.widget.dialog.LoadingDialog
import io.reactivex.disposables.CompositeDisposable

/**
 * A base activity for CP
 * @author n.quoi
 * @date 10.17.2021
 * @param VM view-model
 * @param VDB view-data-binding
 */

abstract class BaseActivity<VM : BindingViewModel, VDB : ViewDataBinding> : AppCompatActivity() {

    protected abstract val viewModel: VM

    protected lateinit var binder: VDB

    protected var bundle: Bundle? = null
        private set

    protected var disposeBag = CompositeDisposable()

    private lateinit var loadingDialog: LoadingDialog

    @get:LayoutRes
    protected abstract val layoutId: Int

    @IdRes
    protected val bindingVariable: Int = BR.viewModel

    @IdRes
    protected open val fragmentContainer: Int = 0

//    protected var idlingResource: FetchingIdlingResource? = null
//        set(value) {
//            viewModel?.fetcherListener = value
//            field = value
//        }

//    /**
//     * Only called from test, creates and returns a new [FetchingIdlingResource].
//     */
//    @VisibleForTesting
//    fun getIdlingResource(): IdlingResource {
//        return idlingResource ?: FetchingIdlingResource().also { idlingResource = it }
//    }

    protected open fun configWindow() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configWindow()
//        if (!BuildConfig.DEBUG) Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())
        binder = DataBindingUtil.setContentView(this, layoutId)
        binder.setVariable(bindingVariable, viewModel)
        binder.executePendingBindings()

        bundle = intent.getBundleExtra(BUNDLE_EXTRA)
        loadingDialog = LoadingDialog()
        //idlingResource = FetchingIdlingResource()

        viewModel.navigationEvent.observe(this, Observer { event ->
            event?.let { onNavigationEvent(event.contentIfNotHandled) }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Handle navigate event from viewModel
     *
     * @param item type of navigate (with data from viewModel)
     */
    protected open fun onNavigationEvent(item: NavigationItem?) {
        item?.navigate(this)
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }

    /**
     * Function help to change fragment from viewModel
     * @param fragment which will replace current view
     * @param addToBackStack add new fragment to back stack or not
     * @param clearBackStack clear current fragment from back stack
     * @param animation transaction animation for the appearance of new fragment
     */
    fun changeFragment(
        fragment: BaseFragment<*, *>,
        clearBackStack: Boolean = false,
        addToBackStack: Boolean = false,
        animation: TransactionAnimation = TransactionAnimation.NONE
    ) {

        if (fragmentContainer == 0) throw IllegalArgumentException("Activity should override fragmentContainer method to support change fragment")

        hideKeyboard()
        val transaction = supportFragmentManager.beginTransaction()

        if (animation !== TransactionAnimation.NONE)
            transaction.setCustomAnimations(
                animation.enter,
                animation.exit,
                animation.popEnter,
                animation.popExit
            )

        if (clearBackStack)
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

//        if (addToBackStack && !clearBackStack) {
        if (addToBackStack) {
            transaction.add(fragmentContainer, fragment)
            transaction.addToBackStack(null)
        } else {
            transaction.replace(fragmentContainer, fragment)
        }
        transaction.commit()
    }

    /**
     * Action: show dialog on activity view
     * @param dialog which will be shown
     * @param tag a tag to separate these dialog with others
     */
    fun showDialog(dialog: DialogFragment, tag: String) {
        dialog.show(supportFragmentManager, tag)
    }

    /**
     * Action: dismiss current dialog
     * @param tag a tag to separate these dialog with others
     */
    fun dismissDialog(tag: String) {
        val dialog: DialogFragment? =
            supportFragmentManager.findFragmentByTag(tag) as? DialogFragment
        dialog?.dismiss()
    }

    /**
     * Action: show/dismiss loading dialog when process/finish a background job
     * @param show boolean value stand for the state of action: show/dismiss
     */
    fun showLoadingDialog(show: Boolean) {
        if (show) {
            if (!loadingDialog.isAdded) loadingDialog.show(supportFragmentManager, "loading")
        } else {
            if (loadingDialog.isAdded) loadingDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!viewModel.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        hideKeyboard()
        if (fragmentContainer != 0) {
            val fragment = supportFragmentManager.findFragmentById(fragmentContainer)
            if (fragment is BaseFragment<*, *>) {
                if (!fragment.onBackPressed()) {
                    super.onBackPressed()
                }
            } else if (!viewModel.onBackPressed()) {
                super.onBackPressed()
            }
        } else if (!viewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }

    companion object {
        const val BUNDLE_EXTRA = "BUNDLE_EXTRA"
    }
}
