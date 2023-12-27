package com.example.waconversationloader.persentation.nav

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.waconversationloader.persentation.chatList.ChatListPage
import com.example.waconversationloader.persentation.chatRoom.ChatroomPage

val LocalNavController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun Navigation() {
    val navHostController = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides navHostController
    ) {
        NavHost(
            navController = navHostController,
            startDestination = Scene.ChatList.route
        ) {
            composable(
                route = Scene.ChatList.route,
                enterTransition = { slideInHorizontally { fullWidth -> -fullWidth } },
                ) {
                ChatListPage()

            }
            composable(
                route = Scene.ChatRoom.route + "/{roomId}",
                enterTransition = { slideInHorizontally { fullWidth -> fullWidth } },
                popExitTransition = { slideOutHorizontally { fullWidth -> fullWidth } },
                arguments = listOf(
                    navArgument("roomId") {
                        type = NavType.IntType
                        nullable = false
                    }
                )
            ) { entry ->
                entry.arguments?.let {
                    ChatroomPage(it.getInt("roomId"))
                } ?: run {
                    navHostController.navigateUp()
                }
            }
        }
    }
}