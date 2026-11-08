package com.participant.service.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiErrorResponse(
                status = 404,
                error = "NOT_FOUND",
                message = ex.message,
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(
        ex: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {

        val status = when (ex.errorCode) {
            "FORBIDDEN" -> HttpStatus.FORBIDDEN
            "CONFLICT" -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }

        return ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                error = ex.errorCode,
                message = ex.message,
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {

        val message = ex.bindingResult
            .fieldErrors
            .joinToString(", ") {
                "${it.field}: ${it.defaultMessage}"
            }

        return ResponseEntity.badRequest().body(
            ApiErrorResponse(
                status = 400,
                error = "VALIDATION_ERROR",
                message = message,
                path = request.requestURI
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {

        ex.printStackTrace()

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiErrorResponse(
                status = 500,
                error = "INTERNAL_SERVER_ERROR",
                message = ex.message ?: "Unknown error",
                path = request.requestURI
            )
        )
    }
}