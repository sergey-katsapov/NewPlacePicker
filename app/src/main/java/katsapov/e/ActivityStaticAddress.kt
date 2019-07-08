package katsapov.e

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ActivityStaticAddress : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val TITLE = "name"
    private val DESCRIPTION = "description"
    private val CM_DELETE_ID = 1
    private var lvSimple: ListView? = null
    private var sAdapter: SimpleAdapter? = null
    private var data: ArrayList<Map<String, Any>>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.static_adress_activity)

        setSupportActionBar(findViewById<View>(R.id.toolbar_top) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)

        data = ArrayList<Map<String, Any>>()
        for (i in 1..4) {
            val m = HashMap<String, Any>()
            m[TITLE] = UUID.randomUUID().toString().replace("-", "") + i
            m[DESCRIPTION] = UUID.randomUUID().toString().replace("-", "")
            data!!.add(m)
        }

        val from = arrayOf<String>(TITLE, DESCRIPTION)
        val to = intArrayOf(R.id.tv_static_address, R.id.tv_static_second_address)
        sAdapter = SimpleAdapter(this, data, R.layout.item_static_adress, from, to)
        lvSimple = findViewById<ListView>(R.id.lv_static_adress)
        lvSimple!!.adapter = sAdapter
        registerForContextMenu(lvSimple)
    }

    private fun onButtonClick(v: ActivityStaticAddress) {
        val m = HashMap<String, Any>()
        m[TITLE] = UUID.randomUUID().toString().replace("-", "") + (data!!.size + 1)
        m[DESCRIPTION] = UUID.randomUUID().toString().replace("-", "")
        data!!.add(m)
        sAdapter!!.notifyDataSetChanged()
    }


    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(0, CM_DELETE_ID, 0, "Удалить запись")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CM_DELETE_ID) {
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo
            data!!.removeAt(acmi.position)
            sAdapter!!.notifyDataSetChanged()
            return true
        }
        return super.onContextItemSelected(item)
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


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Toast.makeText(this, "Not click on me!", Toast.LENGTH_LONG).show()
    }


    private fun openAddAdressActivity(context: Activity) {
        val intent = Intent(context.applicationContext, ActivityAddAddress::class.java)
        intent.putExtra("common", this.toString())
        context.startActivityForResult(intent, 0)
    }



    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        try {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            openAddAdressActivity(this)
            //val adress = addressData!!.addressList!![0].thoroughfare.toString() + " , " + addressData.addressList!![0].featureName.toString()
            // findViewById<TextView>(R.id.address).hint = adress

        } catch (e: Exception) {
            Log.e("MainActivity", "sdasdasdasd")
        }
    }

}
