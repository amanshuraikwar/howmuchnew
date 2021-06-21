package io.github.amanshuraikwar.nxtbuz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMap
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMapViewModel
import io.github.amanshuraikwar.nxtbuz.map.ui.recenter.RecenterButton
import io.github.amanshuraikwar.nxtbuz.map.ui.recenter.RecenterViewModel
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchBar
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchBarDecorationType
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchScreen
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchViewModel
import io.github.amanshuraikwar.nxtbuz.search.ui.model.rememberSearchState
import io.github.amanshuraikwar.nxtbuz.starred.DecorationType
import io.github.amanshuraikwar.nxtbuz.starred.StarredBusArrivals
import io.github.amanshuraikwar.nxtbuz.starred.StarredViewModel
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import io.github.amanshuraikwar.nxtbuz.ui.model.NavigationState

@ExperimentalComposeUiApi
@ExperimentalAnimatedInsets
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    screenState: MainScreenState,
    mainViewModel: MainViewModel,
    busRouteViewModel: BusRouteViewModel,
    busStopArrivalsViewModel: BusStopArrivalsViewModel,
    busStopsViewModel: BusStopsViewModel,
    nxtBuzMapViewModel: NxtBuzMapViewModel,
    starredViewModel: StarredViewModel,
    recenterViewModel: RecenterViewModel,
    searchViewModel: SearchViewModel,
    onSettingsClick: () -> Unit,
) {
    val density = LocalDensity.current
    val insets = LocalWindowInsets.current

    when (screenState) {
        MainScreenState.Fetching -> {

        }
        is MainScreenState.Success -> {
            if (screenState.showMap) {
                Box {
                    NxtBuzMap(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = nxtBuzMapViewModel,
                        onClick = { latLng ->
                            mainViewModel.onMapClick(latLng)
                        }
                    )

                    Column {
                        SearchBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 8.dp
                                ),
                            onClick = {
                                mainViewModel.onSearchClick()
                            },
                            onSettingsClicked = onSettingsClick,
                            decorationType = SearchBarDecorationType.SHADOW
                        )

                        StarredBusArrivals(
                            modifier = Modifier.fillMaxWidth(),
                            vm = starredViewModel,
                            onItemClicked = { busStopCode, busServiceNumber ->
                                mainViewModel.onBusServiceClick(
                                    busStopCode = busStopCode,
                                    busServiceNumber = busServiceNumber
                                )
                            },
                            decorationType = DecorationType.SHADOW
                        )
                    }

                    RecenterButton(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                bottom =
                                (LocalConfiguration.current.screenHeightDp * 2 / 5).dp
                                        + with(density) { insets.statusBars.top.toDp() }
                            )
                            .padding(horizontal = 16.dp),
                        recenterViewModel,
                    )

                    ContentNavGraph(
                        navigationState = screenState.navigationState,
                        onBusStopClick = { busStop ->
                            mainViewModel.onBusStopClick(busStop)
                        },
                        showBottomSheet = true,
                        onBusServiceClick = { busStopCode, busServiceNumber ->
                            mainViewModel.onBusServiceClick(busStopCode, busServiceNumber)
                        },
                        bottomSheetBgOffset =
                        with(density) { insets.statusBars.top.toDp() },
                        busStopsViewModel = busStopsViewModel,
                        busRouteViewModel = busRouteViewModel,
                        busStopArrivalsViewModel = busStopArrivalsViewModel,
                    )

                    if (screenState.navigationState is NavigationState.Search) {
                        SearchScreen(
                            searchState = rememberSearchState(
                                vm = searchViewModel
                            ),
                            onBackClick = {
                                mainViewModel.onBackPressed()
                            },
                            onBusStopSelected = { busStop ->
                                mainViewModel.onBusStopClick(
                                    busStop = busStop,
                                    pushBackStack = false
                                )
                            }
                        )
                    }
                }
            } else {
                Surface {
                    Box {
                        Column {
                            SearchBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                        bottom = 8.dp
                                    ),
                                onClick = {
                                    mainViewModel.onSearchClick()
                                },
                                onSettingsClicked = onSettingsClick,
                                decorationType = SearchBarDecorationType.OUTLINE
                            )

                            StarredBusArrivals(
                                modifier = Modifier.fillMaxWidth(),
                                vm = starredViewModel,
                                onItemClicked = { busStopCode, busServiceNumber ->
                                    mainViewModel.onBusServiceClick(
                                        busStopCode = busStopCode,
                                        busServiceNumber = busServiceNumber
                                    )
                                },
                                decorationType = DecorationType.OUTLINE
                            )

                            Spacer(Modifier.height(8.dp))

                            Divider()

                            ContentNavGraph(
                                navigationState = screenState.navigationState,
                                showBottomSheet = false,
                                onBusStopClick = { busStop ->
                                    mainViewModel.onBusStopClick(busStop)
                                },
                                onBusServiceClick = { busStopCode, busServiceNumber ->
                                    mainViewModel.onBusServiceClick(busStopCode, busServiceNumber)
                                },
                                bottomSheetBgOffset =
                                with(density) { insets.statusBars.top.toDp() },
                                busStopsViewModel = busStopsViewModel,
                                busRouteViewModel = busRouteViewModel,
                                busStopArrivalsViewModel = busStopArrivalsViewModel,
                            )
                        }

                        if (screenState.navigationState is NavigationState.Search) {
                            SearchScreen(
                                searchState = rememberSearchState(
                                    vm = searchViewModel
                                ),
                                onBackClick = {
                                    mainViewModel.onBackPressed()
                                },
                                onBusStopSelected = { busStop ->
                                    mainViewModel.onBusStopClick(
                                        busStop = busStop,
                                        pushBackStack = false
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}