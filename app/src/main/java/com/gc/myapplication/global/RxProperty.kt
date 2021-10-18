package com.gc.myapplication.global

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import io.reactivex.subjects.BehaviorSubject

/**
 * RxProperty class support for Observable
 * @author n.quoi
 * @date 10.18.2021
 */

class RxProperty<T>(value: T? = null) : ObservableField<T>(value) {
    private val subject = BehaviorSubject.create<Optional<T>>()

    init {
        subject.onNext(Optional(value))
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                val currentValue = get()
                subject.onNext(Optional(currentValue))
            }
        })
    }

    /**
     * asObservable
     * @return [io.reactivex.Observable<T>]
     */
    fun asObservable(): io.reactivex.Observable<T> = subject.filter { it.value != null }.map { it.value }

    /**
     * Optional class
     */
    class Optional<T>(val value: T? = null)
}
