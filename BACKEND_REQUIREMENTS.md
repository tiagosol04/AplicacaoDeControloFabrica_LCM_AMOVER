# Backend Requirements — AJP Gestão Fábrica

Documento de referência para a equipa de backend. Descreve os contratos de API consumidos pela app Android, os workarounds ativos por falta de endpoints, e os endpoints que precisam de ser criados para remover esses workarounds.

---

## 1. Configuração de ambiente

| Ambiente | Base URL |
|----------|----------|
| Debug (emulador Android) | `http://10.0.2.2:5137/` |
| Release (produção) | **A configurar** — substituir `CONFIGURAR_URL_PRODUCAO_AQUI` em `build.gradle.kts` |

A app usa `BuildConfig.API_BASE_URL` injectado pelo Gradle. Em release, o tráfego HTTP sem TLS está desativado (`usesCleartextTraffic=false`). Produção tem de servir HTTPS.

---

## 2. Autenticação

### `POST /api/auth/login`

**Request:**
```json
{ "username": "string", "password": "string" }
```

**Response:**
```json
{ "token": "string" }
```

### `GET /api/auth/me`

**Response:**
```json
{
  "idUtilizador": 1,
  "username": "string",
  "email": "string",
  "roles": ["SUPERVISOR", "PRODUCAO"],
  "tipo": "string",
  "ativo": true
}
```

**Notas:**
- O token JWT é enviado em todas as chamadas subsequentes via `Authorization: Bearer <token>`.
- Em resposta 401, a app limpa a sessão e redireciona para o login automaticamente.
- O campo `roles` é usado para derivar o perfil operacional da app. Os valores reconhecidos estão documentados na secção 8.

---

## 3. Endpoints existentes e consumidos

### 3.1 Ordens de Produção

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/ordens?estado={int}` | Lista todas as ordens, filtro opcional por estado |
| `GET` | `/api/ordens/{id}` | Detalhe de uma ordem |
| `GET` | `/api/ordens/{id}/resumo` | Resumo agregado (checklists OK/NOK, contagens) |
| `GET` | `/api/ordens/{id}/motas` | Lista de motas/unidades da ordem |
| `POST` | `/api/ordens/{id}/motas` | Cria mota/unidade na ordem |
| `POST` | `/api/ordens/{id}/iniciar` | Coloca a ordem em produção (estado 0 → 1) |
| `POST` | `/api/ordens/{id}/finalizar` | Finaliza a ordem (→ estado 2) |
| `PUT` | `/api/ordens/{id}/estado` | Atualiza estado da ordem genericamente |
| `POST` | `/api/ordens/from-encomenda/{encomendaId}` | Cria ordens a partir de uma encomenda |

**Contrato de estado da ordem (`estado`):**

| Valor | Significado |
|-------|-------------|
| `0` | Por arrancar |
| `1` | Em produção |
| `2` | Concluída |
| `3` | Bloqueada |

**`POST /api/ordens/{id}/iniciar` — Response esperada:**
```json
{ "message": "string", "ordemId": 1, "estado": 1 }
```

**`POST /api/ordens/{id}/finalizar` — Response esperada:**
```json
{ "message": "string", "ordemId": 1, "estado": 2, "dataConclusao": "ISO8601" }
```

**`GET /api/ordens/{id}/resumo` — Response esperada:**
```json
{
  "ordemId": 1,
  "checklists": {
    "montagemOk": true,
    "embalagemOk": false,
    "controloOk": false
  },
  "servicos": 2,
  "motas": 1
}
```

---

### 3.2 Motas / Unidades / VIN

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/motas?estado=&ordemId=&semVin=` | Lista motas com filtros opcionais |
| `POST` | `/api/motas` | Cria mota diretamente (sem ordem) |
| `GET` | `/api/motas/{id}` | Detalhe de uma mota |
| `GET` | `/api/motas/by-vin/{vin}` | Busca mota pelo VIN/quadro |
| `PUT` | `/api/motas/{id}/estado` | Atualiza estado da mota |
| `PUT` | `/api/motas/{id}/identificacao` | Regista ou atualiza VIN/número de quadro |
| `GET` | `/api/motas/{id}/pecas-sn` | Lista peças com número de série da mota |
| `GET` | `/api/motas/{id}/pecas-sn/resumo` | Resumo das peças SN da mota |
| `POST` | `/api/motas/{id}/pecas-sn` | Adiciona peça com número de série |
| `DELETE` | `/api/motas/pecas-sn/{idMotaPecaSn}` | Remove peça com número de série |

**`POST /api/ordens/{id}/motas` e `POST /api/motas` — Request:**
```json
{
  "numeroIdentificacao": "string",
  "cor": "string",
  "quilometragem": 0.0,
  "estado": 1
}
```
> `cor` é obrigatório. `estado=1` (Ativa) é o valor actualmente enviado como estado neutro seguro — ver decisão 4 na secção 8. `numeroIdentificacao` (VIN) pode ser string vazia no registo inicial — o VIN é depois atualizado via `PUT /api/motas/{id}/identificacao`.

**`PUT /api/motas/{id}/identificacao` — Request:**
```json
{ "numeroIdentificacao": "VINUPPERCASE17CHARS" }
```

**Response:**
```json
{ "message": "string", "idMota": 1, "numeroIdentificacao": "VINUPPERCASE17CHARS" }
```

**`POST /api/motas/{id}/pecas-sn` — Request:**
```json
{ "idPeca": 5, "numeroSerie": "SN-XYZ-001" }
```

> O backend deve rejeitar com `409 Conflict` se o número de série já existir.

---

### 3.3 Checklists

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/checklists` | Lista todos os tipos de checklist |
| `GET` | `/api/ordens/{ordemId}/checklists` | Checklists da ordem agrupados por tipo |
| `PUT` | `/api/ordens/{ordemId}/checklists/montagem/{checklistId}` | Atualiza item de montagem |
| `PUT` | `/api/ordens/{ordemId}/checklists/embalagem/{checklistId}` | Atualiza item de embalagem |
| `PUT` | `/api/ordens/{ordemId}/checklists/controlo/{checklistId}` | Atualiza item de controlo |

**`GET /api/ordens/{ordemId}/checklists` — Response esperada:**
```json
{
  "ordemId": 1,
  "montagem": [
    { "idChecklist": 1, "nome": "Quadro montado", "tipo": 1, "value": 1 }
  ],
  "embalagem": [
    { "idChecklist": 3, "nome": "Embalagem selada", "tipo": 2, "value": 0 }
  ],
  "controlo": [
    { "idChecklist": 5, "nome": "Controlo visual OK", "tipo": 3, "value": 0 }
  ]
}
```

**`PUT /api/ordens/{ordemId}/checklists/{tipo}/{checklistId}` — Request:**
```json
{ "value": 1 }
```
> `value`: `1` = concluído, `0` = pendente.

---

### 3.4 Peças

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/pecas` | Lista todas as peças disponíveis para registo de SN |

---

### 3.5 Serviços / Manutenção / Garantias

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/servicos/meta` | Metadados e contagens de serviços |
| `GET` | `/api/servicos?estado=&motaId=&modeloId=&tipo=&vin=&emAberto=&q=` | Lista serviços com filtros |
| `GET` | `/api/servicos/em-aberto` | Serviços em aberto com total e lista |
| `GET` | `/api/servicos/{id}` | Detalhe de um serviço |
| `POST` | `/api/servicos` | Cria novo serviço |
| `PUT` | `/api/servicos/{id}/estado` | Atualiza estado do serviço |
| `GET` | `/api/servicos/{id}/pecas-alteradas` | Peças alteradas num serviço |
| `POST` | `/api/servicos/{id}/pecas-alteradas` | Adiciona peça alterada |
| `DELETE` | `/api/servicos/pecas-alteradas/{idAssoc}` | Remove peça alterada |
| `GET` | `/api/servicos/motas/{motaId}/historico` | Histórico de serviços por mota |
| `GET` | `/api/servicos/by-vin/{vin}/historico` | Histórico por VIN |
| `GET` | `/api/servicos/modelos/{idModelo}/historico` | Histórico por modelo |
| `GET` | `/api/servicos/modelos/{idModelo}/problemas-frequentes` | Problemas frequentes por modelo |
| `GET` | `/api/servicos/modelos/{idModelo}/garantias` | Garantias por modelo |

**Tipos de serviço (`tipo`):**

| Valor | Tipo |
|-------|------|
| `1` | Manutenção |
| `2` | Avaria |
| `3` | Garantia |

---

### 3.6 Encomendas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/encomendas?clienteId=&estado=` | Lista encomendas com filtros |
| `GET` | `/api/encomendas/{id}` | Detalhe de uma encomenda |
| `POST` | `/api/encomendas` | Cria nova encomenda |
| `PUT` | `/api/encomendas/{id}` | Atualiza encomenda |

**Contrato de estado da encomenda (`estado`):**

| Valor | Significado |
|-------|-------------|
| `0` | Pendente |
| `1` | Em produção |
| `2` | Concluída |

---

### 3.7 Utilizadores, Modelos, Clientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/utilizadores` | Lista utilizadores |
| `GET` | `/api/utilizadores/{id}` | Detalhe do utilizador |
| `PUT` | `/api/utilizadores/{id}/status` | Atualiza status do utilizador |
| `GET` | `/api/utilizadores/{id}/motas?ativasOnly=true` | Motas associadas ao utilizador |
| `GET` | `/api/modelos` | Lista modelos de moto |
| `GET` | `/api/modelos/{id}` | Detalhe de um modelo |
| `GET` | `/api/clientes` | Lista clientes |
| `GET` | `/api/clientes/{id}` | Detalhe de um cliente |

---

## 4. Workarounds ativos — endpoints em falta

Os seguintes comportamentos da app usam workarounds por ausência de endpoints dedicados. Quando os endpoints forem criados, os workarounds devem ser removidos.

---

### 4.1 Bloquear ordem com motivo

**Situação atual:** A app usa `PUT /api/ordens/{id}/estado` com `{ "estado": 3 }`. O campo `motivo` (introduzido pelo utilizador no diálogo de confirmação) é apenas guardado em log local — **não é enviado ao backend**.

**Endpoint necessário:**
```
POST /api/ordens/{id}/bloquear
```

**Request:**
```json
{ "motivo": "string" }
```

**Response:**
```json
{
  "message": "string",
  "ordemId": 1,
  "estado": 3,
  "motivoBloqueio": "string",
  "dataOcorrencia": "ISO8601"
}
```

**Impacto:** Sem este endpoint, os motivos de bloqueio perdem-se. A rastreabilidade de decisões operacionais fica incompleta.

---

### 4.2 Desbloquear ordem

**Situação atual:** A app usa `PUT /api/ordens/{id}/estado` com `{ "estado": 1 }`. Não há confirmação do backend sobre o motivo da resolução.

**Endpoint necessário:**
```
POST /api/ordens/{id}/desbloquear
```

**Request:** (opcional)
```json
{ "resolucao": "string" }
```

**Response:**
```json
{
  "message": "string",
  "ordemId": 1,
  "estado": 1
}
```

---

### 4.3 Alertas operacionais

**Situação atual:** A app **calcula os alertas localmente** a partir das ordens e dos seus resumos. Não existe endpoint de alertas no backend. A app mostra um banner informativo a indicar esta limitação.

A lógica atual gera alertas para:
- Ordens com `estado == 3` (bloqueadas) → severidade **Crítica**
- Ordens sem mota associada e não concluídas → severidade **Alta/Média**
- Ordens com motas sem VIN → severidade **Média**
- Ordens em que montagem + embalagem estão OK mas controlo está pendente → severidade **Alta**
- Ordens em produção com montagem por fechar → severidade **Média**
- Ordens com montagem OK mas embalagem por fechar → severidade **Média**

**Endpoint necessário:**
```
GET /api/alertas
```

**Response esperada:**
```json
[
  {
    "id": 1,
    "titulo": "string",
    "descricao": "string",
    "tipo": "BLOQUEIO | QUALIDADE | OPERACIONAL | PRIORIDADE",
    "severidade": "CRITICA | ALTA | MEDIA | BAIXA",
    "estado": "ABERTO | EM_ANALISE | EM_TRATAMENTO | RESOLVIDO | FECHADO",
    "origem": "FABRICA | QUALIDADE | CLIENTE | SISTEMA",
    "ordemId": 1,
    "modeloId": 2,
    "clienteId": 3,
    "dataCriacaoIso": "ISO8601"
  }
]
```

**Endpoint opcional para persistência:**
```
POST /api/alertas/{id}/resolver
PUT /api/alertas/{id}/estado
```

---

### 4.4 Dashboard agregado

**Situação atual:** O dashboard faz uma chamada `GET /api/ordens` e depois, para cada ordem, duas chamadas paralelas (`GET /api/ordens/{id}/resumo` e `GET /api/ordens/{id}/motas`). Com N ordens, isso gera `1 + 2N` chamadas HTTP. Com 20 ordens, são 41 chamadas só para carregar o dashboard.

**Endpoint necessário:**
```
GET /api/dashboard/resumo
```

**Response esperada:**
```json
{
  "totalOrdens": 12,
  "emProducao": 5,
  "bloqueadas": 1,
  "semUnidade": 2,
  "controloPendente": 1,
  "vinPendente": 3,
  "equipaAtiva": 8,
  "servicosEmAberto": 4,
  "ordens": [
    {
      "ordemId": 1,
      "numeroOrdem": "ORD-001",
      "estado": 1,
      "modeloNome": "Modelo X",
      "clienteNome": "Cliente Y",
      "unidadeRegistada": true,
      "vinPendente": false,
      "montagemOk": true,
      "embalagemOk": false,
      "controloOk": false
    }
  ]
}
```

---

### 4.5 Histórico de bloqueios por ordem

Não existe endpoint para consultar o histórico de bloqueios de uma ordem (quem bloqueou, quando, com que motivo, e como foi resolvido).

**Endpoint necessário:**
```
GET /api/ordens/{id}/historico
```

**Response esperada:**
```json
[
  {
    "id": 1,
    "tipo": "BLOQUEIO | ESTADO | CHECKLIST | VIN",
    "descricao": "string",
    "utilizadorId": 3,
    "utilizadorNome": "string",
    "dataOcorrencia": "ISO8601",
    "valorAnterior": "string",
    "valorNovo": "string"
  }
]
```

---

## 5. Problemas de performance conhecidos

### N+1 em OrdensRealViewModel e DashboardRealViewModel

Tanto a listagem de ordens como o dashboard fazem chamadas individuais por ordem para obter o resumo (`/resumo`) e as motas (`/motas`). Os endpoints `GET /api/dashboard/resumo` (secção 4.4) e a inclusão de campos de resumo na resposta de `GET /api/ordens` resolveriam este problema.

**Alternativa a curto prazo:** O endpoint `GET /api/ordens` pode incluir os campos de resumo inline:

```json
[
  {
    "idOrdemProducao": 1,
    "numeroOrdem": "ORD-001",
    "estado": 1,
    "montagemOk": true,
    "embalagemOk": false,
    "controloOk": false,
    "totalMotas": 2,
    "totalMotasSemVin": 1
  }
]
```

---

## 6. Compatibilidade de nomes de campos JSON

A app aceita nomes de campo em múltiplas capitalizações via `@SerializedName(alternate = [...])` do Gson. Mas a consistência do backend reduz o risco de falhas silenciosas. A convenção preferida para campos novos é **camelCase** (`idOrdemProducao`, `numeroOrdem`, etc.).

Exemplos de aliases atualmente aceites para retrocompatibilidade:

| Campo preferido | Aliases aceites |
|-----------------|-----------------|
| `idOrdemProducao` | `IDOrdemProducao`, `Id`, `id` |
| `idMota` | `IDMota`, `Id` |
| `idChecklist` | `IDChecklist`, `Id`, `ID` |
| `numeroIdentificacao` | `NumeroIdentificacao`, `vin`, `VIN` |
| `idPeca` | `IDPeca`, `IdPeca` |
| `numeroSerie` | `NumeroSerie`, `serialNumber` |

---

## 7. Roles / perfis de utilizador

A app recebe `roles: List<String>` do endpoint `/api/auth/me` e mapeia para um de 6 perfis internos. O matching é feito por **conjuntos explícitos após normalização** (maiúsculas, remoção de acentos) — não por substring. Roles desconhecidas recebem o perfil `GENERICO` sem permissões críticas.

**Conjuntos reconhecidos (após normalização):**

| Conjunto | Valores aceites |
|----------|-----------------|
| Admin | ADMIN, ADMINISTRADOR, ADMINISTRACAO, GESTAO, GESTOR, MANAGER, FABRICANTE, DIRETOR, GERENTE |
| Supervisor | SUPERVISOR, SUPERVISOR_PRODUCAO, PRODUCAO, CHEFE_LINHA, RESPONSAVEL_FABRICA, CHEFE_PRODUCAO, GESTOR_PRODUCAO |
| Linha | OPERADOR, OPERADOR_LINHA, LINHA, MONTAGEM, EMBALAGEM, RESPONSAVEL_LINHA, MECANICO, TECNICO_LINHA |
| Qualidade | QUALIDADE, CONTROLO, CONTROLE, QC, QUALIDADE_PRODUCAO, TECNICO_QUALIDADE, INSPECTOR |
| Pós-venda | POSVENDA, POS_VENDA, GARANTIA, OFICINA, SERVICO, SERVICOS, AFTERSALES, ASSISTENCIA, REPARACAO |

**Recomendação:** Definir um conjunto fixo de roles e partilhá-lo com a equipa mobile para confirmar os aliases aceites.

| Role API (exemplo) | Perfil interno | Permissões-chave |
|--------------------|---------------|------------------|
| `ADMIN`, `ADMINISTRACAO`, `GESTOR` | Administração | Tudo |
| `SUPERVISOR`, `PRODUCAO`, `CHEFE_LINHA` | Supervisor de Produção | Tudo exceto criação de conta |
| `OPERADOR`, `LINHA`, `RESPONSAVEL_LINHA` | Responsável de Linha | Iniciar ordem, registar unidade/VIN, checklists, peças SN |
| `QUALIDADE`, `CONTROLO`, `QC` | Qualidade | Bloquear ordem, marcar checklists |
| `POSVENDA`, `GARANTIA`, `OFICINA` | Pós-venda | Consulta + registar serviços |
| Qualquer outro | Genérico | Consulta apenas |

### Permissões por ação (ficha operacional)

| Ação | Admin | Supervisor | Linha | Qualidade | Pós-venda |
|------|:-----:|:----------:|:-----:|:---------:|:---------:|
| Iniciar ordem | ✓ | ✓ | ✓ | — | — |
| Finalizar ordem | ✓ | ✓ | — | — | — |
| Bloquear/desbloquear | ✓ | ✓ | — | ✓ | — |
| Registar unidade | ✓ | ✓ | ✓ | — | — |
| Registar VIN | ✓ | ✓ | ✓ | — | — |
| Marcar checklist | ✓ | ✓ | ✓ | ✓ | — |
| Registar peças SN | ✓ | ✓ | ✓ | — | — |
| Criar ordens de encomenda | ✓ | ✓ | — | — | — |

---

## 8. Decisões de implementação documentadas

1. **Alertas calculados no cliente:** Enquanto não existir `GET /api/alertas`, os alertas são calculados a partir das ordens. A app mostra um banner informativo para deixar claro que não são dados persistidos no backend.

2. **Bloquear sem motivo persistido:** O motivo de bloqueio é recolhido na app mas só enviado ao backend quando o endpoint `POST /api/ordens/{id}/bloquear` for criado. Até lá, o motivo é apenas registado em `Log.i`.

3. **`criarOrdensFromEncomenda` retorna lista:** O endpoint `POST /api/ordens/from-encomenda/{encomendaId}` retorna `List<IdResponse>`. A app usa o `size` da lista para informar o utilizador do número de ordens criadas.

4. **Mota `estado` no registo — PENDÊNCIA CRÍTICA:** A app enviava `estado=3` ao criar uma mota. Por falta de confirmação do backend sobre a convenção correcta, o valor foi alterado para `estado=1` (Ativa — estado neutro seguro). Se o backend exigir um valor diferente para motas criadas em ordens em produção, ajustar `estado` em `OrdemDetalheRealViewModel.registarMota()` e remover este aviso.

5. **VIN em uppercase:** A app normaliza o VIN para maiúsculas antes de enviar via `PUT /api/motas/{id}/identificacao`. O backend deve armazenar o valor recebido sem transformação para não criar inconsistências.
