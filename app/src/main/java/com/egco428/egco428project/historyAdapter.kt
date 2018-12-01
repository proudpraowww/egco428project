package com.egco428.egco428project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.egco428.egco428project.Model.history
import org.w3c.dom.Text

class historyAdapter(val mContext: Context, val layoutResId: Int, val messageList: List<history>,val status: String): ArrayAdapter<history>(mContext, layoutResId, messageList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val nameHistory = view.findViewById<TextView>(R.id.nameHistory)
        val subjectHistory = view.findViewById<TextView>(R.id.subjectHistory)
        val priceHistory = view.findViewById<TextView>(R.id.priceHistory)
        val dateHistory = view.findViewById<TextView>(R.id.dateHistory)
        val statusHistory = view.findViewById<TextView>(R.id.statusHistory)
        val msg = messageList[position]

        var nameLast = msg.name + " " + msg.lastname
        var statusTemp = status + " :"
        statusHistory.text = statusTemp
        nameHistory.text = nameLast
        subjectHistory.text = "Subject : " + msg.subject
        priceHistory.text = "Course price : " + msg.coursePrice
        dateHistory.text = "Date : " + msg.date.toString()

        return view
    }
}