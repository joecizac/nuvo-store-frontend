package com.jozze.nuvo.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jozze.nuvo.domain.entity.Store
import nuvostore.core.designsystem.generated.resources.Res
import nuvostore.core.designsystem.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoreCard(
    store: Store,
    onClick: () -> Unit,
    onFavouriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            AsyncImage(
                model = store.imageUrl,
                contentDescription = store.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onFavouriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                )
            ) {
                Icon(
                    imageVector = if (store.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favourite",
                    tint = if (store.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = store.name,
                style = MaterialTheme.typography.titleLarge
            )
            store.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${store.rating}",
                    style = MaterialTheme.typography.labelLarge
                )
                store.distance?.let {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${it}km away",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
