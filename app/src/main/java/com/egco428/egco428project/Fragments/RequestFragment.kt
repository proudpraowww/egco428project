package com.egco428.egco428project.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.R
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import android.text.method.Touch.onTouchEvent
import android.view.GestureDetector
import android.view.View.OnTouchListener
import com.egco428.egco428project.Model.RequestStudent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.row_request.view.*

class RequestFragment: Fragment() {

    private var rootView: View? = null
    private var members: ArrayList<Member>? = null
    private var requestStudent: ArrayList<RequestStudent> = ArrayList()
    private var requestListView: ListView? = null
    lateinit var database: DatabaseReference
    lateinit var uid: String
    lateinit var currentEmail: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_request, container, false)

        requestListView = rootView!!.findViewById<ListView>(R.id.requestListView)
        database = FirebaseDatabase.getInstance().getReference("Members")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentEmail = user.email.toString()
            uid = user.uid
        }
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                requestStudent.clear()
                children.forEach{
                    if(it.child("email").value.toString().equals(currentEmail)){
                        it.child("request").children.forEach{
                            //Log.d("Request name", it.child("name").value.toString())
                            val request = RequestStudent(it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("subject").value.toString(), it.child("phone").value.toString())
                            requestStudent!!.add(request)
                        }
                    }
                }
                requestListView!!.adapter = requestListAdapter(database, currentEmail, requestStudent!!)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })

        members = ArrayList<Member>()
        members!!.add(Member("1","a@mail.com","123456","Napasorn","Suesagiamsakul","student","0897446363","","","","","",""))
        members!!.add(Member("2","b@mail.com","123456","Sirapop","Kamrat","student","0971515794","","","","","",""))
        members!!.add(Member("2","b@mail.com","123456","Gus","Dekdok","student","0812345678","","","","","",""))

        if(requestStudent != null){
            requestListView!!.adapter = requestListAdapter(database, currentEmail, requestStudent!!)
        }

        return rootView
    }

    private class requestListAdapter(var database: DatabaseReference, var currentEmail: String, var requestStudent: ArrayList<RequestStudent>) : BaseAdapter() {

        override fun getCount(): Int {
            return requestStudent.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return requestStudent[position]
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            lateinit var student_response: String

            //inflate 0 and update 1 2 3 4 5
            val rowRequest: View

            if(convertView == null){

                val layoutInflater = LayoutInflater.from(viewGroup!!.context)
                rowRequest = layoutInflater.inflate(R.layout.row_request, viewGroup, false)
                val viewHolder = ViewHolder(rowRequest.imageView, rowRequest.nameText, rowRequest.lastnameText, rowRequest.courseText, rowRequest.telText, rowRequest.paymentLayout, rowRequest.waitingLayout)
                rowRequest.tag = viewHolder

            } else {
                rowRequest = convertView
            }

            val viewHolder = rowRequest.tag as ViewHolder
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher)
            viewHolder.nameText.text = requestStudent.get(position).name
            viewHolder.lastnameText.text = requestStudent.get(position).lastname
            viewHolder.courseText.text = "Course : " + requestStudent.get(position).subject
            viewHolder.telText.text = "Tel : " + requestStudent.get(position).phone

            val paymentBtn = rowRequest.findViewById(R.id.paymentBtn) as Button
            val cancelBtn = rowRequest.findViewById(R.id.cancelBtn) as Button

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot!!.children
                    children.forEach{
                        if(it.child("email").value.toString().equals(currentEmail)){
                            it.child("request").children.forEach{
                                student_response = it.child("response").value.toString()
                                if(student_response.equals("true")){
                                    viewHolder.imageView.setImageResource(R.drawable.accept_icon)
                                    //viewHolder.paymentLayout.visibility = View.VISIBLE
                                    viewHolder.waitingLayout.visibility = View.GONE
                                } else {
                                    viewHolder.imageView.setImageResource(R.mipmap.ic_launcher)
                                    viewHolder.paymentLayout.visibility = View.GONE
                                    //viewHolder.waitingLayout.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read
                }
            })

            rowRequest.setOnClickListener {
                Log.d("Click", "row " + requestStudent.get(position).name)
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot!!.children
                        children.forEach{
                            if(it.child("email").value.toString().equals(currentEmail)){
                                it.child("request").children.forEach{
                                    student_response = it.child("response").value.toString()
                                }
                            }
                        }
                        if(student_response.equals("true")){
                            viewHolder.waitingLayout.visibility = View.GONE
                            if(viewHolder.paymentLayout.visibility == View.GONE){
                                viewHolder.paymentLayout.visibility = View.VISIBLE
                            } else if(viewHolder.paymentLayout.visibility == View.VISIBLE){
                                viewHolder.paymentLayout.visibility = View.GONE
                            }
                        } else {
                            viewHolder.paymentLayout.visibility = View.GONE
                            if(viewHolder.waitingLayout.visibility == View.GONE){
                                viewHolder.waitingLayout.visibility = View.VISIBLE
                            } else if(viewHolder.waitingLayout.visibility == View.VISIBLE){
                                viewHolder.waitingLayout.visibility = View.GONE
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read
                    }
                })
            }

            paymentBtn.setOnClickListener {

            }

            cancelBtn.setOnClickListener {
                Log.d("Cancel", "row " + requestStudent.get(position).name)
                rowRequest.animate().setDuration(1500).alpha(0F).withEndAction(){
                    requestStudent.remove(requestStudent.get(position))
                    notifyDataSetChanged()
                    rowRequest.setAlpha(1.0F)
                }
            }

            return rowRequest
        }

        private class ViewHolder(val imageView: ImageView, val nameText: TextView, val lastnameText: TextView, val courseText: TextView, val telText: TextView, var paymentLayout: LinearLayout, val waitingLayout: LinearLayout)
    }

}