package com.gc.myapplication.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gc.myapplication.BR
import com.gc.myapplication.global.ResourceProvider
import io.reactivex.disposables.CompositeDisposable

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * A base class for dialogFragment
 * @author n.quoi
 * @date 05.10.2019
 *
 * @param VDB view-data-binding
 */

abstract class BaseDialogFragment<VDB : ViewDataBinding> : DialogFragment(), KoinComponent,
    DialogInterface {

    protected val disposeBag = CompositeDisposable()

    protected lateinit var binder: VDB
    var positiveClick = DialogInterface.OnClickListener { _, _ -> dismiss() }
    var okButtonClick = View.OnClickListener {
        positiveClick.onClick(this, DialogInterface.BUTTON_POSITIVE)
        dismiss()
    }

    var negativeClick = DialogInterface.OnClickListener { _, _ -> dismiss() }
    var cancelButtonClick = View.OnClickListener {
        negativeClick.onClick(this, DialogInterface.BUTTON_NEGATIVE)
        dismiss()
    }

    val resourceProvider: ResourceProvider by inject()

    val autoDismiss: Boolean = false

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected val variableId: Int
        @IdRes
        get() = BR.dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle()
    }

    protected fun setStyle() {
        setStyle(STYLE_NORMAL, com.gc.myapplication.R.style.AppTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binder.setVariable(variableId, this)
        return binder.root
    }

    /**
     * Show dialog
     * @param manager [FragmentManager]
     * @param tag tag name of dialog
     * @param positiveClick a listener for positive click action
     * @param negativeClick a listener for negative click action
     */
    open fun show(
        manager: FragmentManager, tag: String, positiveClick: DialogInterface.OnClickListener?,
        negativeClick: DialogInterface.OnClickListener?
    ) {
        if (positiveClick != null) this.positiveClick = positiveClick
        if (negativeClick != null) this.negativeClick = negativeClick
        super.show(manager, tag)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun cancel() {
        disposeBag.dispose()
        dialog?.cancel()
    }
}
