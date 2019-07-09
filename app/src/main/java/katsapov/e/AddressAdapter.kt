package katsapov.e

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.*


class AddressAdapter(private val dataSet: ArrayList<AddressModel>, internal var mContext: Context) :
    ArrayAdapter<AddressModel>(mContext, R.layout.item_address, dataSet), View.OnClickListener {


    private val lastPosition = -1


    private class ViewHolder {
        internal var txtName: TextView? = null
        internal var txtType: TextView? = null
        internal var info: ImageView? = null
    }


    override fun onClick(v: View) {
        val position = v.tag as Int
        val `object` = getItem(position)
        val dataModel = `object` as AddressModel

        when (v.id) {

            R.id.item_info -> Toast.makeText(this.mContext, "dsfsdf", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val dataModel = getItem(position)
        val viewHolder: ViewHolder

        val result: View

        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.item_address, parent, false)
            viewHolder.txtName = convertView!!.findViewById(R.id.tv_name) as TextView?
            viewHolder.txtType = convertView.findViewById(R.id.tv_description) as TextView?
            viewHolder.info = convertView.findViewById(R.id.item_info) as ImageView?
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }


        viewHolder.txtName!!.text = dataModel!!.name
        viewHolder.txtType!!.text = dataModel.type
        viewHolder.info!!.setOnClickListener(this)
        viewHolder.info!!.tag = position
        // Return the completed view to render on screen
        return convertView
    }


}
