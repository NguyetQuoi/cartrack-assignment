package com.gc.myapplication.data.remote

import com.gc.myapplication.data.model.User
import com.gc.myapplication.data.remote.response.BaseResponse
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Interface for api service
 * @author n.quoi
 * @date 10.18.2021
 */

interface ApiService {

    /**
     * Get list of user
     * @return [Observable<List<User>>]
     */
    @GET("/users")
    fun getUser(): Observable<BaseResponse<List<User>>>
}
