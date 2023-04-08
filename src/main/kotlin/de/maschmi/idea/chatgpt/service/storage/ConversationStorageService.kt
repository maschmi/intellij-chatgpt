package de.maschmi.idea.chatgpt.service.storage

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.isDirectory


@Service(Service.Level.PROJECT)
class ConversationStorageService(project: Project) {


    private val baseDir: Path
    private val jsonFormat = Json { prettyPrint = true }

    init {
        val basePath = Path(project.basePath ?: "")
        this.baseDir = basePath
            .resolve(Project.DIRECTORY_STORE_FOLDER)
            .resolve("aibutler")
        if (!baseDir.isDirectory()) {
            baseDir.createDirectory()
        }
    }

    fun storeConversation(conversation: List<ChatMessage>) {

        val storageFile = getFile()

        if (storageFile.exists()) {
            deleteFile(storageFile)
        }

        storageFile.createNewFile()

        val json = jsonFormat.encodeToString(ListSerializer(ChatMessage.serializer()), conversation)

        storageFile.bufferedWriter().use { out ->
            out.write(json)
        }
    }

    private fun deleteFile(storageFile: File) {
        if (!storageFile.delete()) {
            logger<ConversationStorageService>().warn("Could not delete conversation file. Will write empty string to the file.")
            storageFile.printWriter().use { println() }
        }
    }

    fun loadConversation(): List<ChatMessage> {
        val storageFile = getFile();
        if (!storageFile.exists()) {
            return emptyList()
        }
        val content = storageFile.readText()
        return try {
            jsonFormat.decodeFromString(ListSerializer(ChatMessage.serializer()), content)
        } catch (ex: SerializationException) {
            logger<ConversationStorageService>().warnWithDebug("Could not deserialize stored conversation", ex)
            emptyList()
        }

    }

    fun clear() {
        val storageFile = getFile()
        if (storageFile.exists()) {
            deleteFile(storageFile)
        }
    }

    private fun getFile(): File {
        return baseDir
            .resolve("conversation.json")
            .toFile()
    }
}