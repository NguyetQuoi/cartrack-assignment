package com.gc.myapplication.global

import androidx.lifecycle.Observer

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * @author quoi.tran@com.zyrous.com
 * @date 05.10.2019
 */

class Event<T>(private val content: T) {
    var isHasBeenHandled = false
        private set

    /**
     * Returns the content and prevents its use again.
     */
    val contentIfNotHandled: T?
        get() {
            return if (isHasBeenHandled) {
                null
            } else {
                isHasBeenHandled = true
                content
            }
        }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T {
        return content
    }

    /**
     * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
     * already been handled.
     *
     * @param listener onEventUnhandledContent is *only* called if the [Event]'s contents has not been handled.
     */
    class EventObserver<T>(private val listener: OnEventUnhandledContent<T>) : Observer<Event<T>> {

        override fun onChanged(tEvent: Event<T>?) {
            if (tEvent == null) return
            val content = tEvent.contentIfNotHandled
            if (content != null) {
                listener.onEventUnhandled(content)
            }
        }
    }

    /**
     * Interface for [OnEventUnhandledContent]
     * @param T
     */
    interface OnEventUnhandledContent<T> {
        /**
         * on event unhandled
         * @param value event T
         */
        fun onEventUnhandled(value: T)
    }
}
