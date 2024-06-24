package com.safirisalama.bot.android.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SimpleChatScreen(viewModel: SimpleChatViewModel = viewModel()) {
    var userMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(viewModel.chatHistory.value.reversed()) { message ->
                ChatMessage(message, viewModel::sendMessage)
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
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Send")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Suggested Prompts:")
        Column {
            viewModel.suggestedPrompts.value.forEach { prompt ->
                TextButton(onClick = {
                    viewModel.sendMessage(prompt)
                }) {
                    Text(prompt)
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessage(message: Message, onPromptClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Card(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp),
            )
        }
        if (!message.isUser && message.suggestions.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                message.suggestions.forEach { suggestion ->
                    FilterChip(
                        selected = false,
                        onClick = { onPromptClick(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimpleChatScreen()
}