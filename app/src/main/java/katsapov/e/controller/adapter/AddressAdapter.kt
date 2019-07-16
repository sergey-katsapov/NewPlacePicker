package katsapov.e.controller.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import katsapov.e.R
import katsapov.e.model.AddressInfo
import kotlinx.android.synthetic.main.item_address.view.*
import java.util.*


class AddressAdapter(dataSet: ArrayList<AddressInfo>, private var mContext: Context) :
    ArrayAdapter<AddressInfo>(mContext, R.layout.item_address, R.id.tv_name, dataSet), View.OnClickListener {


    private val lastPosition = -1


    private class ViewHolder(view: View) {
        internal var txtTag: TextView? = view.tv_name
        internal var txtAddressName: TextView? = view.tv_description
        internal var info: ImageView? = view.item_info
    }


    override fun onClick(v: View) {
        val position = v.tag as Int
        val `object` = getItem(position)
        val dataModel = `object` as AddressInfo

        when (v.id) {

            R.id.item_info -> Toast.makeText(this.mContext, "dsfsdf", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        var holder: ViewHolder? = view.tag as ViewHolder?
        if (holder == null) {
            holder = ViewHolder(view)
            view.tag = holder
        }

        val dataModel = getItem(position)
        holder.txtTag?.text = dataModel?.tag!!.toUpperCase()
        holder.txtAddressName?.text = dataModel.addressName
        holder.info?.setOnClickListener(this)
        holder.info?.tag = position
        return view
    }


}
