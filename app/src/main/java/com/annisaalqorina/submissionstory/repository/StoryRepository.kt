package com.annisaalqorina.submissionstory.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.annisaalqorina.submissionstory.api.MyApi
import com.annisaalqorina.submissionstory.api.StoryPagingSource
import com.annisaalqorina.submissionstory.response.FileUploadResponse
import com.annisaalqorina.submissionstory.response.GetAllStoryResponse
import com.annisaalqorina.submissionstory.response.ListStoryItem
import com.annisaalqorina.submissionstory.wrapEspressoIdlingResource
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: MyApi,
    private val storyPagingSource: StoryPagingSource
) {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _uploadResponse = MutableLiveData<FileUploadResponse>()
    val uploadResponse: LiveData<FileUploadResponse> = _uploadResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getStoriesPaging(): LiveData<PagingData<ListStoryItem>> {
        wrapEspressoIdlingResource {
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                pagingSourceFactory = {
                    storyPagingSource
                }
            ).liveData
        }
    }

    fun getListStoryItem(token: String) {
        val client = apiService.getStoriesLocation("Bearer $token")
        client.enqueue(object : Callback<GetAllStoryResponse> {
            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _listStory.value = responseBody.listStory!!
                }
            }

            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                Log.e("ResponseError", "onFailure: ${t.message}")
            }
        })
    }

    fun uploadImage(token: String, description: String, imgFile: File, location: LatLng) {
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImage = imgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            requestImage
        )
        val requestBodyLat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
        val requestBodyLon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
        _loading.value = true
        val client = apiService.addStory("Bearer $token", imageMultipart, requestDescription,requestBodyLat,requestBodyLon)
        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(call: Call<FileUploadResponse>, response: Response<FileUploadResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    _uploadResponse.value = response.body()
                    _loading.value = false
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                Log.e("ResponseError", "onFailure: ${t.message}")
                _loading.value = false
            }
        })
    }
}