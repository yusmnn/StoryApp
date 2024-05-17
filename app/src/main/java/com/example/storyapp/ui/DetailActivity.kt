package com.example.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.data.remote.response.StoryDetailResponse
import com.example.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<StoryDetailResponse>(EXTRA_DATA) as StoryDetailResponse
        setStory(detailStory)
    }

    private fun setStory(detailStory: StoryDetailResponse) {
        Glide.with(this)
            .load(detailStory.photoUrl)
            .into(binding.imgDetail)

        binding.apply {
            nameDetail.text = detailStory.name
            descDetail.text = detailStory.description
            dateDetail.text = ListStoryAdapter.dateToString(detailStory.createdAt)
        }
    }

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
    }
}