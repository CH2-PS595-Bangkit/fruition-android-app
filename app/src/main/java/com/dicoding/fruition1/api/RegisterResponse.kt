package com.dicoding.fruition1.api

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("success")
    val success: Int? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("userId")
    val userId: Int? = null,
)

sealed class ApiResponse<out T : Any> {
    data class Success<out T : Any>(val data: T) : ApiResponse<T>()
    data class Error(val error: ApiError) : ApiResponse<Nothing>()
}

data class ApiError(
    @SerializedName("errors")
    val errors: List<ErrorDetail>? = null,
    @SerializedName("error")
    val error: String? = null,
)


data class ErrorResponse(
    val errors: List<ErrorDetail>
)

data class ErrorDetail(
    val type: String,
    val msg: String,
    val path: String,
    val location: String
)

data class SuccessResponse(
    val success: Int,
    val message: String,
    val userId: Int
)

data class EmailAlreadyRegisterResponse(
    val error: String
)

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)





