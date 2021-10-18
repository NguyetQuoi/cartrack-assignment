package com.gc.myapplication.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gc.myapplication.BR
import com.gc.myapplication.extention.hideKeyboard
import com.gc.myapplication.global.NavigationItem
import io.reactivex.disposables.CompositeDisposable

/**
 * An abstract class for base of fragment
 * @author n.quoi
 * @date 05.08.2019
 */

abstract class BaseFragment<VM : BindingViewModel, VDB : ViewDataBinding> : Fragment(), NavigationFragment {

    protected abstract val viewModel: VM
    protected lateinit var binder: VDB

    protected var disposeBag = CompositeDisposable()

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected val bindingVariable: Int
        get() = BR.viewModel

    protected var baseActivity: BaseActivity<*, *>? = null

    var fragmentCallback: FragmentCallback? = null

    protected val fragmentContainer: Int
        @IdRes
        get() = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            baseActivity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.setVariable(bindingVariable, viewModel)
        binder.executePendingBindings()
        viewModel.navigationEvent.observe(this, Observer { event ->
            event?.let { onNavigationEvent(event.contentIfNotHandled) }
        })
        fragmentCallback?.onObtainedViewModel()
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
     * Handle navigate event from viewModel, modified this function if any custom navigate
     * such as add bundle, flag, animation, ...
     *
     * @param item type of navigate (with data from viewModel)
     */
    protected fun onNavigationEvent(item: NavigationItem?) {
        baseActivity?.let { item?.navigate(it) }
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }

    /**
     * Hide soft keyboard
     */
    fun hideKeyboard() {
        baseActivity?.hideKeyboard()
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }

    override fun onUserInteraction() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!viewModel.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * An interface for callback of fragment
     */
    interface FragmentCallback {
        /**
         * Trigger for on-obtained-a-viewModel
         */
        fun onObtainedViewModel()
    }
}