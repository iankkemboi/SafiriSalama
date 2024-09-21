package com.safirisalama.bot.android.chat.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.safirisalama.bot.android.R
import com.safirisalama.bot.android.utils.HorizontalSpacer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GenerateItineraryScreen(
    viewModel: SimpleChatViewModel = viewModel(),
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { it ->
        val destinations = viewModel.showDestinations()
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(destinations[0]) }

        Column(
            modifier = Modifier
                .padding(all = 20.dp)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                Column {
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
                            val message =
                                "Please suggest a " + number + " day itinerary for " + viewModel.destination.value + " " + viewModel.generateDestinationsString() + " with respective contact details and addresses"

                            navController.navigate("home/$message")


                        },
                    ) {
                        Text("Generate itinerary")
                    }

                }
            }
        }
    }
}