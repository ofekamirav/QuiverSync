package org.example.quiversync.presentation.widgets.quiver_screen


import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.domain.model.SurfboardType

data class BoardTypeInfo(
    val type: SurfboardType,
    @DrawableRes val imageRes: Int
)

@Composable
fun SelectableBoardTypeGrid(
    selectedType: SurfboardType,
    onTypeSelected: (SurfboardType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(boardTypes.size) { index ->
            SelectableBoardTypeCard(
                name = boardTypes[index].type.name.lowercase().replaceFirstChar { it.uppercase() },
                imageRes =  boardTypes[index].imageRes,
                isSelected = ( boardTypes[index].type == selectedType),
                onClick = { onTypeSelected(boardTypes[index].type) }
            )
        }
    }
}

val boardTypes = listOf(
    BoardTypeInfo(SurfboardType.SHORTBOARD, R.drawable.shortboard),
    BoardTypeInfo(SurfboardType.LONGBOARD, R.drawable.longboard),
    BoardTypeInfo(SurfboardType.FISHBOARD, R.drawable.fish),
    BoardTypeInfo(SurfboardType.FUNBOARD, R.drawable.funboard),
    BoardTypeInfo(SurfboardType.SOFTBOARD, R.drawable.softboard)
)