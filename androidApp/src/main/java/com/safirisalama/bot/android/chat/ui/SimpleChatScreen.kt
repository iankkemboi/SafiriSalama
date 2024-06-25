package com.safirisalama.bot.android.chat.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safirisalama.bot.android.R
import com.safirisalama.bot.android.utils.HorizontalSpacer

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
fun SimpleChatScreen(viewModel: SimpleChatViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { it ->
        val destinations = viewModel.showDestinations()
        var userMessage by remember { mutableStateOf("") }
        var visible by remember { mutableStateOf(true) }
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(destinations[0]) }
        val isLoading = viewModel.isLoading.value
        Column(
            modifier = Modifier
                .padding(all = 20.dp)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                Column {
                    if (visible) {
                        Text(
                            text = stringResource(R.string.chat_message_header),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            ExposedDropdownMenuBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                expanded = expanded,
                                onExpandedChange = {
                                    expanded = !expanded
                                },
                            ) {
                                TextField(
                                    value = selectedText,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded,
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                )

                                ExposedDropdownMenu(
                                    modifier = Modifier.fillMaxWidth(),
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                ) {
                                    destinations.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(text = item) },
                                            onClick = {
                                                Log.v("Item", item)
                                                selectedText = item
                                                expanded = false
                                                viewModel.setDestination(item)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        var number by remember { mutableStateOf("3") }

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            value = number,
                            onValueChange = { number = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            label = { Text("For how many days?") },
                        )

                        Text(
                            text = stringResource(R.string.activities_text),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                        val buttonList: List<String> = listOf(
                            "Kayaking",
                            "Safari",
                            "Beach",
                            "Clubbing",
                            "Bowling",
                            "Zipline",
                            "Bungee",
                            "Giraffe Breakfast",
                        )


                        FlowRow(modifier = Modifier.padding(1.dp)) {
                            val selectedStates = remember { mutableStateMapOf<String, Boolean>() }

                            // Populate initial states as false for each item
                            buttonList.forEach { item ->
                                selectedStates.putIfAbsent(item, false)
                            }
                            for (listItem in buttonList) {
                                FilterChip(
                                    colors = FilterChipDefaults.filterChipColors(
                                        labelColor = colorResource(
                                            id = R.color.bot_message_text,
                                        ),
                                    ),
                                    onClick = {
                                        selectedStates[listItem] = !selectedStates[listItem]!!
                                        viewModel.setActivities(listItem)
                                    },
                                    label = {
                                        Text(listItem)
                                    },
                                    selected = selectedStates[listItem]!!,
                                    leadingIcon = if (selectedStates[listItem]!!) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = "Done icon",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            )
                                        }
                                    } else {
                                        null
                                    },
                                )
                                HorizontalSpacer(20.dp)
                            }
                        }

                        ElevatedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
                            onClick = {
                                visible = false
                                viewModel.sendMessage(
                                    "Please suggest a " + number + " day itinerary for " + viewModel.destination.value + " " + viewModel.generateDestinationsString() + " with respective contact details and addresses",
                                )
                                userMessage = ""

                            },
                        ) {
                            Text("Generate itinerary")
                        }

                    }



                    Column(modifier = Modifier.padding(16.dp)) {
                        LazyColumn(
                            modifier = Modifier.weight(1f), reverseLayout = true
                        ) {
                            items(viewModel.chatHistory.value.reversed()) { message ->
                                MessageChat(message, viewModel::sendMessage)
                            }
                            if (isLoading) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextField(value = userMessage,
                                onValueChange = { userMessage = it },
                                label = { Text("Enter your message") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.sendMessage(userMessage)
                                    userMessage = ""  // Clear input field
                                }, modifier = Modifier.align(Alignment.CenterVertically)
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
                        viewModel.errorMessage.value = null // Reset error message after showing

                    }
                }
            }
        }
    }
}





