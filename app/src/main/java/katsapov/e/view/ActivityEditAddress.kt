package katsapov.e.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import katsapov.e.R
import katsapov.e.model.AddressInfo
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_main.toolbar

private val PREFERENCES_NAME = "DataStorage"
private val ADDRESS_STATIC_NAME = "AdressStaticName"
private val ADDRESS_NAME = "AdressName"
private val SHARED_PREFERENCES_FILE_ADDRESS_LIST = "userInfoList"
private val SHARED_PREFERENCES_KEY_USER_INFO_LIST = "user_info_list"

class ActivityAddAddress : AppCompatActivity(), View.OnClickListener {

    var mSharedPreferences: SharedPreferences? = null

    var tvAddressName: TextView? = null
    var tvLatitude: TextView? = null
    var tvLongitude: TextView? = null
    var btnUpdate: Button? = null
    var edStaticAddressName: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        edStaticAddressName = findViewById<TextView>(R.id.edAddressStaticName)
        tvAddressName = findViewById<TextView>(R.id.address)
        tvLatitude = findViewById<TextView>(R.id.tv_latitudeAddress)
        tvLongitude = findViewById<TextView>(R.id.tv_longitudeAddress)
        btnUpdate = findViewById<Button>(R.id.btnUpdateInfo)
        btnUpdate!!.setOnClickListener(this)
        tvAddressName!!.setOnClickListener(this)

        mSharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val addressStaticName = intent.getStringExtra("tag")
        val addressName = intent.getStringExtra("addressName")
        val latitude = intent.getStringExtra("latitude")
        val longitude = intent.getStringExtra("longitude")
        edStaticAddressName!!.text = addressStaticName
        tvAddressName!!.text = addressName
        tvLatitude!!.text = latitude
        tvLongitude!!.text = longitude
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return true
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.address -> {
                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(53.867323, 27.508925)
                    .showLatLong(true)
                    .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }

            R.id.btnUpdateInfo -> {
                val addressStaticName = edStaticAddressName!!.text.toString()
                val addressName = tvAddressName!!.text.toString()
                val latitude = tvLatitude!!.text.toString()
                val longitude = tvLongitude!!.text.toString()
                val newAddress = AddressInfo(addressStaticName, addressName, latitude, longitude)


                val list: ArrayList<AddressInfo> = arrayListOf()
                list.add(newAddress)


                val gson = Gson()
                val userInfoListJsonString = gson.toJson(list)
                val sharedPreferences =
                    applicationContext.getSharedPreferences(SHARED_PREFERENCES_FILE_ADDRESS_LIST, MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, userInfoListJsonString)
                editor.apply()
                val intent = Intent(this@ActivityAddAddress, ActivityAddresses::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val adress =
                addressData!!.addressList!![0].thoroughfare.toString() + " , " + addressData.addressList!![0].featureName.toString()
            address.text = adress

            tv_latitudeAddress.text = addressData.addressList!![0].latitude.toString()
            tv_longitudeAddress.text = addressData.addressList!![0].longitude.toString()
        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }
}