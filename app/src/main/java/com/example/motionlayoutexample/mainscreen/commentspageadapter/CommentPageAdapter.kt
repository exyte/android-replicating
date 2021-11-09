package com.example.motionlayoutexample.mainscreen.commentspageadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motionlayoutexample.custom.CommentListLayout
import com.example.motionlayoutexample.databinding.CommentsListItemBinding
import com.example.motionlayoutexample.entities.CommentEntity


class CommentPageAdapter(
        private val clickOnSection: (item: CommentEntity, originalPos: CommentListLayout.ToolSizes) -> Unit,
        private val onScroll: () -> Unit,
) : RecyclerView.Adapter<CommentsPageViewHolder>() {

    companion object {
        const val PAGE_QUANTITY = 2
    }

    private val holderList = mutableListOf<CommentsPageViewHolder>()

    fun updateItemsPadding(padding: Int) {
        holderList.forEach {
            it.setNewPadding(padding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsPageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CommentsListItemBinding.inflate(layoutInflater, parent, false)
        binding.albumRv.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        onScroll()
                    }
                }
        )
        val holder = CommentsPageViewHolder(binding, clickOnSection)
        holderList.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: CommentsPageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = PAGE_QUANTITY
}