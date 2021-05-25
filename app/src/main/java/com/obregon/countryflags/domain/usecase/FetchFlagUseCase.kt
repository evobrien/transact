package com.obregon.countryflags.domain.usecase

import com.obregon.countryflags.data.AppError
import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.repo.CountryFlagsRepo
import com.obregon.countryflags.data.repo.CountryRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class FetchFlagUseCase @Inject constructor(
    private val countryRepo: CountryRepo,
    private val countryFlagsRepo: CountryFlagsRepo
) {
    suspend fun fetchFlag(countryCode: String, style: String, callback: (QueryResult) -> Unit) {
        val country = countryRepo.fetchCountry(countryCode)
        if (country == null) {
            callback(QueryResult.Failure(AppError("Country does not exist")))
        } else {
            countryFlagsRepo.fetchFlag(countryCode, style) {
                when (it) {
                    is QueryResult.Success -> callback(
                        QueryResult.Success(
                            FlagData(it.successPayload as String, countryCode, country.countryName)
                        )
                    )
                    else -> callback(it)
                }
            }
        }
    }
}