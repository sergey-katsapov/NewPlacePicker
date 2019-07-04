package katsapov.e

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker

class StaticAdressActivityK : AppCompatActivity(), AdapterView.OnItemClickListener, LocationListener,
    OnMapReadyCallback {

    private val PLACE_PICKER_REQUEST = 1

    private val mLocationListener: LocationSource.OnLocationChangedListener? = null
    private var mPointMe: LatLng? = null
    private var mMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.static_adress_activity)

        setSupportActionBar(findViewById<View>(R.id.toolbar_top) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        val lvStaticAdress = findViewById<View>(R.id.lv_static_adress) as ListView
        val names = arrayOf("NW", "NS", "NS", "kd", "as", "ads", "qq", "dqw", "qqwe", "bb", "sq")


        val adapter = ArrayAdapter(
            this,
            R.layout.item_static_adress, R.id.tv_static_address, names
        )

        lvStaticAdress.adapter = adapter
        lvStaticAdress.onItemClickListener = this
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_static_adress, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> finish()

            R.id.action_add_address -> openPlacePicker()

            else -> {
            }
        }
        return true
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        setUpMap()
        scalingMap()
    }

    @SuppressLint("MissingPermission")
    public override fun onStart() {
        super.onStart()
    }

    public override fun onResume() {
        super.onResume()
    }


    private fun setUpMap() {
        mMap!!.uiSettings.isZoomControlsEnabled = false
        mMap!!.uiSettings.isMyLocationButtonEnabled = false
    }


    @SuppressLint("MissingPermission")
    fun openPlacePicker() {
        var lm: LocationManager? = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = lm!!.getBestProvider(Criteria(), true)

        if (provider != null) {
            val location = lm.getLastKnownLocation(provider)
            if (location != null) {
                val lat = lm.getLastKnownLocation(provider).latitude
                val lng = lm.getLastKnownLocation(provider).longitude
                mPointMe = LatLng(lat, lng)

                findViewById<ActionMenuItemView>(R.id.action_add_address).setOnClickListener {
                    val intent = PlacePicker.IntentBuilder()
                        .setLatLong(lat, lng)
                        .showLatLong(true)
                        .build(this)
                    startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
                }
            }
            lm.requestLocationUpdates(provider, 1, 1000f, this)
        }
    }


    override fun onLocationChanged(location: Location) {
        if (mLocationListener != null) {
            mLocationListener.onLocationChanged(location)
            val lat = location.latitude
            val lng = location.longitude
            mPointMe = LatLng(lat, lng)
        }
    }


    private fun scalingMap() {
        var lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        @SuppressLint("MissingPermission")
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val longitude: Double = location.longitude
        val latitude: Double = location.latitude

        if (lm == null)
            return

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val builder = LatLngBounds.Builder()

        if (mPointMe != null) {
            builder.include(mPointMe!!)
        }

        val point = LatLng(latitude, longitude)
        builder.include(point)

        val zooming = CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, 30)
        mMap!!.animateCamera(zooming, null)
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Toast.makeText(this, "1234124", Toast.LENGTH_LONG).show()
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
