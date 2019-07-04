package katsapov.e

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import katsapov.e.helper.OnStartDragListenerK
import katsapov.e.helper.SimpleItemTouchHelperCallbackK

class RecyclerListFragmentK : androidx.fragment.app.Fragment(), OnStartDragListenerK {

    private var mItemTouchHelper: ItemTouchHelper? = null
    private val dragStartListener: RecyclerListFragmentK? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return androidx.recyclerview.widget.RecyclerView(container!!.context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerListAdapterK(context!!, this)

        val recyclerView = view as androidx.recyclerview.widget.RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        val callback = SimpleItemTouchHelperCallbackK(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
    }


    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {}
}
