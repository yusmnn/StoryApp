package com.example.storyapp.view.ui

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
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.database.SsPreferences
import com.example.storyapp.data.remote.response.DataLogin
import com.example.storyapp.data.remote.response.ListStoryDetail
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.adapter.ListStoryAdapter
import com.example.storyapp.viewmodel.DataStoreViewModel
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.MainViewModelFactory
import com.example.storyapp.viewmodel.ViewModelFactory



val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        animateObject()
        onClicked()

        val pref = SsPreferences.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLogin().observe(this) { loginTrue ->
            if (loginTrue) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.message.observe(this){ message ->
            loginResponse(message, dataStoreViewModel)
        }

        viewModel.isLoading.observe(this) {
            onLoading(it)
        }
    }

    private fun onClicked() {
        binding.txtBtnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

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
                viewModel.login(requestLogin)
            } else {
                if (!binding.edLoginEmail.emailValid) binding.edLoginEmail.error =
                    getString(R.string.noEmail)
                if (!binding.cvPassword.passValid) binding.cvPassword.error =
                    getString(R.string.noPass)

                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
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

    private fun loginResponse(msg: String, dataStoreViewModel: DataStoreViewModel) {
        if (msg.contains("Hi")) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = viewModel.login.value
            dataStoreViewModel.saveLogin(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<ListStoryDetail>(EXTRA_DATA) as ListStoryDetail
        setStory(detailStory)
    }

    private fun setStory(detailStory: ListStoryDetail) {
        Glide.with(this)
            .load(detailStory.photoUrl)
            .into(binding.imgDetail)

        binding.apply {
            nameDetail.text = detailStory.name
            descDetail.text = detailStory.description
            dateDetail.text = ListStoryAdapter.dateToString(detailStory.createdAt.toString())
        }
    }

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
    }
}