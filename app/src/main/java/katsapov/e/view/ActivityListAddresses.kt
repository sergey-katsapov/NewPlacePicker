package katsapov.e.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import katsapov.e.R
import katsapov.e.controller.adapter.AddressAdapter
import katsapov.e.model.AddressInfo
import kotlinx.android.synthetic.main.activity_addresses.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ListView as ListView1

class ActivityListAddresses : AppCompatActivity() {

    private var adapter: AddressAdapter? = null
    private var dataInfoAddress: ArrayList<AddressInfo> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)


        val sharedPreferences = applicationContext.getSharedPreferences(
            getString(R.string.SHARED_PREFERENCES_FILE_ADDRESS_LIST),
            MODE_PRIVATE
        )
        val userInfoListJsonString =
            sharedPreferences.getString(getString(R.string.SHARED_PREFERENCES_KEY_USER_INFO_LIST), "")
        val gson = Gson()
        val addressInfoArray = gson.fromJson<Array<AddressInfo>>(userInfoListJsonString, Array<AddressInfo>::class.java)
        addressInfoArray?.forEach { addressInfo -> dataInfoAddress.add(addressInfo) }

        adapter = AddressAdapter(this, R.layout.item_address, R.id.tv_name, dataInfoAddress)
        val alert = AlertDialog.Builder(
            this
        )

        lv_adresses.adapter = adapter
        lv_adresses.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ActivityEditAddress::class.java)
            intent.putExtra("uuid", dataInfoAddress[position].uuid)
            startActivity(intent)
        }


        lv_adresses.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, arg3 ->
            alert.setTitle(getString(R.string.titleDelete))
            alert.setMessage(getString(R.string.msgDelete))
            alert.setPositiveButton(
                getString(R.string.positive_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val a = dataInfoAddress[position]
                    adapter?.remove(a)
                    dataInfoAddress.remove(a)
                    val gson = Gson()
                    val userInfoListJsonString = gson.toJson(dataInfoAddress)
                    val sharedPreferences =
                        applicationContext.getSharedPreferences(
                            getString(R.string.SHARED_PREFERENCES_FILE_ADDRESS_LIST),
                            MODE_PRIVATE
                        )
                    val editor = sharedPreferences.edit()
                    editor.putString(getString(R.string.SHARED_PREFERENCES_KEY_USER_INFO_LIST), userInfoListJsonString)
                    editor.apply()
                })
            alert.setNegativeButton(
                getString(R.string.cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            alert.show()
            true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_static_adress, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> finish()

            R.id.action_add_address -> {
                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(53.867323, 27.508925)
                    .showLatLong(true)
                    .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }
            else -> {
            }
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        dataInfoAddress.clear()
        val sharedPreferences = applicationContext.getSharedPreferences(
            getString(R.string.SHARED_PREFERENCES_FILE_ADDRESS_LIST),
            MODE_PRIVATE
        )
        val userInfoListJsonString =
            sharedPreferences.getString(getString(R.string.SHARED_PREFERENCES_KEY_USER_INFO_LIST), "")
        val addressInfoArray =
            Gson().fromJson<Array<AddressInfo>>(userInfoListJsonString, Array<AddressInfo>::class.java) ?: emptyArray()
        dataInfoAddress.addAll(addressInfoArray)
        adapter?.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val adress =
                addressData!!.addressList!![0].thoroughfare?.toString() + " , " + addressData.addressList!![0].featureName?.toString()
            val uuid = generateUuid()
            val addressInfo = AddressInfo(
                uuid,
                "",
                adress,
                addressData.addressList!![0].latitude.toString(),
                addressData.addressList!![0].longitude.toString()
            )
            dataInfoAddress.add(addressInfo)
            val gson = Gson()
            val userInfoListJsonString = gson.toJson(dataInfoAddress)
            val sharedPreferences =
                applicationContext.getSharedPreferences(
                    getString(R.string.SHARED_PREFERENCES_FILE_ADDRESS_LIST),
                    MODE_PRIVATE
                )
            val editor = sharedPreferences.edit()
            editor.putString(getString(R.string.SHARED_PREFERENCES_KEY_USER_INFO_LIST), userInfoListJsonString)
            editor.apply()

        } catch (e: Exception) {
            Log.e("MainActivity", "error")
        }
    }


    private fun generateUuid(): String {
        val timestamp = System.currentTimeMillis()
        return UUID.randomUUID().toString() + "-" + timestamp
    }
}
