package com.example.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryDetail
import com.example.storyapp.databinding.ItemRowStoryBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ListStoryAdapter: PagingDataAdapter<ListStoryDetail, ListStoryAdapter.ListViewHolder>
    (StoryDetailDiffCallback()) {

    companion object {
        @JvmStatic
        fun dateToString(stringDate: String): String {
            val formatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val formatOutput = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
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
        fun onItemClicked(data: ListStoryDetail)
    }

    class ListViewHolder(private var binding: ItemRowStoryBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(data: ListStoryDetail) {
            binding.titleStory.text = data.name
            binding.descStory.text = data.description
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.imgStory)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            getItem(position)?.let { it1 -> onItemClickCallback.onItemClicked(it1) }
        }
    }
    class StoryDetailDiffCallback : DiffUtil.ItemCallback<ListStoryDetail>() {
        override fun areItemsTheSame(oldItem: ListStoryDetail, newItem: ListStoryDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryDetail, newItem: ListStoryDetail): Boolean {
            return oldItem == newItem
        }
    }
}
