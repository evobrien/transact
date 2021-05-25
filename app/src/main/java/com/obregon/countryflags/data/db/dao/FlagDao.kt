package com.obregon.countryflags.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obregon.countryflags.data.db.Flag
import kotlinx.coroutines.flow.*

@Dao
interface FlagDao {

    @Query("SELECT * FROM Flag")
    suspend fun getAll(): List<Flag>

    @Query("DELETE FROM Flag")
    suspend fun deleteAll()

    @Query("DELETE FROM Flag WHERE country_code=:countryCode")
    suspend fun delete(countryCode: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg flag: Flag)

    @Query("SELECT count(*) FROM Flag")
    fun getFlagCount(): Flow<Int>


}