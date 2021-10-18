package com.gc.myapplication.util.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * A class provide thread for rxJava
 * @author n.quoi
 * @date 10.19.2021
 */

class AppSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.io()

    override fun ui() = AndroidSchedulers.mainThread()

    override fun computation() = Schedulers.computation()
}