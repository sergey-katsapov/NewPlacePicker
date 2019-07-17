package katsapov.e.controller.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import katsapov.e.model.AddressInfo
import kotlinx.android.synthetic.main.item_spinner_address.view.*
import java.util.*


class AddressAdapter(mContext: Activity, resouceId: Int, textViewId: Int, dataSet: ArrayList<AddressInfo>) :
    ArrayAdapter<AddressInfo>(mContext, resouceId, textViewId, dataSet) {

    private class ViewHolder(view: View) {
        internal var txtTag: TextView? = view.tv_name
        internal var txtAddressName: TextView? = view.tv_description
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        var holder: ViewHolder? = view.tag as ViewHolder?
        if (holder == null) {
            holder = ViewHolder(view)
            view.tag = holder
        }

        val rowItem = getItem(position)
        holder.txtTag?.text = rowItem?.tag
        holder.txtAddressName?.text = rowItem?.addressName
        return view
    }
}