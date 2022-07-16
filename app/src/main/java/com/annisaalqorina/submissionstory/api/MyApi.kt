package com.annisaalqorina.submissionstory.api

import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.modeldata.SignUpBody
import com.annisaalqorina.submissionstory.response.FileUploadResponse
import com.annisaalqorina.submissionstory.response.GetAllStoryResponse
import com.annisaalqorina.submissionstory.response.SignInResponse
import com.annisaalqorina.submissionstory.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MyApi {

    @POST("login")
    fun login(
        @Body loginUser: SignInBody
    ): Call<SignInResponse>


    @POST("register")
    fun register(
        @Body registerUser: SignUpBody
    ): Call<SignUpResponse>

    @GET("stories")
        suspend fun getStories(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetAllStoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody,
    ): Call<FileUploadResponse>

    @GET("stories")
    fun getStoriesLocation(
        @Header("Authorization") auth: String,
        @Query("location")location: Int = 1
    ): Call<GetAllStoryResponse>
}