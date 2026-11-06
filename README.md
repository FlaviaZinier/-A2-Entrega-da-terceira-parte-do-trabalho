# 📚 WSL BOOK: Arquitetura MVVM (Trabalho 4)

Aplicativo Android de leitura e organização de livros, feito em **Kotlin + Jetpack Compose**.

| Trabalho | O que o app ganhou |
|---|---|
| 1 | 3 telas estilizadas, com navegação "de mentirinha" |
| 2 (MAF) | Navegação real entre 8 telas e duas listas que mudam (Livros e Anotações), só na memória |
| 3 | Banco de dados **Room**: os dados sobrevivem ao fechar o app |
| **4** | **Arquitetura MVVM**: o mesmo app, reorganizado em camadas (Model, Repository, ViewModel, View), com **Flow**, **UiState** e **ViewModelFactory** |

📄 Histórico dos trabalhos anteriores: [README-TRABALHO3.md](README-TRABALHO3.md) · [PROCESSO-TRABALHO2.md](PROCESSO-TRABALHO2.md)

> Neste trabalho **nenhuma tela nova e nenhuma funcionalidade nova** foram criadas. O app faz exatamente o que fazia no Trabalho 3: adicionar, listar, editar, remover e guardar os dados. O que mudou foi **a organização do código por dentro**.

---

## ▶️ Como rodar o projeto

1. Clone o repositório: `git clone <URL-DESTE-REPOSITORIO>`
2. Android Studio → **File → Open** → escolha a pasta do projeto.
3. Espere o **Gradle Sync** terminar.
4. Escolha o emulador ou o celular e clique em **Run ▶**.

**Se der erro:**
- **"Using kotlin.sourceSets DSL... not allowed with built-in Kotlin"**: confira se o `gradle.properties` tem a linha `android.disallowKotlinSourceSets=false`.
- **App fecha ao abrir, com erro do Room**: desinstale o app do emulador e rode de novo.
- **Erro do foojay / Java**: File → Settings → Build Tools → Gradle → Gradle JDK → **Embedded JDK**.

---

## 🧱 As camadas do MVVM

```
   VIEW (ui)                 VIEWMODEL                 REPOSITORY              MODEL (data.local)
┌──────────────┐  chama   ┌────────────────┐  chama  ┌──────────────┐  chama  ┌──────────────┐
│TelaBiblioteca │ ──────▶ │ LivrosViewModel │ ──────▶ │LivroRepository│ ──────▶ │   LivroDAO   │──▶ Room
│  (Composable) │ ◀────── │   uiState       │ ◀────── │               │ ◀────── │  Flow<List>  │
└──────────────┘  observa └────────────────┘  Flow   └──────────────┘  Flow   └──────────────┘
```

| Camada | Pacote | O que faz | O que **não** faz |
|---|---|---|---|
| **View** | `ui` | Desenha a tela e avisa o ViewModel quando a pessoa toca em algo | Não fala com o banco, não abre Coroutine |
| **ViewModel** | `viewmodel` | Guarda o estado da tela (`UiState`) e decide o que fazer em cada ação | Não sabe que o Room existe |
| **Repository** | `data.repository` | É a única porta de entrada dos dados | Não sabe nada de tela |
| **Model** | `data.local` | As tabelas (`@Entity`), os DAOs e o banco (`@Database`) | Não sabe nada de tela nem de ViewModel |

---

## 🗂️ Organização em pacotes

```
app/src/main/java/com/example/wslbook/
├── MainActivity.kt
├── data/
│   ├── local/                     ← MODEL (Room)
│   │   ├── Livro.kt               @Entity
│   │   ├── Anotacao.kt            @Entity (ForeignKey para Livro)
│   │   ├── LivroDAO.kt            @Dao
│   │   ├── AnotacaoDAO.kt         @Dao
│   │   ├── AppDatabase.kt         @Database (Singleton)
│   │   └── DadosIniciais.kt       livros e anotações de exemplo
│   └── repository/                ← REPOSITORY
│       ├── LivroRepository.kt
│       └── AnotacaoRepository.kt
├── viewmodel/                     ← VIEWMODEL
│   ├── LivrosViewModel.kt         + LivrosUiState
│   ├── AnotacoesViewModel.kt      + AnotacoesUiState
│   ├── DetalheLivroViewModel.kt   + DetalheLivroUiState
│   ├── LivroFormViewModel.kt      + LivroFormUiState
│   └── AppViewModelFactory.kt     injeta os Repositories nos ViewModels
└── ui/                            ← VIEW
    ├── AppNavigation.kt           rotas + cria os ViewModels com a Factory
    ├── Rotas.kt
    ├── componentes/               peças reutilizadas (barra de navegação, capa, filtro...)
    ├── telas/                     as 8 telas
    └── theme/                     cores e tema
```

---

## 📝 Processo e decisões do Trabalho 4

> 🎬 (https://drive.google.com/file/d/1TvHOaO-4A0ZPVmshbzaQpZDcBBsVLv5B/view?usp=sharing)

### 1. Como estava o projeto no Trabalho 3, e o que mudou na reorganização?

No Trabalho 3 o app já salvava tudo no Room, mas o código estava **misturado**:
- Um **único `LivrosViewModel`** fazia tudo: guardava as listas de livros **e** de anotações, abria o banco e chamava os DAOs direto.
- As telas ainda tinham **lógica de banco**: o Detalhe do livro chamava funções `suspend` dentro de um `LaunchedEffect`, e o formulário de Novo livro decidia sozinho se fazia `inserir` ou `atualizar`.
- As listas eram buscadas **na mão** (`carregarLivros()` em cada tela, `recarregarLivros()` depois de cada inserir/remover).
- Os arquivos ficavam em pastas soltas (`dados/`, `telas/`, `componentes/` e arquivos na raiz).

O que mudou:
1. O código foi dividido em **4 pacotes**: `ui`, `data.local`, `data.repository` e `viewmodel`.
2. Criamos **um Repository por Entity** (`LivroRepository` e `AnotacaoRepository`). Só eles falam com os DAOs.
3. O `buscarTodos()` dos DAOs passou a retornar **`Flow<List<...>>`**. As listas se atualizam sozinhas.
4. O ViewModel único virou **4 ViewModels**, cada um com o seu **`UiState`**, no padrão `_uiState` privado + `uiState` público.
5. Criamos a **`AppViewModelFactory`**, que entrega o Repository certo para cada ViewModel.
6. As telas passaram a só **ler o `uiState`** e **chamar funções do ViewModel**.

### 2. Como o trio decidiu dividir os pacotes?

| Pacote | O que foi para lá | Por quê |
|---|---|---|
| `data.local` | `Livro`, `Anotacao`, `LivroDAO`, `AnotacaoDAO`, `AppDatabase` e os dados de exemplo | Tudo que é do **Room** fica junto. Se o banco mudar, só esta pasta muda |
| `data.repository` | `LivroRepository` e `AnotacaoRepository` | A "porta de entrada" dos dados, separada de onde eles moram |
| `viewmodel` | Os 4 ViewModels, os `UiState` (no mesmo arquivo de cada ViewModel) e a `AppViewModelFactory` | A lógica de cada tela num lugar só |
| `ui` | As 8 telas, os componentes reutilizados, o tema, as rotas e o `AppNavigation` | Tudo que **aparece na tela** |

**Decisões:**
- Deixamos cada `UiState` **no mesmo arquivo** do seu ViewModel, porque um só existe por causa do outro.
- A função `capaPorNome()`, que transforma o nome `"img_10"` na imagem, foi para `ui/componentes`. Ela só serve para **desenhar**, e o banco só guarda o nome.
- O objeto `Cores` foi para `ui/theme`, junto com o tema.

### 3. Por que o Repository existe? O que ele resolve?

Antes, o ViewModel falava direto com o DAO, ou seja, ele **sabia** que os dados vinham do Room. Agora:

```kotlin
class LivroRepository(private val livroDAO: LivroDAO) {
    fun buscarTodos(): Flow<List<Livro>> = livroDAO.buscarTodos()
    fun observarPorId(id: Int): Flow<Livro?> = livroDAO.observarPorId(id)
    suspend fun buscarPorId(id: Int): Livro? = livroDAO.buscarPorId(id)
    suspend fun inserir(livro: Livro) = livroDAO.inserir(livro)
    suspend fun atualizar(livro: Livro) = livroDAO.atualizar(livro)
    suspend fun deletar(livro: Livro) = livroDAO.deletar(livro)
}
```

Hoje ele só **repassa** para o DAO. A vantagem aparece no futuro: se um dia os livros vierem de uma **API na internet**, só o Repository muda. O ViewModel continua pedindo `buscarTodos()`, e as telas nem percebem. Ele é a **única fonte de verdade** dos dados.

### 4. DAO reativo com Flow

```kotlin
@Query("SELECT * FROM livros ORDER BY id")
fun buscarTodos(): Flow<List<Livro>>    // não é mais "suspend"
```

O `Flow` é uma **lista "ao vivo"**: o Room avisa sozinho sempre que a tabela muda. Por isso **apagamos** todo o código de "buscar de novo depois de inserir/remover" (`carregarLivros()`, `recarregarLivros()` e os `LaunchedEffect(Unit)` de busca nas telas).

Usamos Flow também em `observarPorId(id)` e `buscarPorLivro(livroId)`. Assim, no Detalhe do livro, o **+10 pág** aparece na hora, sem a tela precisar buscar de novo.

### 5. O que tem dentro do UiState de cada tela, e por quê?

| ViewModel | UiState | Campos | Usado por |
|---|---|---|---|
| `LivrosViewModel` | `LivrosUiState` | `livros`, `totalAnotacoes`, `carregando` | Início, Biblioteca, Leitura, Perfil |
| `AnotacoesViewModel` | `AnotacoesUiState` | `anotacoes`, `livros`, `carregando`, os campos do formulário (`formularioAberto`, `livroEscolhidoId`, `trecho`, `paginaTexto`, `erro`) e o filtro `soFavoritas` | Anotações, Detalhe da anotação |
| `DetalheLivroViewModel` | `DetalheLivroUiState` | `livro`, `anotacoes` (só as daquele livro), `carregando` | Detalhe do livro |
| `LivroFormViewModel` | `LivroFormUiState` | `livroId`, `titulo`, `autor`, `genero`, `totalPaginasTexto`, `paginasLidasTexto`, `capa`, `erro`, `salvo` | Novo livro e Editar livro |

**Por que esses campos:**
- **`carregando`**: a tela mostra "Carregando..." até o banco responder, em vez de "Livro não encontrado".
- **Campos do formulário dentro do UiState**: o texto digitado mora no ViewModel, então **não some ao girar o celular**.
- **`erro`**: a validação ("Informe o título do livro.") é feita no ViewModel, e a tela só mostra a mensagem.
- **`salvo`**: quando o ViewModel termina de gravar, ele muda para `true` e a tela volta sozinha.
- **`totalAnotacoes`**: o Perfil só precisa do **número** de anotações, e não da lista inteira.

**O padrão `_uiState` / `uiState`** (igual em todos os ViewModels):
```kotlin
private val _uiState = MutableStateFlow(LivrosUiState())          
val uiState: StateFlow<LivrosUiState> = _uiState.asStateFlow()    
```
E na tela:
```kotlin
val uiState by viewModel.uiState.collectAsState()
```

### 6. ViewModelFactory

O Android não sabe criar sozinho um ViewModel que **recebe parâmetros** (no nosso caso, os Repositories). A `AppViewModelFactory` é a "fábrica" que monta cada ViewModel já com o Repository certo:

```kotlin
// AppNavigation.kt
val factory = remember { AppViewModelFactory.criar(context) }  
val livrosViewModel: LivrosViewModel = viewModel(factory = factory)
```

**Decisão:** fizemos **uma Factory só**, reaproveitada pelos 4 ViewModels, em vez de 4 Factories quase iguais.

**Onde cada ViewModel é criado:**
- `LivrosViewModel` e `AnotacoesViewModel` são criados **uma vez**, no topo do `AppNavigation`, porque várias telas compartilham os mesmos dados.
- `DetalheLivroViewModel` e `LivroFormViewModel` são criados **dentro da rota** de cada tela. Assim, cada vez que o formulário abre, ele começa **vazio**, e cada Detalhe aberto tem o seu próprio livro.

### 7. As telas agora

Nenhuma tela tem mais `dao`, `CoroutineScope` ou `Dispatchers.IO`. Cada tela faz só duas coisas:
1. **Lê** o `uiState` (`collectAsState()`).
2. **Chama** funções do ViewModel (`viewModel.salvar()`, `viewModel.deletarLivro(livro)`, `viewModel.marcarFavorita(...)`).

O único estado que ficou nas telas (com `rememberSaveable`) é o que é **só visual**: o texto da busca, os filtros de gênero e de status, o tamanho da fonte na Leitura e o botão "Seguir" do Perfil.

### 8. Alguma funcionalidade quebrou durante a reorganização?

Durante a reorganização, a principal dificuldade foi atualizar os pacotes e imports depois de mover os arquivos. Algumas telas deixaram de localizar classes como Livro, Cores e Rotas. Corrigimos os imports e conferimos a ligação entre as telas, os ViewModels e os Repositories. Depois dos ajustes, os testes listados abaixo confirmaram que as funcionalidades do Trabalho 3 continuaram funcionando.

### 9. Teste: o app continua fazendo tudo que fazia no Trabalho 3

- [ X ] Adicionar um livro e uma anotação
- [ X ] Editar um livro (✏️ no Detalhe) e o +10 pág
- [ X ] Marcar uma anotação como favorita
- [ X ] Remover um livro (as anotações dele somem juntas)
- [ X ] Fechar o app e reabrir: tudo continua lá
- [ X ] Girar o celular no meio do formulário: o texto continua lá

🎬 _Link do vídeo: (https://drive.google.com/file/d/1TvHOaO-4A0ZPVmshbzaQpZDcBBsVLv5B/view?usp=sharing)

10. Dificuldades de cada integrante

Gabriele: teve dificuldade para entender como o Flow atualiza as listas automaticamente quando os dados mudam no Room. Depois de acompanhar os dados desde o DAO até o uiState, conseguiu entender melhor o fluxo.

Flavia: achou trabalhoso reorganizar os arquivos nos pacotes data.local, data.repository, viewmodel e ui, porque foi necessário revisar os pacotes e imports das classes movidas.

Michelly: teve dificuldade para configurar a AppViewModelFactory e garantir que cada ViewModel recebesse o Repository correto. A revisão conjunta do AppNavigation ajudou a resolver a integração.

---

## 🧰 Tecnologias
Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Room · KSP · **MVVM** · **Repository** · **Flow** · **StateFlow** · **UiState** · **ViewModelFactory** · Coroutines

## 👥 Integrantes
- Gabriele
- Flavia
- Michelly
