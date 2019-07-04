package katsapov.e

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import katsapov.e.helper.OnStartDragListenerK
import katsapov.e.helper.SimpleItemTouchHelperCallbackK

class RecyclerListFragmentK : Fragment(), OnStartDragListenerK {

    private var mItemTouchHelper: ItemTouchHelper? = null
    private val dragStartListener: RecyclerListFragmentK? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return RecyclerView(container!!.context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerListAdapterK(context!!, this)

        val recyclerView = view as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val callback = SimpleItemTouchHelperCallbackK(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
    }


    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {}
}
