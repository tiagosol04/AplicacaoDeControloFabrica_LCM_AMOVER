
```
     ╔════════════════════════════════════════════════════════════════════╗
     ║                                                                    ║
     ║      █████╗       ███╗   ███╗  ██████╗ ██╗   ██╗ ███████╗██████╗   ║
     ║     ██╔══██╗      ████╗ ████║ ██╔═══██╗██║   ██║ ██╔════╝██╔══██╗  ║
     ║     ███████║█████╗██╔████╔██║ ██║   ██║██║   ██║ █████╗  ██████╔╝  ║
     ║     ██╔══██║╚════╝██║╚██╔╝██║ ██║   ██║╚██╗ ██╔╝ ██╔══╝  ██╔══██╗  ║
     ║     ██║  ██║      ██║ ╚═╝ ██║ ╚██████╔╝ ╚████╔╝  ███████╗██║  ██║  ║
     ║     ╚═╝  ╚═╝      ╚═╝     ╚═╝  ╚═════╝   ╚═══╝   ╚══════╝╚═╝  ╚═╝  ║
     ║                                                                    ║
     ║                   G E S T Ã O   D E   F Á B R I C A                ║
     ║                                                                    ║
     ╚═══════════════════════════════════════════════════════════════════ ╝
```

### Aplicação Móvel de Gestão — AJP Motorcycles

*Visão 360° da fábrica, na palma da mão do gestor.*

[![Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](#)
[![MVVM](https://img.shields.io/badge/MVVM-Architecture-FF9800?style=for-the-badge)](#)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens)](#)

---

</div>

## 📱 O que é

A **Gestão de Fábrica A-MoVeR** é uma aplicação Android nativa para **gestores, supervisores e responsáveis de qualidade** da AJP Motorcycles. É o complemento móvel da plataforma web — traz toda a informação de produção, serviços, garantias e equipa para o telemóvel, permitindo decisões em tempo real sem estar preso a um computador.

```
  ┌──────────────────────────────────────────────────────────────────┐
  │                                                                  │
  │   📊 DASHBOARD    →   Visão instantânea de toda a fábrica        │
  │   🏭 PRODUÇÃO     →   Ordens, estados, checklists, peças         │
  │   🔧 SERVIÇOS     →   Manutenção, avarias, garantias             │
  │   📦 ENCOMENDAS   →   Pipeline de encomendas de clientes         │
  │   📈 RASTREIO     →   Histórico completo por mota/VIN            │
  │   👥 EQUIPA       →   Disponibilidade e carga de trabalho        │
  │   👤 PERFIL       →   Sessão, roles, preferências                │
  │                                                                  │
  └──────────────────────────────────────────────────────────────────┘
```

---

## ⚡ Funcionalidades

### 🎯 Dashboard Operacional

O dashboard não mostra números genéricos — mostra **o que precisa de atenção agora**:

| Indicador | O que significa |
|-----------|----------------|
| **Ordens bloqueadas** | Produção parada — precisa de decisão imediata |
| **Sem unidade registada** | Motas em produção sem rastreabilidade |
| **VIN pendente** | Quadro por fechar — risco para expedição |
| **Controlo pendente** | Quase prontas, falta validação final |
| **Serviços em aberto** | Manutenções e garantias por resolver |
| **Equipa indisponível** | Cobertura de turno insuficiente |

O dashboard calcula **ações imediatas** e ordena por prioridade. As zonas de fábrica (Montagem, Embalagem, Controlo, Exceções) mostram carga em tempo real.

### 🏭 Produção

- Lista de ordens com filtros (estado, modelo, prioridade)
- **Ficha Operacional** completa por ordem:
  - Gates de rastreabilidade (Unidade ✓ → VIN ✓ → Qualidade ✓)
  - Estado dos checklists (montagem, embalagem, controlo)
  - Contexto (cliente, modelo, destino)
  - **Iniciar** e **Finalizar** ordem via API com validação completa
- Resumo de risco e próxima ação sugerida

### 🔧 Serviços, Manutenção & Garantias

Módulo completo de pós-venda:

```
                    ┌──────────────────┐
                    │   LISTA SERVIÇOS │
                    │  KPIs + Filtros  │
                    │  + Pesquisa VIN  │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │ DETALHE SERVIÇO  │
                    │ ┌──────────────┐ │
                    │ │ Info da mota │ │
                    │ │ Tipo/Estado  │ │
                    │ │ Peças alter. │ │
                    │ │ Notas        │ │
                    │ └──────────────┘ │
                    │ ┌──────────────┐ │
                    │ │ Problemas    │ │
                    │ │ frequentes   │ │
                    │ │ do modelo    │ │
                    │ └──────────────┘ │
                    │ ┌──────────────┐ │
                    │ │   Ações:     │ │
                    │ │ Iniciar      │ │
                    │ │ Concluir     │ │
                    │ └──────────────┘ │
                    └──────────────────┘
```

**8 tipos de serviço**: Manutenção, Avaria, Garantia, Inspeção, Diagnóstico, Preparação/Entrega, Campanha Técnica, Outro

**Análise por modelo**: problemas frequentes agrupados + total de garantias — permite identificar padrões e tomar decisões de engenharia.

### 📦 Encomendas

Visão do pipeline comercial:
- KPIs: Pendentes / Em produção / Concluídas
- Cliente, modelo, quantidade, data de entrega
- Acessível via atalho na top bar (para gestores/admin)

### 📈 Rastreio (Histórico)

- Pesquisa por ordem, VIN ou destino
- Filtros: Com VIN, Concluídas, Com serviços
- Resumo técnico por mota: modelo, VIN, país, serviços, estado dos checklists

### 🔐 Sistema de Roles

A app adapta-se ao perfil do utilizador:

| Perfil | Dashboard | Produção | Serviços | Encomendas | Equipa | Rastreio |
|--------|:---------:|:--------:|:--------:|:----------:|:------:|:--------:|
| **Administração** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Supervisor** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Qualidade** | ✅ | ✅ | ✅ | — | ✅ | ✅ |
| **Pós-venda** | — | ✅ | ✅ | — | ✅ | ✅ |
| **Operador** | — | ✅ | ✅ | — | ✅ | — |

A bottom bar mostra apenas os módulos a que o perfil tem acesso. Atalhos na top bar para encomendas, ocorrências e equipa aparecem apenas para quem pode vê-los.

---

## 🏗 Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                      UI LAYER                                │
│   Jetpack Compose · Material 3 · Navegação por Roles        │
│                                                              │
│   ┌───────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐   │
│   │ Dashboard │ │ Produção │ │ Serviços │ │ Encomendas │   │
│   └───────────┘ └──────────┘ └──────────┘ └────────────┘   │
│   ┌───────────┐ ┌──────────┐ ┌──────────┐                  │
│   │  Rastreio │ │  Equipa  │ │  Perfil  │                  │
│   └───────────┘ └──────────┘ └──────────┘                  │
├─────────────────────────────────────────────────────────────┤
│                   VIEWMODEL LAYER                            │
│   DashboardRealVM · OrdensRealVM · OrdemDetalheRealVM        │
│   ServicosVM · ServicoDetalheVM · EncomendasVM               │
│   AlertasVM · HistoricoVM · EquipaVM · PerfilRealVM          │
├─────────────────────────────────────────────────────────────┤
│                   REPOSITORY LAYER                           │
│   FabricaRepository (interface) → FabricaRepositoryImpl      │
│   AuthRepository (interface) → AuthRepositoryImpl            │
├─────────────────────────────────────────────────────────────┤
│                    NETWORK LAYER                             │
│   Retrofit 2 · OkHttp · JWT Interceptor · DataStore          │
│   ApiService (65+ endpoints mapeados)                        │
├─────────────────────────────────────────────────────────────┤
│                     BACKEND                                  │
│   API A-MoVeR (ASP.NET Core) · SQL Server · Identity + JWT  │
└─────────────────────────────────────────────────────────────┘
```

### Princípios

- **MVVM** com separação completa UI ↔ Lógica ↔ Dados
- **StateFlow** para estado reativo — a UI recompõe automaticamente
- **Repository Pattern** — a UI nunca toca na API diretamente
- **Role-Based Access** — navegação e funcionalidades adaptam-se ao perfil JWT
- **Offline-Resilient** — token persiste em DataStore, sessão sobrevive a reinícios

---

## 📁 Estrutura do Projeto

```
app/src/main/java/com/example/aplicacaodecontrolofabrica/
│
├── auth/
│   ├── AuthDataStore.kt              ← Persistência JWT
│   ├── AuthHeaderInterceptor.kt      ← Injeta token nos pedidos
│   ├── AuthFailureInterceptor.kt     ← Trata 401/403
│   └── UserSession.kt               ← Dados da sessão
│
├── data/
│   ├── dto/
│   │   ├── AuthDtos.kt              ← Login/Me
│   │   ├── OrdemDtos.kt             ← Ordens + Resumo
│   │   ├── ServicoDtos.kt           ← 15+ DTOs de serviços ★
│   │   ├── EncomendasAlertasDto.kt  ← Encomendas
│   │   ├── ChecklistDtos.kt        ← Checklists
│   │   ├── MotaPecasDtos.kt        ← Motas + Peças SN
│   │   ├── UtilizadorDtos.kt       ← Utilizadores + Associações
│   │   ├── ModelosDtos.kt          ← Modelos de mota
│   │   ├── ClientesDtos.kt         ← Clientes
│   │   └── ExtraDtos.kt            ← Requests genéricos
│   ├── model/                       ← Modelos de domínio UI
│   │   ├── Servico.kt              ← 8 tipos, 3 estados, cobertura
│   │   ├── RoleAccessUi.kt         ← 6 perfis operacionais ★
│   │   ├── Alerta.kt               ← Ocorrências com severidade
│   │   └── ...
│   ├── mapper/
│   │   ├── DtoHelpers.kt           ← Conversões seguras DTO→UI
│   │   └── DtoMappers.kt           ← Mapeamentos complexos
│   └── repository/
│       ├── FabricaRepository.kt     ← Interface (40+ métodos)
│       ├── FabricaRepositoryImpl.kt ← Implementação
│       ├── AuthRepository.kt        ← Interface auth
│       ├── AuthRepositoryImpl.kt    ← Implementação auth
│       └── ServiceLocator.kt        ← DI manual
│
├── features/
│   ├── cockpit/
│   │   ├── Dashboard.kt            ← 515 linhas de UI ★
│   │   └── DashboardRealViewModel.kt ← Zonas, KPIs, ações
│   ├── operacao/
│   │   ├── OperacaoScreen.kt       ← Lista de ordens
│   │   ├── FichaOperacionalScreen.kt ← Detalhe + iniciar/finalizar ★
│   │   ├── OrdensRealViewModel.kt   ← Lista com filtros
│   │   └── OrdemDetalheRealViewModel.kt ← Iniciar/Finalizar/VIN ★
│   ├── servicos/                    ★ NOVO
│   │   ├── ServicosScreen.kt        ← Lista + KPIs + filtros
│   │   ├── ServicosViewModel.kt     ← Pesquisa + filtros
│   │   ├── ServicoDetalheScreen.kt  ← Detalhe + problemas modelo
│   │   └── ServicoDetalheViewModel.kt ← Carrega serviço + análise
│   ├── encomendas/                  ★ NOVO
│   │   ├── EncomendasScreen.kt      ← Pipeline de encomendas
│   │   └── EncomendasViewModel.kt   ← Resolve clientes/modelos
│   ├── alertas/
│   ├── historico/
│   ├── equipa/
│   ├── perfil/
│   └── login/
│
├── network/
│   ├── ApiService.kt               ← 65+ endpoints ★
│   ├── ApiModule.kt                ← Configuração Retrofit
│   ├── ApiConfig.kt                ← URL base
│   └── UiErrors.kt                 ← Tratamento de erros
│
├── ui/
│   ├── components/                  ← 8 componentes reutilizáveis
│   └── theme/                       ← Material 3 customizado
│
├── di/
│   └── ViewModelFactory.kt         ← 11 ViewModels registados
│
├── AppNavigation.kt                ← 10 rotas + role-based nav ★
└── MainActivity.kt
```

---

## 🔌 Cobertura da API

A app consome **65+ endpoints** da API A-MoVeR:

| Módulo | Endpoints | Descrição |
|--------|:---------:|-----------|
| **Auth** | 2 | Login JWT + perfil |
| **Ordens** | 9 | CRUD + iniciar + finalizar + resumo + motas |
| **Motas** | 10 | CRUD + VIN + peças SN + resumo + estado |
| **Checklists** | 4 | Por ordem, toggle individual por tipo |
| **Serviços** | 14 | CRUD + estados + peças alteradas + histórico por mota/VIN/modelo + problemas frequentes + garantias |
| **Encomendas** | 4 | CRUD + filtros |
| **Utilizadores** | 5 | CRUD + motas associadas + status |
| **Modelos** | 2 | Lista + detalhe |
| **Clientes** | 2 | Lista + detalhe |
| **Peças** | 1 | Catálogo |

---

## 🚀 Setup

### Pré-requisitos

- Android Studio Hedgehog+ (2024.x)
- Kotlin 1.9+ / Compose BOM 2024.09
- API A-MoVeR a correr (porta 5137)

### Configuração

1. **Clonar o repositório**
2. **Verificar URL da API** em `build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:5137/\"")
   ```
3. **Build & Run**
4. **Login** com credenciais da plataforma web (mesmo sistema Identity)

---

## 🧩 Relação com o Ecossistema

```
  ┌─────────────────────────┐
  │     APP WEB (Desktop)   │  ← Gestão completa: encomendas,
  │     ASP.NET Core MVC    │     modelos, peças, documentos,
  │     + Razor Views       │     compras, material recebido
  └───────────┬─────────────┘
              │
  ┌───────────▼─────────────┐
  │      API A-MoVeR        │  ← Camada central de dados
  │      ASP.NET Core       │     JWT Auth · SQL Server
  │      Web API            │     65+ endpoints REST
  └───────────┬─────────────┘
              │
    ┌─────────┴──────────┐
    │                    │
    ▼                    ▼
  ┌────────────┐   ┌─────────────────┐
  │ APP GESTÃO │   │ APP LINHA       │
  │ (este app) │   │ DE MONTAGEM     │
  │            │   │                 │
  │ Gestores   │   │ Operadores      │
  │ Supervisão │   │ Chão de fábrica │
  │ Qualidade  │   │ Tablet/Scanner  │
  └────────────┘   └─────────────────┘
```

A **App de Gestão** e a **App de Linha de Montagem** são complementares:
- A app de gestão dá a **visão macro** — dashboard, métricas, decisões
- A app de linha dá o **controlo micro** — peça a peça, checklist a checklist
- Ambas lêem e escrevem nos mesmos dados via API
- Uma alteração na linha aparece no dashboard do gestor em tempo real

---

## 🔮 Roadmap

- [ ] Push notifications para alertas críticos (ordens bloqueadas, garantias)
- [ ] Gráficos de produção (motas/semana, tempo médio por ordem)
- [ ] Modo offline com sync automático
- [ ] Exportação de relatórios PDF
- [ ] Integração com PHC (faturação) para evitar duplicação de dados
- [ ] Scan de QR Code para acesso rápido a ordem/mota
- [ ] Widget Android para KPIs no home screen

---

<div align="center">

```
  A fábrica na palma da mão.
  
  AJP Motorcycles × Projeto A-MoVeR
  Penafiel, Portugal · 2025
```

</div>
