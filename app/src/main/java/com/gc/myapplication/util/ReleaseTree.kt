package com.gc.myapplication.util

import android.util.Log

import timber.log.Timber

/**
 * Extend class of Timber.Tree
 * @author n.quoi
 * @date 10.18.2021
 */

class ReleaseTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        //        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
        //        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
        //        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);
        //
        //        if (t == null) {
        //            Crashlytics.logException(new Exception(message));
        //        } else {
        //            Crashlytics.logException(t);
        //        }
    }

    companion object {
        private val CRASHLYTICS_KEY_PRIORITY = "priority"
        private val CRASHLYTICS_KEY_TAG = "tag"
        private val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}
