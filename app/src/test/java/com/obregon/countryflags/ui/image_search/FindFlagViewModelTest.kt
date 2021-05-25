package com.obregon.countryflags.ui.image_search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.obregon.countryflags.MainCoroutineRule
import com.obregon.countryflags.data.AppError
import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.db.Country
import com.obregon.countryflags.domain.usecase.FetchCountryUseCase
import com.obregon.countryflags.domain.usecase.FetchFlagUseCase
import com.obregon.countryflags.domain.usecase.FlagData
import com.obregon.countryflags.domain.usecase.SaveFlagUseCase
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.Exception

@RunWith(JUnit4::class)
class FindFlagViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var fetchFlagUseCase: FetchFlagUseCase

    @MockK
    private lateinit var fetchCountryUseCase: FetchCountryUseCase

    @MockK
    private lateinit var saveFlagUseCase: SaveFlagUseCase

    companion object {
        private const val STYLE = "flat"
        const val countryCode = "DE"
        const val countryName = "Germany"
        const val path = "/this/is/an/image/path"
        const val invalidCountry = "NN"
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `verify FetchFlag fails with invalid country code`() = runBlocking {
        val countryCode = "NN"
        val countries = mutableListOf(Country("DE", "Germany"))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }
        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        findFlagViewModel.fetchFlag(countryCode, STYLE)
        assertEquals(findFlagViewModel.error.value, FindFlagViewModel.ERR_COUNTRY_CODE_NOT_EXIST)
    }

    @Test
    fun `verify FetchFlag succeeds with valid country code`() = runBlocking {

        val countries = mutableListOf(Country(countryCode, countryName))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }

        val captureCallback = slot<(callback: QueryResult) -> Unit>()
        val flagDataResult = FlagData(path, countryCode, countryName)
        val querySuccess = QueryResult.Success(flagDataResult)
        coEvery {
            fetchFlagUseCase.fetchFlag(
                countryCode.toUpperCase(),
                STYLE,
                capture(captureCallback)
            )
        }.answers {
            captureCallback.captured.invoke(querySuccess)
        }

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        findFlagViewModel.fetchFlag(countryCode, STYLE)
        assertEquals(findFlagViewModel.flag.value?.get(0), flagDataResult)
    }

    @Test
    fun `verify FetchFlag fails with use case error`() = runBlocking {

        val countries = mutableListOf(Country(countryCode, countryName))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }

        val captureCallback = slot<(callback: QueryResult) -> Unit>()
        val queryFailure = QueryResult.Failure(AppError("error", null, null))
        coEvery {
            fetchFlagUseCase.fetchFlag(
                countryCode.toUpperCase(),
                STYLE,
                capture(captureCallback)
            )
        }.answers {
            captureCallback.captured.invoke(queryFailure)
        }

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        findFlagViewModel.fetchFlag(countryCode, STYLE)
        val errorString = "Could not retrieve flag for $countryCode"
        assertEquals(findFlagViewModel.error.value, errorString)
    }

    @Test
    fun `verify IsValidCountry succeeds with valid country`() = runBlocking {

        val countries = mutableListOf(Country(countryCode, countryName))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        assertTrue(findFlagViewModel.isValidCountry(countryCode))
    }

    @Test
    fun `verify IsValidCountry succeeds with invalid country`() = runBlocking {

        val countries = mutableListOf(Country(countryCode, countryName))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        assertFalse(findFlagViewModel.isValidCountry(invalidCountry))
    }

    @Test
    fun `verify IsValidCountry succeeds with empty country list`() = runBlocking {

        coEvery { fetchCountryUseCase.fetchCountries() }.answers {
            QueryResult.Failure(AppError("error", null, null))
        }

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        assertTrue(findFlagViewModel.countryList.value == null)
        assertFalse(findFlagViewModel.isValidCountry(countryCode))
    }

    @Test
    fun `verify saveImage runs without error in happy path`() = runBlocking {

        val countries = mutableListOf(Country(countryCode, countryName))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }

        val captureCallback = slot<(callback: QueryResult) -> Unit>()
        val flagDataResult = FlagData(path, countryCode, countryName)
        val querySuccess = QueryResult.Success(flagDataResult)
        coEvery {
            fetchFlagUseCase.fetchFlag(
                countryCode.toUpperCase(),
                STYLE, capture(captureCallback)
            )
        }.answers {
            captureCallback.captured.invoke(querySuccess)
        }

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        findFlagViewModel.fetchFlag(countryCode, STYLE)
        assertEquals(findFlagViewModel.flag.value?.get(0), flagDataResult)
        findFlagViewModel.saveImage()
        coVerify(exactly = 1) { saveFlagUseCase.saveFlag(flagDataResult) }
        assertNull(findFlagViewModel.error.value)
    }

    @Test
    fun `verify saveImage raise error on exception`() = runBlocking {
        val countries = mutableListOf(Country(countryCode, countryName))
        coEvery { fetchCountryUseCase.fetchCountries() }.answers { QueryResult.Success(countries) }

        val captureCallback = slot<(callback: QueryResult) -> Unit>()
        val flagDataResult = FlagData(path, countryCode, countryName)
        val querySuccess = QueryResult.Success(flagDataResult)
        coEvery {
            fetchFlagUseCase.fetchFlag(
                countryCode.toUpperCase(),
                STYLE, capture(captureCallback)
            )
        }.answers {
            captureCallback.captured.invoke(querySuccess)
        }

        coEvery { saveFlagUseCase.saveFlag(flagDataResult) }.throws(Exception())

        val findFlagViewModel =
            FindFlagViewModel(fetchFlagUseCase, fetchCountryUseCase, saveFlagUseCase)
        findFlagViewModel.fetchFlag(countryCode, STYLE)
        assertEquals(findFlagViewModel.flag.value?.get(0), flagDataResult)
        findFlagViewModel.saveImage()
        coVerify(exactly = 1) { saveFlagUseCase.saveFlag(flagDataResult) }
        assertEquals(FindFlagViewModel.ERR_FAILED_TO_SAVE_FLAG, findFlagViewModel.error.value)
    }
}