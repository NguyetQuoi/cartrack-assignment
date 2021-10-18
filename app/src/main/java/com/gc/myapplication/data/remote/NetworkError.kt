package com.gc.myapplication.data.remote

import com.gc.myapplication.data.remote.response.BaseResponse
import com.google.gson.annotations.SerializedName

/**
 * A data class for network error
 * @author n.quoi
 * @date 10.18.2021
 */

data class NetworkError(
    var success: Boolean = false,
    var name: String? = null,
    var created: String? = null,
    @SerializedName("errorMessage")
    var errorMessage: String? = null
) {
    constructor(response: BaseResponse<*>) : this() {
        errorMessage = response.message
    }
}
