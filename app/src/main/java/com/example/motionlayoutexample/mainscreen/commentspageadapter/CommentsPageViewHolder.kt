package com.example.motionlayoutexample.mainscreen.commentspageadapter

import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.motionlayoutexample.comments
import com.example.motionlayoutexample.custom.CommentListLayout
import com.example.motionlayoutexample.databinding.CommentsListItemBinding
import com.example.motionlayoutexample.entities.CommentEntity
import com.example.motionlayoutexample.mainscreen.commentsadapter.CommentsAdapter
import com.example.motionlayoutexample.popularComments

class CommentsPageViewHolder(
        private val binding: CommentsListItemBinding,
        private val clickOnSection:
        (item: CommentEntity, originalPos: CommentListLayout.ToolSizes) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun setNewPadding(padding: Int) {
        binding.albumRv.updatePadding(bottom = padding)
    }

    fun bind(position: Int) {
        val adapter = CommentsAdapter(clickOnSection)
        binding.albumRv.adapter = adapter
        if (position == 0) {
            adapter.submitList(popularComments)
        } else {
            adapter.submitList(comments)
        }
    }
}