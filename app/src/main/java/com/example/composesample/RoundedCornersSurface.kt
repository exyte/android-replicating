package com.example.composesample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composesample.ui.theme.PlayerTheme

/*
 * Created by Exyte on 15.10.2021.
 */
private const val CORNERS_SIZE = 48

@Composable
fun RoundedCornersSurface(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    elevation: Dp = 0.dp,
    color: Color = MaterialTheme.colors.surface,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(bottomStart = CORNERS_SIZE.dp,
                    bottomEnd = CORNERS_SIZE.dp))
            .padding(top = topPadding),
        shape = RoundedCornerShape(bottomStart = CORNERS_SIZE.dp, bottomEnd = CORNERS_SIZE.dp),
        color = color,
        elevation = elevation,
        content = content,
    )
}

@Composable
@Preview
private fun PreviewRoundedCornersSurface() {
    PlayerTheme(darkTheme = false) {
        RoundedCornersSurface(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            topPadding = 48.dp,
        ) {

        }
    }
}