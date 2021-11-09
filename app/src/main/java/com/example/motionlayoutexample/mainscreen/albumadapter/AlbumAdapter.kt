package com.example.motionlayoutexample.mainscreen.albumadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motionlayoutexample.databinding.AlbumItemBinding
import com.example.motionlayoutexample.entities.AlbumEntity

class AlbumAdapter : ListAdapter<AlbumEntity, AlbumAdapter.AlbumViewHolder>(AlbumsDiffUtil) {
    lateinit var artistSelectedListener: ArtistSelectedListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AlbumItemBinding.inflate(layoutInflater, parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object AlbumsDiffUtil : DiffUtil.ItemCallback<AlbumEntity>() {
        override fun areItemsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean =
                oldItem == newItem

        override fun areContentsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean =
                oldItem.id == newItem.id

    }

    inner class AlbumViewHolder(
            private val binding: AlbumItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlbumEntity) {
            with(binding) {
                albumName.text = item.albumName
                albumYear.text = item.albumYear
                albumImage.setImageResource(item.imageUrl)
                albumImage.setOnClickListener {
                    artistSelectedListener.onArtistSelected(item, binding)
                }
            }

        }
    }

    interface ArtistSelectedListener {
        fun onArtistSelected(artist: AlbumEntity, albumItemBinding: AlbumItemBinding)
    }
}