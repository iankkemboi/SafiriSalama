package com.safirisalama.bot.android.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.safirisalama.bot.android.R
import com.safirisalama.bot.android.chat.data.Message
import com.safirisalama.bot.android.chat.data.MessageStatus
import com.safirisalama.bot.android.utils.HorizontalSpacer
import com.safirisalama.bot.android.utils.VerticalSpacer
import com.safirisalama.bot.models.Data
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

data class ChatScreenUiHandlers(
    val onSendMessage: (String) -> Unit = {},
    val onResendMessage: (Message) -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = getViewModel(),
) {
    val uiHandlers = ChatScreenUiHandlers(
        onSendMessage = viewModel::sendMessage,
        onResendMessage = viewModel::resendMessage,
    )
    val conversation by viewModel.conversation.observeAsState()
    val isSendingMessage by viewModel.isSendingMessage.observeAsState()
    val destinations = viewModel.showDestinations()
    val snackbarHostState = remember { SnackbarHostState() }
    var inputValue by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun sendMessage() {
        uiHandlers.onSendMessage(inputValue)
        inputValue = ""
        coroutineScope.launch {
            listState.animateScrollToItem(conversation?.list?.size ?: 0)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(destinations[0]) }
        var number by remember { mutableStateOf("3") }
        val buttonList = listOf(
            "Kayaking", "Safari", "Beach", "Clubbing", "Bowling", "Zipline", "Bungee", "Giraffe Breakfast"
        )
        val selectedStates = remember { mutableStateMapOf<String, Boolean>().apply {
            buttonList.forEach { put(it, false) }
        }}

        Column(modifier = Modifier.padding(paddingValues).padding(20.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                Column {
                    if (visible) {
                        Text(
                            text = stringResource(R.string.chat_message_header),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)) {
                            ExposedDropdownMenuBox(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                TextField(
                                    value = selectedText,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    modifier = Modifier.fillMaxWidth(),
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    destinations.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(text = item) },
                                            onClick = {
                                                selectedText = item
                                                expanded = false
                                                viewModel.setDestination(item)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            value = number,
                            onValueChange = { number = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("For how many days?") }
                        )
                        Text(
                            text = stringResource(R.string.activities_text),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        FlowRow(modifier = Modifier.padding(1.dp)) {
                            buttonList.forEach { item ->
                                FilterChip(
                                    colors = FilterChipDefaults.filterChipColors(labelColor = colorResource(id = R.color.bot_message_text)),
                                    onClick = {
                                        selectedStates[item] = !selectedStates[item]!!
                                        viewModel.setActivities(item)
                                    },
                                    label = { Text(item) },
                                    selected = selectedStates[item]!!,
                                    leadingIcon = if (selectedStates[item]!!) {
                                        { Icon(imageVector = Icons.Filled.Done, contentDescription = "Done icon", modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                                    } else null
                                )
                                HorizontalSpacer(20.dp)
                            }
                        }
                        ElevatedButton(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
                            onClick = {
                                visible = false
                                uiHandlers.onSendMessage(
                                    "Please suggest a $number day itinerary for ${viewModel.destination.value} ${viewModel.generateDestinationsString()} with respective contact details and addresses"
                                )
                            }
                        ) {
                            Text("Generate itinerary")
                        }
                    }

                    conversation?.list?.let { messagesList ->
                        MessageList(messagesList, listState, uiHandlers.onResendMessage, viewModel)
                    }
                }
            }
            Row(modifier = Modifier.background(color = Color.White).padding(10.dp)) {
                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text(stringResource(id = R.string.chat_message_placeholder)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions { sendMessage() },
                    modifier = Modifier.weight(1f)
                        .border(1.dp, colorResource(id = R.color.input_field_border), RoundedCornerShape(15.dp))
                        .background(Color.White, RoundedCornerShape(15.dp)),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, focusedIndicatorColor = Color.White, unfocusedIndicatorColor = Color.White, focusedTextColor = colorResource(id = R.color.bot_message_text))
                )
                HorizontalSpacer(8.dp)
                Button(
                    modifier = Modifier.height(56.dp),
                    onClick = { sendMessage() },
                    enabled = inputValue.isNotBlank() && isSendingMessage != true
                ) {
                    if (isSendingMessage == true) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = "Sending")
                    } else {
                        Image(painter = painterResource(id = R.drawable.send_message), contentDescription = "Send")
                    }
                }
            }
        }
    }
}

sealed class ListItem {
    data class MessageListItem(val onResendMessage: (Message) -> Unit, val message: Message) : ListItem()
    data class FlightSearchItem(val flightList: List<Data>) : ListItem()
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageList(
    messagesList: List<Message>,
    listState: LazyListState,
    onResendMessage: (Message) -> Unit,
    viewModel: ChatViewModel,
) {
    LazyColumn(state = listState) {
        items(messagesList) { message ->
            MessageListItem(onResendMessage = onResendMessage, message = message)
        }
    }
}

@Composable
fun MessageListItem(
    onResendMessage: (Message) -> Unit,
    message: Message,
) {
    Row {
        if (message.isFromUser) {
            HorizontalSpacer(width = 16.dp)
            Box(modifier = Modifier.weight(1f))
        }
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (message.isFromUser) colorResource(R.color.white) else colorResource(R.color.bot_message_text),
            textAlign = if (message.isFromUser) TextAlign.End else TextAlign.Start,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                .background(
                    when (message.messageStatus) {
                        MessageStatus.Error -> MaterialTheme.colorScheme.errorContainer
                        else -> if (message.isFromUser) colorResource(R.color.user_message) else colorResource(R.color.bot_message)
                    }
                )
                .clickable(enabled = message.messageStatus == MessageStatus.Error) { onResendMessage(message) }
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
        if (!message.isFromUser) {
            HorizontalSpacer(width = 16.dp)
            Box(modifier = Modifier.weight(1f))
        }
    }
    if (message.messageStatus == MessageStatus.Sending) {
        Text(
            text = stringResource(R.string.chat_message_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalSpacer(width = 32.dp)
    }
    if (message.messageStatus == MessageStatus.Error) {
        Row(modifier = Modifier.clickable { onResendMessage(message) }) {
            Box(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.chat_message_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
    VerticalSpacer(height = 8.dp)
}


