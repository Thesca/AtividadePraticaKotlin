package com.example.atividadepratica

data class Produto(val nome: String, val categoria: String, val preco: Double, val quantidade: Int) {
    fun exibir() = "$nome (${quantidade} unidades)"
}