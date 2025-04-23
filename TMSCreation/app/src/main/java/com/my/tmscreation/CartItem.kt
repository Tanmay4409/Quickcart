package com.my.tmscreation
import java.io.Serializable


data class CartItem(
    val name: String = "",
    val price: String = "",
    val imageResId: Int = 0,
    var key: String = ""
) :Serializable
