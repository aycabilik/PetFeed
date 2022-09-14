package com.example.petfeed.models

class ModelPlaces {

    var id:String = ""
    var street:String = ""
    var state:String = ""
    var country:String = ""
    var placeImage:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()
    constructor(
        id: String,
        streetEt: String,
        stateEt: String,
        countryEt: String,
        placeImage: String,
        timestamp: Long,
        uid: String
    ) {
        this.id = id
        this.street = streetEt
        this.state = stateEt
        this.country = countryEt
        this.placeImage = placeImage
        this.timestamp = timestamp
        this.uid = uid
    }


}