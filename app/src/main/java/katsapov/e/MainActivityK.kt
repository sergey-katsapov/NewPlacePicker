package katsapov.e

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import katsapov.e.helper.LocationService
import pub.devrel.easypermissions.EasyPermissions

class MainActivityK : AppCompatActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks, LocationListener,
    OnMapReadyCallback {

    private val PLACE_PICKER_REQUEST = 1
    private var tvLatitude: TextView? = null
    private var tvLongitude: TextView? = null
    private var tvAdress: TextView? = null
    private var btnTest: Button? = null
    private var mAddAddress: MenuItem? = null
    private var mAddCustomer: MenuItem? = null
    private var mLocationListener: LocationSource.OnLocationChangedListener? = null
    private var mPointMe: LatLng? = null
    private var mMap: GoogleMap? = null

    companion object {
        private const val TAG_CONFIRM_EXIT = "TAG_CONFIRM_EXIT"
        private const val TASK_TAG_GET_PASS = "TASK_TAG_GET_PASS"

        private const val PERMISSIONS_REQUEST_CODE = 20001
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CONTROL_LOCATION_UPDATES
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<View>(R.id.toolbar_top) as Toolbar)
        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_longitude)
        tvAdress = findViewById(R.id.tv_adress)

        if (savedInstanceState == null) {
            val fragment = RecyclerListFragmentK()
            supportFragmentManager.beginTransaction()
                .add(R.id.content, fragment)
                .commit()
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        setUpMap()
        scalingMap()
    }

    private fun setUpMap() {
        mMap!!.uiSettings.isZoomControlsEnabled = false
        mMap!!.uiSettings.isMyLocationButtonEnabled = false
    }


    @SuppressLint("MissingPermission")
    public override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            startLocationService()
        } else {
            requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            openMapLocation()
        }
    }

    public override fun onResume() {
        super.onResume()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        startLocationService()
    }

    private fun startLocationService() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            startService(Intent(this, LocationService::class.java))
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_visitplan, menu)
        mAddAddress = menu.findItem(R.id.action_add_adress)
        mAddCustomer = menu.findItem(R.id.action_add_customer)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_add_adress -> openStaticAdressActivity(this)

            R.id.action_add_customer -> Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()

            else -> {
            }
        }

        return true
    }


    @SuppressLint("MissingPermission")
    override fun onClick(v: View) {
        val btnLoc = findViewById<ImageView>(R.id.btn_location)
        btnLoc.setOnClickListener {
            val tvPositiveButton = "Выбрать на карте"

            val mBuilder = AlertDialog.Builder(this@MainActivityK)
            val mView = layoutInflater.inflate(R.layout.spinner_dialog, null)
            val mSpinner = mView.findViewById<View>(R.id.dialog_spinner) as Spinner

            mBuilder.setTitle("Выбор места:")
            val adapter = ArrayAdapter(
                this@MainActivityK,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.dummy_items)
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinner.adapter = adapter

            mBuilder.setPositiveButton(tvPositiveButton) { dialog, which ->
                //TODO add place picker on map
                // Toast.makeText(this,"afsafs", Toast.LENGTH_SHORT).show()
                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(53.867323, 27.508925)
                    .showLatLong(true)
                    .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }

            mView.findViewById<View>(R.id.dialog_add_location)
                .setOnClickListener { openStaticAdressActivity(this@MainActivityK) }

            mBuilder.setCancelable(true)
            mBuilder.setView(mView)
            val dialog = mBuilder.create()
            dialog.show()
        }
    }


    @SuppressLint("MissingPermission")
    private fun openMapLocation() {
        var lm: LocationManager? = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = lm!!.getBestProvider(Criteria(), true)

        if (provider != null) {
            val location = lm.getLastKnownLocation(provider)
            if (location != null) {
                val lat = lm.getLastKnownLocation(provider).latitude
                val lng = lm.getLastKnownLocation(provider).longitude
                mPointMe = LatLng(lat, lng)
            }
            lm.requestLocationUpdates(provider, 1, 1000f, this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            findViewById<TextView>(R.id.tv_latitude).text = addressData!!.latitude.toString()
            findViewById<TextView>(R.id.tv_longitude).text = addressData.longitude.toString()
            val adress = addressData.addressList!![0].thoroughfare.toString() + " , " + addressData.addressList!![0].featureName.toString()
            findViewById<TextView>(R.id.tv_adress).text = adress
        } catch (e: Exception) {
            Log.e("MainActivity", e.message)
        }
    }


    fun openStaticAdressActivity(context: Activity) {
        val intent = Intent(context.applicationContext, StaticAdressActivityK::class.java)
        intent.putExtra("common", this.toString())
        context.startActivityForResult(intent, 0)
    }

    override fun onLocationChanged(location: Location) {
        if (mLocationListener != null) {
            mLocationListener!!.onLocationChanged(location)
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
