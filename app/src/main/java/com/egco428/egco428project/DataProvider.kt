package com.egco428.egco428project

object DataProvider {
    private val data = ArrayList<PersonData>()
    fun getData(): ArrayList<PersonData>{
        return data
    }

//    init {
//        data.add(PersonData(101,"hello", 35.53,103.25))
//        data.add(PersonData(102,"hi", 35.54,103.25))
//        data.add(PersonData(103,"hihi", 35.55,103.25))
//    }
}