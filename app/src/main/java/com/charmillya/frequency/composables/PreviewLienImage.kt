package com.charmillya.frequency.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.charmillya.frequency.R
import sv.lib.squircleshape.SquircleShape

@Composable
fun PreviewLienImage(
    name: String,
    imageUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = SquircleShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(SquircleShape(20.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().clickable { onClick() }) {
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.photo_of_desc, name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}