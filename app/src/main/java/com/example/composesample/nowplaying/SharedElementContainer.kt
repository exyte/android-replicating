package com.example.composesample.nowplaying

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composesample.lerp
import com.example.composesample.toDp
import com.example.composesample.toPx
import kotlinx.coroutines.launch

/*
 * Created by Exyte on 17.10.2021.
 */

@Composable
fun SharedElementContainer(
    modifier: Modifier = Modifier,
    params: SharedElementParams,
    isForward: Boolean,
    onTransitionFinished: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val topOffsetOnScreen = 96.dp //Top padding + TopMenu height
    val density = LocalDensity.current
    val offsetProgress = remember(key1 = isForward) { Animatable(if (isForward) 0f else 1f) }
    val cornersProgress = remember(key1 = isForward) { Animatable(if (isForward) 0f else 1f) }
    LaunchedEffect(key1 = isForward, block = {
        launch {
            offsetProgress.animateTo(
                targetValue = if (isForward) 1f else 0f,
                animationSpec = tween(ANIM_DURATION),
            )
            onTransitionFinished()
        }
        launch {
            cornersProgress.animateTo(
                targetValue = if (isForward) 1f else 0f,
                animationSpec = tween(2 * ANIM_DURATION / 3),
            )
        }
    })

    val initialTopOffset = remember(key1 = params) {
        params.initialOffset.copy(y = params.initialOffset.y - topOffsetOnScreen.toPx(density))
    }
    val targetTopOffset = remember(key1 = params) {
        params.targetOffset.copy(y = 32.dp.toPx(density).toFloat())
    }

    val currentOffset = androidx.compose.ui.geometry.lerp(
        initialTopOffset,
        targetTopOffset,
        offsetProgress.value
    )
    val cornersSize = lerp(
        params.initialCornerRadius,
        params.targetCornerRadius,
        cornersProgress.value,
    )
    val currentSize = lerp(
        params.initialSize,
        params.targetSize,
        offsetProgress.value
    )

    SharedElementContainer(
        modifier = modifier,
        coverOffset = currentOffset,
        coverSize = currentSize,
        coverCornersRadius = cornersSize,
        content = content
    )
}

@Composable
fun SharedElementContainer(
    modifier: Modifier = Modifier,
    coverOffset: Offset,
    coverSize: Dp,
    coverCornersRadius: Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .padding(top = coverOffset.y.toDp(density))
                .offset(x = coverOffset.x.toDp(density)) //Can't use padding here, because offset might be < 0
                .size(coverSize)
                .clip(RoundedCornerShape(coverCornersRadius)),
            content = content,
        )
    }
}