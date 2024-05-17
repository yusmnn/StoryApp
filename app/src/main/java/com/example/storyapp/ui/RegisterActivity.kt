package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.database.SsPreferences
import com.example.storyapp.data.remote.response.DataLogin
import com.example.storyapp.data.remote.response.DataRegister
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.UserViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        animateObject()
        onClicked()

        val ssPref = SsPreferences.getInstance(dataStore)
        val userViewModel = ViewModelProvider(this, ViewModelFactory(ssPref))[UserViewModel::class.java]
        userViewModel.getLogin().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginViewModel.message.observe(this) { msg ->
            registerResponse(loginViewModel.isError, msg)
        }

        loginViewModel.message.observe(this) { msg ->
            loginResponse(loginViewModel.isError, msg, userViewModel)
        }

        loginViewModel.loading.observe(this) {
            onLoading(it)
        }
    }

    private fun registerResponse(error: Boolean, msg: String) {
        if (!error) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = DataLogin(
                binding.edRegisterEmail.text.toString(),
                binding.cvPassword.text.toString()
            )
            loginViewModel.getLoginResponse(user)
        } else {
            binding.edRegisterEmail.setErrorMsg(resources.getString(R.string.emailTaken), binding.edRegisterEmail.text.toString())
            Toast.makeText(this, resources.getString(R.string.emailTaken), Toast.LENGTH_SHORT).show()
        }
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

    private fun onClicked() {

        binding.seePass.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cvPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.edConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.cvPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.edConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            binding.cvPassword.text?.let { binding.cvPassword.setSelection(it.length) }
            binding.edConfirmPassword.text?.let { binding.edConfirmPassword.setSelection(it.length) }
        }

        binding.btnRegister.setOnClickListener {
            binding.apply {
                edRegisterName.clearFocus()
                edRegisterEmail.clearFocus()
                cvPassword.clearFocus()
                edConfirmPassword.clearFocus()
            }

            if (binding.edRegisterName.nameValid && binding.edRegisterEmail.emailValid && binding.cvPassword.passValid && binding.edConfirmPassword.samePassValid) {
                val dataRegisterAccount = DataRegister(
                    name = binding.edRegisterName.text.toString().trim(),
                    email = binding.edRegisterEmail.text.toString().trim(),
                    password = binding.cvPassword.text.toString().trim()
                )
                loginViewModel.getRegisterResponse(dataRegisterAccount)
            } else {
                if (!binding.edRegisterName.nameValid) binding.edRegisterName.error = resources.getString(R.string.noName)
                if (!binding.edRegisterEmail.emailValid) binding.edRegisterEmail.error = resources.getString(R.string.noEmail)
                if (!binding.cvPassword.passValid) binding.cvPassword.error = resources.getString(R.string.noPass)
                if (!binding.edConfirmPassword.samePassValid) binding.edConfirmPassword.error = resources.getString(R.string.noSamePass)
                Toast.makeText(this, "Data yang dimasukkan tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun animateObject() {
        ObjectAnimator.ofFloat(binding.onboardingImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edRegisterName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 0f, 1f).setDuration(300)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 0f, 1f).setDuration(300)
        val cvPass = ObjectAnimator.ofFloat(binding.cvPassword, View.ALPHA, 0f, 1f).setDuration(300)
        val edConfirmPassword = ObjectAnimator.ofFloat(binding.edConfirmPassword, View.ALPHA, 0f, 1f).setDuration(300)
        val seePass = ObjectAnimator.ofFloat(binding.seePass, View.ALPHA, 0f, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f).setDuration(300)
        val txtBackLogin = ObjectAnimator.ofFloat(binding.txtBackLogin, View.ALPHA, 0f, 1f).setDuration(300)
        val btnBackLogin = ObjectAnimator.ofFloat(binding.btnBackLogin, View.ALPHA, 0f, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(edRegisterName, edRegisterEmail, cvPass, edConfirmPassword, seePass, btnRegister, txtBackLogin, btnBackLogin)
            start()
        }
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

}