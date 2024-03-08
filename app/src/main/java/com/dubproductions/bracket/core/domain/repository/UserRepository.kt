package com.dubproductions.bracket.core.domain.repository

import com.dubproductions.bracket.core.domain.model.user.User

interface UserRepository {
    fun fetchUserData(onComplete: (User) -> Unit)

    fun fetchUserId(): String

}
