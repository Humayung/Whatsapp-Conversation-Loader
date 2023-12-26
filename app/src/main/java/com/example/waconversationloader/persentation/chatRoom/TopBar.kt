package com.example.waconversationloader.persentation.chatRoom

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waconversationloader.persentation.nav.LocalNavController
import com.example.waconversationloader.persentation.theme.Theme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    name: String = "Name",
    profileImage: Painter = rememberVectorPainter(image = Icons.Default.Person),
    onSearchNext: (String) -> Unit = {},
    onSearchPrev: (String) -> Unit = {},
    onClickMenu: () -> Unit = {}
) {
    val navController = LocalNavController.current
    var isSearchMode by remember { mutableStateOf(false) }

    AnimatedContent(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        targetState = isSearchMode,
        label = "topbar",
    ) { isSearchModeT ->
        if (isSearchModeT) {
            SearchMode(
                onBack = { isSearchMode = false },
                onSearchNext = onSearchNext,
                onSearchPrev = onSearchPrev
            )
        } else {
            DefaultMode(
                onBack = { navController?.navigateUp() },
                profileImage = profileImage,
                name = name,
                onClickSearch = { isSearchMode = true },
                onClickMenu = onClickMenu
            )
        }
    }
}


@Composable
fun DefaultMode(
    onBack: () -> Unit,
    profileImage: Painter,
    onClickSearch: () -> Unit,
    onClickMenu: () -> Unit,
    name: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onBack() }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    painter = profileImage,
                    contentDescription = "person"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, color = Color.White)
        }
        Row {

            Icon(imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onClickSearch()
                    }
                    .padding(4.dp)
            )
            Icon(imageVector = Icons.Default.MoreVert,
                contentDescription = "menu",
                tint = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onClickMenu()
                    }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun SearchMode(
    onBack: () -> Unit,
    onSearchNext: (String) -> Unit,
    onSearchPrev: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var searchField by remember { mutableStateOf("") }
    BackHandler {
        onBack()
        searchField = ""
    }

    TextField(
        value = searchField,
        onValueChange = { searchField = it },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .clip(RoundedCornerShape(50)),
        colors = TextFieldDefaults.colors(
            cursorColor = Color.White,
            disabledIndicatorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                backgroundColor = Color.White.copy(alpha = 0.3f),
                handleColor = Color.White
            ),
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onSearchNext(searchField)
        }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        singleLine = true,
        trailingIcon = {
            Row(modifier = Modifier.padding(end = 8.dp)) {
                Icon(imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "menu",
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onSearchNext(searchField)
                        }
                        .padding(4.dp)
                )
                Icon(imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "menu",
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onSearchPrev(searchField)
                        }
                        .padding(4.dp)
                )
            }

        },
        placeholder = {
            Text(text = "Search...")
        },
        leadingIcon = {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onBack()
                        }
                        .padding(4.dp)
                )
            }
        },
    )

}

@Preview
@Composable
fun TopBarPreview() {
    Theme {
        TopBar(
            modifier = Modifier.fillMaxWidth()
        )
    }
}