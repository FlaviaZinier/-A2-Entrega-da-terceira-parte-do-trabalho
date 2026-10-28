# 📝 PROCESSO: do Trabalho 1 ao MAF

Este documento conta, com as nossas palavras, como o app WSL BOOK saiu de 3 telas estáticas e chegou a um app com navegação real e listas que funcionam.

> 📸 Os prints ficam na pasta `docs/prints/`.

---

## 1. Como estava o projeto no Trabalho 1, e o que mudou para chegar aqui

### Como estava
No Trabalho 1 tínhamos **3 telas estilizadas**, cada uma num arquivo separado:

| Arquivo | Tela |
|---|---|
| `paginainicial.kt` | Início ("Da sua biblioteca", pesquisa, filtros e catálogo) |
| `biblioteca.kt` | Biblioteca ("Últimas leituras" e livros com status) |
| `usuario.kt` | Perfil (foto, seguidores, favoritos e meta de leitura) |

Problemas que esse formato tinha:
- **Cada arquivo tinha a sua própria `MainActivity`.** Funcionava para testar uma tela por vez, mas não dava para juntar tudo num app só.
- **Os botões eram "de mentirinha"**: os ícones da barra de baixo não faziam nada (em duas telas eram só uma `Row` de ícones).
- **Os dados eram fixos** no código: títulos, porcentagens e números escritos à mão.
- Cada tela usava um **tom de vermelho diferente** (`#D9364F` e `#B03A3A`).

![Trabalho 1: Início](docs/prints/00-t1-inicio.png) ![Trabalho 1: Biblioteca](docs/prints/00-t1-biblioteca.png) ![Trabalho 1: Perfil](docs/prints/00-t1-perfil.png)

### O que mudou
1. **Uma `MainActivity` só.** Ela abre o `AppNavigation`, que conhece todas as telas.
2. **Navegação de verdade** com `NavHost` + objeto `Rotas`, e uma **BottomNavigation funcional**.
3. **Duas data classes** (`Livro` e `Anotacao`) guardadas em listas **reativas** (`mutableStateListOf`).
4. As telas do Trabalho 1 foram **evoluídas**:
   - **Início**: a pesquisa agora filtra, os filtros de gênero ligam e desligam, e o "Continue lendo" vem da lista.
   - **Biblioteca**: virou uma lista de verdade (`LazyColumn` + `Card`), com adicionar e remover.
   - **Perfil**: o botão Seguir funciona, e os números (livros, lidos, páginas, meta) são **calculados** das listas.
5. Entraram **5 telas novas**: Novo livro, Detalhe do livro, Leitura, Anotações e Detalhe da anotação.
6. Criamos um objeto `Cores` para o app inteiro usar a mesma paleta.
7. **Cada livro ganhou a sua própria capa.** No Trabalho 1 todos os livros usavam a mesma imagem (`capa.png`). Agora a data class `Livro` tem um campo `capa`, que guarda qual imagem de `res/drawable` mostrar. Livros novos, cadastrados pelo formulário, usam a `capa.png` como capa padrão.

---

## 2. Por que essas telas novas, e o que cada uma faz

Nosso app é de **leitura**. Então nos perguntamos: além do livro, o que uma leitora guarda? Os **trechos que ela marca**. Daí veio a segunda data class, `Anotacao`.

| # | Tela | Para que serve | Print |
|---|---|---|---|
| 1 | Início | Vitrine: continue lendo, busca, filtros, catálogo | ![](docs/prints/01-inicio.png) |
| 2 | Biblioteca | **Lista de Livros**: adicionar, remover, filtrar por status | ![](docs/prints/02-biblioteca.png) |
| 3 | Novo livro | Formulário para cadastrar livro | ![](docs/prints/03-novo-livro.png) |
| 4 | Detalhe do livro | Tudo sobre um livro + progresso editável + anotações dele | ![](docs/prints/04-detalhe-livro.png) |
| 5 | Leitura | Ler, ajustar fonte e avançar páginas | ![](docs/prints/05-leitura.png) |
| 6 | Anotações | **Lista de Anotações**: adicionar, favoritar (Checkbox), remover | ![](docs/prints/06-anotacoes.png) |
| 7 | Detalhe da anotação | Trecho completo, posição no livro, link para o livro | ![](docs/prints/07-detalhe-anotacao.png) |
| 8 | Perfil | Estatísticas calculadas, meta e livros lidos | ![](docs/prints/08-perfil.png) |

**Por que o formulário de livro é uma tela separada e o de anotação fica dentro da lista?**
O livro tem muitos campos (5), então numa tela própria fica mais organizado. A anotação tem só 3 campos, então colocamos um formulário que **abre e fecha** dentro da própria lista. Assim usamos dois jeitos diferentes de adicionar.

---

## 3. Decisões de configuração e organização do código

### 3.1 Uma porta de entrada só
Ficou **uma `MainActivity`**, que só chama `AppNavigation()`. Pensamos nela como a **porta da frente da casa**: uma porta só, e os cômodos (telas) ficam lá dentro, ligados por corredores (navegação).

### 3.2 Como organizamos as rotas
Todas as rotas ficam no objeto `Rotas` (em `Modelos.kt`), como `const val String`:

```kotlin
object Rotas {
    const val INICIO = "inicio"
    const val BIBLIOTECA = "biblioteca"
    const val ANOTACOES = "anotacoes"
    const val PERFIL = "perfil"
    const val NOVO_LIVRO = "novo_livro"
    const val DETALHE_LIVRO = "detalhe_livro/{livroId}"
    const val DETALHE_ANOTACAO = "detalhe_anotacao/{anotacaoId}"
    const val LEITURA = "leitura/{livroId}"

    fun detalheLivro(id: Int) = "detalhe_livro/$id"
    ...
}
```

- As telas de Detalhe recebem **o `id` do item pela rota** (ex: `detalhe_livro/3`), e não o objeto inteiro. A tela usa esse id para **buscar o item atualizado** na lista. Assim, se o livro for editado (ex: +10 páginas), o Detalhe mostra o valor novo.
- Criamos funções como `detalheLivro(id)` para não precisar montar o texto da rota "na mão" em cada tela, o que evita erro de digitação.

### 3.3 Onde ficou a lista
As duas listas (`livros` e `anotacoes`) ficam **no `AppNavigation`**, e não dentro de uma tela.

**Por quê?** Porque várias telas precisam dos mesmos dados: a Biblioteca adiciona um livro, e ele tem que aparecer no Início, no Perfil e no formulário de Anotações. Se a lista morasse dentro da Biblioteca, as outras telas não a enxergariam. As telas recebem a lista por parâmetro.

Para **editar** um item (ex: mudar páginas lidas ou marcar favorita), usamos `copy()` da data class e trocamos o item na lista. Assim a lista "percebe" a mudança e as telas se atualizam.

Quando um livro é removido, **as anotações dele também são removidas**, para não sobrar anotação "órfã".

### 3.4 Como estruturamos o NavHost
- O `NavHost` fica dentro de um `Scaffold` no `AppNavigation`.
- A **BottomNavigation só aparece nas 4 telas principais** (Início, Biblioteca, Anotações, Perfil). Nas telas de Detalhe, Leitura e Novo livro ela some e aparece uma **TopAppBar com botão de voltar** (`popBackStack()`).
- Ao trocar de aba usamos `popUpTo` + `launchSingleTop` + `restoreState`, para não empilhar telas repetidas quando se clica várias vezes na barra.

### 3.5 Pastas
Separamos o código em `telas/` (uma tela por arquivo) e `componentes/` (peças reaproveitadas, como a barra de baixo, a barra do topo, a capa e o filtro). No Trabalho 1 cada tela tinha a sua própria barra de navegação copiada; agora existe uma só.

### 3.6 Leitura saiu da barra de baixo
No Trabalho 1 a barra tinha um ícone de "Leitura". Mas para ler é preciso saber **qual livro**. Por isso a Leitura agora abre pelo **Detalhe do livro** ("Começar a ler" / "Continuar leitura"), e o lugar dela na barra ficou com **Anotações**.

---

## 4. A complexidade extra no Detalhe (seção 3.2 do enunciado)

Escolhemos o **Detalhe do livro** para ter o "passo a mais", e ele faz **quatro coisas** além de mostrar os campos:

1. **Informação calculada**: porcentagem lida, páginas que faltam e uma **estimativa de dias para terminar** (lendo 20 páginas por dia). O **status** ("Quero ler", "Lendo", "Lido") também é calculado a partir das páginas.
2. **Edição no próprio Detalhe**: botões **-10 pág**, **+10 pág** e **Concluir** mudam o progresso na hora. A mudança aparece também na Biblioteca, no Início e no Perfil.
3. **Combinação das duas listas**: mostra as **anotações daquele livro**, buscadas na outra lista pelo `livroId`.
4. **Navegação secundária**: dali se abre a **Leitura** e o **Detalhe de cada anotação**.

O **Detalhe da anotação** também tem algo a mais: calcula **em que ponto do livro** o trecho está (ex: "página 42 de 320, 13% do livro"), avisa se a leitora já passou daquele trecho e leva de volta ao livro dono.

**Por que escolhemos isso?** Porque é o que faria sentido num app de leitura de verdade: quem lê quer saber quanto falta e atualizar o progresso rápido, sem ter que entrar num formulário.

![Detalhe antes do +10](docs/prints/09-detalhe-antes.png) ![Detalhe depois do +10](docs/prints/10-detalhe-depois.png)

---

## 5. Dificuldades e como resolvemos

> ✏️ *Cada integrante deve revisar e completar esta parte com a própria experiência.*

- **Três `MainActivity`**: ao juntar os arquivos do Trabalho 1 no mesmo projeto, apareceu erro de "Redeclaration". Resolvemos deixando uma `MainActivity` só e transformando cada tela numa função `@Composable` comum.
- **Nomes repetidos**: havia uma `data class Livro` no Início e uma função `Livro()` na Biblioteca. Unificamos tudo na data class `Livro` do `Modelos.kt`.
- **Versões das bibliotecas**: tivemos erro de "AAR metadata". Resolvemos baixando `core-ktx` para `1.13.1` e `lifecycle-runtime-ktx` para `2.8.7`.
- **Projeto sem o Gradle**: na primeira tentativa abrimos só a pasta com o código, e o botão de rodar ficou desativado. Entendemos que o código precisa estar dentro de um projeto Android completo, com os arquivos do Gradle. Resolvemos criando um projeto novo (Empty Activity, pacote `com.example.wslbook`) e colocando nossos arquivos dentro dele.
- **103 erros de "Unresolved reference"**: a biblioteca de navegação não estava instalada, então o Android Studio não reconhecia `NavHost`, `composable` e `navArgument`. Resolvemos adicionando `navigation-compose` e `material-icons-extended` no `build.gradle.kts (Module :app)` e fazendo o Sync. Aprendemos que um único item faltando pode gerar dezenas de erros em cadeia.
- **Imagem com nome inválido**: o arquivo da capa ficou `capa.png.png`, porque o Windows esconde a extensão. O Android só aceita letras minúsculas, números e `_` no nome dos recursos. Resolvemos renomeando para `capa.png`.
- _(Integrante 3: escreva aqui uma dificuldade sua e como resolveu)_

---

## 6. Vídeo
🎬 _Link do vídeo mostrando o app funcionando: (colar aqui)_
