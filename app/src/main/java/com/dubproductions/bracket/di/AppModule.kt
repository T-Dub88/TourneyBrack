package com.dubproductions.bracket.di

import com.dubproductions.bracket.data.remote.FirebaseAuthService
import com.dubproductions.bracket.data.remote.FirestoreService
import com.dubproductions.bracket.data.repository.OnboardingRepositoryImpl
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.data.repository.UserRepositoryImpl
import com.dubproductions.bracket.domain.repository.OnboardingRepository
import com.dubproductions.bracket.domain.repository.TournamentRepository
import com.dubproductions.bracket.domain.repository.UserRepository
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
    fun provideFirestoreServices(): FirestoreService {
        return FirestoreService()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
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
        firebaseAuthService: FirebaseAuthService,
        firestoreService: FirestoreService
    ): OnboardingRepository {
        return OnboardingRepositoryImpl(firebaseAuthService, firestoreService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestoreService: FirestoreService
    ): UserRepository {
        return UserRepositoryImpl(firestoreService)
    }

}
