package com.dubproductions.bracket.domain.repository

import com.dubproductions.bracket.domain.model.User

interface UserRepository {
    suspend fun fetchUserData(): User
}