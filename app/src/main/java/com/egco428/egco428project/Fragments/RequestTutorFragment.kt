package com.egco428.egco428project.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.egco428.egco428project.Model.Member
import com.egco428.egco428project.Model.RequestStudent
import com.egco428.egco428project.Model.RequestTutor
import com.egco428.egco428project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.row_tutor_request.view.*

class RequestTutorFragment: Fragment() {

    private var rootView: View? = null
    private var members: ArrayList<Member>? = null
    private var requestTutor: ArrayList<RequestTutor> = ArrayList()
    private var requestListView: ListView? = null
    lateinit var database: DatabaseReference
    lateinit var uid: String
    lateinit var currentEmail: String
    lateinit var studentKey: String
    lateinit var tutor_study_status: String
    lateinit var tutor_id: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tutor_request, container, false)

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
                requestTutor.clear()
                children.forEach{
                    if(it.child("email").value.toString().equals(currentEmail)){

                        tutor_study_status = it.child("study_status").value.toString()
                        tutor_id = it.child("id").value.toString()

                        it.child("request").children.forEach{
                            //Log.d("Request name", it.child("name").value.toString())
                            //Log.d("Key", it.key)
                            //studentKey = it.key
                            val request = RequestTutor(it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("school").value.toString(), it.child("phone").value.toString(), it.key, tutor_study_status, tutor_id)
                            requestTutor!!.add(request)
                        }
                    }
                }
                requestListView!!.adapter = RequestTutorFragment.requestListAdapter(database, currentEmail, requestTutor!!)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })

        members = ArrayList<Member>()
        members!!.add(Member("1","a@mail.com","123456","Napasorn","Suesagiamsakul","student","0897446363","","","","","",""))
        members!!.add(Member("2","b@mail.com","123456","Sirapop","Kamrat","student","0971515794","","","","","",""))
        members!!.add(Member("2","b@mail.com","123456","Gus","Dekdok","student","0812345678","","","","","",""))

        if(requestTutor != null){
            requestListView!!.adapter = RequestTutorFragment.requestListAdapter(database, currentEmail, requestTutor!!)
        }

        return rootView
    }

    private class requestListAdapter(var database: DatabaseReference, var currentEmail: String, var requestTutor: ArrayList<RequestTutor>) : BaseAdapter() {

        lateinit var student_study_status: String
        lateinit var tutor_response: String

        override fun getCount(): Int {
            return requestTutor.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return requestTutor[position]
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            //inflate 0 and update 1 2 3 4 5
            val rowRequest: View

            if(convertView == null){

                val layoutInflater = LayoutInflater.from(viewGroup!!.context)
                rowRequest = layoutInflater.inflate(R.layout.row_tutor_request, viewGroup, false)
                val viewHolder = ViewHolder(rowRequest.imageView, rowRequest.nameText, rowRequest.lastnameText, rowRequest.schoolText, rowRequest.telText, rowRequest.answerLayout, rowRequest.waitingLayout)
                rowRequest.tag = viewHolder

            } else {
                rowRequest = convertView
            }

            val viewHolder = rowRequest.tag as ViewHolder
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher)
            viewHolder.nameText.text = requestTutor.get(position).name
            viewHolder.lastnameText.text = requestTutor.get(position).lastname
            viewHolder.courseText.text = "School : " + requestTutor.get(position).school
            viewHolder.telText.text = "Tel : " + requestTutor.get(position).phone

            val acceptTutorBtn = rowRequest.findViewById(R.id.acceptTutorBtn) as Button
            val rejectTutorBtn = rowRequest.findViewById(R.id.rejectTutorBtn) as Button

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot!!.children
                    children.forEach{
                        if(it.child("email").value.toString().equals(currentEmail)){
                            it.child("request").children.forEach{
                                tutor_response = it.child("response").value.toString()
                                if(tutor_response.equals("true")){
                                    viewHolder.imageView.setImageResource(R.drawable.accept_icon)
                                    viewHolder.waitingLayout.visibility = View.VISIBLE
                                    viewHolder.answerLayout.visibility = View.GONE
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
                Log.d("Click", "row " + requestTutor.get(position).name)
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot!!.children
                        children.forEach{
                            if(it.child("email").value.toString().equals(currentEmail)){
                                it.child("request").children.forEach{
                                    tutor_response = it.child("response").value.toString()
                                }
                            }
                        }
                        if(tutor_response.equals("true")){
                            viewHolder.answerLayout.visibility = View.GONE
                            if(viewHolder.waitingLayout.visibility == View.GONE){
                                viewHolder.waitingLayout.visibility = View.VISIBLE
                            } else if(viewHolder.waitingLayout.visibility == View.VISIBLE){
                                viewHolder.waitingLayout.visibility = View.GONE
                            }
                        } else {
                            viewHolder.waitingLayout.visibility = View.GONE
                            if(viewHolder.answerLayout.visibility == View.GONE){
                                viewHolder.answerLayout.visibility = View.VISIBLE
                            } else if(viewHolder.answerLayout.visibility == View.VISIBLE){
                                viewHolder.answerLayout.visibility = View.GONE
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read
                    }
                })
            }

            acceptTutorBtn.setOnClickListener {
                //Log.d("Accept", "row " + members.get(position).name)
                //study_status at student+tutor == null? and response at student == null? if true > accepted

                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot!!.children
                        children.forEach{
                            if(it.key.equals(requestTutor.get(position).student_id)){
                                Log.d("student_study_status", it.child("study_status").value.toString())
                                student_study_status = it.child("study_status").value.toString()
                            }
                        }
                        if(requestTutor.get(position).tutor_study_status.equals("") && student_study_status.equals("")){
                            //request at tutor with student_id
                            database.child(requestTutor.get(position).tutor_id)
                                    .child("request")
                                    .child(requestTutor.get(position).student_id)
                                    .child("response")
                                    .setValue("true")
                            //request at student with tutor_id
                            database.child(requestTutor.get(position).student_id)
                                    .child("request")
                                    .child(requestTutor.get(position).tutor_id)
                                    .child("response")
                                    .setValue("true")
                            viewHolder.imageView.setImageResource(R.drawable.accept_icon)
                            viewHolder.waitingLayout.visibility = View.VISIBLE
                            viewHolder.answerLayout.visibility = View.GONE
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read
                    }
                })

                //after student payment
                //database.child(requestTutor.get(position).tutor_id).child("study_status").setValue("true")
                //database.child(requestTutor.get(position).student_id).child("study_status").setValue("true")
            }

            rejectTutorBtn.setOnClickListener{
                Log.d("Reject", "row " + requestTutor.get(position).name)
                rowRequest.animate().setDuration(1500).alpha(0F).withEndAction(){
                    requestTutor.remove(requestTutor.get(position))
                    notifyDataSetChanged()
                    rowRequest.setAlpha(1.0F)
                }
            }

            return rowRequest

        }

        private class ViewHolder(val imageView: ImageView, val nameText: TextView, val lastnameText: TextView, val courseText: TextView, val telText: TextView, val answerLayout: LinearLayout, val waitingLayout: LinearLayout)
    }

}