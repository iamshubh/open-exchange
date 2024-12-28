package com.skopenweb.exchange.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.skopenweb.exchange.design.theme.Spacing

@Composable
fun AppIcon(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(com.skopenweb.exchange.resources.R.drawable.logo_exchange),
        tint = Color.Unspecified,
        contentDescription = null,
    )
}

@Composable
fun VerticalDivision(modifier: Modifier = Modifier) {
    VerticalDivider(
        modifier = modifier
            .padding(all = Spacing.extraSmall)
            .background(color = Color.DarkGray)
            .width(1.dp)
    )
}