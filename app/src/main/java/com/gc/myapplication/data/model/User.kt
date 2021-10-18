package com.gc.myapplication.data.model

/**
 * @author n.quoi
 * @date 10.18.2021
 */

data class User(
    val id: Int,
    val name: String,
    val useName: String,
    val email: String,
    val address: String,
    val street: String,
    val suite: String,
    val city: String,
    val zipCode: String,
    val geo: Geo,
    val phone: String,
    val website: String,
    val company: Company
)