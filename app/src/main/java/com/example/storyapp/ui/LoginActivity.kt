package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.database.SsPreferences
import com.example.storyapp.data.remote.response.DataLogin
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.UserViewModel
import com.example.storyapp.viewmodel.ViewModelFactory


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        animateObject()
        onClicked()

        val pref = SsPreferences.getInstance(dataStore)
        val userViewModel = ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        userViewModel.getLogin().observe(this) { loginTrue ->
            if (loginTrue) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginViewModel.message.observe(this) { msg ->
            loginResponse(loginViewModel.isError, msg, userViewModel)
        }

        loginViewModel.loading.observe(this) {
            onLoading(it)
        }
    }

    private fun onClicked() {

        binding.seePass.setOnCheckedChangeListener { _, checked ->
            binding.cvPassword.transformationMethod = if (checked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.cvPassword.text?.let { binding.cvPassword.setSelection(it.length) }
        }

        binding.btnLogin.setOnClickListener {
            binding.edLoginEmail.clearFocus()
            binding.cvPassword.clearFocus()

            if (dataValid()) {
                val requestLogin = DataLogin(
                    binding.edLoginEmail.text.toString().trim(),
                    binding.cvPassword.text.toString().trim()
                )
                loginViewModel.getLoginResponse(requestLogin)

            } else {
                if (!binding.edLoginEmail.emailValid) binding.edLoginEmail.error =
                    getString(R.string.noEmail)
                if (!binding.cvPassword.passValid) binding.cvPassword.error =
                    getString(R.string.noPass)

                Toast.makeText(this, "Login Salah", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtBtnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun dataValid(): Boolean {
        return binding.edLoginEmail.emailValid && binding.cvPassword.passValid
    }

    private fun animateObject() {
        ObjectAnimator.ofFloat(binding.onboardingImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edLoginEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(300)
        val cvPass = ObjectAnimator.ofFloat(binding.cvPassword, View.ALPHA, 1f).setDuration(300)
        val seePass = ObjectAnimator.ofFloat(binding.seePass, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val txtBtnRegister = ObjectAnimator.ofFloat(binding.txtBtnRegister, View.ALPHA, 1f).setDuration(300)
        val txtNotRegister = ObjectAnimator.ofFloat(binding.txtNotRegister, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(edLoginEmail, cvPass, seePass, btnLogin, txtBtnRegister, txtNotRegister)
            start()
        }
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun loginResponse(error: Boolean, msg: String, userViewModel: UserViewModel) {
        if (!error) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = loginViewModel.userLogin.value
            userViewModel.saveLogin(true)
            user?.loginResult!!.token.let { userViewModel.saveToken(it) }
            user.loginResult.name.let { userViewModel.saveName(it) }
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}