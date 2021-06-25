package me.duncte123.antihonde

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Listener : EventListener {
    private val log = LoggerFactory.getLogger(Listener::class.java)
    private val executor = Executors.newSingleThreadScheduledExecutor {
        val t = Thread(it)
        t.isDaemon = true
        t
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> {
                // TODO: auto update?
                //jda.presence.activity = Activity.watching("over ${jda.guildCache.size()} servers | ping me!")
                log.info("${event.jda.selfUser.asTag} is ready to rumble!")
            }
            is GuildReadyEvent -> {
                // ban when logged in
                log.info("${event.guild} is ready! Doing startup ban!")
                this.startupBan(event.guild)
            }
            is GuildJoinEvent -> {
                val firstGuild = event.guild

                // we joined, initiate ban
                log.info("Just joined $firstGuild! Doing startup ban!")

                executor.schedule(
                {
                    val guild = event.jda.getGuildById(firstGuild.idLong)!!

                    val channel = guild.defaultChannel ?: guild.textChannelCache.first { it.canTalk() }

                    val hasPerm = guild.selfMember.hasPermission(Permission.BAN_MEMBERS)
                    val permMsg = if (hasPerm) "" else "\nI currently do not have the permission to ban members, please give me that permission so that I can ban them"

                    channel?.sendMessage("""Hello and thank you for adding me!
                        |Here is how I work in a few simple steps with no setup required:
                        |1. When first added I will scan all members to see if there are any h0nde bots, and if there are I will ban those.
                        |2. When a new member joins the server I will do the same check to see if it is a h0nde bot, if it is I will ban them
                        |3. Finally, if a member updates their nickname to look like a h0nde bot I will also attempt to ban them.
                        |
                        |To make sure that I can ban new members make sure that my own role is above any automatically applied roles.$permMsg
                    """.trimMargin())?.queue()

                    this.startupBan(guild)
                }
                , 1L, TimeUnit.SECONDS)
            }
            is GuildMemberRoleAddEvent -> {}
            is GuildLeaveEvent -> {
                log.info("Just left ${event.guild}!")
            }
            is GuildMemberJoinEvent -> {
                // member joined, ban h0nde
                this.banHonde(event.member)
            }
            is GuildMemberUpdateEvent -> {
                // Member updated? check for h0nde
                this.banHonde(event.member)
            }
            is MessageReceivedEvent -> {
                val channel = event.channel
                val jda = event.jda
                val self = jda.selfUser

                if (!MENTION_REGEX.matches(event.message.contentRaw) ||
                    (channel is TextChannel && !channel.canTalk())
                ) {
                    return
                }

                channel.sendMessage("""Hello I'm `${self.asTag}`, here to ban these annoying h0nde bots
                    |
                    |Invite me: <https://duncte.bot/antihonde>
                    |View source code: <https://github.com/DuncteBot/anti-honde>
                    |
                    |I currently am watching over `${jda.guildCache.size()}` servers
                """.trimMargin())
                    .reference(event.message)
                    .queue()
            }
        }
    }

    private fun hondeFilter(it: Member): Boolean {
        if (it == it.guild.selfMember) {
            return false
        }

        val parsed = it.effectiveName.lowercase()
                // replace zero width spaces
            .replace("\u200B", "")

        return parsed.contains("h0nde") || parsed.contains("honde")
    }

    private fun startupBan(guild: Guild) {
        guild.findMembers(this::hondeFilter)
            .onSuccess {
                it.forEach(this::banHonde)
            }
    }

    private fun banHonde(member: Member) {
        val self = member.guild.selfMember

        if (this.hondeFilter(member) && self.canInteract(member)) {
            member.ban(7, "h0nde")
                .reason("h0nde")
                .queue()
        }
    }
}