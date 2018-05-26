package me.aberrantfox.hotbot.services

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.GsonBuilder
import me.aberrantfox.hotbot.logging.ChannelIdHolder
import me.aberrantfox.hotbot.permissions.PermissionLevel
import java.io.File
import java.util.*

open class Configuration(open val serverInformation: ServerInformation = ServerInformation(),
                         val security: Security = Security(),
                         val messageChannels: MessageChannels = MessageChannels(),
                         val apiConfiguration: ApiConfiguration = ApiConfiguration(),
                         val databaseCredentials: DatabaseCredentials = DatabaseCredentials(),
                         val logChannels: ChannelIdHolder = ChannelIdHolder(),
                         val permissionedActions: PermissionedActions = PermissionedActions(),
                         val botInformation: BotInformation = BotInformation())

open class ServerInformation(val token: String = "insert-token",
                             open val ownerID: String = "insert-id",
                             var prefix: String = "insert-prefix",
                             open val guildid: String = "insert-guild-id",
                             val suggestionPoolLimit: Int = 20)

data class Security(val ignoredIDs: MutableSet<String> = mutableSetOf(),
                    var lockDownMode: Boolean = false,
                    val infractionActionMap: HashMap<Int, InfractionAction> = HashMap(),
                    val mutedRole: String = "Muted",
                    val strikeCeil: Int = 3)

data class MessageChannels(val welcomeChannel: String = "insert-id",
                           val suggestionChannel: String = "insert-id",
                           val profileChannel: String = "insert-channel-id",
                           val remWelcomeOnLeave: Boolean = false)

data class ApiConfiguration(val cleverbotAPIKey: String = "insert-api-key",
                            val cleverBotApiCallLimit: Int = 10000,
                            val enableCleverBot: Boolean = false)

data class PermissionedActions(var sendInvite: PermissionLevel = PermissionLevel.Moderator,
                               var sendURL: PermissionLevel = PermissionLevel.Moderator,
                               var commandMention: PermissionLevel = PermissionLevel.Moderator,
                               val ignoreLogging: PermissionLevel = PermissionLevel.Moderator,
                               var voiceChannelMuteThreshold: PermissionLevel = PermissionLevel.Moderator)

data class DatabaseCredentials(val username: String = "root",
                               val password: String = "",
                               val hostname: String = "hotbotdb",
                               val database: String = "hotbot")

data class BotInformation(val developmentMode: Boolean = true)

enum class InfractionAction {
    Warn, Mute, Kick, Ban
}

private val configDir = System.getenv("HOTBOT_CONFIG_DIR") ?: "config"
private val configLocation = "config.json"
private val gson = GsonBuilder().setPrettyPrinting().create()

fun configPath(fileName: String) = "${configDir}/${fileName}"

fun loadConfig(): Configuration? {
    val configFile = File(configPath(configLocation))

    if(!configFile.exists()) {
        val jsonData = gson.toJson(Configuration())
        configFile.printWriter().use { it.print(jsonData) }

        return null
    }

    val json = configFile.readLines().stream().reduce("", { a: String, b: String -> a + b })
    val configuration = gson.fromJson<Configuration>(json)

    return configuration
}

fun saveConfig(config: Configuration) {
    val file = File(configPath(configLocation))
    val json = gson.toJson(config)

    file.delete()
    file.printWriter().use { it.print(json) }
}

