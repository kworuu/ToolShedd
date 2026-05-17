package com.example.toolshedd.screens.addtool

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.toolshedd.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class LocationPickerActivity : Activity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var selectedLat: Double = 10.3157
    private var selectedLng: Double = 123.8854
    private var hasSelection = false
    private lateinit var tvSelectedLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        tvSelectedLocation = findViewById(R.id.tvSelectedLocation)

        val mapFragment = fragmentManager.findFragmentById(R.id.locationMapFragment) as MapFragment
        mapFragment.getMapAsync(this)

        // Search button
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val query = findViewById<EditText>(R.id.etSearch).text.toString()
            if (query.isNotBlank()) searchLocation(query)
        }

        // Confirm button
        findViewById<Button>(R.id.btnConfirmLocation).setOnClickListener {
            if (!hasSelection) {
                Toast.makeText(this, "Please tap on the map to pick a location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = Intent().apply {
                putExtra("LAT", selectedLat)
                putExtra("LNG", selectedLng)
                putExtra("LABEL", tvSelectedLocation.text.toString())
            }
            setResult(RESULT_OK, result)
            finish()
        }

        findViewById<android.view.View>(R.id.ivBack).setOnClickListener { finish() }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val cebu = LatLng(10.3157, 123.8854)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cebu, 13f))

        map.setOnMapClickListener { latLng ->
            selectedLat = latLng.latitude
            selectedLng = latLng.longitude
            hasSelection = true

            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Tool location"))
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            // Reverse geocode to show address
            reverseGeocode(latLng)
        }
    }

    private fun searchLocation(query: String) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val results = geocoder.getFromLocationName(query, 1)
            if (!results.isNullOrEmpty()) {
                val location = results[0]
                val latLng = LatLng(location.latitude, location.longitude)
                selectedLat = location.latitude
                selectedLng = location.longitude
                hasSelection = true

                googleMap?.clear()
                googleMap?.addMarker(MarkerOptions().position(latLng).title(query))
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                val address = location.getAddressLine(0) ?: query
                tvSelectedLocation.text = address
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun reverseGeocode(latLng: LatLng) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val results = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!results.isNullOrEmpty()) {
                tvSelectedLocation.text = results[0].getAddressLine(0) ?: "Selected location"
            } else {
                tvSelectedLocation.text = "%.4f, %.4f".format(latLng.latitude, latLng.longitude)
            }
        } catch (e: Exception) {
            tvSelectedLocation.text = "%.4f, %.4f".format(latLng.latitude, latLng.longitude)
        }
    }
}