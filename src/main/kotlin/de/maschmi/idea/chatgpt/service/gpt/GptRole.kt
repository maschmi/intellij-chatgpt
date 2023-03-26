@file:OptIn(ExperimentalSerializationApi::class)

package de.maschmi.idea.chatgpt.service.gpt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GptRoleSerializer::class)
enum class GptRole(val role: String) {
    USER("user"),
    SYSTEM("system"),
    ASSISTANT("assistant");

    companion object {
        fun fromString(value: String) = GptRole.values().first { it.role == value}
    }
}

@Serializer(forClass = GptRole::class)
class GptRoleSerializer : KSerializer<GptRole> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GptRole", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: GptRole) {
        return encoder.encodeString(value.role.lowercase())
    }

    override fun deserialize(decoder: Decoder): GptRole {
        return GptRole.fromString(decoder.decodeString().lowercase())
    }
}