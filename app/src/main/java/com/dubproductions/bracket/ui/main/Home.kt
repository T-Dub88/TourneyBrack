package com.dubproductions.bracket.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun HomeScreen(userViewModel: UserViewModel) {

    val userInfo by userViewModel.user.collectAsStateWithLifecycle()

    Column {
        Text(text = userInfo.toString())
    }

}
