package com.obregon.countryflags.ui.saved_image

import androidx.lifecycle.*
import com.obregon.countryflags.domain.usecase.FetchCountryUseCase
import com.obregon.countryflags.domain.usecase.FlagData
import dagger.hilt.android.lifecycle.HiltViewModel
import com.obregon.countryflags.domain.usecase.FetchSavedFlagUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SavedFlagViewModel @Inject constructor(
    private val fetchSavedFlagUseCase: FetchSavedFlagUseCase,
    private val fetchCountryUseCase: FetchCountryUseCase
) : ViewModel() {

    private var _savedFlagDataList = MutableLiveData<List<FlagData>>()
    val savedFlagList: LiveData<List<FlagData>> = _savedFlagDataList

    private var _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var _savedFlagCount = MutableLiveData<Int>()
    var savedFlagCount: LiveData<Int> = _savedFlagCount

    companion object {
        const val ERR_GENERIC_ERROR = "An error occurred retrieving flag data"
    }

    init {
        listenForChanges()
    }

    fun fetchFlagData(searchTerm: String) {
        viewModelScope.launch {
            try {
                val data = fetchSavedFlagUseCase.fetchSavedFlagData()
                if (searchTerm.length > 2) {
                    _savedFlagDataList.value =
                        data.filter { it.countryName.equals(searchTerm, true) }
                } else {
                    _savedFlagDataList.value =
                        data.filter { it.countryCode.equals(searchTerm, true) }
                }
            } catch (e: Exception) {
                _error.value = ERR_GENERIC_ERROR
            }
        }
    }

    fun fetchAllFlagData() {
        viewModelScope.launch {
            try {
                _savedFlagDataList.value = fetchSavedFlagUseCase.fetchSavedFlagData()
            } catch (e: Exception) {
                _error.value = ERR_GENERIC_ERROR
            }
        }
    }


    private fun listenForChanges() {
        viewModelScope.launch {
            savedFlagCount = fetchSavedFlagUseCase.fetchSavedFlagCount().asLiveData()
        }
    }
}
