package com.gc.myapplication.data.remote.response

/**
 * Base class for the response
 * @author n.quoi
 * @date 10.18.2021
 */

data class BaseResponse<T>(
    val data: T,
    var message: String? = null,
    private val responseCode: Int = 0,
    val isErrorResponse: Boolean = responseCode != 200
)