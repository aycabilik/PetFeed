package com.example.petfeed.models

class ModelLives {

    var id:String = ""
    var name:String = ""
    var text:String = ""
    var liveImage:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()
    constructor(
        id: String,
        name: String,
        text: String,
        liveImage: String,
        timestamp: Long,
        uid: String
    ) {
        this.id = id
        this.name = name
        this.text = text
        this.liveImage = liveImage
        this.timestamp = timestamp
        this.uid = uid
    }


}