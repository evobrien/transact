package com.obregon.countryflags.ui.image_search

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.db.Country
import com.obregon.countryflags.domain.usecase.FetchCountryUseCase
import com.obregon.countryflags.domain.usecase.FlagData
import dagger.hilt.android.lifecycle.HiltViewModel
import com.obregon.countryflags.domain.usecase.SaveFlagUseCase
import com.obregon.countryflags.domain.usecase.FetchFlagUseCase
import com.obregon.countryflags.ui.common.SaveImage
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FindFlagViewModel @Inject constructor(
    private val fetchFlagUseCase: FetchFlagUseCase,
    private val fetchCountryUseCase: FetchCountryUseCase,
    private val saveFlagUseCase: SaveFlagUseCase
) : ViewModel(), SaveImage {

    private val _flag = MutableLiveData<List<FlagData>>()
    val flag: LiveData<List<FlagData>> = _flag

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _countryList = MutableLiveData<List<Country>>()
    val countryList: LiveData<List<Country>> = _countryList

    init {
        fetchCountries()
    }

    companion object {
        const val ERR_COUNTRY_CODE_NOT_EXIST =
            "Country code does not exist. Please enter a valid 2 character country code"
        const val ERR_FAILED_TO_SAVE_FLAG = "Failed to save image"
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            when (val result = fetchCountryUseCase.fetchCountries()) {
                is QueryResult.Success -> _countryList.value =
                    result.successPayload as List<Country>
                is QueryResult.Failure -> processError(result.error)
            }
        }
    }

    fun fetchFlag(countryCode: String, style: String) {
        if (!isValidCountry(countryCode)) {
            _error.value = ERR_COUNTRY_CODE_NOT_EXIST
        } else {
            viewModelScope.launch {
                fetchFlagUseCase.fetchFlag(countryCode.toUpperCase(), style) {
                    when (it) {
                        is QueryResult.Success -> processResult(it.successPayload as FlagData)
                        else -> processError(it, "Could not retrieve flag for $countryCode")
                    }
                }
            }
        }
    }

    @VisibleForTesting
    fun isValidCountry(countryCode: String): Boolean {
        val countries = countryList.value?.filter {
            it.countryCode.equals(countryCode, true)
        }

        return countries != null && countries.isNotEmpty()

    }

    override fun saveImage() {
        viewModelScope.launch {
            try {
                flag.value?.let {
                    saveFlagUseCase.saveFlag(it[0])
                }
            } catch (e: Exception) {
                _error.value = ERR_FAILED_TO_SAVE_FLAG
            }
        }
    }

    private fun processResult(flagData: FlagData) {
        _flag.value = mutableListOf(flagData)
    }

    private fun processError(error: Any, message: String = "An error occurred") {
        error.let {
            _error.value = message
        }
    }


}