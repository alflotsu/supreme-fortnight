package io.peng.sparrowdelivery.presentation.features.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.theme.StitchTheme
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    driverName: String,
    driverPhone: String,
    driverVehicle: String,
    driverPlateNumber: String,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Initialize chat with driver info
    LaunchedEffect(driverName) {
        viewModel.initializeChat(
            driverName = driverName,
            driverPhone = driverPhone,
            driverVehicle = driverVehicle,
            driverPlateNumber = driverPlateNumber
        )
    }
    
    // Scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    // Mark messages as read when screen is viewed
    LaunchedEffect(Unit) {
        viewModel.markMessagesAsRead()
    }
    
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Scaffold(
            containerColor = stitchColors.background,
            topBar = {
                ChatTopBar(
                    driverName = driverName,
                    isOnline = uiState.chatInfo?.isDriverOnline ?: true,
                    lastSeen = uiState.chatInfo?.getLastSeenText() ?: "Online",
                    onBackClick = onBackClick,
                    onCallClick = onCallClick
                )
            },
            bottomBar = {
                ChatInputField(
                    text = uiState.currentMessageText,
                    onTextChange = viewModel::updateMessageText,
                    onSendClick = {
                        viewModel.sendMessage()
                        coroutineScope.launch {
                            delay(100) // Small delay to ensure keyboard has time to process
                            keyboardController?.hide()
                        }
                    },
                    isSending = uiState.isSendingMessage,
                    focusRequester = focusRequester
                )
            },
            modifier = modifier
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(stitchColors.background)
                    .padding(innerPadding)
            ) {
                // Chat message list
                ChatMessageList(
                    messages = uiState.messages,
                    listState = listState,
                    isTyping = uiState.isTyping,
                    driverName = driverName
                )
                
                // Loading indicator
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                // Error message
                uiState.errorMessage?.let { error ->
                    StitchCard(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        backgroundColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StitchText(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.weight(1f))
//                            TextButton(
//                                text = "Dismiss",
//                                onClick = { viewModel.clearErrorMessage() },
//                                variant = StitchButtonVariant.Outline
//                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    driverName: String,
    isOnline: Boolean,
    lastSeen: String,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Chat with Driver",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
                Text(
                    text = driverName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xFF96C5A9)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onCallClick) {
                Icon(Icons.Default.Phone, contentDescription = "Call driver", tint = Color.White)
            }
            IconButton(onClick = { /* Menu options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF122118),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    isTyping: Boolean,
    driverName: String
) {
    val systemMessageBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF9EB2EF),
            Color(0xFF94ADEC)
        )
    )
    
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { message ->
            when (message.type) {
                MessageType.SYSTEM_NOTIFICATION -> {
                    SystemMessageBubble(
                        message = message,
                        backgroundBrush = systemMessageBrush
                    )
                }
                else -> {
                    if (message.sender == MessageSender.CUSTOMER) {
                        CustomerMessageBubble(message = message)
                    } else {
                        DriverMessageBubble(message = message)
                    }
                }
            }
        }
        
        item {
            AnimatedVisibility(
                visible = isTyping,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$driverName is typing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TypingIndicator()
                }
            }
        }
    }
}

@Composable
fun SystemMessageBubble(
    message: ChatMessage,
    backgroundBrush: Brush
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundBrush)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CustomerMessageBubble(message: ChatMessage) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 64.dp, end = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 4.dp
                    )
                )
                .background(Color(0xFF38E07B))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color(0xFF122118)
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = message.getFormattedTime(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            // Message status indicator
            when (message.status) {
                MessageStatus.SENDING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.dp,
                        color = Color(0xFF38E07B).copy(alpha = 0.6f)
                    )
                }
                MessageStatus.SENT -> {
                    Text(
                        text = "✓",
                        fontSize = 10.sp,
                        color = Color(0xFF38E07B).copy(alpha = 0.6f)
                    )
                }
                MessageStatus.DELIVERED -> {
                    Text(
                        text = "✓✓",
                        fontSize = 10.sp,
                        color = Color(0xFF38E07B).copy(alpha = 0.6f)
                    )
                }
                MessageStatus.READ -> {
                    Text(
                        text = "✓✓",
                        fontSize = 10.sp,
                        color = Color(0xFF38E07B)
                    )
                }
                MessageStatus.FAILED -> {
                    Text(
                        text = "!",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun DriverMessageBubble(message: ChatMessage) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 64.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(Color(0xFF264532))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = message.getFormattedTime(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = Color(0xFF96C5A9),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF264532))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800,
                        easing = LinearEasing,
                        delayMillis = index * 150
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$index"
            )
            
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = (-offset).dp)
                    .background(
                        Color(0xFF96C5A9),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun ChatInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean,
    focusRequester: FocusRequester
) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Color(0xFF122118)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text field
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        "Type a message...", 
                        color = Color(0xFF96C5A9),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (text.isNotEmpty()) {
                            onSendClick()
                        }
                    }
                ),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38E07B),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF264532),
                    unfocusedContainerColor = Color(0xFF264532)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Send button
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotEmpty() && !isSending,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF38E07B),
                    contentColor = Color(0xFF122118),
                    disabledContainerColor = Color(0xFF38E07B).copy(alpha = 0.6f),
                    disabledContentColor = Color(0xFF122118).copy(alpha = 0.6f)
                )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val mockMessages = listOf(
        ChatMessage(
            text = "Hello! I'm your delivery driver. On my way to pick up your package!",
            sender = MessageSender.DRIVER,
            timestamp = System.currentTimeMillis() - 1800000
        ),
        ChatMessage(
            text = "Great! How long will it take approximately?",
            sender = MessageSender.CUSTOMER,
            timestamp = System.currentTimeMillis() - 1740000
        ),
        ChatMessage(
            text = "About 5-10 minutes. Traffic is light today.",
            sender = MessageSender.DRIVER,
            timestamp = System.currentTimeMillis() - 1680000
        ),
        ChatMessage(
            text = "Perfect! I'll be waiting outside. The building has a blue gate.",
            sender = MessageSender.CUSTOMER,
            timestamp = System.currentTimeMillis() - 1620000
        )
    )
    
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Surface {
            Column {
                ChatTopBar(
                    driverName = "Kwame Asante",
                    isOnline = true,
                    lastSeen = "Online",
                    onBackClick = {},
                    onCallClick = {}
                )
                
                Box(modifier = Modifier.weight(1f)) {
                    ChatMessageList(
                        messages = mockMessages,
                        listState = rememberLazyListState(),
                        isTyping = true,
                        driverName = "Kwame"
                    )
                }
                
                ChatInputField(
                    text = "Hi there",
                    onTextChange = {},
                    onSendClick = {},
                    isSending = false,
                    focusRequester = remember { FocusRequester() }
                )
            }
        }
    }
}
