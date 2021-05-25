package com.obregon.countryflags.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obregon.countryflags.data.db.Country

@Dao
interface CountryDao {
    @Query("SELECT * FROM Country")
    suspend fun getAll(): List<Country>

    @Query("DELETE FROM Country")
    suspend fun deleteAll()

    @Query("DELETE FROM Country WHERE country_code=:countryCode")
    suspend fun delete(countryCode: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg country: Country)
}