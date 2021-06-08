package me.duncte123.antihonde

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory

class Listener : EventListener {
    private val log = LoggerFactory.getLogger(Listener::class.java)

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> {
                log.info("${event.jda.selfUser.asTag} is ready to rumble!")
            }
            is GuildReadyEvent -> {
                // ban when logged in
                log.info("${event.guild} is ready! Doing startup ban!")
                this.startupBan(event.guild)
            }
            is GuildJoinEvent -> {
                // we joined, initiate ban
                log.info("Just joined${event.guild}! Doing startup ban!")
                this.startupBan(event.guild)
            }
            is GuildMemberJoinEvent -> {
                // member joined, ban all honde
                this.banHonde(event.member)
            }
        }
    }

    private val hondeFilter: (Member) -> Boolean = {
        val parsed = it.effectiveName.lowercase()
                // replace zero width spaces
            .replace("\u200B", "")

        parsed.contains("h0nde") || parsed.contains("honde")
    }

    private fun startupBan(guild: Guild) {
        guild.findMembers(hondeFilter)
            .onSuccess {
                it.forEach(this::banHonde)
            }
    }

    private fun banHonde(member: Member) {
        if (hondeFilter(member) && member.guild.selfMember.canInteract(member)) {
            member.ban(7, "h0nde")
                .reason("h0nde")
                .queue()
        }
    }
}