package com.obregon.countryflags.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.obregon.countryflags.data.AppError
import com.obregon.countryflags.data.QueryResult
import com.obregon.countryflags.data.api.CountryFlagsApi
import com.obregon.countryflags.data.db.Flag
import com.obregon.countryflags.data.db.dao.FlagDao
import com.obregon.countryflags.domain.usecase.FlagData
import com.obregon.countryflags.utils.saveAs
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


class CountryFlagsRepo @Inject constructor(
    private val appContext: Context,
    private val countryFlagsApi: CountryFlagsApi,
    private val flagDao: FlagDao
) {
    companion object {
        const val ERR_FAILED_TO_RETRIEVE_COUNTRY = "Failed to retrieve country"
    }

    fun fetchFlag(countryCode: String, style: String, callback: (QueryResult) -> Unit) {
        try {
            countryFlagsApi.getCountryFlag(countryCode, style)
                .enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        callback(
                            QueryResult.Failure(
                                AppError(
                                    ERR_FAILED_TO_RETRIEVE_COUNTRY,
                                    t as Exception
                                )
                            )
                        )
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        if (response?.isSuccessful == true) {
                            response.body()?.let {
                                val responseBytes = it.bytes()
                                val flag = BitmapFactory.decodeByteArray(
                                    responseBytes,
                                    0,
                                    responseBytes.size
                                )
                                val flagPath = saveFlag(countryCode, flag)
                                callback(QueryResult.Success(flagPath))
                            }
                        } else {
                            response?.errorBody()?.let {
                                callback(
                                    QueryResult.Failure(
                                        AppError(
                                            ERR_FAILED_TO_RETRIEVE_COUNTRY, null,
                                            it.toString()
                                        )
                                    )
                                )
                            }
                        }
                    }
                })
        } catch (exception: Exception) {
            QueryResult.Failure(AppError(ERR_FAILED_TO_RETRIEVE_COUNTRY, exception))
        }
    }

    suspend fun fetchSavedFlags(): List<Flag> {
        return flagDao.getAll()
    }

    fun fetchSavedFlagCount(): Flow<Int> {
        return flagDao.getFlagCount()
    }

    suspend fun saveFlagData(flagData: FlagData) {
        val flag = Flag(flagData.countryCode, flagData.imagePath)
        flagDao.insertAll(flag)
    }

    private fun saveFlag(countryCode: String, flag: Bitmap): String {
        val path = "${appContext.filesDir}/${countryCode}.png"
        flag.saveAs(path)
        return path
    }
}