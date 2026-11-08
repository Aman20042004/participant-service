package com.participant.service.exception

class BusinessException(
    val errorCode: String,
    override val message: String
) : RuntimeException(message)