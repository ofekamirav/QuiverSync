import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.quiversync.android.R
import com.example.quiversync.android.navigation.RootNavGraph
import com.ramcosta.composedestinations.annotation.Destination

data class Surfboard(
    val model: String,
    val company: String,
    val height: String,
    val width: String,
    val volume: String,
    val imageRes: Int,
    val addedDate: String
)

@RootNavGraph
@Destination<RootNavGraph>
@Composable
fun QuiverScreen() {
    val boards = listOf(
        Surfboard(
            model = "Holly Grail",
            company = "Hayden Shapes",
            imageRes = R.drawable.hs_shortboard,
            height = "6'2\"",
            volume = "32L",
            width = "19\"",
            addedDate = "2024-05-01"
        ),
        Surfboard(
            model = "FRK+",
            company = "Slater Designs",
            imageRes = R.drawable.sd_surfboard,
            height = "5'8\"",
            volume = "28L",
            width = "20 1/4\"",
            addedDate = "2024-04-15"
        ),
    )

    var selectedBoard by remember { mutableStateOf<Surfboard?>(null) }
    
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf9fafb))
                .padding(16.dp)
        ) {
            Text(
                "My Quiver",
                color = Color(0xFF3366FF),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(22.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(boards.size) { index ->
                    BoardCard(
                        board = boards[index],
                        onLongClick = { selectedBoard = boards[index] }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            FloatingActionButton(
                onClick = { /* TODO: תנו כאן את ההוספה */ },
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp)
                    .align(alignment = Alignment.End),
                containerColor = Color.Unspecified,
                contentColor = Color.Unspecified,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add Surfboard"
                )
            }
        }
    // Show the dialog for the selected board
    selectedBoard?.let { board ->
        SurfboardDetailDialog(
            board = board,
            visible = selectedBoard != null,
            onDismiss = { selectedBoard = null }
        )
    }
}

@Composable
fun BoardCard(board: Surfboard, onLongClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFE0E7FF) else Color(0xFFF5F5F5),
        label = "BackgroundColorAnimation"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() }
                )
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = board.imageRes),
                contentDescription = "Surfboard Image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                board.model,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
        }
    }
}


@Composable
fun SurfboardDetailDialog(board: Surfboard, visible: Boolean, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ){
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr){
            Dialog(onDismissRequest = onDismiss) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(16.dp),
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = board.model,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Image(
                            painter = painterResource(id = board.imageRes),
                            contentDescription = "Surfboard Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Company:", fontWeight = FontWeight.SemiBold)
                            Text(board.company)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Height:", fontWeight = FontWeight.SemiBold)
                            Text(board.height)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Width:", fontWeight = FontWeight.SemiBold)
                            Text(board.width)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Volume:", fontWeight = FontWeight.SemiBold)
                            Text(board.volume)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Added:", fontWeight = FontWeight.SemiBold)
                            Text(board.addedDate)
                        }
                    }
                }
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun QuiverScreenPreview() {
    QuiverScreen()
}