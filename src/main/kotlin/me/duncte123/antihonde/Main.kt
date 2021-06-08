package me.duncte123.antihonde

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import java.lang.System.getenv

fun main() {
    val token = getenv("TOKEN")

    if (token == null) {
        println("Cannot start without token")
        return
    }

    JDABuilder.createLight(token)
        .enableIntents(
            GatewayIntent.GUILD_MEMBERS
        )
        .addEventListeners(Listener())
        .build()
}