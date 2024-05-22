package com.example.locationscaner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationscaner.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                showUserLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                showUserLocation()
            }
            else -> {
                Snackbar.make(
                    binding.root,
                    "Ну и не будет приложение работать так как надо",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        val isCourseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        when {
            isCourseLocationPermissionGranted && isFineLocationPermissionGranted-> {
                // You can use the API that requires the permission.
                showUserLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                notifyUserAboutPermission()
            }
            else -> requestLocationPermission()
        }
    }

    private fun notifyUserAboutPermission() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder
            .setTitle("Сейчас будет запрошено разрешение")
            .setMessage("Для того чтобы мы могли получить ваше местоположение, нам нужно " +
                    "получить разрешение на геопозицию")
            .setPositiveButton("Хорошо") { dialogInterface, _ ->
                requestLocationPermission()
            }
            .setNegativeButton("Не хочу") { _, _ ->
                Toast.makeText(
                    this@MainActivity,
                    "Ну и пожалуйста, не будет работать у тебя функционал :Р",
                    Toast.LENGTH_LONG
                ).show()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }


    @SuppressLint("MissingPermission")
    private fun showUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Snackbar.make(binding.root, "Последнее известное местоположение: $location", Snackbar.LENGTH_LONG).show()
            }
    }
}