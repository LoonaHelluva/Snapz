package com.example.snapz.Classes

data class ChatModel(
    var id: String,
    var name: String,
    var image: String
){
    constructor() : this("", "", "")
}
