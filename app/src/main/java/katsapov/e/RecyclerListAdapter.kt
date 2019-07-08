package katsapov.e

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import katsapov.e.helper.ItemTouchHelperAdapterK
import katsapov.e.helper.ItemTouchHelperViewHolderK
import java.util.*

class RecyclerListAdapter(private val mContext: Context, private val mDragStartListener: RecyclerListFragment) :
    androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>(), ItemTouchHelperAdapterK {

    private val mItems = ArrayList<String>()

    init {
        mItems.addAll(Arrays.asList(*mContext.resources.getStringArray(R.array.dummy_items)))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        val itemViewHolder = ItemViewHolder(view)
        itemViewHolder.itemView.setOnClickListener { Toast.makeText(mContext, "12312312", Toast.LENGTH_SHORT).show() }
        return itemViewHolder
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.textView.text = mItems[position]
    }


    override fun onItemDismiss(position: Int) {
        mItems.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(mItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }


    override fun getItemCount(): Int {
        return mItems.size
    }


    class ItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolderK {
        val textView: TextView = itemView.findViewById<View>(R.id.text) as TextView

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.RED)
            itemView.isClickable = true
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }
}
