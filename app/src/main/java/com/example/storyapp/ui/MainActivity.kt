package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.data.database.SsPreferences
import com.example.storyapp.data.remote.response.StoryDetailResponse
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.UserViewModel
import com.example.storyapp.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var token: String
    private lateinit var preferences : SsPreferences
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicked()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)
        preferences = SsPreferences.getInstance(dataStore)

        binding.pullRefresh.setOnRefreshListener {
            loadData()
        }

        loadData()

        mainViewModel.loading.observe(this) {
            onLoading(it)
        }

        mainViewModel.message.observe(this) {
            setDataStory(mainViewModel.story)
            showToast(it)
        }
    }

    private fun loadData() {
        val userViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[UserViewModel::class.java]
        userViewModel.getToken().observe(this) { user ->
            token = user
            mainViewModel.getStory(token)
        }
    }

    private fun showToast(msg: String) {
        if (mainViewModel.isError) {
            Toast.makeText(this, "${getString(R.string.error_load)} $msg", Toast.LENGTH_LONG).show()
        }
    }

    private fun onClicked() {
        binding.btnFloating.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setDataStory(story: List<StoryDetailResponse>) {
        val adapter = ListStoryAdapter(story)
        binding.rvStories.adapter = adapter
        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryDetailResponse) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }

    private fun onLoading(isLoading: Boolean) {
        binding.pullRefresh.isRefreshing = isLoading
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.languange -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                val loginViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[UserViewModel::class.java]
                loginViewModel.clearLogin()
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            } else -> onOptionsItemSelected(item)
        }
    }
}