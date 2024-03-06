package com.example.snapz.Classes

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val profileImage: String
){
    constructor() : this("", "", "", "")
}
