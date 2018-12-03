package com.egco428.egco428project.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_request.*
import kotlinx.android.synthetic.main.fragment_tutor_request.*
import kotlinx.android.synthetic.main.row_tutor_request.view.*
import java.io.File

class RequestTutorFragment: Fragment() {

    private var rootView: View? = null
    private var requestTutor: ArrayList<RequestTutor> = ArrayList()
    private var requestListView: ListView? = null
    lateinit var database: DatabaseReference
    lateinit var uid: String
    lateinit var currentEmail: String
    lateinit var studentKey: String
    lateinit var tutor_study_status: String
    lateinit var tutor_id: String
    lateinit var student_id: String
    lateinit var subject: String
    lateinit var imageStudent: ImageView
    lateinit var nameStudent: TextView
    lateinit var lastnameStudent: TextView
    lateinit var subjectStudent: TextView
    lateinit var timeStudent: TextView
    lateinit var finishBtn: Button
    lateinit var studentLayout: ConstraintLayout
    lateinit var textLayout: ConstraintLayout
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tutor_request, container, false)

        requestListView = rootView!!.findViewById<ListView>(R.id.requestListView)
        imageStudent = rootView!!.findViewById(R.id.imageStudent)
        nameStudent = rootView!!.findViewById(R.id.nameStudent)
        lastnameStudent = rootView!!.findViewById(R.id.lastnameStudent)
        subjectStudent = rootView!!.findViewById(R.id.subjectStudent)
        timeStudent = rootView!!.findViewById(R.id.timeStudent)
        finishBtn = rootView!!.findViewById(R.id.finishBtn)

        studentLayout = rootView!!.findViewById(R.id.studentLayout)
        textLayout = rootView!!.findViewById(R.id.textLayout)

        database = FirebaseDatabase.getInstance().getReference("Members")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentEmail = user.email.toString()
            uid = user.uid
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

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
                            val request = RequestTutor(it.child("name").value.toString(), it.child("lastname").value.toString(), it.child("school").value.toString(), it.child("phone").value.toString(), it.key, tutor_study_status, tutor_id, it.child("response").value.toString())
                            requestTutor!!.add(request)
                        }
                    }
                }

                if(requestTutor.size > 0){

                    studentLayout!!.visibility = View.GONE
                    textLayout.visibility = View.GONE
                    requestListView!!.adapter = RequestTutorFragment.requestListAdapter(uid, storageReference!!, activity!!.applicationContext, database, currentEmail, requestTutor!!)
                    requestListView!!.visibility = View.VISIBLE
                } else {

                    if(!tutor_study_status.equals("")){

                        requestListView!!.visibility = View.GONE
                        textLayout!!.visibility = View.GONE
                        studentLayout!!.visibility = View.VISIBLE

                        //get student id from tutor_study_status
                        database.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val children = dataSnapshot!!.children
                                children.forEach{
                                    if(it.key.equals(uid)){ //tutor
                                        student_id = it.child("study_status").value.toString() //student_id
                                        subject = it.child("subject").value.toString() //student_id
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
                                    if(it.key.equals(student_id)){ //student
                                        nameStudent.text = "Name : \t" + it.child("name").value.toString()
                                        lastnameStudent.text ="Lastname : \t" + it.child("lastname").value.toString()
                                        subjectStudent.text = "Subject : \t" + subject
                                        timeStudent.text = "Start Time : \t" + it.child("start_time").value.toString()

                                        val photoRef = storageReference!!.child("photo/"+student_id)
                                        val localFile = File.createTempFile("images", "jpg")
                                        photoRef.getFile(localFile)
                                                .addOnSuccessListener(OnSuccessListener<Any> {
                                                    val uri = Uri.fromFile(localFile)
                                                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.applicationContext.contentResolver,uri)
                                                    imageStudent.setImageBitmap(bitmap)

                                                }).addOnFailureListener(OnFailureListener {

                                                })
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read
                            }
                        })
                    } else {

                        requestListView!!.visibility = View.GONE
                        studentLayout!!.visibility = View.GONE
                        textLayout.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read
            }
        })

        if(requestTutor != null){
            requestListView!!.adapter = RequestTutorFragment.requestListAdapter(uid, storageReference!!, activity!!.applicationContext, database, currentEmail, requestTutor!!)
        }

        finishBtn.setOnClickListener{
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot!!.children
                    children.forEach{
                        if(it.key.equals(uid)){
                            database.child(uid).child("study_status").setValue("")
                            database.child(uid).child("start_time").setValue("")
                            database.child(it.child("study_status").value.toString()).child("study_status").setValue("")
                            database.child(it.child("study_status").value.toString()).child("start_time").setValue("")
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read
                }
            })
            studentLayout.visibility = View.GONE
            textLayout.visibility = View.VISIBLE
        }

        return rootView
    }

    private class requestListAdapter(var uid: String, var storageReference: StorageReference, var context: Context, var database: DatabaseReference, var currentEmail: String, var requestTutor: ArrayList<RequestTutor>) : BaseAdapter() {

        lateinit var student_study_status: String
        lateinit var tutor_response: String
        private var itemId: Long? = null

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
            viewHolder.nameText.text = requestTutor.get(position).name
            viewHolder.lastnameText.text = requestTutor.get(position).lastname
            viewHolder.courseText.text = "School : " + requestTutor.get(position).school
            viewHolder.telText.text = "Tel : " + requestTutor.get(position).phone

            val acceptTutorBtn = rowRequest.findViewById(R.id.acceptTutorBtn) as Button
            val rejectTutorBtn = rowRequest.findViewById(R.id.rejectTutorBtn) as Button

            if(requestTutor.get(position).response.equals("true")){
                viewHolder.imageView.setImageResource(R.drawable.accept_icon)
                viewHolder.answerLayout.visibility = View.GONE
            } else {
                viewHolder.waitingLayout.visibility = View.GONE
            }

            val photoRef = storageReference!!.child("photo/"+requestTutor.get(position).student_id)

            val localFile = File.createTempFile("images", "jpg")
            photoRef.getFile(localFile)
                    .addOnSuccessListener(OnSuccessListener<Any> {
                        val uri = Uri.fromFile(localFile)
                        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver,uri)
                        viewHolder.imageView.setImageBitmap(bitmap)

                    }).addOnFailureListener(OnFailureListener {

                    })

            rowRequest.setOnClickListener {
                Log.d("Click", "row " + requestTutor.get(position).name)
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot!!.children
                        children.forEach{
                            if(it.child("email").value.toString().equals(currentEmail)){
                                for(it in it.child("request").children){
                                    tutor_response = it.child("response").value.toString()
                                    if(tutor_response.equals("true")){
                                        return@forEach
                                    }
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
            }

            rejectTutorBtn.setOnClickListener{
                Log.d("Reject", "row " + requestTutor.get(position).name)
                rowRequest.animate().setDuration(1500).alpha(0F).withEndAction(){
                    requestTutor.remove(requestTutor.get(position))
                    notifyDataSetChanged()
                    rowRequest.setAlpha(1.0F)
                }
                database.child(requestTutor.get(position).tutor_id)
                        .child("request")
                        .child(requestTutor.get(position).student_id)
                        .removeValue()
                database.child(requestTutor.get(position).student_id)
                        .child("request")
                        .child(requestTutor.get(position).tutor_id)
                        .removeValue()
            }

            return rowRequest

        }

        private class ViewHolder(val imageView: ImageView, val nameText: TextView, val lastnameText: TextView, val courseText: TextView, val telText: TextView, val answerLayout: LinearLayout, val waitingLayout: LinearLayout)
    }

}