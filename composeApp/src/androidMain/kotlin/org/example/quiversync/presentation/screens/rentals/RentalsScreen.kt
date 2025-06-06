package org.example.quiversync.presentation.screens.rentals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.quiversync.R
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults


data class UserProfile(
    val id: String,
    val name: String,
    val imageUrl: String
)

data class BoardForRent(
    val id: String,
    val boardName: String,
    val boardType: String,
    val size: String,
    val pricePerDay: Double,
    val location: String,
    val boardImageUrl: String,
    val owner: UserProfile
)

enum class RentalStatus {
    PENDING, APPROVED, COMPLETED, REJECTED, CANCELLED
}

data class RentalRequest(
    val requestId: String,
    val board: BoardForRent,
    val renter: UserProfile,
    val startDate: Long,
    val endDate: Long,
    val status: RentalStatus
)


fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
}

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


@Composable
fun OwnerInfoChip(name: String, imageUrl: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.hs_shortboard), // החלף ב-imageUrl עם Coil/Glide
            contentDescription = name,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RentalBoardCard(board: BoardForRent, modifier: Modifier = Modifier) {
    val cardColor = if(isSystemInDarkTheme()) OceanPalette.DarkSurface else Color.White
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(cardColor)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.hs_shortboard),
                contentDescription = board.boardName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = board.boardName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${board.boardType} - ${board.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hs_shortboard),
                        contentDescription = board.owner.name,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    Text(
                        text = board.owner.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "$${String.format("%.0f", board.pricePerDay)}/day",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RentalRequestCard(
    request: RentalRequest,
    isMyOffer: Boolean,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    val cardColor = if(isSystemInDarkTheme()) OceanPalette.DarkSurface else Color.White
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "For: ${request.board.boardName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OwnerInfoChip(
                        name = if(isMyOffer) "From: ${request.renter.name}" else "To: ${request.board.owner.name}",
                        imageUrl = if(isMyOffer) request.renter.imageUrl else request.board.owner.imageUrl
                    )
                    Text(
                        text = "Dates: ${formatDate(request.startDate)} - ${formatDate(request.endDate)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = request.status.name,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = colorForStatus(request.status)
                    )
                )
            }

            if (isMyOffer && request.status == RentalStatus.PENDING) {
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    OutlinedButton(onClick = { onReject(request.requestId) }) { Text("Reject") }
                    Button(onClick = { onApprove(request.requestId) }) { Text("Approve") }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalsHubScreen(
    communityBoards: List<BoardForRent>,
    myRentalRequests: List<RentalRequest>,
    myOfferRequests: List<RentalRequest>,
    onApproveOffer: (String) -> Unit,
    onRejectOffer: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Explore", "My Rentals", "My Offers")

    Column(
        modifier = Modifier
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
                    text = { Text(text = title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> RentalBoardList(boards = communityBoards)
            1 -> RentalRequestList(requests = myRentalRequests, isMyOffer = false)
            2 -> RentalRequestList(
                requests = myOfferRequests,
                isMyOffer = true,
                onApprove = onApproveOffer,
                onReject = onRejectOffer
            )
        }
    }
}

@Composable
fun RentalBoardList(boards: List<BoardForRent>) {
    if (boards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No boards available.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(boards.chunked(2)) { boardPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    boardPair.forEach { board ->
                        RentalBoardCard(
                            board = board,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (boardPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun RentalRequestList(
    requests: List<RentalRequest>,
    isMyOffer: Boolean,
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {}
) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No requests found.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(requests) { request ->
                RentalRequestCard(
                    request = request,
                    isMyOffer = isMyOffer,
                    onApprove = onApprove,
                    onReject = onReject
                )
            }
        }
    }
}


// --- שלב 5: תצוגה מקדימה עשירה ---

@Preview(showBackground = true, name = "Rentals Hub - Light Mode")
@Composable
fun RentalsHubScreenPreview() {
    // --- Mock Data ---
    val userMe = UserProfile("0", "Me", "")
    val userJohn = UserProfile("1", "John D.", "")
    val userJane = UserProfile("2", "Jane S.", "")

    val board1 = BoardForRent("b1", "Al Merrick Flyer", "Shortboard", "5'10\"", 45.0, "La Jolla, CA", "", userJohn)
    val board2 = BoardForRent("b2", "Stewart Longboard", "Longboard", "9'6\"", 50.0, "Santa Cruz, CA", "", userJane)
    val myBoard = BoardForRent("b3", "My Firewire Seaside", "Fish", "5'6\"", 60.0, "Venice, CA", "", userMe)

    val communityBoards = listOf(board1, board2)

    val myRentalRequests = listOf(
        RentalRequest("r1", board1, userMe, Date().time, Date().time, RentalStatus.APPROVED),
        RentalRequest("r2", board2, userMe, Date().time, Date().time, RentalStatus.REJECTED)
    )

    val myOfferRequests = listOf(
        RentalRequest("r3", myBoard, userJane, Date().time, Date().time, RentalStatus.PENDING),
        RentalRequest("r4", myBoard, userJohn, Date().time, Date().time, RentalStatus.APPROVED),
        RentalRequest("r5", myBoard, userJohn, Date().time, Date().time, RentalStatus.COMPLETED)
    )

    QuiverSyncTheme(darkTheme = false) {
        RentalsHubScreen(
            communityBoards = communityBoards,
            myRentalRequests = myRentalRequests,
            myOfferRequests = myOfferRequests,
            onApproveOffer = {},
            onRejectOffer = {}
        )
    }
}