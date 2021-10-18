package com.gc.myapplication.global

import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

/**
 * A class extend CallAdapter.Factory
 * @author n.quoi
 * @date 05.12.2019
 */

class RxErrorHandlingCallAdapterFactory(subscribeScheduler: Scheduler) : CallAdapter.Factory() {
    private val origin = RxJava2CallAdapterFactory.createWithScheduler(subscribeScheduler)

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<Any, Observable<Any>>? {
        val wrapped: CallAdapter<Any, Observable<Any>>? =
            origin.get(returnType, annotations, retrofit) as? CallAdapter<Any, Observable<Any>>
        return wrapped?.let { RxCallAdapterWrapper(it, retrofit) }
    }

    private inner class RxCallAdapterWrapper<R>(
        private val wrappedCallAdapter: CallAdapter<R, Observable<R>>,
        private val retrofit: Retrofit
    ) : CallAdapter<R, Observable<R>> {

        override fun responseType(): Type {
            return wrappedCallAdapter.responseType()
        }

        override fun adapt(call: Call<R>): Observable<R> {
            val adapted = wrappedCallAdapter.adapt(call)
            return adapted.onErrorResumeNext(Function { throwable ->
                Observable.error<R>(
                    asRetrofitException(throwable)
                )
            })
        }

        private fun asRetrofitException(throwable: Throwable): RetrofitException {
            // We had non-200 http error
            if (throwable is HttpException) {
                val response = throwable.response()
                return RetrofitException.httpError(
                    response?.raw()?.request()?.url().toString(),
                    response,
                    retrofit
                )
            }

            // A network error happened
            if (throwable is IOException) {
                return RetrofitException.networkError(throwable)
            }

            return if (throwable is JsonSyntaxException) {
                RetrofitException.jsonSyntaxError(throwable)
            } else RetrofitException.unexpectedError(throwable)

            // We don't know what happened. We need to simply convert to an unknown error
        }

    }

    companion object {
        /**
         * Create factory
         * Extend [CallAdapter.Factory]
         */
        fun create(subscribeScheduler: Scheduler): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory(subscribeScheduler)
        }
    }
}
