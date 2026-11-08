package com.participant.service.controller

import com.participant.service.dto.JoinParticipantRequest
import com.participant.service.dto.MuteParticipantRequest
import com.participant.service.dto.RemoveParticipantRequest
import com.participant.service.dto.VideoParticipantRequest
import com.participant.service.service.ParticipantService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/participants")
class ParticipantController(
    private val participantService: ParticipantService
) {

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    fun join(
        @Valid @RequestBody request: JoinParticipantRequest
    ) = participantService.join(request)

    @GetMapping("/session/{sessionId}")
    fun getParticipantsBySession(
        @PathVariable sessionId: String
    ) = participantService.getParticipantsBySession(sessionId)

    @GetMapping("/{participantId}")
    fun getParticipant(
        @PathVariable participantId: String
    ) = participantService.getParticipant(participantId)

    @PostMapping("/{participantId}/leave")
    fun leave(
        @PathVariable participantId: String
    ) = participantService.leave(participantId)

    @PostMapping("/{participantId}/remove")
    fun remove(
        @PathVariable participantId: String,
        @Valid @RequestBody request: RemoveParticipantRequest
    ) = participantService.remove(
        participantId,
        request
    )

    @PostMapping("/{participantId}/mute")
    fun mute(
        @PathVariable participantId: String,
        @RequestBody request: MuteParticipantRequest
    ) = participantService.updateMute(
        participantId,
        request
    )

    @PostMapping("/{participantId}/video")
    fun video(
        @PathVariable participantId: String,
        @RequestBody request: VideoParticipantRequest
    ) = participantService.updateVideo(
        participantId,
        request
    )

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "service" to "participant-service",
            "status" to "healthy"
        )
    }
}