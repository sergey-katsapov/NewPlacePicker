package katsapov.e

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker


class ActivityAddresses : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        setSupportActionBar(findViewById<View>(R.id.toolbar_top) as? Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        val listView = findViewById<ListView>(R.id.lv_adresses)
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
            intent.putExtra("addressStaticName", dataModels[position].name.toUpperCase())
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
}
