package com.example.coagusearch.patient.PatientAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.coagusearch.R
import com.example.coagusearch.network.Users.response.SingleAppointmentResponse
import kotlinx.android.synthetic.main.informationcard.view.*
import kotlinx.android.synthetic.main.oldappointmentcard.view.*

class OldAppointmentsAdapterAF : BaseAdapter {
    var listOfTicket = ArrayList<SingleAppointmentResponse>()
    var context: Context? = null

    constructor(context: Context, listOfTicket: ArrayList<SingleAppointmentResponse>) : super() {
        this.listOfTicket = listOfTicket
        this.context = context
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var inflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var myView:View?=null
        if(listOfTicket.size==0){
             myView = inflater.inflate(R.layout.informationcard, null)
             myView.infoText.text=context!!.getString(R.string.PatientAppointmentsInfo)
        }
        else {
            val ticket = listOfTicket[p0]
            myView = inflater.inflate(R.layout.oldappointmentcard, null)
            myView.appointentdoctor.text = ticket.DoctorName()
            myView.appointmentdate.text = ticket.appointmentDate()
            myView.appointmenttime.text = ticket.timeSlot()
        }
        return myView
    }

    override fun getItem(p0: Int): Any {
        return listOfTicket[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        if(listOfTicket.size==0)
            return 1
        else return listOfTicket.size
    }
}