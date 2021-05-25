package com.obregon.countryflags.domain.usecase

import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.db.Country
import com.obregon.countryflags.data.repo.CountryFlagsRepo
import com.obregon.countryflags.data.repo.CountryRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchSavedFlagUseCase @Inject constructor(
    private val countryFlagsRepo: CountryFlagsRepo,
    private val countryRepo: CountryRepo
) {

    suspend fun fetchSavedFlagData(): List<FlagData> {

        val flagDataList = mutableListOf<FlagData>()
        val mapCountry = getCountryMap()
        val savedFlags = countryFlagsRepo.fetchSavedFlags()

        for (flag in savedFlags) {
            val countryName = mapCountry[flag.countryCode.toUpperCase()]
            countryName?.let {
                flagDataList.add(
                    FlagData(
                        flag.imagePath,
                        flag.countryCode,
                        it
                    )
                )
            }
        }
        return flagDataList
    }

    private suspend fun getCountryMap(): Map<String, String> {
        val countryResult = countryRepo.fetchCountries()
        var countryMap = mapOf<String, String>()

        when (countryResult) {
            is QueryResult.Success -> countryResult.successPayload.also { countries ->
                countryMap = (countries as List<Country>).map {
                    it.countryCode to it.countryName
                }.toMap()
            }
            else -> countryMap
        }
        return countryMap
    }

    fun fetchSavedFlagCount(): Flow<Int> {
        return countryFlagsRepo.fetchSavedFlagCount()
    }


}