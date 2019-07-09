package katsapov.e

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker


class ActivityAddAddress : AppCompatActivity(), View.OnClickListener {

    var tvAddressName: TextView? = null
    var btnUpdate: Button? = null
    var edStaticAddressName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        setSupportActionBar(findViewById<View>(R.id.toolbar_top) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        edStaticAddressName = findViewById<TextView>(R.id.edAddressStaticName)
        tvAddressName = findViewById<TextView>(R.id.address)
        btnUpdate = findViewById<Button>(R.id.btnUpdate)

        tvAddressName!!.setOnClickListener(this)
        btnUpdate!!.setOnClickListener(this)

        val addressStaticName = intent.getStringExtra("addressStaticName")
        val addressName = intent.getStringExtra("addressName")


        edStaticAddressName!!.text = "$addressStaticName"
        tvAddressName!!.text = "$addressName"
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
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(53.867323, 27.508925)
            .showLatLong(true)
            .build(this)
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
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
            findViewById<TextView>(R.id.address).text = adress
        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }

    fun onClickButton(v: View?) {
        /* val intent = Intent(this@ActivityAddAddress, ActivityAddresses::class.java)
         edStaticAddressName!!.text =  findViewById<TextView>(R.id.tv_name).text
         tvAddressName!!.text =  findViewById<TextView>(R.id.tv_description).text
         intent.putExtra("newAddressStaticName", edStaticAddressName!!.text)
         intent.putExtra("newAddressName",  tvAddressName!!.text )
         startActivity(intent)*/
    }
}