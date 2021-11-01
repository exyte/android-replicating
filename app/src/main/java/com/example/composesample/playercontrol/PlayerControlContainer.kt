package com.example.composesample.playercontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composesample.*
import com.example.composesample.R
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 08.10.2021.
 */
@Composable
fun PlayerControlContainer(
    modifier: Modifier,
    topPadding: Dp = 0.dp,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    RoundedCornersSurface(
        modifier = modifier,
        topPadding = topPadding,
        color = MaterialTheme.colors.primary,
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopMenu(onBackClick = onBackClick, onIconClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = "",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ContentTitle(
                text = "Aurora Aksnes",
                animate = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 1.dp)
                    .background(
                        MaterialTheme.colors.onSecondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(1.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            ContentSubtitle(
                text = "Norwegian singer/songwriter AURORA works in similar dark pop milieu as artists like Oh Land, Lykke Li.",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 48.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProgressBar(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(1f)
            )
        }

    }
}

@Composable
private fun ContentTitle(modifier: Modifier = Modifier, text: String, animate: Boolean) {
    AnimatedText(
        modifier = modifier,
        text = text,
        useAnimation = animate,
        style = MaterialTheme.typography.h4,
        textColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primary),
    )
}

@Composable
private fun ContentSubtitle(text: String, modifier: Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primary),
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center,
        modifier = modifier,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
}

@Composable
@Preview
private fun PreviewPlayerControlContainer() {
    PlayerTheme(darkTheme = false) {
        CompositionLocalProvider(LocalPreviewMode provides true) {
            PlayerControlContainer(
                modifier = Modifier.fillMaxWidth(1f),
                topPadding = 48.dp,
            )
        }
    }
}