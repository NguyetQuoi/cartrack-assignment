package com.gc.myapplication.global

import com.gc.myapplication.data.remote.NetworkError
import com.gc.myapplication.data.remote.response.BaseResponse
import com.gc.myapplication.global.RetrofitException.Kind.*
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * A class handle for retrofit exception
 * @author n.quoi
 * @date 10.18.2021
 */

class RetrofitException(
    message: String?,
    var response: Response<*>?,
    var kind: Kind?,
    exception: Throwable?,
    var retrofit: Retrofit?
) : RuntimeException(message, exception) {
    var errorData: NetworkError? = null

    private fun deserializeServerError() {
        errorData = getErrorBodyAs(NetworkError::class.java)
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @throws Exception if unable to convert the body to the specified `type`.
     */
    @Throws(Exception::class)
    fun <T> getErrorBodyAs(type: Class<T>): T? {
        response?.errorBody()?.let { body ->
            retrofit?.let { retrofit ->
                val converter = retrofit.responseBodyConverter<T>(type, arrayOfNulls(0))
                return converter.convert(body)
            }
        }

        return null
    }

    /**
     * Enum stand for all Kinds of exception
     * [NETWORK] An [IOException] occurred while communicating to the server.
     * [HTTP] A non-200 HTTP status code was received from the server.
     * [JSON_SYNTAX] An [JsonSyntaxException] occurred when parse json to response
     * [UNEXPECTED]   An internal error occurred while attempting to execute a request. It is best practice to re-throw this exception so your application crashes.
     */
    enum class Kind {
        NETWORK,
        HTTP,
        HTTP_403,
        JSON_SYNTAX,
        UNEXPECTED
    }

    /**
     * Map action for handle of errors
     */
    companion object {
        /**
         * Handler for http error
         * @param url the request url
         * @param response response from url
         * @param retrofit
         * @return [RetrofitException] check type: [HTTP_403] - response code 403, [HTTP] - response code 400
         */
        fun httpError(url: String, response: Response<*>?, retrofit: Retrofit): RetrofitException {
            val message = response.code().toString() + " " + response.message()
            val error: RetrofitException
            if (response.code() == 403) {
                error = RetrofitException(message, response, HTTP_403, null, retrofit)
            } else {
                if (response.code() == 400 && url.contains("/device")) {
                    error = RetrofitException(message, response, HTTP, null, retrofit)
                } else {
                    error = RetrofitException(message, response, HTTP, null, retrofit)
                    error.deserializeServerError()
                }
            }
            return error
        }

        /**
         * Handler for http error
         * @param response [BaseResponse<Any>]
         * @return [RetrofitException] with errorMessage and type [HTTP]
         */
        fun httpError(response: BaseResponse<*>): RetrofitException {
            val error = RetrofitException(response.message, null, HTTP, null, null)
            error.errorData = NetworkError(response)
            return error
        }

        /**
         * Handler for network error
         * @param exception [IOException]
         * @return [RetrofitException] with sms and type [NETWORK]
         */
        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(exception.message, null, NETWORK, exception, null)
        }

        /**
         * Handler for json syntax error
         * @param exception JsonSyntaxException
         * @return RetrofitException with message, type [JSON_SYNTAX]
         */
        fun jsonSyntaxError(exception: JsonSyntaxException): RetrofitException {
            return RetrofitException(exception.message, null, JSON_SYNTAX, exception, null)
        }

        /**
         * Handle for unexpected error
         * @param exception [Throwable]
         * @return [RetrofitException] with message, type [UNEXPECTED]
         */
        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(exception.message, null, UNEXPECTED, exception, null)
        }
    }
}
