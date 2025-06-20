package org.example.quiversync.presentation.widgets.rentals_screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.quiversync.presentation.screens.rentals.OwnerInfoChip
import org.example.quiversync.presentation.screens.rentals.RentalRequest
import org.example.quiversync.presentation.screens.rentals.RentalStatus
import org.example.quiversync.presentation.screens.rentals.colorForStatus
import org.example.quiversync.presentation.screens.rentals.formatDate
import org.example.quiversync.presentation.theme.OceanPalette

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