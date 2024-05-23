package com.example.storyapp.view.ui

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
import com.example.storyapp.viewmodel.DataStoreViewModel
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.MainViewModelFactory
import com.example.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        animateObject()
        onClicked()

        val pref = SsPreferences.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLogin().observe(this) { loginTrue ->
            if (loginTrue) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.message.observe(this) { msg ->
            registerResponse(msg)
        }

        viewModel.isLoading.observe(this) {
            onLoading(it)
        }

        viewModel.message.observe(this) { msg ->
            loginResponse(msg, dataStoreViewModel)
        }
    }

    private fun registerResponse(msg: String) {
        if (msg == "Berhasil membuat akun") {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = DataLogin(
                binding.edRegisterEmail.text.toString(),
                binding.cvPassword.text.toString()
            )
            viewModel.login(user)
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            binding.edRegisterEmail.setErrorMsg(resources.getString(R.string.emailTaken), binding.edRegisterEmail.text.toString())
            Toast.makeText(this, resources.getString(R.string.emailTaken), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginResponse(
        msg: String,
        dataStoreViewModel: DataStoreViewModel
    ){
        if (msg.contains("Hi!")) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = viewModel.login.value
            dataStoreViewModel.saveLogin(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }


    private fun onClicked() {
        binding.seePass.setOnCheckedChangeListener { _, checked ->
            if (checked) {
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
                viewModel.register(dataRegisterAccount)
            } else {
                if (!binding.edRegisterName.nameValid) binding.edRegisterName.error = resources.getString(R.string.noName)
                if (!binding.edRegisterEmail.emailValid) binding.edRegisterEmail.error = resources.getString(R.string.noEmail)
                if (!binding.cvPassword.passValid) binding.cvPassword.error = resources.getString(R.string.noPass)
                if (!binding.edConfirmPassword.samePassValid) binding.edConfirmPassword.error = resources.getString(R.string.noSamePass)
                Toast.makeText(this,
                    getString(R.string.data_not_valid), Toast.LENGTH_SHORT).show()
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