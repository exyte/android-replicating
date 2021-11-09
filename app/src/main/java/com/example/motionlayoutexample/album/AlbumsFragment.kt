package com.example.motionlayoutexample.album

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.*
import com.example.motionlayoutexample.R
import com.example.motionlayoutexample.album.trackadapter.TrackAdapter
import com.example.motionlayoutexample.albumList
import com.example.motionlayoutexample.custom.CornersTransition
import com.example.motionlayoutexample.databinding.AlbumPlaylistBinding
import com.example.motionlayoutexample.entities.AlbumEntity
import com.example.motionlayoutexample.singer
import com.example.motionlayoutexample.tracks


class AlbumsFragment : Fragment() {

    private lateinit var binding: AlbumPlaylistBinding

    companion object {
        const val ANIM_DURATION = 300L
    }

    private var albumEntity: AlbumEntity? = null
    private val destinationArgs: AlbumsFragmentArgs by navArgs()
    private var isMusicPlaying = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AlbumPlaylistBinding.inflate(layoutInflater)
        setInsets()
        setButtonImage()
        getIdFromNav()
        initTrackRv()
        setContent()
        bindButtons()
        addTransitionListener()
        setEnterAnimation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.requestApplyInsets(view)
    }

    private fun setEnterAnimation() {
        val transition = CornersTransition()
        transition.setValues(calculateStartCorner(), calculateEndCorner())
        transition.addTarget(binding.albumImage)
        transition.duration = ANIM_DURATION

        val changeBounds = ChangeBounds()
        changeBounds.duration = ANIM_DURATION

        val changeImageTransform = ChangeImageTransform()
        changeImageTransform.duration = ANIM_DURATION

        val changeTransform = ChangeTransform()
        changeTransform.duration = ANIM_DURATION

        val transitionSet = TransitionSet()
        transitionSet.addTransition(transition)
        transitionSet.addTransition(changeBounds)
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(changeImageTransform)
        transitionSet.addTransition(changeTransform)
        transitionSet.ordering = TransitionSet.ORDERING_TOGETHER
        sharedElementEnterTransition = transitionSet
    }

    private fun calculateStartCorner(): Float =
            TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    resources.getDimension(R.dimen.corner_size_album_item),
                    resources.displayMetrics
            )

    private fun calculateEndCorner() =
            getScreenWidth() * 0.3f

    private fun getScreenWidth(): Int {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds: Rect = windowMetrics.bounds
            bounds.width()
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun getIdFromNav() {
        val id = destinationArgs.uri.toString()
        albumEntity = albumList.find {
            it.id == id
        }
        albumEntity?.imageUrl?.let { binding.albumImage.setImageResource(it) }
    }

    private fun setInsets() {
        binding.navLayout.updatePadding(top = destinationArgs.topInset)
        binding.playerButtonsLayout.updatePadding(bottom = destinationArgs.bottomInset)
    }

    private fun addTransitionListener() {
        binding.root.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                val height = binding.albumImage.height
                val shapeAppearanceModel = binding.albumImage.shapeAppearanceModel.toBuilder()
                        .setAllCornerSizes((height / 2).toFloat())
                        .build()
                binding.albumImage.shapeAppearanceModel = shapeAppearanceModel
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                val height = binding.albumImage.height
                val shapeAppearanceModel = binding.albumImage.shapeAppearanceModel.toBuilder()
                        .setAllCornerSizes((height / 2).toFloat())
                        .build()
                binding.albumImage.shapeAppearanceModel = shapeAppearanceModel
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        }
        )
    }

    private fun bindButtons() {
        binding.arrowBackAlbumList.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.playButton.setOnClickListener {
            isMusicPlaying = !isMusicPlaying
            setButtonImage()
        }
    }

    private fun setButtonImage() {
        val play = binding.playButton
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.setSize(
                resources.getDimensionPixelSize(R.dimen.button_play_size),
                resources.getDimensionPixelSize(R.dimen.button_play_size))
        play.background = shape
        val typedValue = TypedValue()
        val theme = requireContext().theme

        if (isMusicPlaying) {
            play.setImageResource(R.drawable.ic_pause_big)
            theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
            @ColorInt val primaryColorVariant = typedValue.data
            shape.setColor(primaryColorVariant)
        } else {
            play.setImageResource(R.drawable.ic_play_arrow_big)
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            @ColorInt val primaryColor = typedValue.data
            shape.setColor(primaryColor)
        }
    }

    private fun setContent() {
        binding.singer.text = singer
        binding.albumName.text = albumEntity?.albumName
    }

    private fun initTrackRv() {
        val trackAdapter = TrackAdapter()
        binding.trackRv.adapter = trackAdapter
        trackAdapter.submitList(tracks)
    }
}
