package com.example.ngamingcase.presentation.posts.validation

import javax.inject.Inject

class PostInputValidator @Inject constructor() {
    fun validate(title: String, body: String): ValidationResult {
        val titleError = when {
            title.isBlank() -> "Title cannot be empty"
            title.trim().length < 3 -> "Title is too short"
            else -> null
        }

        val bodyError = when {
            body.isBlank() -> "Description cannot be empty"
            body.trim().length < 5 -> "Description is too short"
            else -> null
        }

        return ValidationResult(titleError = titleError, bodyError = bodyError)
    }
}

data class ValidationResult(
    val titleError: String? = null,
    val bodyError: String? = null
) {
    val isValid: Boolean get() = titleError == null && bodyError == null
}

