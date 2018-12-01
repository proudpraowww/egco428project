package com.egco428.egco428project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.egco428.egco428project.Model.studentHistory

class studentHistoryAdapter(val mContext: Context, val layoutResId: Int, val messageList: List<studentHistory>): ArrayAdapter<studentHistory>(mContext, layoutResId, messageList) {

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
//        val view: View = layoutInflator.inflate(layoutResId, null)
//        val msgTextView = view.findViewById<TextView>(R.id.msgView)
//        val msgRating = view.findViewById<TextView>(R.id.msgRating)
//
//        val msg = messageList[position]
//
//        msgTextView.text = msg.message
//        msgRating.text = msg.rating.toString()
//
//
//        return view
//    }
}