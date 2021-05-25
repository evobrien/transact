package com.obregon.countryflags.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.obregon.countryflags.data.AppError
import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.db.Country
import com.obregon.countryflags.data.repo.CountryFlagsRepo
import com.obregon.countryflags.data.repo.CountryRepo
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class FetchFlagUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var countryRepo: CountryRepo

    @MockK
    lateinit var countryFlagsRepo: CountryFlagsRepo

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun ` fetchFlag is successful with valid data`() = runBlocking {
        val countryCode = "ie"
        val countryName = "Ireland"
        val countryResponse = Country(countryCode, countryName)

        val style = "flat"
        val flagPath = "/this/is/a/flag/path"
        val querySuccess = QueryResult.Success(flagPath)
        val captureCallback = slot<(callback: QueryResult) -> Unit>()

        coEvery { countryRepo.fetchCountry(countryCode) }.returns(countryResponse)
        coEvery { countryFlagsRepo.fetchFlag(countryCode, style, capture(captureCallback)) }
            .answers {
                captureCallback.captured.invoke(querySuccess)
            }

        val expected = FlagData(flagPath, countryCode, countryName)
        val fetchFlagsUseCase = FetchFlagUseCase(countryRepo, countryFlagsRepo)
        fetchFlagsUseCase.fetchFlag(countryCode, style) {
            when (it) {
                is QueryResult.Success -> assertTrue(it.successPayload == expected)
                else -> fail()
            }
        }

        coVerify(exactly = 1) { countryRepo.fetchCountry(countryCode) }
        val capturedData = slot<(callback: QueryResult) -> Unit>()
        verify(exactly = 1) {
            countryFlagsRepo.fetchFlag(
                countryCode,
                style,
                capture(capturedData)
            )
        }
    }

    @Test
    fun ` fetchFlag calls back with failure when countryFlagsRepo fails`() = runBlocking {
        val countryCode = "ie"
        val countryName = "Ireland"
        val countryResponse = Country(countryCode, countryName)

        val style = "flat"
        val error = AppError(CountryFlagsRepo.ERR_FAILED_TO_RETRIEVE_COUNTRY, null, null)
        val queryFailure = QueryResult.Failure(
            AppError(
                CountryFlagsRepo.ERR_FAILED_TO_RETRIEVE_COUNTRY,
                null,
                null
            )
        )
        val captureCallback = slot<(callback: QueryResult) -> Unit>()

        coEvery { countryRepo.fetchCountry(countryCode) }.returns(countryResponse)
        coEvery { countryFlagsRepo.fetchFlag(countryCode, style, capture(captureCallback)) }
            .answers {
                captureCallback.captured.invoke(queryFailure)
            }

        val fetchFlagsUseCase = FetchFlagUseCase(countryRepo, countryFlagsRepo)
        fetchFlagsUseCase.fetchFlag(countryCode, style) {
            when (it) {
                is QueryResult.Failure -> assertTrue(it.error == error)
                else -> fail()
            }
        }

        coVerify(exactly = 1) { countryRepo.fetchCountry(countryCode) }
        val capturedData = slot<(callback: QueryResult) -> Unit>()
        verify(exactly = 1) {
            countryFlagsRepo.fetchFlag(
                countryCode,
                style,
                capture(capturedData)
            )
        }
    }

    @Test
    fun ` fetchFlagfails early when country is not found`() = runBlocking {
        val countryCode = "NN"
        val style = "flat"

        coEvery { countryRepo.fetchCountry(countryCode) }.returns(null)

        val expected = AppError("Country does not exist")
        val fetchFlagsUseCase = FetchFlagUseCase(countryRepo, countryFlagsRepo)
        fetchFlagsUseCase.fetchFlag(countryCode, style) {
            when (it) {
                is QueryResult.Failure -> assertTrue(it.error == expected)
                else -> fail()
            }
        }

        coVerify(exactly = 1) { countryRepo.fetchCountry(countryCode) }
        verify(exactly = 0) { countryFlagsRepo.fetchFlag(any(), any(), any()) }
    }
}