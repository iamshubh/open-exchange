package com.skopenweb.exchange.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.skopenweb.exchange.design.theme.OpenExchangeTypography
import com.skopenweb.exchange.design.theme.Spacing
import com.skopenweb.exchange.resources.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(redirectToMainScreen: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .padding(all = Spacing.big),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val redirectCallback = rememberUpdatedState(redirectToMainScreen)
        LaunchedEffect(Unit) {
            delay(500)
            redirectCallback.value.invoke()
        }
        Image(
            painter = painterResource(R.drawable.logo_exchange),
            contentDescription = "logo",
            modifier = Modifier
                .fillMaxWidth(0.5f),
            contentScale = ContentScale.FillWidth // Scale the content
        )
        Spacer(Modifier.height(Spacing.big))
        Text(text = stringResource(R.string.open), style = OpenExchangeTypography.headlineMedium)
        Spacer(Modifier.height(Spacing.medium))
        Text(
            text = stringResource(R.string.exchange),
            style = OpenExchangeTypography.headlineMedium.copy(
                color = Color.Black.copy(alpha = 0.5f),
                fontWeight = FontWeight.W700,
            )
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(redirectToMainScreen = {})
}