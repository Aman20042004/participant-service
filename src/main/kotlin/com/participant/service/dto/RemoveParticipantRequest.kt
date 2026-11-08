
package com.participant.service.dto

import jakarta.validation.constraints.NotBlank

data class RemoveParticipantRequest(

    @field:NotBlank(message = "ownerId is required")
    val ownerId: String
)