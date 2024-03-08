package com.dubproductions.bracket.auth.di

import com.dubproductions.bracket.auth.data.remote.FirebaseAuthService
import com.dubproductions.bracket.core.data.remote.FirestoreService
import com.dubproductions.bracket.auth.data.repository.OnboardingRepositoryImpl
import com.dubproductions.bracket.auth.domain.repository.OnboardingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
    }

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        firebaseAuthService: FirebaseAuthService,
        firestoreService: FirestoreService
    ): OnboardingRepository {
        return OnboardingRepositoryImpl(firebaseAuthService, firestoreService)
    }

}
