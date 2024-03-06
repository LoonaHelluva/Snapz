package com.example.snapz.Classes

data class MessageModel(
    val id: String,
    val type: String,
    val link: String,
    var text: String,
    val sender: String
){
    constructor() : this("", "", "", "","")
}
