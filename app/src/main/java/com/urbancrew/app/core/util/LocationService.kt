package com.urbancrew.app.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationName(): String? {
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val subLocality = address.subLocality ?: ""
                    val locality = address.locality ?: ""
                    val adminArea = address.adminArea ?: ""
                    
                    val parts = listOf(subLocality, locality, adminArea).filter { it.isNotBlank() }
                    if (parts.isNotEmpty()) parts.joinToString(", ") else "Unknown Location"
                } else {
                    "Unknown Location"
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
