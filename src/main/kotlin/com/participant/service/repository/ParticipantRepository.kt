
package com.participant.service.repository

import com.participant.service.model.Participant
import com.participant.service.model.ParticipantStatus
import org.springframework.data.mongodb.repository.MongoRepository

interface ParticipantRepository : MongoRepository<Participant, String> {

    fun findBySessionId(sessionId: String): List<Participant>

    fun findBySessionIdAndUserIdAndStatus(
        sessionId: String,
        userId: String,
        status: ParticipantStatus
    ): Participant?

    fun countBySessionIdAndStatus(
        sessionId: String,
        status: ParticipantStatus
    ): Long

    fun findBySessionIdAndRole(
        sessionId: String,
        role: com.participant.service.model.ParticipantRole
    ): Participant?
}