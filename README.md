# 📚 WSL BOOK: Persistência com Room (Trabalho 3)

Aplicativo Android de leitura e organização de livros, feito em **Kotlin + Jetpack Compose**.

| Trabalho | O que o app ganhou |
|---|---|
| 1 | 3 telas estilizadas, com navegação "de mentirinha" |
| 2 (MAF) | Navegação real entre 8 telas e duas listas que mudam (Livros e Anotações), mas só **na memória** |
| **3** | **Banco de dados Room**: tudo que é adicionado, editado ou removido **continua lá depois de fechar o app**. E o app **não perde mais a tela ao girar o celular** (ViewModel) |

📄 A história do Trabalho 2 está em [PROCESSO-TRABALHO2.md](PROCESSO-TRABALHO2.md).

---

##  Como rodar o projeto

1. Clone o repositório: `git clone <URL-DESTE-REPOSITORIO>`
2. Android Studio → **File → Open** → escolha a pasta do projeto.
3. Espere o **Gradle Sync** terminar.
4. Escolha o emulador ou o celular e clique em **Run ▶**.

**Se der erro:**
- **"Using kotlin.sourceSets DSL... not allowed with built-in Kotlin"**: confira se o `gradle.properties` tem a linha `android.disallowKotlinSourceSets=false`.
- **App fecha ao abrir, com erro do Room ("cannot verify the data integrity")**: o formato do banco mudou desde a última instalação. **Desinstale o app** do emulador e rode de novo.
- **Erro do foojay / Java**: File → Settings → Build Tools → Gradle → Gradle JDK → **Embedded JDK**.

---

##  O que o app faz (CRUD com banco)

| Ação | Onde | Operação no banco |
|---|---|---|
| **Buscar** | Ao abrir Início, Biblioteca, Anotações e Perfil | `buscarTodos()` / `buscarTodas()` |
| **Inserir** livro | Biblioteca → **Adicionar** | `livroDAO.inserir()` |
| **Inserir** anotação | Anotações → **Nova anotação** | `anotacaoDAO.inserir()` |
| **Atualizar** livro | Detalhe →  **Editar** (formulário) ou **+10 / -10 / Concluir** | `livroDAO.atualizar()` |
| **Atualizar** anotação | Checkbox  de favorita | `anotacaoDAO.atualizar()` |
| **Remover** | 🗑️ lixeira nas duas listas e "Excluir anotação" | `excluir()` |
| **Buscar por id** | Detalhe do livro e tela de Editar | `livroDAO.buscarPorId(id)` |

---

##  Processo e decisões do Trabalho 3

>  **Vídeo do teste de persistência:** https://drive.google.com/drive/folders/10BdfiakQQgYS7CZQNhlcZgEALxLb0DFB?usp=drive_link
>  Prints em https://drive.google.com/drive/folders/1HKmxuwz4TSBQKqyk4Ce1B6eu7bXS2se3?usp=sharing

### 1. Como estava o projeto no Trabalho 2, e o que mudou?

No Trabalho 2, as listas de livros e anotações eram `mutableStateListOf` dentro do `AppNavigation`. Adicionar e remover funcionava, mas **ao fechar o app tudo voltava para os dados de exemplo**. E ao **girar o celular**, a tela "recomeçava": filtros, busca e o que estava digitado nos formulários sumiam.

O que mudou:
1. As duas data classes viraram **tabelas** do Room (`@Entity`).
2. Criamos os **DAOs** (os "pedidos" ao banco) e o **`AppDatabase`** (padrão Singleton).
3. Criamos o **`LivrosViewModel`**, que guarda as listas e faz todas as operações de banco.
4. As telas de lista **buscam do banco ao abrir** (`LaunchedEffect(Unit)`).
5. O **Detalhe do livro busca o livro direto do banco pelo id**.
6. Criamos a tela de **Editar livro** (o formulário de "Novo livro" reaproveitado).
7. O estado das telas (busca, filtros, campos dos formulários) passou de `remember` para **`rememberSaveable`**.

### 2. Quais Entity, DAO e Database foram criados, e por que esses campos?

**`Livro`** → tabela `livros`
| Campo | Por quê |
|---|---|
| `id` (`@PrimaryKey(autoGenerate = true)`) | Identifica cada livro. O banco gera sozinho (1, 2, 3...) |
| `titulo`, `autor`, `genero` | O que aparece nos cards e nos filtros |
| `totalPaginas`, `paginasLidas` | Daqui saem os valores **calculados**: %, status, páginas restantes |
| `capa` (texto, ex: `"img_10"`) | O **nome** da imagem. Não guardamos o número `R.drawable`, porque ele muda a cada compilação e o banco ficaria com um número velho |

**`Anotacao`** → tabela `anotacoes`
| Campo | Por quê |
|---|---|
| `id` (`@PrimaryKey(autoGenerate = true)`) | Identifica cada anotação |
| `livroId` (`ForeignKey` com `CASCADE`) | Liga a anotação ao livro. Se o livro for apagado, as anotações dele são apagadas juntas |
| `trecho`, `pagina` | O conteúdo da anotação |
| `favorita` | Marcada pelo Checkbox (é a edição da lista de anotações) |

**`AppDatabase`**: `@Database(entities = [Livro::class, Anotacao::class], version = 1)`, com `companion object` + `@Volatile` + `synchronized`, igual ao exemplo da aula. Assim o app inteiro usa **um banco só**.

### 3. Como estruturamos as queries do DAO?

Os **4 básicos** em cada DAO, todos `suspend`: `@Insert`, `@Query` (buscar todos), `@Update` e `@Delete`.

**Consultas extras:**
| Query | Para quê |
|---|---|
| `SELECT * FROM livros WHERE id = :id` (`buscarPorId`) | O Detalhe e a tela de Editar buscam o livro certo no banco |
| `SELECT * FROM anotacoes WHERE livroId = :livroId ORDER BY pagina` (`buscarPorLivro`) | O Detalhe do livro mostra só as anotações daquele livro, já em ordem de página |
| `SELECT * FROM anotacoes WHERE id = :id` (`buscarPorId`) | Buscar uma anotação específica |
| `SELECT COUNT(*) FROM livros` (`contar`) | Saber se o banco está vazio na primeira vez, para colocar os livros de exemplo |
| `inserirTodos` / `inserirTodas` | Inserir os dados de exemplo de uma vez |

**Coroutines:** toda chamada ao DAO roda numa Coroutine no **`Dispatchers.IO`** (`viewModelScope.launch(Dispatchers.IO) { ... }`), com **`try/catch` e `Log.e`**, como no exemplo da aula. Usamos o `viewModelScope` em vez de `CoroutineScope(...)` solto porque ele é cancelado sozinho quando o ViewModel deixa de existir.

### 4. Detalhe lendo do banco (seção 3.3)

A tela de Detalhe do livro recebe **só o id** pela rota (`detalhe_livro/{livroId}`). Ao abrir, um `LaunchedEffect` chama `livroDAO.buscarPorId(id)` e `anotacaoDAO.buscarPorLivro(id)`. Quando o livro é editado (+10 pág ou pelo formulário), a busca roda de novo e a tela mostra o valor que **está salvo no banco**.

![Detalhe do livro] (https://drive.google.com/file/d/1wQhhGzcByI7T3LhctRhHkpTgfjLNLYLP/view?usp=drive_link) ![Editar livro] (https://drive.google.com/file/d/1tj0bJfkQKjErxf0aG45yMKYBwoU0olG7/view?usp=drive_link).

### 5. A rotação de tela e o ViewModel

Ao girar o celular, o Android **destrói e recria** a tela. Tudo que estava em `remember` era perdido, e a tela "recomeçava".
- **Listas:** passaram para o **`LivrosViewModel`**. O ViewModel **não é destruído** na rotação, então as listas continuam carregadas.
- **Campos e filtros das telas:** passaram para **`rememberSaveable`**, que guarda o valor durante a rotação.

![Antes de girar] (https://drive.google.com/file/d/1cza6SHwc3N_qd92nCOtBcSWG22vkM6PJ/view?usp=drive_link) ![Depois de girar] (https://drive.google.com/file/d/1M1pv4jQ5XgrQOCH7y6Jm_QR7CzRH3v8u/view?usp=drive_link)

### 6. Qual foi a dificuldade em migrar de `mutableStateListOf` para Room?

- **Imports faltando:** `@Entity`, `@PrimaryKey`, `ForeignKey` e `Flow` não eram reconhecidos. Faltavam os imports do `androidx.room`, e os arquivos do banco estavam na pasta `telas`, sem enxergar as data classes. Movemos o banco para uma pasta própria, `dados/`.
- **Telas ainda mexendo na lista antiga:** a Biblioteca ainda fazia `livros.remove(...)`, mas agora a lista vem do banco e não pode ser alterada direto. Trocamos por uma chamada ao banco (`excluirLivro`).
- **Erro do KSP com o AGP 9:** o Sync falhou com *"Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin"*. Resolvemos com `android.disallowKotlinSourceSets=false` no `gradle.properties`.
- **Capa salva como número:** guardávamos `R.drawable.img_10` (um número) no banco. Esse número muda a cada compilação, então trocamos para o **nome** da imagem.
- **App fechando depois de mudar uma `@Entity`:** o banco instalado no emulador ainda tinha o formato antigo. Resolvemos desinstalando o app antes de testar.
- **Tela recomeçando ao girar o celular:** resolvido com ViewModel + `rememberSaveable`.

### 7. Teste de persistência 

1. Adicionar um livro novo na Biblioteca.
2. Editar esse livro (no Detalhe).
3. Remover outro livro.
4. **Fechar o app completamente** (tirar dos apps recentes).
5. Abrir de novo: o livro adicionado e a edição **continuam lá**, e o removido **continua removido**.

 _Link do vídeo: (https://drive.google.com/drive/folders/1_NQwK8sZW3sbIknNpzjZzZiTyLziVbVn?usp=sharing)

### 8. Dificuldades de cada integrante

- *Gabriele:* fiquei com a migração das listas (mutableStateListOf) para o Room, e foi a parte mais trabalhosa do trabalho.
  - Na primeira versão o projeto *não compilava*. As data classes usavam @Entity, @PrimaryKey e ForeignKey, mas faltavam os imports do androidx.room. Os arquivos AppDatabase.kt e Daos.kt estavam dentro da pasta telas, em outro pacote, e não enxergavam o Livro e a Anotacao. Resolvemos criando a pasta dados/ só para o banco, com todos os imports certos.
  - A tela de Biblioteca ainda tentava apagar da lista antiga (livros.remove(livro)), mas agora a lista vem do banco e não pode ser alterada direto. Trocamos por uma chamada ao banco (excluirLivro). Com o ForeignKey em CASCADE, as anotações do livro são apagadas juntas, sem precisar de código a mais.
  - Depois disso, o *Sync falhou* com o erro "Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin". Pesquisando, entendemos que o KSP (a ferramenta que gera o código do Room) ainda registra os arquivos do jeito antigo, e o AGP 9 bloqueia isso. Resolvemos com android.disallowKotlinSourceSets=false no gradle.properties.
  - Também percebi que salvar a capa como R.drawable.img_10 era um problema. Esse valor é um *número gerado a cada compilação: se uma imagem fosse adicionada ou removida, o banco ficaria com um número velho e mostraria a capa errada. Trocamos para salvar o **nome* da imagem ("img_10") e criamos a função capaPorNome() para converter.
  - Por fim, depois de mudar uma @Entity, o app fechava ao abrir, porque o banco instalado no emulador ainda tinha o formato antigo. *Aprendi* que, sempre que a estrutura de uma tabela muda, é preciso desinstalar o app antes de testar (ou aumentar a version do banco).

- *Flavia:* tive dificuldade para *rodar o projeto no computador da faculdade*.
  - O Android Studio de lá é mais antigo e mostrou o erro "The project is using an incompatible version (AGP 9.3.3)... Latest supported version is AGP 9.0.0". O projeto tinha sido criado num Android Studio mais novo. Resolvemos ajustando as versões no libs.versions.toml e no build.gradle.kts: AGP 9.0.0, compileSdk e targetSdk 36, e versões do core-ktx, lifecycle e Compose compatíveis.
  - Apareceu também um erro do foojay ("Could not HEAD 'https://api.foojay.io/...'"): o Gradle tentava baixar sozinho um Java 25 e o download falhava. Resolvemos apagando o arquivo gradle-daemon-jvm.properties e escolhendo o *Embedded JDK, o Java que já vem com o Android Studio, em *Settings → Build Tools → Gradle.
  - *Aprendi* que nem todo erro é do código: muitas vezes é a versão da ferramenta. A mensagem do erro geralmente diz o que está faltando.

- *Michelly:* fiquei com dois problemas que apareciam na hora de *testar e apresentar* o app.
  - Ao *girar o celular, a tela "recomeçava": o filtro escolhido, o texto da busca e o que estava digitado no formulário sumiam. Entendemos que, na rotação, o Android **destrói e recria a tela, e tudo que estava em remember é perdido. Resolvemos de dois jeitos: as listas foram para o *LivrosViewModel*, que não é destruído na rotação, e os campos e filtros passaram de remember para *rememberSaveable**, que guarda o valor durante a rotação. Testamos girando o emulador com Ctrl + F11 no meio do formulário de Novo livro, e o texto continuou lá.
  - As *imagens do README não apareciam* no GitHub. O texto procurava arquivos como docs/prints/01-inicio.png, mas os prints tinham sido salvos como inicio.jpeg, sem o número e com outra extensão. Para o GitHub, qualquer diferença no nome é como pedir um arquivo que não existe. Corrigimos todos os caminhos para os nomes reais.
  - *Aprendi* que é melhor copiar e colar o nome do arquivo do que digitar, e usar sempre nomes em minúsculas, sem espaço e sem acento.

---

## Organização do código

```
app/src/main/java/com/example/wslbook/
├── MainActivity.kt
├── AppNavigation.kt        ← rotas + cria o ViewModel com viewModel()
├── LivrosViewModel.kt      ← listas + todas as operações de banco (Dispatchers.IO)
├── Modelos.kt              ← @Entity Livro e Anotacao, Rotas, Cores, dados iniciais
├── dados/
│   ├── AppDatabase.kt      ← @Database, Singleton
│   └── Daos.kt             ← LivroDAO e AnotacaoDAO
├── componentes/Componentes.kt
└── telas/                  ← as 8 telas (Novo livro também faz a Edição)
```

## Tecnologias
Kotlin · Jetpack Compose · Material 3 · Navigation Compose · **Room** (`@Entity`, `@Dao`, `@Database`) · **KSP** · **ViewModel** · Coroutines (`Dispatchers.IO`) · `LaunchedEffect` · `rememberSaveable`

## Integrantes
- Flavia Zinier
- Gabriele Cubas
- Michelly Wu
