package katsapov.e.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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


class ActivityAddAddress : AppCompatActivity(), View.OnClickListener {

    var mSharedPreferences: SharedPreferences? = null
    var uuidAddress: String? = null
    var edStaticAddressName: TextView? = null
    var addressInfoArray: Array<AddressInfo>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        edStaticAddressName = findViewById<TextView>(R.id.edAddressStaticName)

        btnUpdateInfo!!.setOnClickListener(this)
        tv_address!!.setOnClickListener(this)

        mSharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE)
        val uuidAdress = intent.getStringExtra("uuid")
        uuidAddress = uuidAdress

        val sharedPreferences =
            applicationContext.getSharedPreferences(getString(R.string.SHARED_PREFERENCES_FILE_ADDRESS_LIST), MODE_PRIVATE)
        val userInfoListJsonString = sharedPreferences.getString(getString(R.string.SHARED_PREFERENCES_KEY_USER_INFO_LIST), "")
        val gson = Gson()
        addressInfoArray = gson.fromJson<Array<AddressInfo>>(userInfoListJsonString, Array<AddressInfo>::class.java)
        addressInfoArray?.forEach { addressInfo ->
            if (addressInfo.uuid == uuidAdress) {
                edStaticAddressName!!.text = addressInfo.tag
                tv_address.text = addressInfo.addressName
                tv_latitudeAddress!!.text = addressInfo.latitude
                tv_longitudeAddress!!.text = addressInfo.longitude
            }
        }
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

            R.id.tv_address -> {
                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(53.867323, 27.508925)
                    .showLatLong(true)
                    .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }

            R.id.btnUpdateInfo -> {
                val idAdress = uuidAddress
                val addressStaticName = edStaticAddressName!!.text.toString()
                val addressName = tv_address.text.toString()
                val latitude = tv_latitudeAddress!!.text.toString()
                val longitude = tv_longitudeAddress!!.text.toString()

                addressInfoArray?.forEach { addressInfo ->
                    if (addressInfo.uuid == idAdress) {
                        addressInfo.tag = addressStaticName
                        addressInfo.addressName = addressName
                        addressInfo.latitude = latitude
                        addressInfo.longitude = longitude
                    }
                }

                val sharedPreferences = applicationContext.getSharedPreferences(getString(R.string.SHARED_PREFERENCES_FILE_ADDRESS_LIST), MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val gson = Gson()
                val userInfoListJsonString = gson.toJson(addressInfoArray)
                editor.putString(getString(R.string.SHARED_PREFERENCES_KEY_USER_INFO_LIST), userInfoListJsonString)
                editor.apply()
                finish()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            addressData?.let {
                addressData.addressList!![0].let { address ->
                    val street = address.thoroughfare.toString()
                    val home = address.featureName.toString()
                    tv_address.text = "$street , $home"
                    tv_latitudeAddress.text = address.latitude.toString()
                    tv_longitudeAddress.text = address.longitude.toString()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }
}