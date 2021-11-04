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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
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

class PlayerScreenState(
    constraints: Constraints,
    private val density: Density,
    isInPreviewMode: Boolean,
) {
    @Stable
    private fun Float.toDp() = this.toDp(density)

    @Stable
    private fun Int.toDp() = this.toDp(density)

    private val easing = FastOutLinearInEasing

    var currentScreen by mutableStateOf(Screen.Player)

    var currentDragOffset by mutableStateOf(0f)
    var playerControlHeight by mutableStateOf(0)
    var songInfoHeight by mutableStateOf(0)

    val maxContentWidth = constraints.maxWidth.toFloat()
    val maxContentHeight = constraints.maxHeight.toFloat()

    val backHandlerEnabled by derivedStateOf { currentScreen != Screen.Player }

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

    val albumsInfoSize by derivedStateOf { (maxContentHeight * 0.4f).toDp() }

    val photoScale by derivedStateOf {
        easing.transform(
            lerpF(1f,
                1.3f,
                fromPlayerControlsToAlbumsListProgress)
        )
    }
    val commentsListOffset by derivedStateOf {
        -(maxContentWidth * (1f - fromPlayerControlsToAlbumsListProgress)).toDp()
    }

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
}

@Composable
fun rememberPlayerScreenState(
    constraints: Constraints,
    density: Density = LocalDensity.current,
    isInPreviewMode: Boolean = LocalPreviewMode.current,
) = remember(constraints) {
    PlayerScreenState(
        constraints,
        density,
        isInPreviewMode
    )
}

@Composable
fun PlayerScreen(playbackData: PlaybackData = PlaybackData()) = ProvideWindowInsets {
    BoxWithConstraints {
        val sharedElementTargetSize = 230.dp
        val draggableButtonSize = DpSize(width = 64.dp, height = 48.dp)

        val density = LocalDensity.current

        val insets = LocalWindowInsets.current
        val bottomInset by derivedStateOf { insets.navigationBars.bottom.toDp(density) }
        val topInset by derivedStateOf { insets.statusBars.top.toDp(density) }

        val screenState = rememberPlayerScreenState(constraints)

        var toNowPlayingTransition by remember { mutableStateOf(ToNowPlaying.None) }

        val animScope = rememberCoroutineScope()
        val commentsScrollState = rememberScrollState()

        var transitioned by remember { mutableStateOf(false) }

        var sharedElementParams by remember { mutableStateOf(SharedElementData.NONE) }
        val titleProgressForward = remember { Animatable(0f) }
        val sharedProgress by derivedStateOf { titleProgressForward.value }

        fun animateOffset(initialValue: Float, targetValue: Float, onEnd: () -> Unit) {
            val distance = abs(targetValue - initialValue)
            val distancePercent = distance / screenState.maxContentWidth
            val duration = (250 * distancePercent).toInt()

            animScope.launch {
                animate(
                    initialValue = initialValue,
                    targetValue = targetValue,
                    animationSpec = tween(duration),
                ) { value, _ -> screenState.currentDragOffset = value }
                onEnd()
            }
        }

        fun expand() {
            animateOffset(
                initialValue = screenState.currentDragOffset,
                targetValue = screenState.maxContentWidth
            ) {
                screenState.currentScreen = Screen.Comments
            }
        }

        fun collapse() {
            animateOffset(
                initialValue = screenState.currentDragOffset,
                targetValue = 0f
            ) {
                screenState.currentScreen = Screen.Player
            }
        }

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
        ) {
            val (control, statsInfo, albumInfo, commentsList, albumsList, draggableButton) = createRefs()

            if (screenState.currentScreen != Screen.Comments) {
                AlbumInfoContainer(
                    modifier = Modifier
                        .constrainAs(albumInfo) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.value(screenState.albumsInfoSize)
                        },
                    photoScale = screenState.photoScale,
                )
            }

            if (screenState.currentScreen != Screen.Player) {
                Box(
                    modifier = Modifier
                        .shadow(elevation = 2.dp)
                        .constrainAs(commentsList) {
                            top.linkTo(albumsList.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start, margin = screenState.commentsListOffset)
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
                    appearingAnimationProgress = screenState.fromPlayerControlsToAlbumsListProgress,
                    onBackClick = {
                        screenState.currentScreen = Screen.TransitionToComments
                        collapse()
                    },
                    onInfoClick = { data, x, y, size ->
                        sharedElementParams = SharedElementData(
                            data,
                            x.toDp(density),
                            y.toDp(density),
                            size.toDp(density)
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

            if (screenState.currentScreen != Screen.Comments) {
                SongInfoContainer(
                    modifier = Modifier
                        .onSizeChanged { (_, h) -> screenState.songInfoHeight = h }
                        .constrainAs(statsInfo) {
                            top.linkTo(control.bottom, margin = screenState.songInfoOffset - 48.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.wrapContent
                        },
                    topPadding = screenState.songInfoTopPadding,
                )

                PlayerControlContainer(
                    modifier = Modifier
                        .onSizeChanged { (_, h) -> screenState.playerControlHeight = h }
                        .constrainAs(control) {
                            top.linkTo(parent.top, margin = screenState.playerControlOffset)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.wrapContent
                        },
                    topPadding = topInset + 24.dp,
                )

                DraggableButton(
                    modifier = Modifier
                        .constrainAs(draggableButton) {
                            start.linkTo(parent.start,
                                margin = screenState.currentDragOffset.toDp(density))
                            bottom.linkTo(parent.bottom, margin = 64.dp)
                        }
                        .size(draggableButtonSize),
                    onPointerDown = {
                        animScope.coroutineContext.cancelChildren()
                        screenState.currentScreen = Screen.TransitionToComments
                    },
                    onClick = {
                        animScope.coroutineContext.cancelChildren()
                        screenState.currentScreen = Screen.TransitionToComments
                        expand()
                    },
                    onOffsetChange = { dragAmount ->
                        val newOffset = minOf(
                            screenState.currentDragOffset + dragAmount,
                            (screenState.maxContentWidth - draggableButtonSize.width.toPx(density))
                        )
                        if (newOffset >= 0) {
                            screenState.currentDragOffset = newOffset
                        }
                    },
                    onDragFinished = {
                        val shouldExpand =
                            screenState.currentDragOffset > screenState.maxContentWidth / 2f
                        if (shouldExpand) {
                            expand()
                        } else {
                            collapse()
                        }
                    }
                )
            }

            val goBackFromNowPlayingScreen: () -> Unit = remember {
                {
                    transitioned = false
                    animScope.launch {
                        titleProgressForward.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(ANIM_DURATION),
                        )
                    }
                }
            }

            if (toNowPlayingTransition == ToNowPlaying.Stable) {
                NowPlayingScreen(
                    albumInfo = sharedElementParams.albumInfo,
                    isAppearing = transitioned,
                    sharedElementParams = SharedElementParams(
                        initialOffset = Offset(
                            sharedElementParams.offsetX.toPx(density).toFloat(),
                            sharedElementParams.offsetY.toPx(density).toFloat(),
                        ),
                        targetOffset = Offset(
                            x = screenState.maxContentWidth / 2f - sharedElementTargetSize.toPx(
                                density) / 2f,
                            y = 50.dp.toPx(density).toFloat()
                        ),
                        initialSize = sharedElementParams.size,
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
                    insets = DpInsets.from(topInset = topInset + defaultStatusBarPadding, bottomInset = bottomInset)
                )
            }

            BackHandler(screenState.backHandlerEnabled) {
                when {
                    transitioned -> goBackFromNowPlayingScreen()
                    screenState.currentScreen != Screen.Player -> {
                        screenState.currentScreen = Screen.TransitionToComments
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