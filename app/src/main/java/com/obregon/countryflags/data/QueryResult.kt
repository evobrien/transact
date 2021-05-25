package com.obregon.countryflags.data

import java.lang.Exception


sealed class QueryResult {
    data class Success(val successPayload: Any) : QueryResult()
    data class Failure(val error: AppError) : QueryResult()
}

data class AppError(val message: String, val e: Exception? = null, val response: String? = null)