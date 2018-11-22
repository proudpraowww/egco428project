package com.egco428.egco428project

object DataProvider {
    private val data = ArrayList<LocationLatLng>()
    fun getData(): ArrayList<LocationLatLng>{
        return data
    }

    init {
        data.add(LocationLatLng(101,"hello", 35.53,103.25))
        data.add(LocationLatLng(102,"hi", 35.54,103.25))
        data.add(LocationLatLng(103,"hihi", 35.55,103.25))
    }
}