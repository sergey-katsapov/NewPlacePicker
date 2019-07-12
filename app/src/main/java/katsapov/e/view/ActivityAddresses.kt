package katsapov.e.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import katsapov.e.R
import katsapov.e.controller.adapter.AddressAdapter
import katsapov.e.model.AddressModel
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ListView as ListView1


class ActivityAddresses : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)


        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)



        val listView = findViewById<android.widget.ListView>(R.id.lv_adresses)
        val dataModels = java.util.ArrayList<AddressModel>()

        dataModels.add(AddressModel("Дом", "Алибегова, 27", "1"))
        dataModels.add(AddressModel("Работа", "Софьи Ковалевской, 60", "2"))

        val adapter = AddressAdapter(dataModels, applicationContext)
        val alert = AlertDialog.Builder(
            this
        )


        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ActivityAddAddress::class.java)
            intent.putExtra("tag", dataModels[position].name.toUpperCase())
            intent.putExtra("addressName", dataModels[position].type)
            startActivity(intent)
        }



        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, arg3 ->
            alert.setTitle("Delete")
            alert.setMessage("Do you want delete this item?")
            alert.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                adapter.remove(dataModels[position])
                adapter.notifyDataSetChanged()
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
