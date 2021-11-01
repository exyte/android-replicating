package com.example.composesample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize

/*
 * Created by Exyte on 17.10.2021.
 */
@Stable
fun Int.toDp(density: Density): Dp = with(density) { this@toDp.toDp() }

@Stable
fun Float.toDp(density: Density): Dp = with(density) { this@toDp.toDp() }

@Stable
fun Dp.toPx(density: Density): Int = with(density) { this@toPx.roundToPx() }

@Stable
fun Dp.toPxf(density: Density): Float = with(density) { this@toPxf.toPx() }

@Stable
@Composable
fun Dp.toPx(): Int = toPx(LocalDensity.current)

@Stable
@Composable
fun Dp.toPxf(): Float = toPxf(LocalDensity.current)

@Stable
@Composable
fun Float.toDp() = this.toDp(LocalDensity.current)

@Stable
@Composable
fun Int.toDp() = this.toDp(LocalDensity.current)

@Stable
val <E> Collection<E>.lastIndex
    get() = count() - 1

@Stable
fun lerpF(start: Float, stop: Float, amount: Float): Float = (1 - amount) * start + amount * stop

/*
    DpSize is inline class here.
    Use this wrapper to 'inline' offsets too.
 */
@JvmInline
@Immutable
value class DpInsets private constructor(private val dpSize: DpSize) {
    @Stable
    val topInset: Dp
        get() = dpSize.width

    @Stable
    val bottomInset: Dp
        get() = dpSize.height


    companion object {
        @Stable
        fun from(topInset: Dp, bottomInset: Dp) = DpInsets(DpSize(topInset, bottomInset))

        val Zero: DpInsets get() = DpInsets(DpSize.Zero)
    }
}