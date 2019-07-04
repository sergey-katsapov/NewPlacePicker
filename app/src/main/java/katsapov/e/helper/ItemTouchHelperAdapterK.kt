package katsapov.e.helper

interface ItemTouchHelperAdapterK {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}
