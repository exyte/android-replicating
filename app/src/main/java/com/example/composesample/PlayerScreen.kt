package com.example.composesample

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.composesample.albums.AlbumsListContainer
import com.example.composesample.albums.WidgetCommentsList
import com.example.composesample.nowplaying.ANIM_DURATION
import com.example.composesample.nowplaying.NowPlayingScreen
import com.example.composesample.nowplaying.SharedElementParams
import com.example.composesample.playercontrol.AlbumInfoContainer
import com.example.composesample.playercontrol.DraggableButton
import com.example.composesample.playercontrol.PlayerControlContainer
import com.example.composesample.playercontrol.SongInfoContainer
import com.example.composesample.ui.theme.PlayerTheme
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

/*
 * Created by Exyte on 11.10.2021.
 */
enum class Screen {
    Player,
    TransitionToComments,
    Comments,
    NowPlaying,
}

enum class ToNowPlaying {
    None, Stable, Forward, Backward,
}

@Composable
fun PlayerScreen(playbackData: PlaybackData = PlaybackData()) = ProvideWindowInsets {
    BoxWithConstraints {
        val isInPreviewMode = LocalPreviewMode.current
        val density = LocalDensity.current
        val maxContentWidth = constraints.maxWidth.toFloat()
        val maxContentHeight = constraints.maxHeight.toFloat()

        val insets = LocalWindowInsets.current
        val bottomInset by derivedStateOf { insets.navigationBars.bottom.toDp(density) }
        val topInset by derivedStateOf { insets.statusBars.top.toDp(density) }

        fun Float.toDp() = this.toDp(density)
        fun Int.toDp() = this.toDp(density)

        val sharedElementTargetSize = 230.dp
        val draggableButtonSize = DpSize(width = 64.dp, height = 48.dp)
        var currentScreen by remember { mutableStateOf(Screen.Player) }
        val backHandlerEnabled by derivedStateOf { currentScreen != Screen.Player }
        var toNowPlayingTransition by remember { mutableStateOf(ToNowPlaying.None) }
        var currentDragOffset by remember { mutableStateOf(0f) }
        var playerControlHeight by remember { mutableStateOf(0) }
        var songInfoHeight by remember { mutableStateOf(0) }
        val fromPlayerControlsToAlbumsListProgress by derivedStateOf {
            if (isInPreviewMode) {
                0f
            } else {
                currentDragOffset / maxContentWidth
            }
        }
        val playerControlOffset by derivedStateOf {
            -(playerControlHeight * fromPlayerControlsToAlbumsListProgress * 1.33f).toDp()
        }
        val songInfoTopPadding by derivedStateOf {
            lerpF(
                playerControlHeight.toFloat(),
                0f,
                (1f - fromPlayerControlsToAlbumsListProgress)
            ).toDp()
        }
        val easing = FastOutLinearInEasing

        val songInfoOffset by derivedStateOf {
            if (fromPlayerControlsToAlbumsListProgress < 0.25f) {
                0.dp
            } else {
                val progress =
                    easing.transform((1f - fromPlayerControlsToAlbumsListProgress) / 0.75f)
                val progress1 =
                    min(1f, easing.transform((1.5f - fromPlayerControlsToAlbumsListProgress)))
                val off = lerpF(
                    0f,
                    playerControlHeight.toFloat(),
                    progress
                )
                val toff = (songInfoHeight - lerpF(
                    0f,
                    songInfoHeight.toFloat(),
                    progress1
                ))
                -(playerControlHeight.toFloat() - off + toff).toDp()
            }
        }
        val albumsInfoSize by derivedStateOf { (maxContentHeight * 0.4f).toDp() }

        val photoScale by derivedStateOf {
            easing.transform(lerpF(1f,
                1.3f,
                fromPlayerControlsToAlbumsListProgress))
        }
        val commentsListOffset by derivedStateOf {
            -(maxContentWidth * (1f - fromPlayerControlsToAlbumsListProgress)).toDp()
        }
        val animScope = rememberCoroutineScope()
        val commentsScrollState = rememberScrollState()

        var transitioned by remember { mutableStateOf(false) }

        var sharedElementInitialData by remember { mutableStateOf(SharedElementData.NONE) }
        val titleProgressForward = remember { Animatable(0f) }
        val sharedProgress by derivedStateOf { titleProgressForward.value }

        fun animateOffset(initialValue: Float, targetValue: Float, onEnd: () -> Unit) {
            val distance = abs(targetValue - initialValue)
            val distancePercent = distance / maxContentWidth
            val duration = (250 * distancePercent).toInt()

            animScope.launch {
                animate(
                    initialValue = initialValue,
                    targetValue = targetValue,
                    animationSpec = tween(duration),
                ) { value, _ -> currentDragOffset = value }
                onEnd()
            }
        }

        fun expand() {
            animateOffset(
                initialValue = currentDragOffset,
                targetValue = maxContentWidth
            ) {
                currentScreen = Screen.Comments
            }
        }

        fun collapse() {
            animateOffset(
                initialValue = currentDragOffset,
                targetValue = 0f
            ) {
                currentScreen = Screen.Player
            }
        }


        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
        ) {
            val (control, statsInfo, albumInfo, commentsList, albumsList, draggableButton) = createRefs()

            if (currentScreen != Screen.Comments) {
                AlbumInfoContainer(
                    modifier = Modifier
                        .constrainAs(albumInfo) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.value(albumsInfoSize)
                        },
                    photoScale = photoScale,
                )
            }

            if (currentScreen != Screen.Player) {
                Box(
                    modifier = Modifier
                        .shadow(elevation = 2.dp)
                        .constrainAs(commentsList) {
                            top.linkTo(albumsList.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start, margin = commentsListOffset)
                            height = Dimension.fillToConstraints
                        }
                ) {
                    WidgetCommentsList(
                        scrollState = commentsScrollState,
                        comments = playbackData.comments
                    )
                }

                AlbumsListContainer(
                    modifier = Modifier
                        .offset(y = (-400).dp * sharedProgress)
                        .constrainAs(albumsList) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    topPadding = topInset + 24.dp,
                    albumData = playbackData.albums,
                    transitionAnimationProgress = sharedProgress,
                    appearingAnimationProgress = fromPlayerControlsToAlbumsListProgress,
                    onBackClick = {
                        currentScreen = Screen.TransitionToComments
                        collapse()
                    },
                    onInfoClick = { data, x, y, size ->
                        sharedElementInitialData = SharedElementData(
                            data,
                            x.toDp(),
                            y.toDp(),
                            size.toDp()
                        )
                        transitioned = true
                        toNowPlayingTransition = ToNowPlaying.Stable
                        animScope.launch {
                            titleProgressForward.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(ANIM_DURATION),
                            )
                        }
                    }
                )
            }

            if (currentScreen != Screen.Comments) {
                SongInfoContainer(
                    modifier = Modifier
                        .onSizeChanged { (_, h) -> songInfoHeight = h }
                        .constrainAs(statsInfo) {
                            top.linkTo(control.bottom, margin = songInfoOffset - 48.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.wrapContent
                        },
                    topPadding = songInfoTopPadding,
                )

                PlayerControlContainer(
                    modifier = Modifier
                        .onSizeChanged { (_, h) -> playerControlHeight = h }
                        .constrainAs(control) {
                            top.linkTo(parent.top, margin = playerControlOffset)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.wrapContent
                        },
                    topPadding = topInset + 24.dp,
                )

                DraggableButton(
                    modifier = Modifier
                        .constrainAs(draggableButton) {
                            start.linkTo(parent.start, margin = currentDragOffset.toDp())
                            bottom.linkTo(parent.bottom, margin = 64.dp)
                        }
                        .size(draggableButtonSize),
                    onPointerDown = {
                        animScope.coroutineContext.cancelChildren()
                        currentScreen = Screen.TransitionToComments
                    },
                    onClick = {
                        animScope.coroutineContext.cancelChildren()
                        currentScreen = Screen.TransitionToComments
                        expand()
                    },
                    onOffsetChange = { dragAmount ->
                        val newOffset = minOf(
                            currentDragOffset + dragAmount,
                            (maxContentWidth - draggableButtonSize.width.toPx(density))
                        )
                        if (newOffset >= 0) {
                            currentDragOffset = newOffset
                        }
                    },
                    onDragFinished = {
                        val shouldExpand = currentDragOffset > maxContentWidth / 2f
                        if (shouldExpand) {
                            expand()
                        } else {
                            collapse()
                        }
                    }
                )
            }

            fun goBackFromNowPlayingScreen() {
                transitioned = false
                animScope.launch {
                    titleProgressForward.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(ANIM_DURATION),
                    )
                }
            }

            if (toNowPlayingTransition == ToNowPlaying.Stable) {
                NowPlayingScreen(
                    albumInfo = sharedElementInitialData.albumInfo,
                    isAppearing = transitioned,
                    sharedElementParams = SharedElementParams(
                        initialOffset = Offset(
                            sharedElementInitialData.offsetX.toPx(density).toFloat(),
                            sharedElementInitialData.offsetY.toPx(density).toFloat(),
                        ),
                        targetOffset = Offset(
                            x = maxContentWidth / 2f - sharedElementTargetSize.toPx(density) / 2f,
                            y = 50.dp.toPx(density).toFloat()
                        ),
                        initialSize = sharedElementInitialData.size,
                        targetSize = sharedElementTargetSize,
                        initialCornerRadius = 10.dp,
                        targetCornerRadius = sharedElementTargetSize / 2
                    ),
                    onBackClick = {
                        goBackFromNowPlayingScreen()
                    },
                    onTransitionFinished = {
                        if (!transitioned) {
                            toNowPlayingTransition = ToNowPlaying.None
                        }
                    },
                    insets = DpInsets.from(topInset = topInset + 24.dp, bottomInset = bottomInset)
                )
            }

            BackHandler(backHandlerEnabled) {
                when {
                    transitioned -> goBackFromNowPlayingScreen()
                    currentScreen != Screen.Player -> {
                        currentScreen = Screen.TransitionToComments
                        collapse()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMainScreen() {
    PlayerTheme(darkTheme = false) {
        CompositionLocalProvider(LocalPreviewMode provides true) {
            PlayerScreen()
        }
    }
}