package com.skopenweb.exchange.conversion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.skopenweb.exchange.conversion.ui.model.CurrencyUiItem
import com.skopenweb.exchange.conversion.ui.model.dummyConvertedCurrencyList
import com.skopenweb.exchange.conversion.ui.model.dummyCurrency
import com.skopenweb.exchange.core.ui.VerticalDivision
import com.skopenweb.exchange.design.theme.OpenExchangeTypography
import com.skopenweb.exchange.design.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeCurrencyModalSheet(
    currencies: List<CurrencyUiItem>,
    show: Boolean,
    onClose: () -> Unit,
    onCurrencySelected: (CurrencyUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!show) return
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        modifier = modifier,
        sheetState = bottomSheetState,
        onDismissRequest = {
            onClose.invoke()
        }
    ) {
        CurrencyItemListView(
            items = currencies,
            onSelection = { currencyId ->
                scope.launch {
                    bottomSheetState.hide()
                }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        onCurrencySelected.invoke(currencyId)
                        onClose.invoke()
                    }
                }
            }
        )
    }
}

@Composable
fun CurrencyItemView(
    item: CurrencyUiItem,
    onSelection: (CurrencyUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    with(item) {
        Row(
            modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = Spacing.medium)
                .clickable {
                    onSelection.invoke(item)
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(0.2f),
                text = id,
                style = OpenExchangeTypography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            VerticalDivision(Modifier.padding(horizontal = Spacing.small))
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                style = OpenExchangeTypography.bodyMedium
            )
            RadioButton(selected = item.selected, onClick = { onSelection.invoke(item) })
        }
    }
}

@Composable
fun CurrencyItemListView(
    items: List<CurrencyUiItem>,
    onSelection: (CurrencyUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium),
        contentPadding = PaddingValues(vertical = Spacing.medium),
    ) {
        items(items, key = { it.id }) {
            CurrencyItemView(item = it, onSelection = onSelection)
            HorizontalDivider()
        }
    }
}


@Preview
@Composable
fun CurrencyItemListPreview(modifier: Modifier = Modifier) {
    CurrencyItemListView(dummyCurrency, onSelection = {})
}

@Preview
@Composable
fun CurrencyItemPreview(modifier: Modifier = Modifier) {
    CurrencyItemView(
        item = CurrencyUiItem(
            id = "USD",
            text = "American Dollar",
            selected = true
        ), onSelection = {})
}

@Preview
@Composable
private fun ConvertedCurrencyUiItem() {
    ConvertedCurrencyUiListView(dummyConvertedCurrencyList)
}
