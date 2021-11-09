package com.example.motionlayoutexample.album.trackadapter

import androidx.recyclerview.widget.RecyclerView
import com.example.motionlayoutexample.databinding.TrackItemBinding
import com.example.motionlayoutexample.entities.TrackEntity

class TrackViewHolder(
        private val binding: TrackItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: TrackEntity) {
        with(binding) {
            trackNumber.text = track.id
            trackTime.text = track.trackDuration
            trackName.text = track.trackName
            singer.text = track.singer
            like.isChecked = track.favorite
        }
    }
}