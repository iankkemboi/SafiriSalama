package com.safirisalama.bot.android.chat.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
fun SimpleChatScreen(viewModel: SimpleChatViewModel = viewModel(), message: String) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { it ->
        var userMessage by remember { mutableStateOf("") }

        val chatHistory = viewModel.chatHistory.value
        val isLoading = viewModel.isLoading.value
        val listState = rememberLazyListState()
        LaunchedEffect(message) {
            viewModel.sendMessage(message)
        }
        Column(
            modifier = Modifier
                .padding(all = 20.dp)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                Column {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val previousChatHistorySize = remember { chatHistory.size }
/*
                        LaunchedEffect(chatHistory.size) {
                            if (chatHistory.size > previousChatHistorySize) {
                                if (chatHistory.size > 1) {
                                    listState.scrollToItem(1) // Scroll to the top when new messages are added

                                }
                            }
                        }*/
                        LazyColumn(
                            modifier = Modifier.weight(1f), reverseLayout = true,
                            state = listState,
                        ) {
                            items(viewModel.chatHistory.value.reversed()) { message ->
                                MessageChat(message, viewModel::sendMessage)
                            }

                        }
                        if (isLoading) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.chat_message_loading),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    HorizontalSpacer(width = 32.dp)
                                }
                            }


                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextField(
                                value = userMessage,
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





