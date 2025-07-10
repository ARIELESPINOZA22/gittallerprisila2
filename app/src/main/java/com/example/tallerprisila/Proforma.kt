package com.example.tallerprisila

data class Proforma(
    val cliente: String = "",
    val telefono: String = "",
    val fecha: String = "",
    val items: List<Item> = listOf(),
    val total: Double = 0.0,
    val observaciones: String = ""
)
