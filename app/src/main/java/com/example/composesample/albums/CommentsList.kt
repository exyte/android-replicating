package com.example.composesample.albums

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composesample.ModelComment
import com.example.composesample.PlaybackData
import com.example.composesample.lastIndex
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 11.10.2021.
 */
@Composable
fun CommentListItem(
    comment: ModelComment,
    isActionsVisible: Boolean,
    onClick: (ModelComment) -> Unit = {},
    onActionClick: (Action) -> Unit = {},
) {
    var contentHeight by remember { mutableStateOf(0) }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { (_, h) ->
                contentHeight = h
            }
            .background(color = MaterialTheme.colors.surface),
    ) {
        val density = LocalDensity.current
        val maxWidth = with(density) { constraints.maxWidth.toDp() }
        val maxHeight by derivedStateOf {
            with(density) { contentHeight.toDp() }
        }

        CommentStateless(
            avatarId = comment.avatarId,
            name = comment.name,
            text = comment.text,
            date = comment.date,
            onClick = { onClick(comment) },
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp),
        ) {
            ActionPanel(
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                isVisible = isActionsVisible,
                onActionClick = onActionClick
            )
        }
    }
}

@Composable
fun CommentStateless(
    avatarId: Int,
    name: String,
    text: String,
    date: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = avatarId), contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = name,
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
            )
        }
        Text(
            text = date,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Composable
fun WidgetCommentsList(
    scrollState: ScrollState = rememberScrollState(),
    comments: Collection<ModelComment>,
    onActionClick: (Action) -> Unit = {},
) {
    var selectedComment by remember { mutableStateOf<ModelComment?>(null) }
    Surface(color = MaterialTheme.colors.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            SectionSelector(
                modifier = Modifier
                    .size(width = 260.dp, height = 50.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(48.dp))
            comments.forEachIndexed { index, comment ->
                CommentListItem(
                    comment = comment,
                    isActionsVisible = selectedComment == comment,
                    onClick = {
                        if (selectedComment != comment) {
                            selectedComment = comment
                        }
                    },
                    onActionClick = { action ->
                        selectedComment = null
                        onActionClick(action)
                    },
                )
                Spacer(modifier = Modifier.height(if (index != comments.lastIndex) 8.dp else 72.dp))
            }
        }
    }
}

@Composable
@Preview
fun PreviewListItem() {
    PlayerTheme(darkTheme = false) {
        CommentListItem(
            comment = PlaybackData().comments.first(),
            isActionsVisible = false,
        )
    }
}

@Composable
@Preview
fun PreviewComment() {
    PlayerTheme(false) {
        WidgetCommentsList(
            comments = PlaybackData().comments,
            onActionClick = {},
        )
    }
}