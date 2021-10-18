package com.gc.myapplication.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gc.myapplication.BR

/**
 * An interface for BaseView
 * @author n.quoi
 * @date 10.18.2021
 */
interface BaseView<VDB : ViewDataBinding, VM : BaseViewModel> {
    /**
     * set viewModel
     * @param viewModel viewModel
     */
    fun setViewModel(viewModel: VM)
}

/**
 * Abstract class for Constraint Layout
 * @param VDB view-data-binding
 * @param VM view-model
 */
abstract class BaseConstraintLayout<VDB : ViewDataBinding,
        VM : BaseViewModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), BaseView<VDB, VM> {

    protected lateinit var binder: VDB

    lateinit var mViewModel: VM

    override fun setViewModel(viewModel: VM) {
        mViewModel = viewModel
        binder.setVariable(BR.viewModel, viewModel)
    }

    protected fun initView(layoutId: Int) {
        binder = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, true)
    }
}

abstract class BaseLinearLayout<VDB : ViewDataBinding,
        VM : BaseViewModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), BaseView<VDB, VM> {
    protected lateinit var binder: VDB

    lateinit var mViewModel: VM

    override fun setViewModel(viewModel: VM) {
        mViewModel = viewModel
        binder.setVariable(BR.viewModel, viewModel)
    }

    protected fun initView(layoutId: Int) {
        binder = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, true)
    }
}