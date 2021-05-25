package com.obregon.countryflags.data.repo

import com.obregon.countryflags.data.AppError
import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.db.Country
import com.obregon.countryflags.data.db.dao.CountryDao
import timber.log.Timber
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject

class CountryRepo @Inject constructor(private val countryDao: CountryDao) {

    suspend fun fetchCountries(): QueryResult {
        return try {
            QueryResult.Success(countryDao.getAll())
        } catch (e: Exception) {
            Timber.e(e)
            QueryResult.Failure(AppError("Failed to retrieve country from db", e, null))
        }
    }

    suspend fun fetchCountry(countryCode: String): Country? {
        return try {
            val country = countryDao.getAll().filter {
                it.countryCode.equals(countryCode, true)
            }
            country[0]
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
            null
        }
    }
}