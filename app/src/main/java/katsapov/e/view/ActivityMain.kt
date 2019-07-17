package katsapov.e.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import katsapov.e.R
import katsapov.e.controller.adapter.AddressAdapter
import katsapov.e.controller.service.LocationService
import katsapov.e.model.AddressInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.spinner_dialog.view.*
import pub.devrel.easypermissions.EasyPermissions

private const val SHARED_PREFERENCES_FILE_USER_INFO_LIST = "userInfoList"
private const val SHARED_PREFERENCES_KEY_USER_INFO_LIST = "user_info_list"

class ActivityMain : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var spinner: Spinner? = null

    private var mAddAddress: MenuItem? = null
    private var mAddCustomer: MenuItem? = null

    private var listTagsAddress: ArrayList<String> = ArrayList()
    private var listObjAddress: Array<AddressInfo> = emptyArray()

    private var fragment: RecyclerListFragment? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private var selectedAddress: AddressInfo? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        val list = listOf("АНДРОСОВ К.Н.Ип", "БУРШТАТ ЧТУП", "ГХУ ГУ", "ДИПСЕРВИС ГУ", "РСО-ТОРГ ООО")

        if (savedInstanceState == null) {
            fragment = RecyclerListFragment(list)
            supportFragmentManager.beginTransaction()
                .add(R.id.content, fragment!!)
                .commit()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtieneLocalizacion()

        btn_location.setOnClickListener {
            val mView = layoutInflater.inflate(R.layout.spinner_dialog, null)
            val mBuilder = AlertDialog.Builder(this@ActivityMain)
            mBuilder.setTitle("Выбор места:")
            mBuilder.setNegativeButton("Выбрать на карте") { dialog, which ->
                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(latitude!!, longitude!!)
                    .showLatLong(true)
                    .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }

            mBuilder.setPositiveButton("Выбрать") { dialog, which ->
                val id = spinner?.selectedItemId?.toInt() ?: 0
                if (listObjAddress.isNotEmpty()) {
                    selectedAddress = listObjAddress[id]
                    if (selectedAddress != null) {
                        tv_tag.text = selectedAddress?.tag
                        tv_adress.text = selectedAddress?.addressName
                        tv_latitude.hint = selectedAddress?.latitude
                        tv_longitude.hint = selectedAddress?.longitude
                    }
                }
            }
            mBuilder.setCancelable(true)
            mBuilder.setView(mView)
            val dialog = mBuilder.create()
            dialog.show()

            val adapter = AddressAdapter(
                this,
                R.layout.item_spinner_address,
                R.id.tv_name,
                listObjAddress.toCollection(ArrayList())
            )
            adapter.setDropDownViewResource(R.layout.item_spinner_address)

            spinner = mView.dialog_spinner
            spinner?.adapter = adapter
            mView.dialog_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View,
                    selectedItemPosition: Int,
                    selectedId: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            for ((index, value) in listObjAddress.withIndex()) {
                if (value.uuid == selectedAddress?.uuid) {
                    spinner?.setSelection(index)
                }
            }

            mView.dialog_add_location.setOnClickListener { openAddressesActivity(this@ActivityMain) }
        }

        setSupportActionBar(toolbar)

        btn_route.setOnClickListener {
            val uri =
                Uri.parse("yandexmaps://maps.yandex.ru/?rtext=53.919219,27.487495 ~ 53.898340,27.469642 ~ 53.869131,27.584312 ~ 53.869131,27.584312  ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 ~ 53.869131,27.584312 &rtt=auto")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    //work with Permissions
    public override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            startLocationService()
        } else {
            requestPermissions(
                PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    public override fun onResume() {
        super.onResume()
        listTagsAddress.clear()

        val sharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES_FILE_USER_INFO_LIST, MODE_PRIVATE)
        val userInfoListJsonString = sharedPreferences.getString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, "")

        listObjAddress =
            Gson().fromJson<Array<AddressInfo>>(userInfoListJsonString, Array<AddressInfo>::class.java) ?: emptyArray()
        listObjAddress.forEach { addressInfo ->
            listTagsAddress.add(addressInfo.tag!!)
        }
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
        menuInflater.inflate(R.menu.activity_visitplan, menu)
        mAddAddress = menu.findItem(R.id.action_add_adress)
        mAddCustomer = menu.findItem(R.id.action_add_customer)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_adress -> openAddressesActivity(this)
            R.id.action_add_customer -> Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val address =
                addressData!!.addressList!![0].thoroughfare?.toString() + " , " + addressData.addressList!![0].featureName?.toString()
            tv_tag.text = " "
            tv_latitude.text = addressData.latitude.toString()
            tv_longitude.text = addressData.longitude.toString()
            tv_adress.text = address
        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }


    private fun openAddressesActivity(context: Activity) {
        startActivity(Intent(context.applicationContext, ActivityListAddresses::class.java))
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                latitude = location?.latitude
                longitude = location?.longitude
            }
    }
}
