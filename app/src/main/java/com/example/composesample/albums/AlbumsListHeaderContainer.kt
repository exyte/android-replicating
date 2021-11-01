package com.example.composesample.albums

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composesample.*
import com.example.composesample.R
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 11.10.2021.
 */
@Composable
fun AlbumsListContainer(
    modifier: Modifier = Modifier,
    listScrollState: ScrollState = rememberScrollState(),
    albumData: Collection<ModelAlbumInfo>,
    topPadding: Dp = 0.dp,
    transitionAnimationProgress: Float = 0f,
    appearingAnimationProgress: Float = 1f,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onInfoClick: (info: ModelAlbumInfo, offsetX: Float, offsetY: Float, size: Int) -> Unit = { _, _, _, _ -> },
) {
    var clickedItemIndex by remember { mutableStateOf(-1) }
    val transitionInProgress by derivedStateOf { transitionAnimationProgress > 0f }

    RoundedCornersSurface(
        modifier = modifier,
        topPadding = topPadding,
        elevation = 4.dp,
        color = MaterialTheme.colors.primary,
    ) {
        Column(
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            TopMenu(
                title = "Albums",
                onBackClick = onBackClick,
                onIconClick = onShareClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.horizontalScroll(listScrollState)
            ) {
                albumData.forEachIndexed { index, info ->
                    Spacer(modifier = Modifier.width(if (index == 0) 24.dp else 16.dp))
                    val itemAlpha =
                        if (clickedItemIndex == index && transitionInProgress) 0f else 1f
                    CompositionLocalProvider(LocalContentAlpha provides itemAlpha) {
                        val topOffset = ((index + 1) * (1f - appearingAnimationProgress) * 10).dp
                        AlbumListItem(
                            modifier = Modifier
                                .offset(y = topOffset)
                                .alpha(appearingAnimationProgress),
                            info = info,
                            onClick = { clickedInfo, offsetX, offsetY, size ->
                                clickedItemIndex = index
                                onInfoClick(clickedInfo, offsetX, offsetY, size)
                            })
                    }
                    if (index == albumData.lastIndex) {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumListItem(
    modifier: Modifier = Modifier,
    info: ModelAlbumInfo,
    onClick: (info: ModelAlbumInfo, offsetX: Float, offsetY: Float, size: Int) -> Unit,
) {
    var parentOffset by remember { mutableStateOf(Offset.Unspecified) }
    var mySize by remember { mutableStateOf(0) }
    Column(
        modifier = modifier.width(150.dp),
    ) {
        Image(
            painter = painterResource(id = info.cover),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .onGloballyPositioned { coordinates ->
                    parentOffset = coordinates.positionInRoot()
                    mySize = coordinates.size.width
                }
                .clip(RoundedCornerShape(10.dp))
                .alpha(LocalContentAlpha.current)
                .clickable { onClick(info, parentOffset.x, parentOffset.y, mySize) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = info.title,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = info.year.toString(),
            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
@Preview
private fun PreviewAlbumHeader() {
    PlayerTheme(false) {
        AlbumsListContainer(albumData = PlaybackData().albums)
    }
}