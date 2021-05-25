package com.obregon.countryflags.ui.saved_image

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.obregon.countryflags.MainCoroutineRule
import com.obregon.countryflags.domain.usecase.FetchCountryUseCase
import com.obregon.countryflags.domain.usecase.FetchSavedFlagUseCase
import com.obregon.countryflags.domain.usecase.FlagData
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.Exception

@RunWith(JUnit4::class)
class SavedFlagViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var fetchSavedFlagUseCase: FetchSavedFlagUseCase

    @MockK
    lateinit var fetchCountryUseCase: FetchCountryUseCase

    companion object {
        private const val STYLE = "flat"
        const val COUNTRY_CODE = "DE"
        const val COUNTRY_NAME = "Germany"
        const val IMAGE_PATH = "/this/is/an/image/path"
        const val INVALID_COUNTRY_CODE = "NN"
    }

    private val flagDataList = listOf(
        FlagData(IMAGE_PATH, COUNTRY_CODE, COUNTRY_NAME),
        FlagData("/another/path", "IE", "Ireland")
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `verify fetchFlagData with valid country code`() = runBlockingTest {

        coEvery { fetchSavedFlagUseCase.fetchSavedFlagCount() }.answers {
            flow {
                emit(flagDataList.size)
            }
        }
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagData() }.answers { flagDataList }

        val savedImageViewModel = SavedFlagViewModel(fetchSavedFlagUseCase, fetchCountryUseCase)
        savedImageViewModel.fetchFlagData(COUNTRY_CODE)

        coVerify(exactly = 1) { fetchSavedFlagUseCase.fetchSavedFlagCount() }

        coVerify(exactly = 1) { fetchSavedFlagUseCase.fetchSavedFlagData() }
        val expected = listOf(FlagData(IMAGE_PATH, COUNTRY_CODE, COUNTRY_NAME))
        assertEquals(expected, savedImageViewModel.savedFlagList.value)

    }

    @Test
    fun `verify fetchFlagData with valid country name`() = runBlockingTest {

        coEvery { fetchSavedFlagUseCase.fetchSavedFlagCount() }.answers {
            flow {
                emit(flagDataList.size)
            }
        }
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagData() }.answers { flagDataList }

        val savedImageViewModel = SavedFlagViewModel(fetchSavedFlagUseCase, fetchCountryUseCase)
        savedImageViewModel.fetchFlagData(COUNTRY_NAME)

        coVerify(exactly = 1) { fetchSavedFlagUseCase.fetchSavedFlagCount() }
        coVerify(exactly = 1) { fetchSavedFlagUseCase.fetchSavedFlagData() }
        val expected = listOf(FlagData(IMAGE_PATH, COUNTRY_CODE, COUNTRY_NAME))
        assertEquals(expected, savedImageViewModel.savedFlagList.value)

    }

    @Test
    fun `verify fetchFlagData exception handled`() = runBlockingTest {

        coEvery { fetchSavedFlagUseCase.fetchSavedFlagCount() }.answers {
            flow {
                emit(flagDataList.size)
            }
        }
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagData() }.throws(Exception())

        val savedImageViewModel = SavedFlagViewModel(fetchSavedFlagUseCase, fetchCountryUseCase)
        savedImageViewModel.fetchFlagData(INVALID_COUNTRY_CODE)

        coVerify(exactly = 1) { fetchSavedFlagUseCase.fetchSavedFlagCount() }
        coVerify(exactly = 1) { fetchSavedFlagUseCase.fetchSavedFlagData() }

        assertEquals(null, savedImageViewModel.savedFlagList.value)
        assertEquals(SavedFlagViewModel.ERR_GENERIC_ERROR, savedImageViewModel.error.value)
    }

    @Test
    fun `verify FetchAllFlagData succeeds`() {
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagCount() }.answers {
            flow {
                emit(flagDataList.size)
            }
        }
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagData() }.answers { flagDataList }

        val savedImageViewModel = SavedFlagViewModel(fetchSavedFlagUseCase, fetchCountryUseCase)
        savedImageViewModel.fetchAllFlagData()
        assertEquals(flagDataList, savedImageViewModel.savedFlagList.value)
    }

    @Test
    fun `verify FetchAllFlagData handles exception`() {
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagCount() }.answers {
            flow {
                emit(flagDataList.size)
            }
        }
        coEvery { fetchSavedFlagUseCase.fetchSavedFlagData() }.throws(Exception())

        val savedImageViewModel = SavedFlagViewModel(fetchSavedFlagUseCase, fetchCountryUseCase)
        savedImageViewModel.fetchAllFlagData()
        assertEquals(null, savedImageViewModel.savedFlagList.value)
        assertEquals(SavedFlagViewModel.ERR_GENERIC_ERROR, savedImageViewModel.error.value)
    }

}