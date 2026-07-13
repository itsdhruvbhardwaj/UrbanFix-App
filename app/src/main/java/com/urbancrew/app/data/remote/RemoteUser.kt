package com.urbancrew.app.data.remote

/**
 * The data structure as it exists in the remote database (Firestore/MongoDB/Postgres).
 * This ensures consistency across Android and iOS.
 */
data class RemoteUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = 0L
)
