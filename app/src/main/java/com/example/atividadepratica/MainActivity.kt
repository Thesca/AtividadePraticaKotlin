package com.example.atividadepratica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class Inventario {
    companion object {
        val produtos = mutableListOf<Produto>()

        fun calcularValorTotalEstoque(): Double {
            return produtos.sumOf { it.preco * it.quantidade }
        }

        fun calcularQuantidadeTotalProdutos(): Int {
            return produtos.sumOf { it.quantidade }
        }

    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}


@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastro") {
        composable("cadastro") { TelaCadastro(navController) }
        composable("listaProdutos") { TelaListaProdutos(navController) }
        composable("estatisticasProdutos") { TelaEstatisticas(navController) }
        composable("detalhesProduto/{produtoNome}") { backStackEntry ->
            val produtoNome = backStackEntry.arguments?.getString("produtoNome") ?: ""
            TelaDetalhesProduto(navController, produtoNome)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(navController: NavController) {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nome.isNotEmpty() && categoria.isNotEmpty() && preco.isNotEmpty() && quantidade.isNotEmpty()) {
                val precoDouble = preco.toDoubleOrNull()
                val quantidadeInt = quantidade.toIntOrNull()
                if (precoDouble != null && quantidadeInt != null) {
                    Inventario.produtos.add(Produto(nome, categoria, precoDouble, quantidadeInt))
                    Toast.makeText(context, "Produto cadastrado!", Toast.LENGTH_SHORT).show()
                    navController.navigate("listaProdutos")
                }else if (quantidade.toInt() <= 0 || preco.toInt() <= 0){
                    Toast.makeText(context, "Quantidade e preço devem ser maiores que 0", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, "Preço e Quantidade devem ser numéricos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Cadastrar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("listaProdutos") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Ir para a Lista")
        }
    }
}

@Composable
fun TelaListaProdutos(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Lista de Produtos", fontSize = 30.sp, modifier = Modifier.padding(top = 30.dp))
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(Inventario.produtos) { produto ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${produto.nome} (${produto.quantidade} unidades)")
                    Button(onClick = {
                        navController.navigate("detalhesProduto/${produto.nome}")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("estatisticasProdutos") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Ver Estatísticas")
        }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Voltar ao Cadastro")
        }
    }
}

@Composable
fun TelaDetalhesProduto(navController: NavController, produtoNome: String) {
    val produto = Inventario.produtos.find { it.nome == produtoNome }

    if (produto != null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Detalhes do Produto", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nome: ${produto.nome}")
            Text(text = "Categoria: ${produto.categoria}")
            Text(text = "Preço: R$${produto.preco}")
            Text(text = "Quantidade em Estoque: ${produto.quantidade}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar")
            }
        }
    } else {
        Text("Produto não encontrado")
    }
}

@Composable
fun TelaEstatisticas(navController: NavController) {
    val valorTotalEstoque = Inventario.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Inventario.calcularQuantidadeTotalProdutos()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Estatísticas do Inventário")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Quantidade Total de Produtos: $quantidadeTotalProdutos")
        Text(text = "Valor Total do Estoque: R$$valorTotalEstoque")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}