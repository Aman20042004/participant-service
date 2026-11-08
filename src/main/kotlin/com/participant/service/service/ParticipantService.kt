package com.participant.service.service


import com.participant.service.dto.JoinParticipantRequest
import com.participant.service.dto.MuteParticipantRequest
import com.participant.service.dto.RemoveParticipantRequest
import com.participant.service.dto.VideoParticipantRequest
import com.participant.service.exception.BusinessException
import com.participant.service.exception.ResourceNotFoundException
import com.participant.service.model.Participant
import com.participant.service.model.ParticipantRole
import com.participant.service.model.ParticipantStatus
import com.participant.service.repository.ParticipantRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ParticipantService(
    private val participantRepository: ParticipantRepository
) {

    companion object {
        const val MAX_LIVE_PARTICIPANTS = 5
    }

    fun join(request: JoinParticipantRequest): Participant {

        val existing = participantRepository
            .findBySessionIdAndUserIdAndStatus(
                request.sessionId,
                request.userId,
                ParticipantStatus.JOINED
            )

        if (existing != null) {
            throw BusinessException(
                "CONFLICT",
                "User is already an active participant in this session"
            )
        }

        val liveParticipantCount =
            participantRepository.countBySessionIdAndStatus(
                request.sessionId,
                ParticipantStatus.JOINED
            )

        if (liveParticipantCount >= MAX_LIVE_PARTICIPANTS) {
            throw BusinessException(
                "CONFLICT",
                "Maximum of 5 live video participants is allowed"
            )
        }

        /*
         * Initial standalone implementation:
         * The first participant becomes OWNER.
         *
         * Later this will be replaced by authenticated/service-to-service
         * ownership verification with Session Service.
         */

        val role =
            if (liveParticipantCount == 0L) {
                ParticipantRole.OWNER
            } else {
                ParticipantRole.PARTICIPANT
            }

        val participant = Participant(
            sessionId = request.sessionId,
            userId = request.userId,
            role = role,
            status = ParticipantStatus.JOINED,
            videoEnabled = true,
            muted = false,
            joinedAt = Instant.now(),
            leftAt = null
        )

        return participantRepository.save(participant)
    }

    fun getParticipantsBySession(
        sessionId: String
    ): List<Participant> {

        return participantRepository.findBySessionId(sessionId)
    }

    fun getParticipant(
        participantId: String
    ): Participant {

        return participantRepository.findById(participantId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Participant not found: $participantId"
                )
            }
    }

    fun leave(
        participantId: String
    ): Participant {

        val participant = getParticipant(participantId)

        if (participant.status != ParticipantStatus.JOINED) {
            throw BusinessException(
                "BAD_REQUEST",
                "Only a JOINED participant can leave the session"
            )
        }

        val updated = participant.copy(
            status = ParticipantStatus.LEFT,
            leftAt = Instant.now()
        )

        return participantRepository.save(updated)
    }

    fun remove(
        participantId: String,
        request: RemoveParticipantRequest
    ): Participant {

        val participant = getParticipant(participantId)

        if (participant.status != ParticipantStatus.JOINED) {
            throw BusinessException(
                "BAD_REQUEST",
                "Only a JOINED participant can be removed"
            )
        }

        val owner = participantRepository.findBySessionIdAndRole(
            participant.sessionId,
            ParticipantRole.OWNER
        )

        if (owner == null) {
            throw BusinessException(
                "BAD_REQUEST",
                "Session owner participant record not found"
            )
        }

        if (owner.userId != request.ownerId) {
            throw BusinessException(
                "FORBIDDEN",
                "Only the session owner can remove a participant"
            )
        }

        if (participant.role == ParticipantRole.OWNER) {
            throw BusinessException(
                "FORBIDDEN",
                "Session owner cannot be removed as a participant"
            )
        }

        val updated = participant.copy(
            status = ParticipantStatus.REMOVED,
            leftAt = Instant.now()
        )

        return participantRepository.save(updated)
    }

    fun updateMute(
        participantId: String,
        request: MuteParticipantRequest
    ): Participant {

        val participant = getParticipant(participantId)

        ensureActive(participant)

        val updated = participant.copy(
            muted = request.muted
        )

        return participantRepository.save(updated)
    }

    fun updateVideo(
        participantId: String,
        request: VideoParticipantRequest
    ): Participant {

        val participant = getParticipant(participantId)

        ensureActive(participant)

        val updated = participant.copy(
            videoEnabled = request.videoEnabled
        )

        return participantRepository.save(updated)
    }

    private fun ensureActive(
        participant: Participant
    ) {

        if (participant.status != ParticipantStatus.JOINED) {
            throw BusinessException(
                "BAD_REQUEST",
                "Participant is not currently active in the session"
            )
        }
    }
}