package com.safirisalama.bot.android.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.* // ktlint-disable no-wildcard-imports
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val conversation = viewModel.conversation
    val isSendingMessage = viewModel.isSendingMessage
    val destinations = viewModel.showDestinations()
    val snackbarHostState = remember { SnackbarHostState() }
    var inputValue by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversationState by conversation.observeAsState()
    val isSendingMessageState by isSendingMessage.observeAsState()
    var previousList by remember { mutableStateOf<List<Message>?>(null) }

    fun sendMessageUiHandler(input: String) {
        uiHandlers.onSendMessage(input)
    }

    fun sendMessage() {
        sendMessageUiHandler(inputValue)
        inputValue = ""
        coroutineScope.launch {
            listState.animateScrollToItem(conversationState?.list?.size ?: 0)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { it ->
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(destinations[0]) }
        var messagesList by remember { mutableStateOf(listOf<Message>()) }
        var cleanMessagesList by remember { mutableStateOf(listOf<Message>()) }
        var message by remember { mutableStateOf(Message(text = "", isFromUser = true)) }

        var listItems = remember { mutableStateListOf<ListItem>() }
        fun addMessageListItem(message: Message, onResendMessage: (Message) -> Unit) {
            listItems.add(
                ListItem.MessageListItem(
                    onResendMessage = onResendMessage,
                    message = message,
                ),
            )
        }

        conversationState?.let { it1 ->
            messagesList = it1.list
        }


        Column(
            modifier = Modifier
                .padding(all = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
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
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                ) {
                                    destinations.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(text = item) },
                                            onClick = {
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
                                sendMessageUiHandler(
                                    "Please suggest a " + number + " day itinerary for " + viewModel.destination.value + " " +
                                            viewModel.generateDestinationsString() +
                                            " with respective contact details and addresses",
                                )
                            },
                        ) {
                            Text("Generate itinerary")
                        }
                    }

                     if (messagesList.isNotEmpty()) {
                         if (listItems.size > 0) {
                             listItems.removeRange(0, listItems.size - 1)
                         }
                         for (messageText in messagesList) {
                             addMessageListItem(messageText, uiHandlers.onResendMessage)
                         }
                     }
                    /* LaunchedEffect(messagesList) {
                         if (!cleanMessagesList.contains(it)){
                             cleanMessagesList[0] = it
                         }
                         addMessageListItem(it, uiHandlers.onResendMessage)
                     }
                     messagesList.co
                    while( messagesList)
                     { it ->
                         if (!cleanMessagesList.contains(it)){
                             cleanMessagesList[0] = it
                         }
                         addMessageListItem(it, uiHandlers.onResendMessage)
                     }*/
                    // MyList(listItems = listItems)

                    MessageList(
                        messagesList,
                        listState,
                        uiHandlers.onResendMessage,
                        viewModel,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(all = 10.dp),
            ) {
                TextField(
                    value = inputValue,
                    onValueChange = {
                        inputValue = it
                    },
                    label = { Text(stringResource(id = R.string.chat_message_placeholder)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions {
                        sendMessage()
                    },

                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.input_field_border),
                            shape = RoundedCornerShape(size = 15.dp),
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(size = 15.dp),
                        ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        focusedTextColor = colorResource(id = R.color.bot_message_text),
                    ),
                )
                HorizontalSpacer(8.dp)
                Button(

                    modifier = Modifier.height(56.dp),
                    onClick = {
                        sendMessage()
                    },
                    enabled = inputValue.isNotBlank() && isSendingMessageState != true,
                ) {
                    if (isSendingMessageState == true) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sending",
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.send_message),
                            contentDescription = "Send",
                        )
                    }
                }
            }
        }
    }
}

sealed class ListItem {
    data class MessageListItem(
        val onResendMessage: (Message) -> Unit,
        val message: Message,
    ) : ListItem()

    data class FlightSearchItem(val flightList: List<Data>) : ListItem()
}
/*
@Composable
fun MyList(listItems: List<ListItem>) {
    LazyColumn {
        items(listItems) { item ->
            when (item) {
                is ListItem.MessageListItem -> MessageListItem(
                    onResendMessage = item.onResendMessage,
                    message = item.message,
                )
            }
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageList(
    messagesList: List<Message>,
    listState: LazyListState,
    onResendMessage: (Message) -> Unit,
    viewModel: ChatViewModel,
) {
    LazyColumn(
        state = listState,
    ) {
        items(messagesList) { message ->
            MessageListItem(
                onResendMessage = onResendMessage,
                message = message,
            )
        }
    }
    /*viewModel.flightList.observeAsState().let {
        it.value?.let { it1 -> SearchFlight(flightList = it1) }
    }*/
}

@Composable
fun MessageListItem(
    onResendMessage: (Message) -> Unit,
    message: Message,
) {
    Row {
        if (message.isFromUser) {
            HorizontalSpacer(width = 16.dp)
            Box(
                modifier = Modifier.weight(weight = 1f),
            )
        }
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (message.isFromUser) {
                colorResource(R.color.white)
            } else {
                colorResource(R.color.bot_message_text)
            },
            textAlign = if (message.isFromUser) {
                TextAlign.End
            } else {
                TextAlign.Start
            },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (message.messageStatus == MessageStatus.Error) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        if (message.isFromUser) {
                            colorResource(R.color.user_message)
                        } else {
                            colorResource(R.color.bot_message)
                        }
                    },
                )
                .clickable(enabled = message.messageStatus == MessageStatus.Error) {
                    onResendMessage(message)
                }
                .padding(vertical = 8.dp, horizontal = 16.dp),

            )
        if (!message.isFromUser) {
            HorizontalSpacer(width = 16.dp)
            Box(
                modifier = Modifier.weight(weight = 1f),
            )
        }
    }
    if (message.messageStatus == MessageStatus.Sending) {
        Text(
            text = stringResource(R.string.chat_message_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        HorizontalSpacer(width = 32.dp)
    }
    if (message.messageStatus == MessageStatus.Error) {
        Row(
            modifier = Modifier
                .clickable {
                    onResendMessage(message)
                },
        ) {
            Box(
                modifier = Modifier.weight(weight = 1f),
            )
            Text(
                text = stringResource(R.string.chat_message_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
    VerticalSpacer(height = 8.dp)
}