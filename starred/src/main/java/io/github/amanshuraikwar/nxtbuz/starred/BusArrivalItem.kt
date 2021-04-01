package io.github.amanshuraikwar.nxtbuz.starred

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import kotlin.math.roundToInt

@Composable
@ExperimentalMaterialApi
fun <T : Any> rememberSwipeableState(
    initialValue: T,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (newValue: T) -> Boolean = { true }
): SwipeableState<T> {
    return remember {
        SwipeableState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun BusArrivalItem(
    busStopDescription: String,
    busServiceNumber: String,
    arrivals: Arrivals,
    onClick: () -> Unit = {},
    onUnStarClicked: () -> Unit = {},
) {
    val busType = when (arrivals) {
        is Arrivals.Arriving -> {
            arrivals.nextArrivingBus.type
        }
        Arrivals.DataNotAvailable,
        Arrivals.NotOperating -> {
            BusType.SD
        }
    }

    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { 48.dp.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)

    Surface(
        modifier = Modifier.swipeable(
            state = swipeableState,
            anchors = anchors,
            thresholds = { _, _ -> FractionalThreshold(0.3f) },
            orientation = Orientation.Vertical
        ),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.star,
        elevation = 8.dp,
    ) {
        LastWrapBox {
            CompositionLocalProvider(
                LocalIndication provides rememberRipple(color = MaterialTheme.colors.onStar)
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            onUnStarClicked()
                        }
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "UN-STAR",
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colors.onStar,
                        style = MaterialTheme.typography.button,
                    )
                }
            }

            LastWrapColumn(
                modifier = Modifier
                    .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                    .background(
                        color = MaterialTheme.colors.surface,
                    )
                    .clickable {
                        onClick()
                    }
                    .animateContentSize()
                    .padding(8.dp)
            ) {
                Text(
                    busStopDescription,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body2,
                )

                Spacer(modifier = Modifier.size(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(
                            when (busType) {
                                BusType.SD -> R.drawable.ic_bus_normal_16
                                BusType.DD -> R.drawable.ic_bus_dd_16
                                BusType.BD -> R.drawable.ic_bus_feeder_16
                            }
                        ),
                        modifier = Modifier.size(16.dp),
                        contentDescription = "Bus Type",
                        tint = MaterialTheme.colors.onSurface
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colors.primary
                            )
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "961M",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.alpha(0f)
                        )

                        Text(
                            text = busServiceNumber,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }

                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Rounded.ArrowRight,
                        contentDescription = "Arrow",
                        tint = MaterialTheme.colors.onSurface,
                    )

                    Text(
                        text = when (arrivals) {
                            is Arrivals.Arriving -> arrivals.nextArrivingBus.arrival
                            Arrivals.DataNotAvailable -> "N/A"
                            Arrivals.NotOperating -> "N/A"
                        },
                        style = MaterialTheme.typography.body1Bold,
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun BusArrivalItemPreview() {
//    PreviewSurface {
//        BusArrivalItem(
//            "Opp Blk 19",
//            "961M",
//            Arrivals.NotOperating,
//        )
//    }
}

