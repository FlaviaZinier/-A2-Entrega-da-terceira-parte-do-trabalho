# 📚 WSL BOOK: Persistência com Room (Trabalho 3)

Aplicativo Android de leitura e organização de livros, feito em **Kotlin + Jetpack Compose**.

| Trabalho | O que o app ganhou |
|---|---|
| 1 | 3 telas estilizadas, com navegação "de mentirinha" |
| 2 (MAF) | Navegação real entre 8 telas e duas listas que mudam (Livros e Anotações), mas só **na memória** |
| **3** | **Banco de dados Room**: tudo que é adicionado, editado ou removido **continua lá depois de fechar o app**. E o app **não perde mais a tela ao girar o celular** (ViewModel) |

📄 A história do Trabalho 2 está em [PROCESSO-TRABALHO2.md](PROCESSO-TRABALHO2.md).

---

## ▶️ Como rodar o projeto

1. Clone o repositório: `git clone <URL-DESTE-REPOSITORIO>`
2. Android Studio → **File → Open** → escolha a pasta do projeto.
3. Espere o **Gradle Sync** terminar.
4. Escolha o emulador ou o celular e clique em **Run ▶**.

**Se der erro:**
- **"Using kotlin.sourceSets DSL... not allowed with built-in Kotlin"**: confira se o `gradle.properties` tem a linha `android.disallowKotlinSourceSets=false`.
- **App fecha ao abrir, com erro do Room ("cannot verify the data integrity")**: o formato do banco mudou desde a última instalação. **Desinstale o app** do emulador e rode de novo.
- **Erro do foojay / Java**: File → Settings → Build Tools → Gradle → Gradle JDK → **Embedded JDK**.

---

## ✨ O que o app faz (CRUD com banco)

| Ação | Onde | Operação no banco |
|---|---|---|
| **Buscar** | Ao abrir Início, Biblioteca, Anotações e Perfil | `buscarTodos()` / `buscarTodas()` |
| **Inserir** livro | Biblioteca → **Adicionar** | `livroDAO.inserir()` |
| **Inserir** anotação | Anotações → **Nova anotação** | `anotacaoDAO.inserir()` |
| **Atualizar** livro | Detalhe → ✏️ **Editar** (formulário) ou **+10 / -10 / Concluir** | `livroDAO.atualizar()` |
| **Atualizar** anotação | Checkbox ⭐ de favorita | `anotacaoDAO.atualizar()` |
| **Remover** | 🗑️ lixeira nas duas listas e "Excluir anotação" | `excluir()` |
| **Buscar por id** | Detalhe do livro e tela de Editar | `livroDAO.buscarPorId(id)` |

---

## 📝 Processo e decisões do Trabalho 3

> 🎬 **Vídeo do teste de persistência:** _(colar o link aqui)_
> 📸 Prints em `docs/prints/trabalho3/`

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

![Antes: dados somem ao reabrir](docs/prints/trabalho3/01-antes.png)

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

![Detalhe do livro](docs/prints/trabalho3/02-detalhe.png) ![Editar livro](docs/prints/trabalho3/03-editar.png)

### 5. A rotação de tela e o ViewModel

Ao girar o celular, o Android **destrói e recria** a tela. Tudo que estava em `remember` era perdido, e a tela "recomeçava".
- **Listas:** passaram para o **`LivrosViewModel`**. O ViewModel **não é destruído** na rotação, então as listas continuam carregadas.
- **Campos e filtros das telas:** passaram para **`rememberSaveable`**, que guarda o valor durante a rotação.

![Antes de girar](docs/prints/trabalho3/04-retrato.png) ![Depois de girar](docs/prints/trabalho3/05-paisagem.png)

### 6. Qual foi a dificuldade em migrar de `mutableStateListOf` para Room?

- **Imports faltando:** `@Entity`, `@PrimaryKey`, `ForeignKey` e `Flow` não eram reconhecidos. Faltavam os imports do `androidx.room`, e os arquivos do banco estavam na pasta `telas`, sem enxergar as data classes. Movemos o banco para uma pasta própria, `dados/`.
- **Telas ainda mexendo na lista antiga:** a Biblioteca ainda fazia `livros.remove(...)`, mas agora a lista vem do banco e não pode ser alterada direto. Trocamos por uma chamada ao banco (`excluirLivro`).
- **Erro do KSP com o AGP 9:** o Sync falhou com *"Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin"*. Resolvemos com `android.disallowKotlinSourceSets=false` no `gradle.properties`.
- **Capa salva como número:** guardávamos `R.drawable.img_10` (um número) no banco. Esse número muda a cada compilação, então trocamos para o **nome** da imagem.
- **App fechando depois de mudar uma `@Entity`:** o banco instalado no emulador ainda tinha o formato antigo. Resolvemos desinstalando o app antes de testar.
- **Tela recomeçando ao girar o celular:** resolvido com ViewModel + `rememberSaveable`.

### 7. Teste de persistência 🎬

1. Adicionar um livro novo na Biblioteca.
2. Editar esse livro (✏️ no Detalhe).
3. Remover outro livro.
4. **Fechar o app completamente** (tirar dos apps recentes).
5. Abrir de novo: o livro adicionado e a edição **continuam lá**, e o removido **continua removido**.

🎬 _Link do vídeo: (colar aqui)_

![Depois de reabrir](docs/prints/trabalho3/06-reaberto.png)

### 8. Dificuldades de cada integrante
- _(Integrante 1: ...)_
- _(Integrante 2: ...)_
- _(Integrante 3: ...)_

---

## 🗂️ Organização do código

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

## 🧰 Tecnologias
Kotlin · Jetpack Compose · Material 3 · Navigation Compose · **Room** (`@Entity`, `@Dao`, `@Database`) · **KSP** · **ViewModel** · Coroutines (`Dispatchers.IO`) · `LaunchedEffect` · `rememberSaveable`

## 👥 Integrantes
- _Nome 1_
- _Nome 2_
- _Nome 3_
