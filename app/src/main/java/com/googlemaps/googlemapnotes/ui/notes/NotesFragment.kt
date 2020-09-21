package com.googlemaps.googlemapnotes.ui.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.di.component.FragmentComponent
import com.googlemaps.googlemapnotes.ui.base.BaseFragment
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity.Companion.CHECK_NOTE
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity.Companion.DISPLAY_LOCATION
import com.googlemaps.googlemapnotes.ui.map.SharedViewModel
import com.googlemaps.googlemapnotes.utils.display.Toaster
import kotlinx.android.synthetic.main.fragment_add_note.*

class NotesFragment : BaseFragment<SharedViewModel>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        viewModel.currentLocation.observe(getViewLifecycleOwner(), Observer {
            currentLocation.setText(it)
        })
        viewModel.locationLatLong.observe(getViewLifecycleOwner(), Observer {
            viewModel.onLatlongChange(it)
        })
        viewModel.getNoteSaved.observe(getViewLifecycleOwner(), Observer {
            val broadcastManager = LocalBroadcastManager.getInstance(this.context!!)
            val intent = Intent(DISPLAY_LOCATION)
            intent.apply {
                putExtra(CHECK_NOTE, it)
            }
            broadcastManager.sendBroadcast(intent)
        })
        viewModel.statusMessage.observe(getViewLifecycleOwner(), Observer {
            it.getIfNotHandled()?.let {
                Toaster.show(this.context!!, it)
                note.text.clear()
            }
            val hideKeyboard =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideKeyboard.hideSoftInputFromWindow(note.windowToken, 0)

        })
    }

    override fun provideLayoutId(): Int = R.layout.fragment_add_note

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupView(view: View) {
        note.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                add.isEnabled = !s.isNullOrEmpty()
                viewModel.onNotesChange(s.toString())
            }

        })


        add.setOnClickListener {
            val text = note.text.toString()
            if (text.isNotEmpty()) {
                viewModel.addNote()
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()

    }

    override fun onDetach() {
        super.onDetach()
        view.let {

        }
    }


}

