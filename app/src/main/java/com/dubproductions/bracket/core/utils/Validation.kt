package com.dubproductions.bracket.core.utils

import android.util.Patterns

object Validation {

    // Check to see if email is a valid pattern
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Check to see if field has text
    fun isFieldEmpty(text: String): Boolean {
        return text.isNotEmpty()
    }

    // Validate if passwords match
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    // Validate if tourney type is selected
    fun selectedTourneyType(selected: String): Boolean {
        return selected != "Select Tournament Type"
    }

}

enum class Type {
    EMAIL,
    PASSWORD,
    EMPTY,
    TOURNEY_TYPE
}
