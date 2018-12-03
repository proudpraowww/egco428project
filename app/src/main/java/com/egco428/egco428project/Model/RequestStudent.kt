package com.egco428.egco428project.Model

class RequestStudent(val name: String , val lastname: String, val subject: String, val phone: String, val response: String, val tutor_id: String, val student_id: String, val student_credit: String){
    constructor(): this("","","","","", "", "", "")
}
