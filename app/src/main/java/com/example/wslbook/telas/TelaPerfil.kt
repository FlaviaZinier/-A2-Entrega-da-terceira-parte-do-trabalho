package com.example.wslbook.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.Anotacao
import com.example.wslbook.Cores
import com.example.wslbook.Livro
import com.example.wslbook.componentes.BarraDeProgresso
import com.example.wslbook.componentes.CapaDoLivro

private const val META_ANUAL = 30

@Composable
fun TelaPerfil(
    livros: List<Livro>,
    anotacoes: List<Anotacao>,
    onAbrirLivro: (Int) -> Unit
) {

    var seguindo by rememberSaveable { mutableStateOf(false) }
    val seguidores = 15 + if (seguindo) 1 else 0


    val livrosLidos = livros.filter { it.status() == "Lido" }
    val paginasLidasTotal = livros.sumOf { it.paginasLidas }
    val metaPorcentagem = (livrosLidos.size * 100 / META_ANUAL).coerceAtMost(100)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Maria", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(
                    text = "Adoro romance, dark romance e casos reais em livros",
                    color = Cores.Cinza,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Localização", tint = Cores.Vermelho, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Paraná, Brasil", color = Cores.Creme, fontSize = 13.sp)
                }
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Cores.Vermelho),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Foto de perfil", tint = Cores.Creme, modifier = Modifier.size(40.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Contador(numero = "$seguidores", rotulo = "Seguidores")
            Spacer(modifier = Modifier.width(18.dp))
            Contador(numero = "30", rotulo = "Seguindo")
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { seguindo = !seguindo },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seguindo) Cores.FundoCard else Cores.Vermelho
                ),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(if (seguindo) "Seguindo ✓" else "Seguir", color = Cores.Creme, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Cores.Vermelho, RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp)
        ) {
            Contador(numero = "${livros.size}", rotulo = "Na biblioteca")
            Contador(numero = "${livrosLidos.size}", rotulo = "Lidos")
            Contador(numero = "$paginasLidasTotal", rotulo = "Páginas")
            Contador(numero = "${anotacoes.size}", rotulo = "Anotações")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Meta de leitura 2026", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("${livrosLidos.size} / $META_ANUAL livros", color = Cores.Cinza, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        BarraDeProgresso(porcentagem = metaPorcentagem)

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Cores.Vermelho, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text("Livros lidos", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (livrosLidos.isEmpty()) {
                Text("Nenhum livro concluído ainda.", color = Cores.Cinza, fontSize = 13.sp)
            } else {
                livrosLidos.chunked(3).forEach { linha ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        linha.forEach { livro ->
                            ItemLivroPerfil(
                                livro = livro,
                                onClick = { onAbrirLivro(livro.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                            )
                        }
                        repeat(3 - linha.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
private fun Contador(numero: String, rotulo: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(numero, color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(rotulo, color = Cores.Cinza, fontSize = 11.sp)
    }
}

@Composable
private fun ItemLivroPerfil(livro: Livro, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clickable { onClick() }) {
        CapaDoLivro(
            titulo = livro.titulo,
            capa = livro.capa,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(livro.titulo, color = Cores.Creme, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(livro.autor, color = Cores.Vermelho, fontSize = 10.sp, maxLines = 1)
    }
}
