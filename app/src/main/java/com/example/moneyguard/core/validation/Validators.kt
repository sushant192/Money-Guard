package com.example.moneyguard.core.validation

/**
 * Pure-Kotlin form validators shared across auth screens (sign up, login,
 * password reset). Keeping these here means they're easy to unit-test without
 * any Android dependencies.
 */
object Validators {

    /**
     * Conservative email regex: local part + "@" + domain + "." + 2+ char TLD.
     * Good enough for client-side hints; the source of truth is server-side.
     */
    private val emailRegex = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    /**
     * At least 8 characters, contains at least one letter and at least one digit.
     */
    private val passwordRegex = Regex(
        "^(?=.*[A-Za-z])(?=.*\\d).{8,}$"
    )

    fun isNotBlank(value: String): Boolean = value.trim().isNotEmpty()

    fun isValidEmail(email: String): Boolean = emailRegex.matches(email.trim())

    fun isValidPassword(password: String): Boolean = passwordRegex.matches(password)

    fun passwordsMatch(password: String, confirm: String): Boolean =
        password == confirm
}
