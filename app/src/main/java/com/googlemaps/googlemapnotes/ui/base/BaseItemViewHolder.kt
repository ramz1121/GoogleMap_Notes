package com.googlemaps.googlemapnotes.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.googlemaps.googlemapnotes.MapApplication
import com.googlemaps.googlemapnotes.di.component.DaggerViewHolderComponent
import com.googlemaps.googlemapnotes.di.component.ViewHolderComponent
import com.googlemaps.googlemapnotes.di.module.ViewHolderModule
import com.googlemaps.googlemapnotes.utils.display.Toaster
import javax.inject.Inject
// making baseviewmodel lifecycle aware
//disadvatages without lifecycle aware:
// if u have a like button on itemviewholder and if u want to make a network call on click of that, it may have to
// go through all the process(viewholder-->adapter-->activity-->viewmodel-->networkcall) to update the Ui
// so activity is get invovled in the process which is not a good architecture
/*principle:
* do not update view if it is not visible
* lifecycle concious way
* each view should be independent inside the viewholder
* to do this we have itemviewModel, each view in the itemviewHolder will have
 itemviewModel and do network call independently
* we use livedata to make it work in a lifecycle concious way which may not work with viewholder which doesn't have lifecycle
* to make Livedate work with itemvieHolder, we have to provide viewholder with activiy lifecycle
-->RecyclerView.Adapter has call backs like onAttachToRecyclerView,onDettachToRecyclerView,onViewAttachedToWindow
 onDettachFromWindow, we use them to make viewholder lifecycle aware
*use lifecycleregistry class here
*Example: when viewholder is created we set the lifecycle  in the init block as created
*
* */

abstract class BaseItemViewHolder<T : Any, VM : BaseItemViewModel<T>>(
    @LayoutRes layoutId: Int, parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false)),
    LifecycleOwner {

    init {
        onCreate()
    }

    @Inject
    lateinit var viewModel: VM

    @Inject
    lateinit var lifecycleRegistry: LifecycleRegistry

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    open fun bind(data: T) {
        viewModel.updateData(data)
    }

    protected fun onCreate() {
        injectDependencies(buildViewHolderComponent())
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        setupObservers()
        setupView(itemView)
    }

    fun onStart() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    fun onStop() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    fun onDestroy() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    private fun buildViewHolderComponent() =
        DaggerViewHolderComponent
            .builder()
            .applicationComponent((itemView.context.applicationContext as MapApplication).applicationComponent)
            .viewHolderModule(ViewHolderModule(this))
            .build()

    fun showMessage(message: String) = Toaster.show(itemView.context, message)

    fun showMessage(@StringRes resId: Int) = showMessage(itemView.context.getString(resId))

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })

        viewModel.messageStringId.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
    }

    protected abstract fun injectDependencies(viewHolderComponent: ViewHolderComponent)

    abstract fun setupView(view: View)

}