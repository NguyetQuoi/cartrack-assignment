package com.gc.myapplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.gc.myapplication.data.pref.PreferenceStorage
import com.gc.myapplication.di.appModule
import com.gc.myapplication.di.networkModule
import com.gc.myapplication.di.repositoryModule
import com.gc.myapplication.di.viewModelModule
import com.gc.myapplication.util.ReleaseTree
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * An Application for MyAssignment
 * @author n.quoi
 * @date 05.07.2019
 */

class AssignmentApplication : Application(), KoinComponent, Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    private val preferenceStorage: PreferenceStorage by inject()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        startKoin {
            // use Koin logger
            printLogger()
            androidContext(this@AssignmentApplication)
            // declare modules
            modules(
                networkModule,
                appModule,
                repositoryModule,
                viewModelModule
            )
        }

    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            Timber.d("App is foreground")
            preferenceStorage.appIsForeground = true
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            Timber.d("App is background")
            preferenceStorage.appIsForeground = false
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}