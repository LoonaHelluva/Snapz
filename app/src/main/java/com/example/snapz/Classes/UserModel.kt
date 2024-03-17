package com.example.snapz.Classes

data class UserModel(
    var id: String,
    var name: String,
    val email: String,
    var profileImage: String
){
    constructor() : this("", "", "", "")
}
