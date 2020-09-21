package com.googlemaps.googlemapnotes.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.googlemaps.googlemapnotes.R
import com.googlemaps.googlemapnotes.di.component.ActivityComponent
import com.googlemaps.googlemapnotes.ui.base.BaseActivity
import com.googlemaps.googlemapnotes.ui.map.GoogleMapActivity
import com.googlemaps.googlemapnotes.ui.signup.SignUpViewModel
import com.googlemaps.googlemapnotes.utils.common.Status
import com.googlemaps.googlemapnotes.utils.display.Toaster
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.signup_btn

class LoginActivity : BaseActivity<LoginViewModel>() {
    private lateinit var auth: FirebaseAuth

    override fun provideLayoutId(): Int = R.layout.activity_login
    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()



        email_edt_text_login.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEmailChange(s.toString())
            }

        })

        pass_edt_text_login.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPasswordChange(s.toString())
            }

        })


        login_btn.setOnClickListener {
            viewModel.login()
        }

    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.statusMessage.observe(this, Observer {
            it.getIfNotHandled()?.let {
                finish()
                Toaster.show(this, it)
                startActivity(Intent(applicationContext, GoogleMapActivity::class.java))

            }
        })

        /*  viewModel.usernameField.observe(this, Observer {
              if (username_edt_text.text.toString() != it) username_edt_text.setText(it)
          })

          viewModel.usernameValidation.observe(this, Observer {
              when (it.status) {
                  Status.ERROR -> username_edt_text.error = it.data?.run { getString(this) }
                  else -> layout_username.isErrorEnabled = false
              }
          })*/

        viewModel.emailField.observe(this, Observer {
            if (email_edt_text_login.text.toString() != it) email_edt_text.setText(it)
        })

        viewModel.emailValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> layout_email_login.error = it.data?.run { getString(this) }
                else -> layout_email_login.isErrorEnabled = false
            }
        })

        viewModel.passwordField.observe(this, Observer {
            if (pass_edt_text_login.text.toString() != it) email_edt_text.setText(it)
        })

        viewModel.passwordValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> layout_password_login.error = it.data?.run { getString(this) }
                else -> layout_password_login.isErrorEnabled = false
            }
        })
    }
}


