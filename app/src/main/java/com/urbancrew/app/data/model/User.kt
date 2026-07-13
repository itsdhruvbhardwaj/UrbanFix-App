package com.urbancrew.app.data.model

/**
 * Shared User model for the entire system.
 * Designed to be compatible with cross-platform backends (Firebase/Node.js).
 * Default values are provided for Firebase serialization.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val name: String? = null,
    val role: String? = null,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
