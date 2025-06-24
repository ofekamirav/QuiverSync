package org.example.quiversync.presentation.screens.rentals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.domain.model.RentalStatus
import org.example.quiversync.features.rentals.explore.ExploreViewModel
import org.example.quiversync.features.rentals.my_offers.MyOffersViewModel
import org.example.quiversync.features.rentals.my_rentals.MyRentalsViewModel
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
fun colorForStatus(status: RentalStatus): Color {
    return when (status) {
        RentalStatus.PENDING -> OceanPalette.SandOrange
        RentalStatus.APPROVED -> MaterialTheme.colorScheme.onSecondary
        RentalStatus.COMPLETED -> Color.Gray
        RentalStatus.REJECTED -> MaterialTheme.colorScheme.error
        RentalStatus.CANCELLED -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalsHubScreen(
    modifier: Modifier = Modifier,
    exploreViewModel: ExploreViewModel = ExploreViewModel(),
    myOffersViewModel: MyOffersViewModel = MyOffersViewModel(),
    myRentalsViewModel: MyRentalsViewModel = MyRentalsViewModel(),
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Explore", "My Rentals", "My Offers")
    val windowInfo = LocalWindowInfo.current
    val isExpanded = windowInfo.widthSize > WindowWidthSize.COMPACT

    if (isExpanded) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationRailItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        label = { Text(text = title) },
                        icon = {}
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                RentalsHubContent(
                    selectedTabIndex = selectedTabIndex,
                    exploreViewModel = exploreViewModel,
                    myOffersViewModel = myOffersViewModel,
                    myRentalsViewModel = myRentalsViewModel,
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            RentalsHubContent(
                selectedTabIndex = selectedTabIndex,
                exploreViewModel = exploreViewModel,
                myOffersViewModel = myOffersViewModel,
                myRentalsViewModel = myRentalsViewModel,
            )
        }
    }
}

@Composable
private fun RentalsHubContent(
    selectedTabIndex: Int,
    exploreViewModel: ExploreViewModel,
    myOffersViewModel: MyOffersViewModel,
    myRentalsViewModel: MyRentalsViewModel,
) {
    when (selectedTabIndex) {
        0 -> ExploreTab(exploreViewModel)
        1 -> MyRentalsTab(myRentalsViewModel)
        2 -> MyOffersTab(myOffersViewModel)
    }
}

@Preview(showBackground = true, name = "Rentals Hub - Light Mode")
@Composable
fun RentalsHubScreenPreview() {
    QuiverSyncTheme(darkTheme = false) {
        RentalsHubScreen(
            exploreViewModel = ExploreViewModel(),
            myOffersViewModel = MyOffersViewModel(),
            myRentalsViewModel = MyRentalsViewModel(),
        )
    }
}