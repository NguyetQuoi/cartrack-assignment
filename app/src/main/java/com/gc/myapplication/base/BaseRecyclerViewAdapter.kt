package com.gc.myapplication.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.*
import androidx.recyclerview.widget.RecyclerView
import com.gc.myapplication.BR

/**
 * An abstract class for adapters of recycler view
 * @author n.quoi
 * @date 10.18.2021
 */

abstract class BaseRecyclerViewAdapter<IVM : BaseItemViewModel> :
    RecyclerView.Adapter<BaseRecyclerViewAdapter.ItemViewHolder<IVM>>() {

    var itemsSource = ObservableArrayList<IVM>()
    var selectedItems = ArrayList<IVM>()
        private set
    open var maxSelectedItems = 1
    open var minSelectedItems = 0
    var hasData = ObservableBoolean(false)
    private var itemClickListener: ItemClickListener? = null
    private var itemLongClickListener: ItemLongClickListener? = null

    val viewModelVariable: Int
        @IdRes
        get() = BR.viewModel

    init {
        itemsSource.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<IVM>>() {
            override fun onChanged(sender: ObservableList<IVM>) {
                notifyDataSetChanged()
                hasData.set(sender.size > 0)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<IVM>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<IVM>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<IVM>,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<IVM>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
                hasData.set(sender.size > 0)
            }
        })
    }

    @LayoutRes
    protected abstract fun getLayoutId(viewType: Int): Int

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun getItem(position: Int): IVM {
        return itemsSource[position]
    }

    /**
     * Override it if you are using custom ViewHolder (must extend from ItemViewHolder)
     *
     * @param binder : binder that holder layout from getLayoutId
     * @param viewType type of view
     * @return Custom ItemViewHolder object
     */
    fun getItemViewHolder(binder: ViewDataBinding, viewType: Int): ItemViewHolder<IVM> {
        return ItemViewHolder(binder)
    }

    /**
     * Set data for item source
     * @param data [Collection<IVM>]
     */
    fun setData(data: Collection<IVM>) {
        itemsSource.clear()
        itemsSource.addAll(data)
    }

    /**
     * Set item click listener for each item on adapter
     * @param itemClickListener listener
     */
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Set item long click listener for each item on adapter
     * @param itemLongClickListener listener for long click
     */
    fun setItemLongClickListener(itemLongClickListener: ItemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener
    }

    open fun onOverMaxSelectedItems() {}

    open fun onSelectedChanged(selectedItems: ArrayList<IVM>) {}

    open fun onItemClicked() {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder<IVM> {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binder = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getLayoutId(viewType),
            viewGroup,
            false
        )
        return getItemViewHolder(binder, viewType)
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder<IVM>, position: Int) {
        val itemViewModel = itemsSource[position]

        (itemViewHolder.viewModel as? Destroyable)?.onDestroy()
        itemViewHolder.viewModel = itemViewModel
        itemViewHolder.binder.setVariable(viewModelVariable, itemViewModel)
        itemViewHolder.binder.executePendingBindings()
        itemViewModel.onSelectedChanged = {
//            if (it) {
//                selectedItems.add(itemViewModel)
//                if (selectedItems.size > maxSelectedItems) {
//                    val removeItem = selectedItems.removeAt(0)
//                    removeItem.isSelected.set(false)
//                    onOverMaxSelectedItems()
//                }
//            } else {
//                selectedItems.remove(itemViewModel)
//            }
//
//            onSelectedChanged(selectedItems)
        }

        itemViewHolder.setItemClickListener(object : ItemClickListener {
            override fun onClick(view: View, position: Int) {
                if (selectedItems.size == minSelectedItems && itemViewModel in selectedItems) {
                    return
                }

                val selected = itemViewModel.isSelected.get()
                selected?.let {
                    itemViewModel.isSelected.set(!it)

                    // todo this code come from fun onSelectedChanged
                    if (!it) {
                        selectedItems.add(itemViewModel)
                        if (selectedItems.size > maxSelectedItems) {
                            val removeItem = selectedItems.removeAt(0)
                            removeItem.isSelected.set(false)
                            onOverMaxSelectedItems()
                        }
                    } else {
                        selectedItems.remove(itemViewModel)
                    }

                    onSelectedChanged(selectedItems)
                }

                itemClickListener?.onClick(view, position)
            }

        })
        itemViewHolder.setItemLongClickListener(itemLongClickListener)
    }

    override fun getItemCount(): Int {
        return itemsSource.size
    }

    /**
     * internal constructor for item view holder
     * @param binder view-data-binding
     */
    class ItemViewHolder<IVM : BaseItemViewModel> internal constructor(var binder: ViewDataBinding) :
        RecyclerView.ViewHolder(binder.root), View.OnClickListener, View.OnLongClickListener {

        var viewModel: IVM? = null
        private var itemClickListener: ItemClickListener? = null
        private var itemLongClickListener: ItemLongClickListener? = null

        init {
            binder.root.setOnClickListener(this)
            binder.root.setOnLongClickListener(this)
        }

        /**
         * Set item click listener for each item on adapter
         * @param itemClickListener listener
         */
        fun setItemClickListener(itemClickListener: ItemClickListener?) {
            this.itemClickListener = itemClickListener
        }

        /**
         * Set item long click listener for each item on adapter
         * @param itemLongClickListener listener for long click
         */
        fun setItemLongClickListener(itemLongClickListener: ItemLongClickListener?) {
            this.itemLongClickListener = itemLongClickListener
        }

        override fun onClick(v: View) {
            itemClickListener?.onClick(v, adapterPosition)
        }

        override fun onLongClick(v: View): Boolean {
            itemLongClickListener?.apply {
                onLongClick(v, adapterPosition)
                return true
            }
            return false
        }
    }

    /**
     * An interface for item click listener
     */
    interface ItemClickListener {
        /**
         * Function help to trigger click action
         * @param view click on which view
         * @param position index of this view
         */
        fun onClick(view: View, position: Int)
    }

    /**
     * An interface for item long click listener
     */
    interface ItemLongClickListener {
        /**
         * Function help to trigger long click action
         * @param view long click on which view
         * @param position index of this view
         */
        fun onLongClick(view: View, position: Int)
    }
}
