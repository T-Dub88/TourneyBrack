package com.dubproductions.bracket.di

import com.dubproductions.bracket.data.remote.FirestoreService
import com.dubproductions.bracket.data.repository.OnboardingRepositoryImpl
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.domain.repository.OnboardingRepository
import com.dubproductions.bracket.domain.repository.TournamentRepository
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
    fun provideFirebaseServices(): FirestoreService {
        return FirestoreService()
    }

    @Provides
    @Singleton
    fun provideTournamentRepository(
        firestoreService: FirestoreService
    ): TournamentRepository {
        return TournamentRepositoryImpl(firestoreService)
    }

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        firestoreService: FirestoreService
    ): OnboardingRepository {
        return OnboardingRepositoryImpl(firestoreService)
    }

}
