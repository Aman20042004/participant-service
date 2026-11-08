package com.participant.service.model



import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "participants")

data class Participant(

    @Id
    val id: String? = null,

    val sessionId: String,

    val userId: String,

    val role: ParticipantRole,

    val status: ParticipantStatus,

    val videoEnabled: Boolean,

    val muted: Boolean,

    val joinedAt: Instant? = null,

    val leftAt: Instant? = null
)