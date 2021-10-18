package com.gc.myapplication.util.rx

import io.reactivex.Scheduler

/**
 * An interface for Scheduler provider
 * @author n.quoi
 * @date 10.18.2021
 */

interface SchedulerProvider {
    /**
     * IO scheduler
     */
    fun io(): Scheduler

    /**
     * UI Scheduler
     */
    fun ui(): Scheduler

    /**
     * Computation Scheduler
     */
    fun computation(): Scheduler
}