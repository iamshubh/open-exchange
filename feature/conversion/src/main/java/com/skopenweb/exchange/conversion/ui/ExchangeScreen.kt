package com.skopenweb.exchange.conversion.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skopenweb.exchange.conversion.R
import com.skopenweb.exchange.conversion.ui.model.ConvertedCurrencyUiItem
import com.skopenweb.exchange.conversion.ui.model.ExchangeUiContent
import com.skopenweb.exchange.conversion.ui.model.UiAction
import com.skopenweb.exchange.conversion.ui.model.UiState
import com.skopenweb.exchange.conversion.ui.model.dummyUiData
import com.skopenweb.exchange.core.ui.AppIcon
import com.skopenweb.exchange.design.theme.OpenExchangeTypography
import com.skopenweb.exchange.design.theme.Spacing

typealias ActionHandler = (UiAction) -> Unit

@Composable
fun ExchangeScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: ExchangeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        is UiState.Error -> {
            ErrorExchangeScreen(
                modifier = modifier,
                message = (uiState as UiState.Error).message,
                onRetry = viewModel::onRetry,
            )
        }

        is UiState.Loading -> {
            LoadingExchangeScreen()
        }

        is UiState.Success -> {
            ExchangeScreenContent(
                modifier = modifier,
                uiContent = (uiState as UiState.Success<ExchangeUiContent>).data,
                handler = viewModel::handleAction,
            )
        }
    }
}


@Composable
fun LoadingExchangeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun ErrorExchangeScreen(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = Spacing.big),
        verticalArrangement = Arrangement.spacedBy(
            Spacing.big,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppIcon(modifier.size(120.dp))
        Text(
            text = message,
            style = OpenExchangeTypography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Button(modifier = Modifier.padding(horizontal = Spacing.medium), onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun ExchangeScreenContent(
    modifier: Modifier = Modifier,
    uiContent: ExchangeUiContent,
    handler: ActionHandler,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(
                start = Spacing.medium,
                end = Spacing.medium,
                top = Spacing.big,
                bottom = Spacing.extraSmall,
            )
    ) {
        var showCurrencySheet by remember { mutableStateOf(false) }
        var amount by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = amount,
            onValueChange = {
                if (it.length <= 20) {
                    amount = it
                    handler(UiAction.ConvertAction(amount, uiContent.currentSelectedCurrency))
                }
            },
            label = {
                Text(
                    stringResource(R.string.enter_amount),
                    style = OpenExchangeTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                )
            },
            textStyle = OpenExchangeTypography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                fontSize = 24.sp,
            ),
            maxLines = 1
        )
        Spacer(Modifier.height(Spacing.medium))
        ConvertButton(modifier = Modifier.align(Alignment.End),
            text = uiContent.currentSelectedCurrency?.id
                ?: stringResource(R.string.select_currency),
            onClick = {
                showCurrencySheet = true
            })
        Spacer(Modifier.height(Spacing.medium))
        ConvertedCurrencySection(
            items = uiContent.convertedCurrencyList,
            message = uiContent.errorText
        )
        ExchangeCurrencyModalSheet(
            currencies = uiContent.currencyList,
            show = showCurrencySheet,
            onClose = {
                showCurrencySheet = false
            },
            onCurrencySelected = {
                showCurrencySheet = false
                handler(UiAction.CurrencySelection(amount, it))
            },
        )
    }
}

@Composable
fun ConvertButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .border(1.dp, color = Color.Black, shape = RectangleShape)
            .padding(horizontal = Spacing.small, vertical = Spacing.small)
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = OpenExchangeTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.width(Spacing.medium))
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
    }
}

@Composable
fun ConvertedCurrencySection(
    items: List<ConvertedCurrencyUiItem>,
    modifier: Modifier = Modifier,
    @StringRes message: Int? = null,
) {
    if (items.isNotEmpty()) {
        ConvertedCurrencyUiListView(items = items)
    } else {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(message ?: R.string.default_msg_no_converted_currency),
                style = OpenExchangeTypography.bodyMedium
            )
        }
    }
}

@Composable
fun ConvertedCurrencyUiItemView(item: ConvertedCurrencyUiItem, modifier: Modifier = Modifier) {
    with(item) {
        Column(
            modifier
                .width(80.dp)
                .border(1.dp, color = Color.Black, shape = RectangleShape)
                .padding(Spacing.extraSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = currentValue,
                style = OpenExchangeTypography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(Modifier.height(Spacing.small))
            Text(text = id, style = OpenExchangeTypography.labelMedium.copy(color = Color.DarkGray))
        }
    }
}

@Composable
fun ConvertedCurrencyUiListView(
    items: List<ConvertedCurrencyUiItem>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        contentPadding = PaddingValues(Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        items(items, key = { it.id }) {
            ConvertedCurrencyUiItemView(item = it)
        }
    }
}

@Preview
@Composable
fun ExchangeScreenContentPreview() {
    ExchangeScreenContent(uiContent = dummyUiData, handler = {})
}

@Preview
@Composable
private fun ExchangeScreenPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium, Alignment.CenterVertically)
    ) {
        ErrorExchangeScreen(message = "Something went wrong", onRetry = {})
    }
}