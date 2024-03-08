package com.dubproductions.bracket.core.di

import com.dubproductions.bracket.auth.data.remote.FirebaseAuthService
import com.dubproductions.bracket.core.data.remote.FirestoreService
import com.dubproductions.bracket.core.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.core.data.repository.UserRepositoryImpl
import com.dubproductions.bracket.core.domain.repository.TournamentRepository
import com.dubproductions.bracket.core.domain.repository.UserRepository
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
    fun provideTournamentRepository(
        firestoreService: FirestoreService
    ): TournamentRepository {
        return TournamentRepositoryImpl(firestoreService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestoreService: FirestoreService,
        firebaseAuthService: FirebaseAuthService
    ): UserRepository {
        return UserRepositoryImpl(firestoreService, firebaseAuthService)
    }

}
