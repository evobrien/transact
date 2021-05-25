package com.obregon.countryflags.domain.usecase

import com.obregon.countryflags.data.repo.CountryFlagsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class SaveFlagUseCase @Inject constructor(private val countryFlagsRepo: CountryFlagsRepo) {
    suspend fun saveFlag(flagData: FlagData) {
        countryFlagsRepo.saveFlagData(flagData)
    }
}