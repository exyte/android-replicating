package com.example.composesample.nowplaying

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composesample.*
import com.example.composesample.R
import com.example.composesample.ui.theme.PlayerTheme
import kotlinx.coroutines.launch

/*
 * Created by Exyte on 16.10.2021.
 */
@Stable
data class SharedElementParams(
    val initialOffset: Offset,
    val targetOffset: Offset,
    val initialSize: Dp,
    val targetSize: Dp,
    val initialCornerRadius: Dp,
    val targetCornerRadius: Dp,
) {
    companion object {
        val NONE = SharedElementParams(
            initialOffset = Offset.Unspecified,
            targetOffset = Offset.Unspecified,
            initialSize = 0.dp,
            targetSize = 0.dp,
            initialCornerRadius = 0.dp,
            targetCornerRadius = 0.dp
        )
    }
}

@Stable
class LikedIndices(private val indices: MutableSet<Int> = mutableSetOf()) {

    fun onAction(index: Int): LikedIndices {
        if (indices.contains(index)) {
            indices -= index
        } else {
            indices += index
        }
        return LikedIndices(indices)
    }

    fun isLiked(index: Int): Boolean = indices.contains(index)
}


@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    albumInfo: ModelAlbumInfo,
    sharedElementParams: SharedElementParams = SharedElementParams.NONE,
    isAppearing: Boolean,
    insets: DpInsets = DpInsets.Zero,
    onTransitionFinished: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    val sharedElementProgress =
        remember(key1 = isAppearing) { Animatable(if (isAppearing) 0f else 1f) }
    val titleProgress = remember(key1 = isAppearing) { Animatable(if (isAppearing) 0f else 1f) }
    val bgColorProgress = remember(key1 = isAppearing) { Animatable(if (isAppearing) 0f else 1f) }
    val listProgress = remember(key1 = isAppearing) { Animatable(if (isAppearing) 0f else 1f) }

    var likedIndices by remember { mutableStateOf(LikedIndices()) }

    LaunchedEffect(key1 = isAppearing, block = {
        launch {
            sharedElementProgress.animateTo(if (isAppearing) 1f else 0f,
                animationSpec = tween(ANIM_DURATION))
            onTransitionFinished()
        }
        launch {
            titleProgress.animateTo(if (isAppearing) 1f else 0f,
                animationSpec = tween(
                    durationMillis = ANIM_DURATION / 2,
                    delayMillis = if (isAppearing) ANIM_DURATION / 2 else 0
                )
            )
        }
        launch {
            listProgress.animateTo(if (isAppearing) 1f else 0f,
                animationSpec = tween(
                    durationMillis = ANIM_DURATION / 2,
                    delayMillis = if (isAppearing) ANIM_DURATION / 2 else 0
                )
            )
        }
        launch {
            bgColorProgress.animateTo(if (isAppearing) 1f else 0f,
                animationSpec = tween(
                    durationMillis = ANIM_DURATION,
                )
            )
        }
    })
    val surfaceMaterialColor = MaterialTheme.colors.surface
    val surfaceColor = remember(bgColorProgress.value) {
        androidx.compose.ui.graphics.lerp(
            Color.Transparent,
            surfaceMaterialColor,
            bgColorProgress.value
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = surfaceColor,
    ) {
        Column(
            modifier = modifier.padding(top = insets.topInset),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopMenu(
                modifier = Modifier.alpha(titleProgress.value),
                title = "Now Playing",
                backIconTint = MaterialTheme.colors.onSurface,
                onBackClick = onBackClick,
                onIconClick = onShareClick
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                )
            }
            if (sharedElementParams != SharedElementParams.NONE) {
                SharedElementContainer(
                    modifier = Modifier.height(270.dp),
                    params = sharedElementParams,
                    isForward = isAppearing,
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = albumInfo.cover),
                        contentDescription = "",
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedText(
                modifier = Modifier.alpha(titleProgress.value),
                text = albumInfo.title,
                useAnimation = isAppearing,
                animationDelay = 350L,
                style = MaterialTheme.typography.h4,
                textColor = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = albumInfo.author,
                modifier = Modifier.alpha(titleProgress.value),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
            )

            SongList(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = lerp(400.dp,
                        0.dp,
                        sharedElementProgress.value)
                    )
                    .alpha(sharedElementProgress.value),
                items = albumInfo.songs,
                offsetPercent = sharedElementProgress.value,
                likedIndices = likedIndices,
                onLikeClick = { clickedIndex ->
                    likedIndices = likedIndices.onAction(clickedIndex)
                }
            )

            BottomPlayerControls(
                modifier = Modifier
                    .height(90.dp + insets.bottomInset)
                    .offset(y = lerp(90.dp + insets.bottomInset,
                        0.dp,
                        sharedElementProgress.value)),
                bottomPadding = insets.bottomInset,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewNowPlaying() {
    PlayerTheme(false) {
        CompositionLocalProvider(LocalPreviewMode provides true) {
            NowPlayingScreen(
                albumInfo = PlaybackData().albums.first(),
                isAppearing = false,
            )
        }
    }
}