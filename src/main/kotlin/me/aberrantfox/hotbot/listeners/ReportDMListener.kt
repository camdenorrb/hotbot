package me.aberrantfox.hotbot.listeners

import me.aberrantfox.hotbot.dsls.embed.embed
import me.aberrantfox.hotbot.extensions.jda.descriptor
import me.aberrantfox.hotbot.services.Configuration
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ReportDMListener(val config: Configuration) : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if(event.author.isBot) {
            return
        }

        val message = event.message

        if(message.contentRaw.startsWith(config.serverInformation.prefix)) {
            return
        }

        val channel = event.jda.getGuildById(config.serverInformation.guildid).getTextChannelById(config.messageChannels.reportChannel)
        channel.iterableHistory.queue { messages ->
            val targetMessage = messages.firstOrNull { it.embeds.first().title.contains(event.author.id) }

            if(targetMessage == null) {
                channel.sendMessage(buildReportEmbed(event.message.contentRaw, event.author)).queue()
            } else {
                val embed = targetMessage.embeds.first()
                val response = (embed.description + "\n" + message.contentRaw).reversed().chunked(1000).first().reversed()
                targetMessage.editMessage(buildReportEmbed(response, event.author)).queue()
            }
        }
    }
}

private fun buildReportEmbed(message: String, user: User) = embed {
    title("${user.descriptor()}'s report")
    description(message)
}