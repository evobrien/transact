package com.obregon.countryflags.di

import android.content.Context
import com.obregon.countryflags.data.api.CountryFlagsApi
import com.obregon.countryflags.data.db.AppDatabase
import com.obregon.countryflags.data.db.dao.CountryDao
import com.obregon.countryflags.data.db.dao.FlagDao
import com.obregon.countryflags.data.repo.CountryFlagsRepo
import com.obregon.countryflags.data.repo.CountryRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun providesDb(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getInstance(appContext)
    }

    @Provides
    fun provideFlagDao(appDatabase: AppDatabase): FlagDao {
        return appDatabase.getFlagDao()
    }

    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao {
        return appDatabase.getCountryDao()
    }

    @Singleton
    @Provides
    fun providesCountryFlagsRepo(
        @ApplicationContext appContext: Context,
        countryFlagsApi: CountryFlagsApi, flagDao: FlagDao
    ): CountryFlagsRepo {
        return CountryFlagsRepo(appContext, countryFlagsApi, flagDao)
    }

    @Singleton
    @Provides
    fun providesCountryRepo(countryDao: CountryDao): CountryRepo {
        return CountryRepo(countryDao)
    }

}