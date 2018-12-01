package com.egco428.egco428project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.egco428.egco428project.Model.studentHistory

class studentHistoryAdapter(val mContext: Context, val layoutResId: Int, val messageList: List<studentHistory>): ArrayAdapter<studentHistory>(mContext, layoutResId, messageList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val tutorname = view.findViewById<TextView>(R.id.tutorname)
        val subjectHistory = view.findViewById<TextView>(R.id.subjectHistory)
        val priceHistory = view.findViewById<TextView>(R.id.priceHistory)
        val dateHistory = view.findViewById<TextView>(R.id.dateHistory)

        val msg = messageList[position]
        var name = msg.teacherName + " " + msg.teacherLastname
        tutorname.text = name
        subjectHistory.text = "Subject : " + msg.subject
        priceHistory.text = "Course price : " + msg.coursePrice
        dateHistory.text = "Date : " + msg.date

        return view
    }
}