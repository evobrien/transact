package com.obregon.countryflags.domain.usecase

import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.repo.CountryRepo
import javax.inject.Inject

class FetchCountryUseCase @Inject constructor(private val countryRepo: CountryRepo) {

    suspend fun fetchCountries(): QueryResult {
        return countryRepo.fetchCountries()
    }
}