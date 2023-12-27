package com.example.waconversationloader.persentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waconversationloader.persentation.theme.Theme
import com.example.waconversationloader.utils.generateColorFromHashCode
import io.skipday.takan.extensions.contrasted

@Composable
fun LetterProfileImage(receiverName: String) {
    val bgColor = generateColorFromHashCode(receiverName)
    val letterCount = 3
    val letter = remember(receiverName) {
        receiverName
            .split(" ")
            .map { it.first().uppercase() }
            .take(letterCount.coerceAtMost(4))
            .joinToString("")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        val fontSizeMultiplier = 1 - ((letter.length - 1) / 4f) * 0.5f
        Text(
            text = letter,
            fontSize = with(LocalDensity.current) { (fontSizeMultiplier * 16).dp.toSp() },
            color = Color.Black.contrasted(bgColor)
        )
    }
}


@Preview
@Composable
fun LetterProfileImagePreview() {
    Theme {
        LetterProfileImage(receiverName = "Mirza My Humayung Retno Fujiani")
    }
}