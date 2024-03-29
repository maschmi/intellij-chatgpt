@file:OptIn(ExperimentalSerializationApi::class)

package de.maschmi.idea.chatgpt.chatgpt.gpt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = ModelSerializer::class)
enum class GptModel(val model: String) {
    GTP_3_5_TURBO("gpt-3.5-turbo");

    companion object {
        fun fromString(value: String) = GptModel.values().first { it.model == value }
    }
}

@Serializer(forClass = GptModel::class)
class ModelSerializer : KSerializer<GptModel> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GptModel", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GptModel) {
        encoder.encodeString(value.model)
    }

    override fun deserialize(decoder: Decoder): GptModel {
        return GptModel.fromString(decoder.decodeString().lowercase())
    }
}
