package com.egco428.egco428project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_profile_student.*
import com.google.firebase.database.FirebaseDatabase
import android.support.v7.app.AlertDialog
import android.widget.*
import kotlinx.android.synthetic.main.payment_dialog.*
import android.widget.Toast
import android.widget.CheckBox




class profileStudent : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_student)

        historyBtn.setOnClickListener(this)
        paymentBtn.setOnClickListener(this)
        editBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v===historyBtn){

            val names = arrayOf("A", "B", "C", "D")
            var alertDialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.history_dialog, null) as View
            alertDialog.setView(convertView)
            val lv = convertView.findViewById<View>(R.id.historyList) as ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
            lv.setAdapter(adapter)
            val btn = convertView.findViewById<View>(R.id.okBtn) as Button
            btn.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        else if(v===paymentBtn){

            var alertDialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.payment_dialog, null) as View
            alertDialog.setView(convertView)
            val btn = convertView.findViewById<View>(R.id.okBtn) as Button

            val check100 = convertView.findViewById<View>(R.id.checkbox_cash100) as CheckBox
            val check200 = convertView.findViewById<View>(R.id.checkbox_cash200) as CheckBox
            val check300 = convertView.findViewById<View>(R.id.checkbox_cash300) as CheckBox
            val check400 = convertView.findViewById<View>(R.id.checkbox_cash400) as CheckBox
            val check500 = convertView.findViewById<View>(R.id.checkbox_cash500) as CheckBox
            val check600 = convertView.findViewById<View>(R.id.checkbox_cash600) as CheckBox

            btn.setOnClickListener {
                var values = 0
                if(check100.isChecked){values = values+100}
                if(check200.isChecked){values = values+200}
                if(check300.isChecked){values = values+300}
                if(check400.isChecked){values = values+400}
                if(check500.isChecked){values = values+500}
                if(check600.isChecked){values = values+600}
                creditText.text = "Credit : "+values
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        else if(v===editBtn){

            var alertDialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.student_edit_dialog, null) as View
            alertDialog.setView(convertView)
            var name = convertView.findViewById<View>(R.id.editName) as EditText
            var surename = convertView.findViewById<View>(R.id.editSurename) as EditText
            var school = convertView.findViewById<View>(R.id.editSchool) as EditText
            var tel = convertView.findViewById<View>(R.id.editTel) as EditText
            var email = convertView.findViewById<View>(R.id.editMail) as EditText
            var password = convertView.findViewById<View>(R.id.editPassword) as EditText
            val btn = convertView.findViewById<View>(R.id.okBtn) as Button
            
            btn.setOnClickListener {

                alertDialog.dismiss()
            }
            alertDialog.show()
        }

    }

}
