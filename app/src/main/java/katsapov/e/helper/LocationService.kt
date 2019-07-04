package katsapov.e.helper

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@SuppressLint("MissingPermission")
class LocationService : Service(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    companion object {

        @JvmStatic
        fun calculateDistance(from: LatLng?, to: LatLng?): Double {
            if (from == null || to == null) return Double.MAX_VALUE
            val results = FloatArray(3)
            Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results)
            return (results[0] / 1000).toDouble()
        }

        @JvmStatic
        fun isGPSenabled(context: Context): Boolean {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate() {
        super.onCreate()

        if (!isGPSenabled(applicationContext))
            stopSelf()

        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val sLocationInterval = pref.getString("gps_period_id", "300")
        val locationInterval = (Integer.parseInt(sLocationInterval) * 1000).toLong()

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(locationInterval)

        googleApiClient.connect()
    }

    private fun getLastLocation() {

        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val locationNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val location: Location?

        location =
            if (locationGPS != null && locationNetwork != null)
                if (locationGPS.time > locationNetwork.time) locationGPS else locationNetwork
            else if (locationNetwork == null && locationGPS == null)
                null
            else locationGPS ?: locationNetwork
    }

    override fun onDestroy() {
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient.disconnect()
        }
        super.onDestroy()
    }

    override fun onConnected(bundle: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        getLastLocation()
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (!connectionResult.hasResolution()) {
            Toast.makeText(this,"Location services connection failed with code ",Toast.LENGTH_LONG).show()
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            handleNewLocation(location)
        }
    }

    private fun handleNewLocation(location: Location) {
        Toast.makeText(this,location.toString(),Toast.LENGTH_LONG).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!googleApiClient.isConnected) {
            googleApiClient.connect()
        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}