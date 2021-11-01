package com.example.composesample.playercontrol

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composesample.R
import com.example.composesample.ui.theme.PlayerTheme
import kotlin.math.max

/*
 * Created by Exyte on 10.10.2021.
 */
@Composable
fun AlbumInfoContainer(
    modifier: Modifier,
    photoScale: Float = 1f,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = "12",
                    fontSize = 140.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .alpha(0.07f)
                        .offset(x = 24.dp, y = (-32).dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    color = MaterialTheme.colors.primary,
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.Start,
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_layers),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (-24).dp),
                    )

                    Text(
                        text = "12",
                        fontSize = 48.sp,
                        modifier = Modifier,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colors.primary,
                    )

                    Text(
                        text = "Albums",
                        fontSize = 14.sp,
                        modifier = Modifier,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colors.primary,
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.img_photo),
                contentDescription = "",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentScale = PhotoScale.apply {
                    additionalScale = photoScale
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAlbumsInfo() {
    PlayerTheme(darkTheme = false) {
        AlbumInfoContainer(modifier = Modifier.wrapContentHeight(), photoScale = 1f)
    }
}

object PhotoScale : ContentScale {
    var additionalScale = 1f

    override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
        if (additionalScale > 1f) {
            val newWidth = dstSize.width * additionalScale
            val newHeight = dstSize.height * additionalScale
            return computeFillMaxDimension(srcSize, Size(newWidth, newHeight)).let {
                ScaleFactor(it, it)
            }
        }

        return ContentScale.Crop.computeScaleFactor(srcSize, dstSize)
    }

    private fun computeFillMaxDimension(srcSize: Size, dstSize: Size): Float {
        val widthScale = computeFillWidth(srcSize, dstSize)
        val heightScale = computeFillHeight(srcSize, dstSize)
        return max(widthScale, heightScale)
    }

    private fun computeFillWidth(srcSize: Size, dstSize: Size): Float =
        dstSize.width / srcSize.width

    private fun computeFillHeight(srcSize: Size, dstSize: Size): Float =
        dstSize.height / srcSize.height

}