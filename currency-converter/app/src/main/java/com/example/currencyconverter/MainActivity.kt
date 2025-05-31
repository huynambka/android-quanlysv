package com.example.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*

class MainActivity : ComponentActivity() {
    // Exchange rates
    private val exchangeRates = mapOf(
        "USD" to mapOf(
            "USD" to 1.0,
            "EUR" to 0.93,
            "GBP" to 0.79,
            "JPY" to 151.23,
            "VND" to 23185.0
        ),
        "EUR" to mapOf(
            "USD" to 1.08,
            "EUR" to 1.0,
            "GBP" to 0.85,
            "JPY" to 162.61,
            "VND" to 24925.0
        ),
        "GBP" to mapOf(
            "USD" to 1.27,
            "EUR" to 1.18,
            "GBP" to 1.0,
            "JPY" to 191.28,
            "VND" to 29412.0
        ),
        "JPY" to mapOf(
            "USD" to 0.0066,
            "EUR" to 0.0062,
            "GBP" to 0.0052,
            "JPY" to 1.0,
            "VND" to 153.31
        ),
        "VND" to mapOf(
            "USD" to 0.000043,
            "EUR" to 0.000040,
            "GBP" to 0.000034,
            "JPY" to 0.0065,
            "VND" to 1.0
        )
    )

    private val currencySymbols = mapOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥",
        "VND" to "₫"
    )

    private val currencyNames = listOf("USD", "EUR", "GBP", "JPY", "VND")
    private val currencyFullNames = mapOf(
        "USD" to "United States - Dollar",
        "EUR" to "Europe - Euro",
        "GBP" to "United Kingdom - Pound",
        "JPY" to "Japan - Yen",
        "VND" to "Vietnam - Dong"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CurrencyConverterApp(
                    exchangeRates = exchangeRates,
                    currencySymbols = currencySymbols,
                    currencyNames = currencyNames,
                    currencyFullNames = currencyFullNames
                )
            }
        }
    }
}

@Composable
fun CurrencyConverterApp(
    exchangeRates: Map<String, Map<String, Double>>,
    currencySymbols: Map<String, String>,
    currencyNames: List<String>,
    currencyFullNames: Map<String, String>
) {
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("VND") }
    var amountText by remember { mutableStateOf("") }
    var convertedAmount by remember { mutableStateOf("0") }

    // Calculate conversion when any input changes
    LaunchedEffect(fromCurrency, toCurrency, amountText) {
        if (amountText.isNotEmpty()) {
            try {
                val amount = amountText.toDouble()
                val rate = exchangeRates[fromCurrency]?.get(toCurrency) ?: 1.0
                val result = amount * rate

                val formatter = when (toCurrency) {
                    "JPY", "VND" -> NumberFormat.getNumberInstance(Locale.US).apply {
                        maximumFractionDigits = 0
                    }
                    else -> NumberFormat.getNumberInstance(Locale.US).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                }

                convertedAmount = formatter.format(result)
            } catch (e: Exception) {
                convertedAmount = "Error"
            }
        } else {
            convertedAmount = "0"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // From Currency Section
        FromCurrencySection(
            currencySymbol = currencySymbols[fromCurrency] ?: fromCurrency,
            amount = amountText,
            onAmountChange = { amountText = it },
            selectedCurrency = fromCurrency,
            currencies = currencyNames,
            currencyFullNames = currencyFullNames,
            onCurrencySelected = { fromCurrency = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // To Currency Section
        ToCurrencySection(
            currencySymbol = currencySymbols[toCurrency] ?: toCurrency,
            convertedAmount = convertedAmount,
            selectedCurrency = toCurrency,
            currencies = currencyNames,
            currencyFullNames = currencyFullNames,
            onCurrencySelected = { toCurrency = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Conversion Rate
        val rate = exchangeRates[fromCurrency]?.get(toCurrency) ?: 1.0
        val formattedRate = when {
            rate >= 1000 -> String.format("%.2f", rate)
            rate >= 100 -> String.format("%.2f", rate)
            rate >= 10 -> String.format("%.2f", rate)
            rate >= 1 -> String.format("%.2f", rate)
            else -> String.format("%.5f", rate)
        }

        Text(
            text = "1 $fromCurrency = $formattedRate $toCurrency",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Button just for show, conversion happens automatically
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FromCurrencySection(
    currencySymbol: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedCurrency: String,
    currencies: List<String>,
    currencyFullNames: Map<String, String>,
    onCurrencySelected: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currencySymbol,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = { Text("0") },
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currencyFullNames[selectedCurrency] ?: selectedCurrency,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currencyFullNames[currency] ?: currency) },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToCurrencySection(
    currencySymbol: String,
    convertedAmount: String,
    selectedCurrency: String,
    currencies: List<String>,
    currencyFullNames: Map<String, String>,
    onCurrencySelected: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currencySymbol,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            OutlinedTextField(
                value = convertedAmount,
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currencyFullNames[selectedCurrency] ?: selectedCurrency,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currencyFullNames[currency] ?: currency) },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}