package com.dubproductions.bracket

import android.util.Patterns

class Validation {

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

}

enum class Type {
    EMAIL,
    PASSWORD,
    EMPTY
}
