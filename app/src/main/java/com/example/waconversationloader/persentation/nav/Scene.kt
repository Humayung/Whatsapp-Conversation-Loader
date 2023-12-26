package com.example.waconversationloader.persentation.nav

sealed class Scene(val route: String) {
    data object ChatList : Scene("chat_list")
    data object ChatRoom : Scene("chat_room")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}