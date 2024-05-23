package com.example.storyapp.viewmodel

import com.example.storyapp.data.remote.response.ListStoryDetail


object DataDummy {
    fun generateDummyNewStories(): List<ListStoryDetail> {
        val storyList = ArrayList<ListStoryDetail>()
        for (i in 0..5) {
            val stories = ListStoryDetail(
                "Title $i",
                "This is a Name",
                "This is a Description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2024-05-22T22:22:22Z",
                null,
                null,
            )
            storyList.add(stories)
        }
        return storyList
    }
}