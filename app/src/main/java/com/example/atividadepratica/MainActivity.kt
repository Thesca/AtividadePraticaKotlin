package com.example.atividadepratica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val produtos = remember { mutableListOf<Produto>() }

    NavHost(navController = navController, startDestination = "cadastro") {
        composable("cadastro") { TelaCadastro(navController, produtos) }
        composable("listaProdutos") { TelaListaProdutos(navController, produtos) }
        composable("detalhesProduto/{produtoNome}") { backStackEntry ->
            val produtoNome = backStackEntry.arguments?.getString("produtoNome") ?: ""
            TelaDetalhesProduto(navController, produtoNome)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(navController: NavController, produtos: MutableList<Produto>) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var qtd by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do produto:") })
        TextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoria:") })
        TextField(value = preco, onValueChange = { preco = it }, label = { Text("Preço:") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        TextField(value = qtd, onValueChange = { qtd = it }, label = { Text("Quantidade:") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Button(onClick = {
            if (nome.isBlank() || categoria.isBlank() || preco.isBlank() || qtd.isBlank()) {
                Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            } else {
                val precoDouble = preco.toDoubleOrNull() ?: 0.0
                val quantidadeInt = qtd.toIntOrNull() ?: 0

                val produto = Produto(nome, categoria, precoDouble, quantidadeInt)
                produtos.add(produto)

                // Limpa os campos após cadastrar
                nome = ""
                categoria = ""
                preco = ""
                qtd = ""

                // Navega para a tela de lista de produtos
                navController.navigate("listaProdutos")
            }
        }) {
            Text("Cadastrar Produto")
        }
    }
}

@Composable
fun TelaListaProdutos(navController: NavController, produtos: List<Produto>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(produtos) { produto ->
            ListItem(
                text = { Text(produto.exibir()) },
                trailing = {
                    Button(onClick = {
                        // Navega para a tela de detalhes do produto
                        navController.navigate("detalhesProduto/${produto.nome}")
                    }) {
                        Text("Detalhes")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview()
{

}