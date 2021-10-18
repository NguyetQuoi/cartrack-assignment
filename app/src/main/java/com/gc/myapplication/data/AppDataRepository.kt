package com.gc.myapplication.data

import android.content.Context
import com.gc.myapplication.data.model.User
import com.gc.myapplication.data.pref.PreferenceStorage
import io.reactivex.Observable

/**
 * AppDataRepository all repositories of app
 * @author n.quoi
 * @date 10.18.2021
 */

open class AppDataRepository(
    private val context: Context,
    private val remoteDataSource: DataSource,
    private val offlineDataSource: DataSource,
    private val preferenceStorage: PreferenceStorage
) : DataSource {

    private var users: List<User> = emptyList()
    override fun getUsers(): Observable<List<User>> {
        return if (users.isNullOrEmpty()) {
            remoteDataSource.getUsers()
                .doOnNext { users = it }
        } else Observable.just(users)
    }
}
