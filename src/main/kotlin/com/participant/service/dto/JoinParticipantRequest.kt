package com.participant.service.dto



import jakarta.validation.constraints.NotBlank

data class JoinParticipantRequest(

    @field:NotBlank(message = "sessionId is required")
    val sessionId: String,

    @field:NotBlank(message = "userId is required")
    val userId: String
)