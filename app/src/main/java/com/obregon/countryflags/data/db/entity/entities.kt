package com.obregon.countryflags.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Flag(
    @PrimaryKey
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "image_path") val imagePath: String
)


@Entity
data class Country(
    @PrimaryKey
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "country_name") val countryName: String
)