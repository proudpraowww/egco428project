package com.egco428.egco428project.Fragments

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
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
import com.egco428.egco428project.Model.history
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_request.*
import kotlinx.android.synthetic.main.row_request.view.*
import java.text.SimpleDateFormat
import java.util.*

class RequestFragment: Fragment() {

    private var rootView: View? = null
    private var requestStudent: ArrayList<RequestStudent> = ArrayList()
    private var requestListView: ListView? = null
    lateinit var database: DatabaseReference
    lateinit var uid: String
    lateinit var currentEmail: String
    lateinit var student_id: String
    lateinit var student_credit: String
    lateinit var student_study_status: String
    lateinit var tutor_id: String
    lateinit var nameTutor: TextView
    lateinit var lastnameTutor: TextView
    lateinit var subjectTutor: TextView
    lateinit var timeTutor: TextView
    lateinit var tutorLayout: ConstraintLayout
    lateinit var textLayout: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_request, container, false)

        requestListView = rootView!!.findViewById<ListView>(R.id.requestListView)

        nameTutor = rootView!!.findViewById(R.id.nameTutor)
        lastnameTutor = rootView!!.findViewById(R.id.lastnameTutor)
        subjectTutor = rootView!!.findViewById(R.id.subjectTutor)
        timeTutor = rootView!!.findViewById(R.id.timeTutor)

        tutorLayout = rootView!!.findViewById(R.id.tutorLayout)
        textLayout = rootView!!.findViewById(R.id.textLayout)

        database = FirebaseDatabase.getInstance().getReference("Members")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentEmail = user.email.toString()
            uid = user.uid
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot!!.children
                requestStudent.clear()
                children.forEach{
                    if(it.child("email").value.toString().equals(currentEmail)){

                        student_id = it.child("id").value.toString()
                        student_credit = it.child("credit").value.toString()
                        student_study_status = it.child("study_status").value.toString()

                        it.child("request").children.forEach{
                            val request = RequestStudent(it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("subject").value.toString(), it.child("phone").value.toString(), it.child("response").value.toString(), it.key, student_id, student_credit)
                            requestStudent!!.add(request)
                        }
                    }
                }
                if(requestStudent.size > 0){
                    tutorLayout!!.visibility = View.GONE
                    textLayout.visibility = View.GONE
                    requestListView!!.adapter = requestListAdapter(activity!!.applicationContext, database, currentEmail, requestStudent!!)
                    requestListView!!.visibility = View.VISIBLE
                } else {
                    if(!student_study_status.equals("")){
                        requestListView!!.visibility = View.GONE
                        textLayout!!.visibility = View.GONE
                        tutorLayout!!.visibility = View.VISIBLE

                        //get tutor id from student_study_status
                        database.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val children = dataSnapshot!!.children
                                children.forEach{
                                    if(it.key.equals(uid)){ //student
                                        tutor_id = it.child("study_status").value.toString() //tutor_id
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read
                            }
                        })

                        //set tutor layout
                        database.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val children = dataSnapshot!!.children
                                children.forEach{
                                    //Log.d("TutorLayout -> tutor_id", tutor_id)
                                    if(it.key.equals(tutor_id)){ //student
                                        nameTutor.text = "Name : \t" + it.child("name").value.toString()
                                        lastnameTutor.text ="Lastname : \t" + it.child("lastname").value.toString()
                                        subjectTutor.text = "Subject : \t" + it.child("subject").value.toString()
                                        timeTutor.text = "Start Time : \t" + it.child("start_time").value.toString()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read
                            }
                        })
                    } else {
                        requestListView!!.visibility = View.GONE
                        tutorLayout!!.visibility = View.GONE
                        textLayout.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })

        if(requestStudent != null){
            requestListView!!.adapter = requestListAdapter(activity!!.applicationContext, database, currentEmail, requestStudent!!)
        }

        return rootView
    }

    private class requestListAdapter(var context: Context, var database: DatabaseReference, var currentEmail: String, var requestStudent: ArrayList<RequestStudent>) : BaseAdapter() {

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

            if(requestStudent.get(position).response.equals("true")){
                viewHolder.imageView.setImageResource(R.drawable.accept_icon)
                viewHolder.waitingLayout.visibility = View.GONE
            } else {
                viewHolder.paymentLayout.visibility = View.GONE
            }

            rowRequest.setOnClickListener {
                Log.d("Click", "row " + requestStudent.get(position).name)
                if(requestStudent.get(position).response.equals("true")){
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

            paymentBtn.setOnClickListener {
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot!!.children
                        children.forEach{
                            if(it.key.equals(requestStudent.get(position).tutor_id)){
                                if(requestStudent.get(position).student_credit.toInt() >= it.child("course_price").value.toString().toInt()){

                                    //payment by student credit
                                    database.child(requestStudent.get(position).student_id).child("credit")
                                            .setValue(requestStudent.get(position).student_credit.toInt()
                                                    - it.child("course_price").value.toString().toInt())
                                    database.child(requestStudent.get(position).tutor_id).child("credit")
                                            .setValue(it.child("credit").value.toString().toInt()
                                                    + requestStudent.get(position).student_credit.toInt())
                                    database.child(requestStudent.get(position).tutor_id).child("request").removeValue()
                                    database.child(requestStudent.get(position).student_id).child("request").removeValue()

                                    //set study_status of tutor and student
                                    database.child(requestStudent.get(position).tutor_id).child("study_status").setValue(requestStudent.get(position).student_id)
                                    database.child(requestStudent.get(position).student_id).child("study_status").setValue(requestStudent.get(position).student_id)

                                    //set student and tutor history after payment
                                    val push_key = database.push().key
                                    val date = SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(Date())
                                    val historyTutor = history(push_key, it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("subject").value.toString(), it.child("course_price").value.toString(), date.toString())
                                    val historyStudent = history(push_key, requestStudent.get(position).name, requestStudent.get(position).lastname, it.child("subject").value.toString(), it.child("course_price").value.toString(), date.toString())
                                    database.child(requestStudent.get(position).tutor_id).child("start_time").setValue(date.toString())
                                    database.child(requestStudent.get(position).student_id).child("start_time").setValue(date.toString())
                                    database.child(requestStudent.get(position).tutor_id).child("history").child(push_key).setValue(historyStudent)
                                    database.child(requestStudent.get(position).student_id).child("history").child(push_key).setValue(historyTutor)


                                    Toast.makeText(context, "Payment Successful.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Please check your credits.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read
                    }
                })
            }

            cancelBtn.setOnClickListener {
                Log.d("Cancel", "row " + requestStudent.get(position).name)
                rowRequest.animate().setDuration(1500).alpha(0F).withEndAction(){
                    requestStudent.remove(requestStudent.get(position))
                    notifyDataSetChanged()
                    rowRequest.setAlpha(1.0F)
                }
                database.child(requestStudent.get(position).student_id)
                        .child("request")
                        .child(requestStudent.get(position).tutor_id)
                        .removeValue()
            }

            return rowRequest
        }

        private class ViewHolder(val imageView: ImageView, val nameText: TextView, val lastnameText: TextView, val courseText: TextView, val telText: TextView, var paymentLayout: LinearLayout, val waitingLayout: LinearLayout)
    }

}