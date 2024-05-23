package com.example.storyapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class LocConverter {
    companion object {
        fun getAddress(latlng: LatLng?, context: Context): String {
            var fullAddress = "No Location"

            try {
                if (latlng != null) {
                    val address: Address?
                    val gc = Geocoder(context)
                    val list: List<Address> =
                        gc.getFromLocation(latlng.latitude, latlng.longitude, 1) as List<Address>
                    address = if (list.isNotEmpty()) list[0] else null

                    if (address != null) {
                        val city = address.locality
                        val state = address.adminArea
                        val country = address.countryName

                        fullAddress = address.getAddressLine(0)
                            ?: if (city != null && state != null && country != null) {
                                StringBuilder(city).append(", $state").append(", $country")
                                    .toString()
                            } else if (state != null && country != null) {
                                StringBuilder(state).append(", $country").toString()
                            } else country ?: "Location tidak diketahui"
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR", "ERROR: $e")
            }
            return fullAddress
        }
    }
}