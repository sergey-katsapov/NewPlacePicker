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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class ActivityAddAddress : AppCompatActivity(), View.OnClickListener {

    private val PREFERENCES_NAME = "DataStorage"
    private val ADDRESS_STATIC_NAME = "AdressStaticName"
    private val ADDRESS_NAME = "AdressName"
    private val SHARED_PREFERENCES_FILE_ADDRESS_LIST = "userInfoList"
    private val SHARED_PREFERENCES_KEY_USER_INFO_LIST = "User_Info_List"

    var mSharedPreferences: SharedPreferences? = null

    var tvAddressName: TextView? = null
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
        btnUpdate = findViewById<Button>(R.id.btnUpdateInfo)
        btnUpdate!!.setOnClickListener(this)
        tvAddressName!!.setOnClickListener(this)

        mSharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val addressStaticName = intent.getStringExtra("tag")
        val addressName = intent.getStringExtra("addressName")
        edStaticAddressName!!.text = "$addressStaticName"
        tvAddressName!!.text = "$addressName"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    fun setFieldsFromSaveData() {
        val adressStaticName = mSharedPreferences!!.getString(ADDRESS_STATIC_NAME, "")
        val adressName = mSharedPreferences!!.getString(ADDRESS_NAME, "")

        edStaticAddressName = findViewById<TextView>(R.id.tv_name)
        edStaticAddressName!!.text = adressStaticName

        tvAddressName = findViewById<TextView>(R.id.tv_description)
        tvAddressName!!.text = adressName
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
                val newAddress = AddressInfo()

                val ret = ArrayList<AddressInfo>()
                newAddress.id = Math.random().toInt()
                newAddress.tag = addressStaticName
                newAddress.addressName = addressName
                ret.add(newAddress)

                val gson = Gson()
                val userInfoListJsonString = gson.toJson(ret)
                val sharedPreferences =
                    applicationContext.getSharedPreferences(SHARED_PREFERENCES_FILE_ADDRESS_LIST, MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, userInfoListJsonString)
                editor.apply()
                val intent = Intent(this@ActivityAddAddress, ActivityAddresses::class.java)
                startActivity(intent)

               // Toast.makeText(applicationContext, "Сохранено!", Toast.LENGTH_SHORT).show()
            }
        }

        /* val addressStaticName = edStaticAddressName!!.text.toString()
         val addressName = tvAddressName!!.text.toString()
         val editor: SharedPreferences.Editor = mSharedPreferences!!.edit()

         editor.putString(ADDRESS_STATIC_NAME, addressStaticName)
         editor.putString(ADDRESS_NAME, addressName)
         if (editor.commit()) {
             Toast.makeText(this@ActivityAddAddress, "New adress:\n$tag\n$addressName", Toast.LENGTH_SHORT)
                 .show()
         }
         val intent = Intent(this@ActivityAddAddress, ActivityAddresses::class.java)
         startActivity(intent)
     }*/
    }


    /* private fun initAddressInfoList(): List<AddressInfo> {
         val ret = ArrayList<AddressInfo>()

         val addressStaticName = edStaticAddressName!!.text.toString()
         val addressName = tvAddressName!!.text.toString()

         val newAddress = AddressInfo()
         newAddress.id = Math.random().toInt()
         newAddress.tag = addressStaticName
         newAddress.addressName = addressName
         ret.add(newAddress)

         return ret
     }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val adress =
                addressData!!.addressList!![0].thoroughfare.toString() + " , " + addressData.addressList!![0].featureName.toString()
            findViewById<TextView>(R.id.address).text = adress
        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }
}