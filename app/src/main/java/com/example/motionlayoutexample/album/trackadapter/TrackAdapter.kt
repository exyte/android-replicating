package com.example.motionlayoutexample.album.trackadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.motionlayoutexample.databinding.TrackItemBinding
import com.example.motionlayoutexample.entities.TrackEntity

class TrackAdapter : ListAdapter<TrackEntity, TrackViewHolder>(TrackDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TrackItemBinding.inflate(layoutInflater, parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object TrackDiffUtil : DiffUtil.ItemCallback<TrackEntity>() {
        override fun areItemsTheSame(oldItem: TrackEntity, newItem: TrackEntity): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TrackEntity, newItem: TrackEntity): Boolean =
                oldItem == newItem

    }
}