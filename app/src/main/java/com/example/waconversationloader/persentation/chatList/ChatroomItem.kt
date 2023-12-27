package com.example.waconversationloader.persentation.chatList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waconversationloader.persentation.components.LetterProfileImage
import com.example.waconversationloader.persentation.theme.Theme

@Composable
fun ChatroomItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    name: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            content = { LetterProfileImage(receiverName = name) }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = name)
    }
}

@Preview
@Composable
fun ChatroomItemPrev() {
    Theme {
        ChatroomItem(
            name = "Mirza",
            onClick = {}
        )
    }
}