package com.googlemaps.googlemapnotes.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.di.component.ActivityComponent
import com.googlemaps.googlemapnotes.ui.base.BaseActivity
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity
import com.googlemaps.googlemapnotes.utils.common.Status
import com.googlemaps.googlemapnotes.utils.display.Toaster
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity<SignUpViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_signup
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {

        username_edt_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onUsernameChange(s.toString())
            }

        })

        email_edt_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEmailChange(s.toString())
            }

        })

        pass_edt_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPasswordChange(s.toString())
            }

        })

        signup_btn.setOnClickListener {
            viewModel.SignUp()
        }


    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.statusMessage.observe(this, Observer {
            it.getIfNotHandled()?.let {
                finish()
                Toaster.show(this, it)
                startActivity(Intent(applicationContext, GoogleMapActivity::class.java))
                finish()
            }
        })

          viewModel.usernameField.observe(this, Observer {
              if (username_edt_text.text.toString() != it) username_edt_text.setText(it)
          })

          viewModel.usernameValidation.observe(this, Observer {
              when (it.status) {
                  Status.ERROR -> username_edt_text.error = it.data?.run { getString(this) }
                  else -> layout_username.isErrorEnabled = false
              }
          })

        viewModel.emailField.observe(this, Observer {
            if (email_edt_text.text.toString() != it) email_edt_text.setText(it)
        })

        viewModel.emailValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> email_edt_text.error = it.data?.run { getString(this) }
                else -> layout_email.isErrorEnabled = false
            }
        })

        viewModel.passwordField.observe(this, Observer {
            if (pass_edt_text.text.toString() != it) email_edt_text.setText(it)
        })

        viewModel.passwordValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> layout_password.error = it.data?.run { getString(this) }
                else -> layout_password.isErrorEnabled = false
            }
        })
    }
}


