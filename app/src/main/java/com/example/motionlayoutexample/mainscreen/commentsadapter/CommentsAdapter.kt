package com.example.motionlayoutexample.mainscreen.commentsadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.motionlayoutexample.custom.CommentListLayout
import com.example.motionlayoutexample.databinding.CommentsItemBinding
import com.example.motionlayoutexample.entities.CommentEntity

class CommentsAdapter(
        private val clickOnSection: (item: CommentEntity, originalPos: CommentListLayout.ToolSizes) -> Unit,
) : ListAdapter<CommentEntity, CommentsViewHolder>(CommentsDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CommentsItemBinding.inflate(layoutInflater, parent, false)
        return CommentsViewHolder(binding, clickOnSection)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object CommentsDiffUtil : DiffUtil.ItemCallback<CommentEntity>() {
        override fun areItemsTheSame(oldItem: CommentEntity, newItem: CommentEntity): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CommentEntity, newItem: CommentEntity): Boolean =
                oldItem == newItem

    }
}