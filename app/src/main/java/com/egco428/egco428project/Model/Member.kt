package com.egco428.egco428project.Model

class Member(val id:String , val email: String, val password: String, val name: String, val lastname: String,
             val status:String, val phone: String, val school: String, val statusOnOff: String, val latitude: String, val longitude: String,
             val credit: String, val subject: String, val course_price: String, val study_status: String, val start_time: String) {

    constructor(): this("","","","","","","","","",
            "","","","","","", "")
}
