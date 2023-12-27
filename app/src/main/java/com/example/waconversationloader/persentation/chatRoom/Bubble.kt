package com.example.waconversationloader.persentation.chatRoom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waconversationloader.persentation.theme.Theme
import com.example.waconversationloader.utils.generateColorFromHashCode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Bubble(
    isRight: Boolean,
    text: String,
    dateTime: LocalDateTime?,
    showDate: Boolean,
    sender: String?,
    showName: Boolean = false,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            if (showDate) dateTime?.let { date ->
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shadowElevation = 0.5.dp,
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        fontSize = 12.sp,
                        text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                        modifier = Modifier
                            .background(Color.White)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(
                modifier = Modifier.align(if (isRight) Alignment.End else Alignment.Start),
                shadowElevation = 1.dp,
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(if (isRight) Color(0xffdbf2c7) else Color(0xfffeffff))
                        .clickable { }
                        .combinedClickable(
                            onClick = onClick,
                            onLongClick = onLongClick
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .widthIn(min = 48.dp)
                ) {
                    if (showName && !isRight && !sender.isNullOrEmpty()) {
                        Text(
                            text = sender,
                            fontSize = 12.sp,
                            color = generateColorFromHashCode(sender),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(text = text, fontSize = 12.sp, softWrap = true)
                    Text(
                        text = dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--",
                        modifier = Modifier
                            .align(Alignment.End),
                        color = Color(0xffa0a2a1),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun BubblePreview() {
    Theme {
        Column(Modifier.fillMaxSize()) {
            Bubble(
                showName = true,
                sender = "John",
                isRight = false,
                text = "Hey, good morning!",
                dateTime = LocalDateTime.now(),
                showDate = true
            )

            Bubble(
                showName = true,
                sender = "John",
                isRight = true,
                text = "Hey, good morning! this is a long text with multiple line to demonstrated line wrap. Again, we will demonstrate how lines were wrapped using this method",
                dateTime = LocalDateTime.now(),
                showDate = false
            )
        }
    }
}