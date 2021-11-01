package com.example.composesample.albums

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composesample.R

/*
 * Created by Exyte on 07.10.2021.
 */

enum class Action {
    Back, Like, Flag, Delete,
}

class ActionPanelState {
    var width by mutableStateOf(300.dp)
    var height by mutableStateOf(50.dp)
    var contentAlpha by mutableStateOf(1f)
    var elevation by mutableStateOf(0.dp)
}

@Stable
data class ActionItem(
    @DrawableRes val iconResId: Int,
    val action: Action,
    val description: String,
    val isChecked: Boolean = false,
)

private val icons = listOf(
    ActionItem(R.drawable.ic_back, Action.Back, "Back"),
    ActionItem(R.drawable.ic_favourile_outline, Action.Like, "Like"),
    ActionItem(R.drawable.ic_flag, Action.Flag, "Flag"),
    ActionItem(R.drawable.ic_delete, Action.Delete, "Delete"),
)

private const val CORNER_DURATION = 180
private const val EXPAND_DURATION = 120
private const val MIN_RADIUS = 0

@Composable
fun ActionPanel(
    maxWidth: Dp = 200.dp,
    maxHeight: Dp = 50.dp,
    isVisible: Boolean = false,
    onActionClick: (Action) -> Unit,
) {
    val state = remember { ActionPanelState() }

    val stateTransition = updateTransition(targetState = isVisible, label = "")

    val targetWidth by stateTransition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = CORNER_DURATION + EXPAND_DURATION
                if (targetState) {
                    maxHeight at CORNER_DURATION
                    maxWidth at CORNER_DURATION + EXPAND_DURATION
                } else {
                    maxHeight at EXPAND_DURATION
                    MIN_RADIUS.dp at EXPAND_DURATION + CORNER_DURATION
                }
            }
        },
        label = ""
    ) { value -> if (value) maxWidth else MIN_RADIUS.dp }

    val targetHeight by stateTransition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = CORNER_DURATION + EXPAND_DURATION
                if (targetState) {
                    maxHeight at CORNER_DURATION
                } else {
                    maxHeight at EXPAND_DURATION
                    MIN_RADIUS.dp at EXPAND_DURATION + CORNER_DURATION
                }
            }
        },
        label = ""
    ) { value -> if (value) maxHeight else MIN_RADIUS.dp }

    val targetContentAlpha by remember {
        derivedStateOf { (targetWidth - MIN_RADIUS.dp) / (maxWidth - MIN_RADIUS.dp) }
    }
    val targetElevation by remember {
        derivedStateOf { (2 * ((targetWidth - MIN_RADIUS.dp) / (maxWidth - MIN_RADIUS.dp))).dp }
    }

    state.run {
        width = targetWidth
        height = targetHeight
        contentAlpha = targetContentAlpha
        elevation = targetElevation
    }

    ActionPanelStateless(
        state,
        onActionClick = onActionClick,
    )
}

@Composable
fun ActionPanelStateless(state: ActionPanelState, onActionClick: (Action) -> Unit) {
    Surface(
        modifier = Modifier
            .size(width = state.width, height = state.height)
            .shadow(
                elevation = state.elevation,
                shape = RoundedCornerShape(percent = 50),
                clip = true
            ),
        elevation = state.elevation,
        color = MaterialTheme.colors.primaryVariant,
        shape = RoundedCornerShape(percent = 50),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .alpha(state.contentAlpha),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icons.forEach { (iconId, action, description, checked) ->
                ActionIconButton(
                    iconResId = iconId,
                    description = description,
                    action = action,
                    isChecked = checked,
                    onClick = onActionClick,
                )
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    @DrawableRes iconResId: Int,
    description: String,
    action: Action,
    isChecked: Boolean,
    onClick: (Action) -> Unit,
) {
    IconButton(
        modifier = Modifier.size(32.dp),
        onClick = { onClick(action) },
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = description,
            tint = Color.White,
        )
    }
}

@Preview
@Composable
fun ActionPanelPreviewExpanded() {
    ActionPanel(isVisible = true, onActionClick = {})
}

@Preview
@Composable
fun ActionPanelPreviewCollapsed() {
    ActionPanel(isVisible = false, onActionClick = {})
}