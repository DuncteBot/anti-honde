package me.duncte123.antihonde

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import java.lang.System.getenv

const val SELF_ID = 851848855229038642L
val MENTION_REGEX = "^<@!?$SELF_ID>\$".toRegex()

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
        .setActivity(Activity.playing("Ping me"))
        .addEventListeners(Listener())
        .build()
}