package org.example.quiversync.presentation.widgets.rentals_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.quiversync.R
import org.example.quiversync.domain.model.RentalRequest
import org.example.quiversync.domain.model.RentalStatus
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.rentals.my_rentals.MyRentRequest
import org.example.quiversync.features.rentals.my_rentals.MyRentalsViewModel
import org.example.quiversync.presentation.screens.rentals.colorForStatus
import org.example.quiversync.presentation.theme.OceanPalette


@Composable
fun MyOffersRequestsList(
    requests: List<MyRentRequest>,
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(requests) { request ->
            RentalRequestCard(
                request = request,
                isMyOffer = true,
                onApprove = onApprove,
                onReject = onReject
            )
        }
    }
}
@Composable
fun RentalRequestList(
    requests: List<MyRentRequest>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(requests) { request ->
            RentalRequestCard(request = request)
        }
    }
}


@Composable
fun RentalRequestCard(
    request: MyRentRequest,
    isMyOffer: Boolean = false,
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {}
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
                        text = "For: ${request.boardModel}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OwnerInfoChip(
                        name = if(isMyOffer) "From: ${request.renterName}" else "To: ${request.ownerName}",
                        imageUrl = if(isMyOffer) request.renterImageUrl else request.ownerImageUrl
                    )
                    Text(
                        text = "Dates: ${request.startDate} - ${request.endDate}",
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
