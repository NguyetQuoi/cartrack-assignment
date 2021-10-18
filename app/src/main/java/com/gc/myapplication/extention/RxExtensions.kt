package com.gc.myapplication.extention

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * An extension for rx
 * @author quoi.tran@com.zyrous.com
 * @date 05.13.2019
 */

fun <T> Single<T>.with(): Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

/**
 * Add assign
 * @param disposeBag [CompositeDisposable] which will be added
 */
operator fun Disposable.plusAssign(disposeBag: CompositeDisposable) {
    disposeBag.add(this)
}
