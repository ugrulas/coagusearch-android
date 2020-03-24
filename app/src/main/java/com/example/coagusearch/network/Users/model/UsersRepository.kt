package com.example.coagusearch.network.Users.model

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.coagusearch.*
import com.example.coagusearch.network.Users.request.UserBodyInfoSaveRequest
import com.example.coagusearch.network.Users.response.PatientMainScreenResponse
import com.example.coagusearch.network.Users.response.UserResponse
import com.example.coagusearch.network.onFailureDialog
import com.example.coagusearch.network.shared.RetrofitClient
import com.example.coagusearch.network.shared.response.ApiResponse
import com.example.coagusearch.ui.dialog.showProgressLoading
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class UsersRepository(
    private val context: Context,
    private val retrofitClient: RetrofitClient
) {
    //TODO: Handle the Server error part which shows error on the screen
    fun getUserInfo(context: Context,which:Int): UserResponse? {
        var userResponse: UserResponse? = null
        showProgressLoading(true,context)
        retrofitClient.usersApi().getUserInfo()
            .enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    println( "Failure")

                }
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ){
                  userResponse=response.body()
                    UserInfoSingleton.instance.userInfo=userResponse
                    showProgressLoading(false,context)
                }
            })
        return userResponse
    }

    fun getMyPatients(): List<UserResponse>? {
        var myPatients: List<UserResponse>? = null
        retrofitClient.usersApi().getMyPatient()
            .enqueue(object : Callback<List<UserResponse>> {
                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                }
                override fun onResponse(
                    call: Call<List<UserResponse>>,
                    response: Response<List<UserResponse>>
                ){
                    myPatients = response.body()
                }

            })
        return myPatients
    }

    fun saveBodyInfo(saveBodyInfoSaveRequest: UserBodyInfoSaveRequest,context: Context): ApiResponse? {
        var apiResponse: ApiResponse? = null
        showProgressLoading(true,context)
        retrofitClient.usersApi().saveBodyInfo(saveBodyInfoSaveRequest)
            .enqueue(object : Callback<ApiResponse> {
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                 onFailureDialog(context,t.toString())
                }
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ){
                    if (response.isSuccessful && response.body() is ApiResponse) {
                        apiResponse = response.body()
                        showProgressLoading(false,context)
                        (context as accountPage).saved()
                    }
                    else{
                        val errorResponse =
                            Gson().fromJson<ApiResponse>(response.errorBody()?.string(), ApiResponse::class.java)?.message
                                ?: context.resources.getString(R.string.errorOccurred)
                        showProgressLoading(false,context)
                        onFailureDialog(context,errorResponse)
                    }
                }
            })
        return apiResponse
    }
    fun  getPatientMainScreen(
        context: Context,
        mainmenu: mainmenu
    ): PatientMainScreenResponse? {
        var patientMainScreenResponse:PatientMainScreenResponse?=null
        showProgressLoading(true,context)
        retrofitClient.usersApi().getPatientMainScreen()
            .enqueue(object :Callback<PatientMainScreenResponse>{
                override fun onFailure(call: Call<PatientMainScreenResponse>, t: Throwable) {
                    onFailureDialog(context,t.toString())
                }
                override fun onResponse(
                    call: Call<PatientMainScreenResponse>,
                    response: Response<PatientMainScreenResponse>
                ) {
                    if (response.isSuccessful && response.body() is PatientMainScreenResponse) {
                        patientMainScreenResponse = response.body()
                        showProgressLoading(false,context)
                        mainmenu.setView(patientMainScreenResponse!!)
                    }
                    else{
                        val errorResponse =
                            Gson().fromJson<ApiResponse>(response.errorBody()?.string(), ApiResponse::class.java)?.message
                                ?: context.resources.getString(R.string.errorOccurred)
                        showProgressLoading(false,context)
                        onFailureDialog(context,errorResponse)
                    }
                }
            })
       return  patientMainScreenResponse
    }
}