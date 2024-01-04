package com.dubproductions.bracket.di

import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseManager(): TournamentRepositoryImpl {
        return TournamentRepositoryImpl()
    }
}
