package com.example.stockmarketapp.util

// 구조가 기존의 것들과 비교 했을 때 특이하다
sealed class Result<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Result<T>(data)
    class Error<T>(message: String, data: T? = null) : Result<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true) : Result<T>(null)
}
