package com.dicoding.fruition1.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("users/create")
    suspend fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): RegisterResponse

    @POST("users/create")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequestBody): LoginResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("histories")
    fun uploadHistory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("json") json: RequestBody
    ): Call<ResponseBody>




    /*@GET("histories/allhistories/")
    suspend fun getHistories(): List<HistoryResponse>

    @GET("histories")
    suspend fun getWith(
        @Query("location") location : Int = 1,
    ): HistoryResponse*/

    @Multipart
    @POST("histories")
    fun upload(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<ResponseBody>



    //ON DEVELOPMENT
    @GET("/")
    suspend fun getServiceMessage(): Response<MessageResponse>
    companion object {
        fun create(baseUrl: String): ApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}