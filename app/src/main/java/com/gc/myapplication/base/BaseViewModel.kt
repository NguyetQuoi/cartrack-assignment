package com.gc.myapplication.base

import androidx.lifecycle.ViewModel
import com.gc.myapplication.extention.plusAssign
import com.gc.myapplication.global.RxProperty
import io.reactivex.disposables.CompositeDisposable

/**
 * An open class for general of view-model
 * @author n.quoi
 * @date 10.18.2021
 */
open class BaseViewModel : ViewModel(), Destroyable {

    protected val disposeBag = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }

    override fun onDestroy() {
        disposeBag.dispose()
    }
}

open class BaseItemViewModel(selected: Boolean = false) : BaseViewModel() {
    var isSelected = RxProperty(selected)
    var onSelectedChanged: ((Boolean) -> Unit)? = null

    init {
        isSelected.asObservable().subscribe {
            onSelectedChanged?.invoke(it)
        } += disposeBag
    }
}