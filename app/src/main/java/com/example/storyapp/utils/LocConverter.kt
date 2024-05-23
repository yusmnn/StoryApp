package com.example.storyapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocConverter {
    companion object {
        @Suppress("DEPRECATION")
        suspend fun getAddress(latlng: LatLng?, context: Context): String {
            var fullAddress = "No Location"

            try {
                if (latlng != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1) { addresses ->
                            fullAddress = extractAddress(addresses)
                        }
                    } else {
                        val addresses = withContext(Dispatchers.IO) {
                            geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
                        }
                        fullAddress = extractAddress(addresses)
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR", "ERROR: $e")
            }
            return fullAddress
        }

        private fun extractAddress(addresses: List<Address>?): String {
            if (addresses.isNullOrEmpty()) {
                return "No Location"
            }

            val address = addresses[0]
            val city = address.locality
            val state = address.adminArea
            val country = address.countryName

            return address.getAddressLine(0)
                ?: if (city != null && state != null && country != null) {
                    StringBuilder(city).append(", $state").append(", $country").toString()
                } else if (state != null && country != null) {
                    StringBuilder(state).append(", $country").toString()
                } else {
                    country ?: "Location tidak diketahui"
                }
        }
    }
}
