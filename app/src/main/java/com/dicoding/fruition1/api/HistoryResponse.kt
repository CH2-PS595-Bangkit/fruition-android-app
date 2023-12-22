package com.dicoding.fruition1.api

import com.google.gson.annotations.SerializedName

data class NotHistoryResponse(
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("password")
    val password: String
)

data class HistoryRequest(
    @SerializedName("history")
    val history: String? = null,
    @SerializedName("predictedClass")
    val predictedClass: String? = null,
    @SerializedName("accuracy")
    val accuracy: String? = null
)