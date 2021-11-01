package com.example.composesample.playercontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composesample.R
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 19.10.2021.
 */
suspend fun PointerInputScope.detectHorizontalDragGestures(
    onPointerDown: () -> Unit = {},
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onHorizontalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit,
) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            onPointerDown()
            var overSlop = 0f
            val drag = awaitHorizontalTouchSlopOrCancellation(
                down.id,
            ) { change, over ->
                change.consumePositionChange()
                overSlop = over
            }
            if (drag != null) {
                onDragStart.invoke(drag.position)
                onHorizontalDrag(drag, overSlop)
                if (
                    horizontalDrag(drag.id) {
                        onHorizontalDrag(it, it.positionChange().x)
                        it.consumePositionChange()
                    }
                ) {
                    onDragEnd()
                } else {
                    onDragCancel()
                }
            }
        }
    }
}

@Composable
fun DraggableButton(
    modifier: Modifier = Modifier,
    onPointerDown: () -> Unit = {},
    onClick: () -> Unit = {},
    onDragStart: () -> Unit = {},
    onOffsetChange: (Float) -> Unit = {},
    onDragFinished: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
            )
            .background(
                color = MaterialTheme.colors.primaryVariant,
                shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
            )
            .clickable { onClick() }
            .padding(end = 16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onPointerDown = onPointerDown,
                    onDragStart = { onDragStart() },
                    onDragEnd = onDragFinished,
                ) { change, dragAmount ->
                    change.consumeAllChanges()
                    onOffsetChange(dragAmount)
                }
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "",
            tint = MaterialTheme.colors.onSecondary
        )
    }
}

@Composable
@Preview
private fun PreviewDraggableButton() {
    PlayerTheme(darkTheme = false) {
        DraggableButton(
            modifier = Modifier.size(width = 100.dp, height = 64.dp)
        )
    }
}