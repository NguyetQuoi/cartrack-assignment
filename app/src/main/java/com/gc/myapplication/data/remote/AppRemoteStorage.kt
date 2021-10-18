package com.gc.myapplication.data.remote

import com.gc.myapplication.data.DataSource
import com.gc.myapplication.data.model.User
import io.reactivex.Observable
import retrofit2.Retrofit

/**
 * An app storage remote
 * @author n.quoi
 * @date 10.18.2021
 */

class AppRemoteStorage(retrofit: Retrofit) : DataSource {
    private val apiService: ApiService = retrofit.create(ApiService::class.java)
    override fun getUsers(): Observable<List<User>> {
        return apiService.getUser().map { response -> response.data }
    }
}
