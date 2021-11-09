package com.example.motionlayoutexample.mainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.motionlayoutexample.*
import com.example.motionlayoutexample.custom.CommentListLayout
import com.example.motionlayoutexample.custom.PullingView
import com.example.motionlayoutexample.databinding.AlbumItemBinding
import com.example.motionlayoutexample.databinding.MainSingerFragmentBinding
import com.example.motionlayoutexample.databinding.ToolCommentsItemBinding
import com.example.motionlayoutexample.entities.AlbumEntity
import com.example.motionlayoutexample.mainscreen.albumadapter.AlbumAdapter
import com.example.motionlayoutexample.mainscreen.commentspageadapter.CommentPageAdapter
import com.example.motionlayoutexample.mainscreen.music_things.MusicManager
import com.example.motionlayoutexample.mainscreen.music_things.TimeData
import kotlin.system.exitProcess


class MainSingerFragment : Fragment() {

    private lateinit var binding: MainSingerFragmentBinding

    companion object {
        const val ALBUM_TRANSITION_NAME = "imageViewCard"
        const val DEFAULT_PULL_SPEED = 1000f
        const val INITIAL_SCALE = 1f
        const val SCALE_DIVIDER = 4
        const val END_MOTION_PROGRESS = 0.9f
    }

    private lateinit var toolView: ConstraintLayout
    private lateinit var commentListLayout: CommentListLayout
    private lateinit var musicManager: MusicManager
    private var motionProgress: Float = -1f
    private lateinit var commentPageAdapter: CommentPageAdapter
    private var bottomInset = 0
    private var topInset = 0
    var textAnimationRequired = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainSingerFragmentBinding.inflate(layoutInflater)
        setInsets()
        setMotionProgress()
        setupCommentToolView()
        setupAlbumRv()
        setupCommentsPages()
        bindButtons()
        addTransitionListener()
        setupContent()
        setupPlayer()
        setPullView()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.root.progress == 0f) {
                exitProcess(0)
            } else {
                binding.root.transitionToStart()
            }
        }
        return binding.root
    }

    private fun setMotionProgress() {
        if (motionProgress != -1f) {
            binding.root.progress = motionProgress
        }
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (apiIsSmallerThanTheAndroidR()) {
                @Suppress("DEPRECATION")
                bottomInset = windowInsets.systemWindowInsetBottom
                @Suppress("DEPRECATION")
                topInset = windowInsets.systemWindowInsetTop
            } else {
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                bottomInset = insets.bottom
                topInset = insets.top
            }
                binding.albumList.navLayout.updatePadding(top = topInset)
                binding.musicInfo.navLayout.updatePadding(top = topInset)
                binding.root.requestLayout()
            commentPageAdapter.updateItemsPadding(bottomInset)

            windowInsets
        }
    }

    private fun setPullView() {
        binding.pullingView.registerListener(object : PullingView.OnStateChange {
            override fun onPositionChange(x: Int) {
                if(x>=0){
                    binding.root.progress = x / DEFAULT_PULL_SPEED
                }
            }

            override fun onFingerRelease(x: Float) {
                if (x > binding.root.width / 2) {
                    binding.root.transitionToEnd()
                } else {
                    binding.root.transitionToStart()
                }
            }
        }
        )
    }

    private fun setupPlayer() {
        binding.musicInfo.player.playButtonSize = resources.getDimensionPixelSize(R.dimen.button_play_size)
        musicManager = MusicManager(binding.musicInfo.player, viewLifecycleOwner.lifecycleScope)
        musicManager.registerListener(object : TimeData {
            override fun newTimeData(startTime: String, endTime: String) {
                binding.musicInfo.startTime.text = startTime
                binding.musicInfo.endTime.text = endTime
            }
        })
    }


    private fun setupAlbumRv() {
        val adapter = AlbumAdapter()
        adapter.artistSelectedListener = object : AlbumAdapter.ArtistSelectedListener {
            override fun onArtistSelected(artist: AlbumEntity, albumItemBinding: AlbumItemBinding) {
                navigateToAlbumPlayList(artist, albumItemBinding)
            }
        }
        binding.albumList.albumRv.adapter = adapter
        adapter.submitList(albumList)
    }

    private fun navigateToAlbumPlayList(artist: AlbumEntity, binding: AlbumItemBinding) {
        val extras = FragmentNavigatorExtras(
                binding.albumImage to ALBUM_TRANSITION_NAME
        )
        binding.albumImage.transitionName = ALBUM_TRANSITION_NAME
        val action = MainSingerFragmentDirections.actionStartFragmentToBlankFragment(
                uri = artist.id,
                bottomInset = bottomInset,
                topInset = topInset
        )
        findNavController().navigate(action, extras)
    }

    private fun addTransitionListener() {
        binding.root.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                if (positionIsInAlbumAndCommentScreen()) {
                    binding.musicInfo.musicianName.readyForAnimation()
                    textAnimationRequired = true
                }
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                scaleImage(progress)
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (binding.root.progress <= 0f && textAnimationRequired) {
                    binding.musicInfo.musicianName.playAnimation()
                    textAnimationRequired = false
                }
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }

    private fun scaleImage(progress: Float){
        binding.portraitImage.scaleX = INITIAL_SCALE + progress / SCALE_DIVIDER
        binding.portraitImage.scaleY = INITIAL_SCALE + progress / SCALE_DIVIDER
    }

    private fun positionIsInAlbumAndCommentScreen() = binding.root.progress > END_MOTION_PROGRESS

    private fun setupCommentToolView() {
        commentListLayout = binding.commentList.commentListLayout
        val bindingTools = ToolCommentsItemBinding.inflate(layoutInflater)
        toolView = bindingTools.root
        commentListLayout.setLayout(toolView)
    }

    private fun bindButtons() {
        binding.pullingView.setOnClickListener {
            binding.root.transitionToEnd()
        }
        binding.societyInfo.addComment.setOnClickListener {
            //add comment
        }
        binding.musicInfo.root.setOnClickListener {
            //go to musician page
        }
    }

    private fun setupContent() {
        binding.albumQuantityInfo.albumsQuantity.text = albumQuantity
        binding.albumQuantityInfo.albumsQuantityBig.text = albumQuantity

        binding.societyInfo.firstCommentImage.setImageResource(R.drawable.human_photo5)
        binding.societyInfo.secondCommentImage.setImageResource(R.drawable.human_photo6)
        binding.societyInfo.followersNumber.text = followersNumber
        binding.societyInfo.followingNumber.text = followingNumber
        binding.societyInfo.commentsNumber.text = commentNumber

        binding.musicInfo.musicianName.text = musicianName
        binding.musicInfo.musicianInfo.text = musicianInfo
    }

    private fun setupCommentsPages() {
        commentPageAdapter = CommentPageAdapter(commentListLayout::clickOnSection, commentListLayout::hideToolView)
        binding.commentList.commentsPages.adapter = commentPageAdapter

        binding.commentList.comments.setOnClickListener {
            binding.commentList.commentsPages.currentItem = 0
        }
        binding.commentList.popular.setOnClickListener {
            binding.commentList.commentsPages.currentItem = 1
        }
        binding.commentList.commentsPages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                commentListLayout.hideToolView(false)
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1) {
                    binding.commentList.popular.isChecked = true
                } else {
                    binding.commentList.comments.isChecked = true
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        motionProgress = binding.root.progress
        musicManager.endSession()
    }
}