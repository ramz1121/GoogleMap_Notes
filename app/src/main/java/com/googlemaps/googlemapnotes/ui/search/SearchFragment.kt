package com.googlemaps.googlemapnotes.ui.search

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.di.component.FragmentComponent
import com.googlemaps.googlemapnotes.ui.base.BaseFragment
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity
import com.googlemaps.googlemapnotes.ui.map.SharedViewModel
import com.googlemaps.googlemapnotes.ui.search.adapter.SearchAdapter
import com.googlemaps.googlemapnotes.utils.display.Toaster
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject


class SearchFragment : BaseFragment<SharedViewModel>() {
    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var searchAdapter: SearchAdapter

    private val userName = "user"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.getNoteList(userName)
        pb_loading.visibility = View.VISIBLE
        viewModel.getNoteList.observe(getViewLifecycleOwner(), Observer {
            searchAdapter.clear()
            it?.run { searchAdapter.appendData(this) }
            pb_loading.visibility = View.GONE
        })

        viewModel.userError.observe(getViewLifecycleOwner(), Observer {
            Toaster.show(this.context!!, it)
            pb_loading.visibility = View.GONE
        })
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }


    override fun setupView(view: View) {
        view.sp_search_options.adapter = ArrayAdapter.createFromResource(
            activity!!,
            R.array.search_options,
            android.R.layout.simple_dropdown_item_1line
        )
        search_recyclerview.layoutManager = linearLayoutManager
        search_recyclerview.adapter = searchAdapter

        bt_search_button.setOnClickListener {
            pb_loading.visibility = View.VISIBLE
            searchAdapter.clear()
            viewModel.searchNotes(
                et_search_text.text.toString(),
                sp_search_options.selectedItemPosition, "user"

            )

        }

    }

    override fun setupObservers() {
        super.setupObservers()

    }

    override fun onResume() {
        super.onResume()
    }


}