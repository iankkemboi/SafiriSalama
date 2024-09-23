package com.safirisalama.bot.android.chat.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safirisalama.bot.android.R
import com.safirisalama.bot.models.Data
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageChat(message: Message, onPromptClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Card(
            modifier = Modifier.padding(4.dp), colors = CardDefaults.cardColors(
                contentColor = if (message.isUser) {
                    colorResource(R.color.white)
                } else {
                    colorResource(R.color.bot_message_text)
                }, containerColor = if (message.isUser) {
                    colorResource(R.color.user_message)
                } else {
                    colorResource(R.color.bot_message)
                }
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp),
            )
        }
        if (!message.isUser && message.suggestions.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                message.suggestions.forEach { suggestion ->
                    FilterChip(selected = false,
                        onClick = { onPromptClick(suggestion) },
                        label = { Text(suggestion) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SimpleChatScreen(viewModel: SimpleChatViewModel = viewModel(), message: String) {
    val snackbarHostState = remember { SnackbarHostState() }
    val chatHistory = viewModel.chatHistory.value
    val isLoading = viewModel.isLoading.value
    val listState = rememberLazyListState()
    var userMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(message) {
        viewModel.sendMessage(message)
    }

    // Auto-scroll to the bottom when a new message is added
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
                reverseLayout = false,
                state = listState,
            ) {
                items(chatHistory) { item ->
                    when (item) {
                        is SimpleChatItem.MessageItem -> MessageChat(
                            item.message,
                            viewModel::sendMessage
                        )

                        is SimpleChatItem.FlightSearch -> SearchFlight(item.flights)
                    }
                }

                item {
                    if (isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Input field and send button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                TextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    placeholder = { Text("Enter your message") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Button(
                    onClick = {
                        if (userMessage.isNotBlank()) {
                            viewModel.sendMessage(userMessage)
                            userMessage = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Send")
                }
            }
        }
    }

    val errorMessage = viewModel.errorMessage.value
    if (errorMessage != null) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Long
            )
            viewModel.errorMessage.value = null
        }
    }
}

// Define a sealed class to represent different types of chat items
sealed class SimpleChatItem {
    data class MessageItem(val message: com.safirisalama.bot.android.chat.ui.Message) :
        SimpleChatItem()

    data class FlightSearch(val flights: List<Data>) : SimpleChatItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFlight(flightList: List<Data>) {
    var errorMessage by remember { mutableStateOf("No flight prices found") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (flightList.isEmpty()) {
            Text(
                text = errorMessage,
                modifier = Modifier.padding(8.dp),
                color = colorResource(id = R.color.bot_message_text)
            )
        } else {
            Text(
                text = "Flight Search Results",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp),
                color = colorResource(id = R.color.bot_message_text)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(flightList) { flight ->
                    FlightCard(flight)
                }
            }
        }
    }
}

fun formatDuration(isoDuration: String): String {
    return try {
        val duration = Duration.parse(isoDuration)
        val totalSeconds = duration.seconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60

        when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "Less than 1m"
        }
    } catch (e: Exception) {
        "Invalid Duration"
    }
}
fun formatTimestamp(timestamp: String?): String {
    if (timestamp == null) return "N/A"
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime = LocalDateTime.parse(timestamp, inputFormatter)
        val outputFormatter = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.ENGLISH)
        dateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        "Invalid Date"
    }
}

@Composable
fun FlightCard(flight: Data) {
    val airlineCarrierCodes = mapOf(
        "AA" to "American Airlines",
        "AC" to "Air Canada",
        "AF" to "Air France",
        "AI" to "Air India",
        "AK" to "AirAsia",
        "AM" to "Aeromexico",
        "AS" to "Alaska Airlines",
        "AY" to "Finnair",
        "AZ" to "Alitalia",
        "BA" to "British Airways",
        "CA" to "Air China",
        "CX" to "Cathay Pacific",
        "DL" to "Delta Air Lines",
        "EK" to "Emirates",
        "ET" to "Ethiopian Airlines",
        "EY" to "Etihad Airways",
        "FJ" to "Fiji Airways",
        "FR" to "Ryanair",
        "GA" to "Garuda Indonesia",
        "IB" to "Iberia",
        "JL" to "Japan Airlines",
        "KE" to "Korean Air",
        "KL" to "KLM Royal Dutch Airlines",
        "LA" to "LATAM Airlines",
        "LH" to "Lufthansa",
        "LX" to "Swiss International Air Lines",
        "MH" to "Malaysia Airlines",
        "MS" to "EgyptAir",
        "MU" to "China Eastern Airlines",
        "NH" to "All Nippon Airways",
        "NZ" to "Air New Zealand",
        "OS" to "Austrian Airlines",
        "QF" to "Qantas",
        "QR" to "Qatar Airways",
        "SA" to "South African Airways",
        "SK" to "Scandinavian Airlines",
        "SQ" to "Singapore Airlines",
        "TG" to "Thai Airways",
        "TK" to "Turkish Airlines",
        "UA" to "United Airlines",
        "U2" to "easyJet",
        "VA" to "Virgin Australia",
        "VS" to "Virgin Atlantic",
        "WN" to "Southwest Airlines",
        "WS" to "WestJet"
    )
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .width(280.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.bot_message),
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            val itineraries = flight.itineraries[0]
            val departureTime = formatTimestamp(itineraries.segments[0].departure?.at)
            val arrivalTime = formatTimestamp(itineraries.segments.last().arrival?.at)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${flight.price?.total} ${"€"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.user_message)
                )
                Icon(
                    imageVector = Icons.Default.Flight,
                    contentDescription = "Flight",
                    tint = colorResource(id = R.color.user_message),
                    modifier = Modifier.rotate(rotationState)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Duration",
                    tint = colorResource(id = R.color.bot_message_text),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                itineraries.duration?.let {
                    Text(
                        text = formatDuration(it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.bot_message_text)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "From",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorResource(id = R.color.bot_message_text)
                    )
                    Text(
                        text = itineraries.segments[0].departure?.iataCode ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.bot_message_text)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "To",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorResource(id = R.color.bot_message_text)
                    )
                    Text(
                        text = itineraries.segments.last().arrival?.iataCode ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.bot_message_text)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = colorResource(id = R.color.input_field_border), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Departure: $departureTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.bot_message_text)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Arrival: $arrivalTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.bot_message_text)
                )
                Spacer(modifier = Modifier.height(4.dp))
                val airlineName = airlineCarrierCodes[itineraries.segments.last().carrierCode]
                    ?: "Unknown Airline"

                Text(
                    text = "Airline: $airlineName",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.bot_message_text)
                )
            }
        }
    }
}



