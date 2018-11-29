package com.egco428.egco428project.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import kotlinx.android.synthetic.main.row_request.view.*

class RequestTutorFragment: Fragment() {

    private var rootView: View? = null
    private var members: ArrayList<Member>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tutor_request, container, false)

        val requestListView = rootView!!.findViewById<ListView>(R.id.requestListView)

        members = ArrayList<Member>()

        members!!.add(Member("1","a@mail.com","123456","Napasorn","Suesagiamsakul","student","0897446363","","","","",""))
        members!!.add(Member("2","b@mail.com","123456","Sirapop","Kamrat","student","0971515794","","","","",""))
        members!!.add(Member("2","b@mail.com","123456","Gus","Dekdok","student","0812345678","","","","",""))

        requestListView.adapter = requestListAdapter(members!!)

        return rootView
    }

    private class requestListAdapter(var members: ArrayList<Member>) : BaseAdapter() {

        override fun getCount(): Int {
            return members.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return members[position]
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            //inflate 0 and update 1 2 3 4 5
            val rowRequest: View

            if(convertView == null){

                val layoutInflater = LayoutInflater.from(viewGroup!!.context)
                rowRequest = layoutInflater.inflate(R.layout.row_request, viewGroup, false)
                val viewHolder = ViewHolder(rowRequest.imageView, rowRequest.nameText, rowRequest.lastnameText, rowRequest.courseText, rowRequest.telText)
                rowRequest.tag = viewHolder

            } else {
                rowRequest = convertView
            }

            val viewHolder = rowRequest.tag as ViewHolder
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher)
            viewHolder.nameText.text = members.get(position).name
            viewHolder.lastnameText.text = members.get(position).lastname
            viewHolder.courseText.text = "Course : My Course"
            viewHolder.telText.text = "Tel : " + members.get(position).phone

            val acceptBtn = rowRequest.findViewById(R.id.acceptBtn) as Button
            val rejectBtn = rowRequest.findViewById(R.id.rejectBtn) as Button

            acceptBtn.setOnClickListener {

                //Log.d("Accept", "row " + members.get(position).name)
            }
            rejectBtn.setOnClickListener{
                Log.d("Reject", "row " + members.get(position).name)
                rowRequest.animate().setDuration(1500).alpha(0F).withEndAction(){
                    members.remove(members.get(position))
                    notifyDataSetChanged()
                    rowRequest.setAlpha(1.0F)
                }
            }

            return rowRequest

        }

        private class ViewHolder(val imageView: ImageView, val nameText: TextView, val lastnameText: TextView, val courseText: TextView, val telText: TextView)
    }

}