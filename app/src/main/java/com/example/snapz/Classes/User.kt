package com.example.snapz.Classes

data class User(
    val id: String,
    val name: String,
    val email: String
){
    constructor() : this("", "", "")
}
