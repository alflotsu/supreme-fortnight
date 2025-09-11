package io.peng.sparrowdelivery.core.common

/**
 * A generic wrapper for API responses that encapsulates success and error states
 */
sealed class ApiResult<out T> {
    /**
     * Represents a successful API response
     */
    data class Success<T>(val data: T) : ApiResult<T>()

    /**
     * Represents an API error with message and optional exception
     */
    data class Error(
        val message: String,
        val exception: Throwable? = null,
        val code: Int? = null
    ) : ApiResult<Nothing>()

    /**
     * Represents a loading state
     */
    object Loading : ApiResult<Nothing>()

    /**
     * Returns true if this result represents a successful response
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this result represents an error response
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this result represents a loading state
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the data if successful, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the data if successful, or throws the exception if error
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw (exception ?: RuntimeException(message))
        is Loading -> throw RuntimeException("Result is still loading")
    }

    /**
     * Executes the given block if the result is successful
     */
    inline fun onSuccess(action: (value: T) -> Unit): ApiResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given block if the result is an error
     */
    inline fun onError(action: (message: String, exception: Throwable?) -> Unit): ApiResult<T> {
        if (this is Error) action(message, exception)
        return this
    }

    /**
     * Transforms the success data using the provided transformer
     */
    inline fun <R> map(transform: (T) -> R): ApiResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    companion object {
        /**
         * Creates a success result
         */
        fun <T> success(data: T): ApiResult<T> = Success(data)

        /**
         * Creates an error result
         */
        fun error(message: String, exception: Throwable? = null, code: Int? = null): ApiResult<Nothing> =
            Error(message, exception, code)

        /**
         * Creates a loading result
         */
        fun loading(): ApiResult<Nothing> = Loading

        /**
         * Wraps a suspending function call in try-catch and returns ApiResult
         */
        suspend inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): ApiResult<T> {
            return try {
                Success(apiCall())
            } catch (e: Exception) {
                Error(
                    message = e.message ?: "Unknown error occurred",
                    exception = e
                )
            }
        }
    }
}
