package com.example.motionlayoutexample.mainscreen.commentsadapter

import androidx.recyclerview.widget.RecyclerView
import com.example.motionlayoutexample.custom.CommentListLayout
import com.example.motionlayoutexample.databinding.CommentsItemBinding
import com.example.motionlayoutexample.entities.CommentEntity

class CommentsViewHolder(
        private var bindig: CommentsItemBinding,
        private val clickOnSection: (item: CommentEntity, originalPos: CommentListLayout.ToolSizes) -> Unit,
) : RecyclerView.ViewHolder(bindig.root) {

    fun bind(item: CommentEntity) {
        with(bindig) {
            albumName.text = item.comment
            musicianName.text = item.authorName
            commentDate.text = item.date
            albumImage.setImageResource(item.imageUrl)
            root.setOnClickListener {
                val originalPos = IntArray(2)
                it.getLocationInWindow(originalPos)

                val toolViewSizes = CommentListLayout.ToolSizes(
                        height = root.height,
                        width = root.width,
                        y = originalPos[1] + albumImage.height / 2,
                        x = originalPos[0] + root.width
                )
                clickOnSection(item, toolViewSizes)
            }
        }

    }
}