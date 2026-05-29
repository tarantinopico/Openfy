package com.example.domain.model

sealed class Result<out T, out E : AppError> {
    data class Success<out T>(val data: T) : Result<T, Nothing>()
    data class Error<out E : AppError>(val error: E) : Result<Nothing, E>()
}

sealed interface AppError {
    sealed interface NetworkError : AppError {
        object NoConnection : NetworkError
        object Timeout : NetworkError
        object ServerError : NetworkError
        data class HttpError(val code: Int, val message: String) : NetworkError
        object SerializationError : NetworkError
        object Unknown : NetworkError
    }
    
    sealed interface DatabaseError : AppError {
        object DiskFull : DatabaseError
        object Unknown : DatabaseError
    }
}
