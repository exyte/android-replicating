package com.example.composesample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 14.10.2021.
 */
@Composable
fun TopMenu(
    modifier: Modifier = Modifier,
    title: String = "",
    backIconTint: Color = MaterialTheme.colors.onSecondary,
    onBackClick: () -> Unit = {},
    onIconClick: () -> Unit = {},
    endIcon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_back_24),
                contentDescription = "",
                colorFilter = ColorFilter.tint(backIconTint),
            )
        }

        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        IconButton(onClick = onIconClick, content = endIcon)
    }
}

@Composable
@Preview(name = "Top Menu")
private fun PreviewTopMenu() {
    PlayerTheme(darkTheme = false) {
        TopMenu(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            backIconTint = MaterialTheme.colors.onSurface,
            title = "Now Playing",
        ) {
            val painter = rememberVectorPainter(image = Icons.Default.Share)
            Icon(painter = painter, contentDescription = "")
        }
    }
}