package katsapov.e

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import katsapov.e.helper.LocationService
import pub.devrel.easypermissions.EasyPermissions


class ActivityMain : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var tvLatitude: TextView? = null
    private var tvLongitude: TextView? = null
    private var tvAdress: TextView? = null
    private var mAddAddress: MenuItem? = null
    private var mAddCustomer: MenuItem? = null

    companion object {
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
            val fragment = RecyclerListFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.content, fragment)
                .commit()
        }

        val button = findViewById<ImageButton>(R.id.btn_location)
        button.setOnClickListener {

            val tvPositiveButton = "Выбрать на карте"

            val mBuilder = AlertDialog.Builder(this@ActivityMain)
            val mView = layoutInflater.inflate(R.layout.spinner_dialog, null)
            val mSpinner = mView.findViewById<View>(R.id.dialog_spinner) as Spinner

            mBuilder.setTitle("Выбор места:")
            findViewById<TextView>(R.id.tv_latitude).hint = " "
            findViewById<TextView>(R.id.tv_longitude).hint = " "
            val adapter = ArrayAdapter(
                this@ActivityMain,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.dummy_items)
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinner.adapter = adapter
            mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View, selectedItemPosition: Int, selectedId: Long
                ) {
                    val choose = resources.getStringArray(R.array.dummy_items)
                    findViewById<TextView>(R.id.tv_latitude).hint = " "
                    findViewById<TextView>(R.id.tv_longitude).hint = " "
                    findViewById<TextView>(R.id.tv_adress).text = choose[selectedItemPosition]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            mBuilder.setPositiveButton(tvPositiveButton) { dialog, which ->
                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(53.867323, 27.508925)
                    .showLatLong(true)
                    .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }

            mView.findViewById<View>(R.id.dialog_add_location)
                .setOnClickListener { openStaticAdressActivity(this@ActivityMain) }

            mView.findViewById<View>(R.id.tvOk)
                .setOnClickListener {
                    val dialog = mBuilder.create()
                    // dialog.show()
                    finish()
                }

            mBuilder.setCancelable(true)
            mBuilder.setView(mView)
            val dialog = mBuilder.create()
            dialog.show()
        }
    }

    //work with Permissions
    public override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            startLocationService()
        } else {
            requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE)
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val adress =
                addressData!!.addressList!![0].thoroughfare.toString() + " , " + addressData.addressList!![0].featureName.toString()
            findViewById<TextView>(R.id.tv_latitude).text = addressData.latitude.toString()
            findViewById<TextView>(R.id.tv_longitude).text = addressData.longitude.toString()
            findViewById<TextView>(R.id.tv_adress).text = adress
        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }


    private fun openStaticAdressActivity(context: Activity) {
        val intent = Intent(context.applicationContext, ActivityAddresses::class.java)
        intent.putExtra("common", this.toString())
        context.startActivityForResult(intent, 0)
    }
}
