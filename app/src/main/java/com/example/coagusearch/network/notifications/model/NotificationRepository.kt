package com.example.coagusearch.network.notifications.model

import android.content.Context
import android.widget.Toast
import com.example.coagusearch.R
import com.example.coagusearch.doctor.doctorNotificationFragment
import com.example.coagusearch.medicalTeam.medicalHomeFragment
import com.example.coagusearch.network.notifications.request.CallPatientRequest
import com.example.coagusearch.network.notifications.request.NotifyMedicalRequest
import com.example.coagusearch.network.notifications.response.NotificationResponse
import com.example.coagusearch.network.onFailureDialog
import com.example.coagusearch.network.shared.RetrofitClient
import com.example.coagusearch.network.shared.response.ApiResponse
import com.example.coagusearch.ui.dialog.showProgressLoading
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepository  (private val context: Context,
private val retrofitClient: RetrofitClient
) {

    fun callPatient(
        patientId: CallPatientRequest,
        context: Context
    ): ApiResponse? {
        var r:ApiResponse ? = null
        showProgressLoading(true,context)
        retrofitClient.notificationsApi().callPatientForAppointment(patientId)
            .enqueue(object : Callback<ApiResponse> {
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    showProgressLoading(false, context)
                    onFailureDialog(context, t.toString())
                }
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if(response.isSuccessful &&  response.body() is ApiResponse) {
                        r = response.body()
                        showProgressLoading(false, context)
                        Toast.makeText(context,context.getString(R.string.notifiedsuccess),Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val errorResponse =
                            Gson().fromJson<ApiResponse>(
                                response.errorBody()?.string(),
                                ApiResponse::class.java
                            )?.message
                                ?: context.resources.getString(R.string.errorOccurred)
                        showProgressLoading(false, context)
                        onFailureDialog(context, errorResponse)
                    }
                }
            })
        return r
    }

    fun notifyMedical(
        patientId: NotifyMedicalRequest,
        context: Context
    ): ApiResponse? {
        var r:ApiResponse ? = null
         showProgressLoading(true,context)
        retrofitClient.notificationsApi().notifyMedicalTeam(patientId)
            .enqueue(object : Callback<ApiResponse> {
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    showProgressLoading(false, context)
                    onFailureDialog(context, t.toString())
                }

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if(response.isSuccessful && response.body() is ApiResponse) {
                        r = response.body()
                        Toast.makeText(context,context.getString(R.string.notifiedsuccess),Toast.LENGTH_SHORT).show()
                        showProgressLoading(false,context)
                    }
                    else {
                        val errorResponse =
                            Gson().fromJson<ApiResponse>(
                                response.errorBody()?.string(),
                                ApiResponse::class.java
                            )?.message
                                ?: context.resources.getString(R.string.errorOccurred)
                        showProgressLoading(false, context)
                        onFailureDialog(context, errorResponse)
                    }
                }
            })
        return r
    }

    fun getPage(
        context: Context,
        doctorNotificationFragment: doctorNotificationFragment
    ): List<NotificationResponse>? {
        var r:List<NotificationResponse> ? = null
        showProgressLoading(true,context)
        // showProgressLoading(true,context)
        retrofitClient.notificationsApi().getPage()
            .enqueue(object : Callback<List<NotificationResponse>> {
                override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                    showProgressLoading(false,context)
                    onFailureDialog(context, t.toString())
                }
                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if(response.isSuccessful&& response.body() is List<NotificationResponse>) {
                        r = response.body()
                        if (doctorNotificationFragment.isAdded) {
                            doctorNotificationFragment.setData(r!!)
                        }
                        showProgressLoading(false, context)
                    }
                    else {
                        val errorResponse =
                            Gson().fromJson<ApiResponse>(
                                response.errorBody()?.string(),
                                ApiResponse::class.java
                            )?.message
                                ?: context.resources.getString(R.string.errorOccurred)
                        showProgressLoading(false, context)
                        onFailureDialog(context, errorResponse)
                    }
                }
            })
        return r
    }


    fun getPageMedical(
        context: Context,
        fragment: medicalHomeFragment
    ): List<NotificationResponse>? {
        var r:List<NotificationResponse> ? = null
        showProgressLoading(true,context)
        // showProgressLoading(true,context)
        retrofitClient.notificationsApi().getPage()
            .enqueue(object : Callback<List<NotificationResponse>> {
                override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                    showProgressLoading(false,context)
                    onFailureDialog(context, t.toString())
                }
                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if(response.isSuccessful&& response.body() is List<NotificationResponse>) {
                        r = response.body()
                        if (fragment.isAdded) {
                            ( fragment as medicalHomeFragment).setData(r!!)
                        }
                        showProgressLoading(false, context)
                    }
                    else {
                        val errorResponse =
                            Gson().fromJson<ApiResponse>(
                                response.errorBody()?.string(),
                                ApiResponse::class.java
                            )?.message
                                ?: context.resources.getString(R.string.errorOccurred)
                        showProgressLoading(false, context)
                        onFailureDialog(context, errorResponse)
                    }
                }
            })
        return r
    }
}
