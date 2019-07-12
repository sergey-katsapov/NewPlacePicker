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
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ListView as ListView1

private const val SHARED_PREFERENCES_FILE_USER_INFO_LIST = "userInfoList"
private const val SHARED_PREFERENCES_KEY_USER_INFO_LIST = "user_info_list"

class ActivityAddresses : AppCompatActivity() {

    private var adapter: AddressAdapter? = null
    private var dataInfoAddress: ArrayList<AddressInfo> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        val listView = findViewById<android.widget.ListView>(R.id.lv_adresses)
        val sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREFERENCES_FILE_USER_INFO_LIST, MODE_PRIVATE)
        val userInfoListJsonString = sharedPreferences.getString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, "")
        val gson = Gson()
        val addressInfoArray = gson.fromJson<Array<AddressInfo>>(userInfoListJsonString, Array<AddressInfo>::class.java)
        addressInfoArray?.forEach { addressInfo -> dataInfoAddress.add(addressInfo) }


        adapter = AddressAdapter(dataInfoAddress, applicationContext)
        val alert = AlertDialog.Builder(
            this
        )


        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ActivityAddAddress::class.java)
            intent.putExtra("tag", dataInfoAddress[position].tag!!.toUpperCase())
            intent.putExtra("addressName", dataInfoAddress[position].addressName)
            intent.putExtra("latitude", dataInfoAddress[position].latitude)
            intent.putExtra("longitude", dataInfoAddress[position].longitude)
            startActivity(intent)
        }


        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, arg3 ->
            alert.setTitle("Delete")
            alert.setMessage("Do you want delete this item?")
            alert.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                adapter?.remove(dataInfoAddress[position])
                adapter?.notifyDataSetChanged()
            })
            alert.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
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
        adapter?.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val adress = addressData!!.addressList!![0].thoroughfare.toString() + " , " + addressData.addressList!![0].featureName.toString()
            val addressInfo = AddressInfo("", adress, addressData.addressList!![0].latitude.toString(), addressData.addressList!![0].longitude.toString())
            dataInfoAddress.add(addressInfo)

        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }
}
