package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.StoryDetailResponse
import com.example.storyapp.databinding.ItemRowStoryBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListStoryAdapter(private val storyList: List<StoryDetailResponse>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    companion object {
        @JvmStatic
        fun dateToString(stringDate: String): String {
            val formatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val formatOutput = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault())
            val date: Date?
            var dateOutput = ""
            try {
                date = formatInput.parse(stringDate)
                dateOutput = formatOutput.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dateOutput
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryDetailResponse)
    }

    class ListViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryDetailResponse, onItemClickCallback: OnItemClickCallback) {
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.imgStory)
            binding.titleStory.text = story.name
            binding.descStory.text = story.description
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(storyList[position], onItemClickCallback)
    }

    override fun getItemCount(): Int = storyList.size
}
