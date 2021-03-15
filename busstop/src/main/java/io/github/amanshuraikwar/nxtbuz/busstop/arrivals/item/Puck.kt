package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.theme.outline

@Composable
fun Puck() {
    val puckColor = MaterialTheme.colors.outline

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp),
    ) {
        drawRoundRect(
            color = puckColor,
            topLeft = Offset(center.x, size.height) - Offset(24.dp.toPx(), 4.dp.toPx()),
            size = Size(48.dp.toPx(), 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
    }
}