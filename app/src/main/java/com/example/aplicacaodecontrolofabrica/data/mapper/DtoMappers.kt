package com.example.aplicacaodecontrolofabrica.data.mapper

import com.example.aplicacaodecontrolofabrica.core.auth.UserSession
import com.example.aplicacaodecontrolofabrica.data.dto.AlertaUiDto
import com.example.aplicacaodecontrolofabrica.data.dto.ChecklistDto
import com.example.aplicacaodecontrolofabrica.data.dto.ChecklistItemDto
import com.example.aplicacaodecontrolofabrica.data.dto.ChecklistsStatusDto
import com.example.aplicacaodecontrolofabrica.data.dto.ClienteDto
import com.example.aplicacaodecontrolofabrica.data.dto.LoginResponse
import com.example.aplicacaodecontrolofabrica.data.dto.MeResponse
import com.example.aplicacaodecontrolofabrica.data.dto.ModeloDto
import com.example.aplicacaodecontrolofabrica.data.dto.MotaDto
import com.example.aplicacaodecontrolofabrica.data.dto.MotaPecaSnDto
import com.example.aplicacaodecontrolofabrica.data.dto.OrdemProducaoDto
import com.example.aplicacaodecontrolofabrica.data.dto.OrdemResumoDto
import com.example.aplicacaodecontrolofabrica.data.dto.PecaDto
import com.example.aplicacaodecontrolofabrica.data.dto.UtilizadorDto
import com.example.aplicacaodecontrolofabrica.data.model.Alerta
import com.example.aplicacaodecontrolofabrica.data.model.Checklist
import com.example.aplicacaodecontrolofabrica.data.model.ChecklistExecucao
import com.example.aplicacaodecontrolofabrica.data.model.Cliente
import com.example.aplicacaodecontrolofabrica.data.model.ModeloMota
import com.example.aplicacaodecontrolofabrica.data.model.Mota
import com.example.aplicacaodecontrolofabrica.data.model.MotaPecasSN
import com.example.aplicacaodecontrolofabrica.data.model.OrdemProducao
import com.example.aplicacaodecontrolofabrica.data.model.OrdemResumo
import com.example.aplicacaodecontrolofabrica.data.model.Peca
import com.example.aplicacaodecontrolofabrica.data.model.Responsavel
import com.example.aplicacaodecontrolofabrica.data.model.TipoChecklistUi
import com.example.aplicacaodecontrolofabrica.data.model.tipoChecklistFromApi

// ---------------------------------------------------------
// Auth
// ---------------------------------------------------------

fun LoginResponse.toUserSession(): UserSession =
    UserSession(
        userId = userId,
        username = DtoHelpers.text(username, "Utilizador"),
        email = DtoHelpers.text(email, ""),
        roles = roles
    )

fun MeResponse.toUserSession(): UserSession =
    UserSession(
        userId = userId,
        username = DtoHelpers.text(username, "Utilizador"),
        email = DtoHelpers.text(email, ""),
        roles = roles
    )

// ---------------------------------------------------------
// Alertas
// ---------------------------------------------------------

fun AlertaUiDto.toModel(): Alerta =
    Alerta(
        id = id,
        titulo = DtoHelpers.text(titulo, "Alerta"),
        descricao = DtoHelpers.text(descricao, ""),
        tipo = DtoHelpers.mapTipoAlerta(tipo),
        severidade = DtoHelpers.mapSeveridadeAlerta(prioridade),
        estado = DtoHelpers.mapEstadoAlerta(estado),
        origem = DtoHelpers.mapOrigemAlerta(origem),
        ordemId = ordemId,
        motaId = null,
        modeloId = modeloId,
        clienteId = clienteId,
        vin = DtoHelpers.uppercaseOrNull(vin),
        responsavelId = null,
        responsavelNome = null,
        dataCriacaoIso = dataIso,
        dataAtualizacaoIso = dataIso,
        dataResolucaoIso = null,
        observacoes = null
    )

fun List<AlertaUiDto>.toAlertasModel(): List<Alerta> =
    map { it.toModel() }

// ---------------------------------------------------------
// Ordens
// ---------------------------------------------------------

fun OrdemProducaoDto.toModel(): OrdemProducao =
    OrdemProducao(
        id = idOrdemProducao ?: 0,
        numeroOrdem = DtoHelpers.text(numeroOrdem, "Sem nº"),
        estado = estado ?: 0,
        idModelo = idModelo,
        idCliente = idCliente,
        idEncomenda = idEncomenda,
        paisDestino = DtoHelpers.textOrNull(paisDestino),
        dataCriacaoIso = dataCriacao,
        dataConclusaoIso = dataConclusao,
        prioridadeManual = false,
        motivoPrioridade = null,
        motivoBloqueio = null
    )

fun List<OrdemProducaoDto>.toOrdensModel(): List<OrdemProducao> =
    map { it.toModel() }

fun OrdemResumoDto.toModel(): OrdemResumo =
    OrdemResumo(
        ordemId = ordemId,
        motas = motas,
        servicos = servicos,
        montagemOk = checklists.montagemOk,
        embalagemOk = checklists.embalagemOk,
        controloOk = checklists.controloOk
    )

// ---------------------------------------------------------
// Motas / peças / SN
// ---------------------------------------------------------

fun MotaDto.toModel(): Mota =
    Mota(
        id = idMota ?: 0,
        numeroIdentificacao = DtoHelpers.uppercaseOrNull(numeroIdentificacao),
        cor = DtoHelpers.textOrNull(cor),
        km = quilometragem ?: 0.0,
        estado = DtoHelpers.mapEstadoMota(estado),
        idModelo = idModelo,
        idOrdemProducao = idOrdemProducao,
        dataRegistoIso = dataRegisto,
        dataCriacaoIso = null,
        dataModificacaoIso = null
    )

fun List<MotaDto>.toMotasModel(): List<Mota> =
    map { it.toModel() }

fun MotaPecaSnDto.toModel(): MotaPecasSN =
    MotaPecasSN(
        id = id ?: 0,
        idMota = idMota ?: 0,
        idPeca = idPeca ?: 0,
        serialNumber = DtoHelpers.text(serialNumber),
        nomePeca = null,
        dataCriacaoIso = null,
        dataModificacaoIso = null
    )

fun List<MotaPecaSnDto>.toMotasPecasSnModel(): List<MotaPecasSN> =
    map { it.toModel() }

fun PecaDto.toModel(): Peca =
    Peca(
        id = idPeca ?: 0,
        nome = DtoHelpers.displayName(
            primary = nome,
            secondary = descricao ?: partNumber,
            fallback = "Peça sem nome"
        ),
        tipo = DtoHelpers.textOrNull(tipo),
        quantidade = quantidade,
        estado = estado?.toString(),
        partNumber = DtoHelpers.textOrNull(partNumber),
        descricao = DtoHelpers.textOrNull(descricao),
        serializavel = false,
        critica = false
    )

fun List<PecaDto>.toPecasModel(): List<Peca> =
    map { it.toModel() }

// ---------------------------------------------------------
// Utilizadores / responsáveis
// ---------------------------------------------------------

fun UtilizadorDto.toModel(): Responsavel =
    Responsavel(
        id = idUtilizador ?: 0,
        nome = DtoHelpers.text(username, "Sem responsável"),
        funcao = DtoHelpers.textOrNull(tipo),
        contacto = DtoHelpers.textOrNull(telefone),
        estado = if (DtoHelpers.isUserAtivo(ativo, estado)) "Ativo" else "Inativo",
        email = DtoHelpers.textOrNull(email),
        aspNetUserId = DtoHelpers.textOrNull(keycloakId),
        roles = emptyList()
    )

fun List<UtilizadorDto>.toResponsaveisModel(): List<Responsavel> =
    map { it.toModel() }

// ---------------------------------------------------------
// Clientes / modelos
// ---------------------------------------------------------

fun ClienteDto.toModel(): Cliente =
    Cliente(
        id = idCliente ?: 0,
        nome = DtoHelpers.text(nome, "Sem cliente"),
        nif = DtoHelpers.textOrNull(nif),
        localidade = DtoHelpers.textOrNull(localidade),
        email = DtoHelpers.textOrNull(email),
        telefone = DtoHelpers.textOrNull(telefone),
        morada = null,
        codigoPostal = null,
        pais = null,
        estado = if (DtoHelpers.isUserAtivo(ativo, estado)) "Ativo" else "Inativo",
        dataCriacaoIso = dataCriacao,
        dataModificacaoIso = dataModificacao,
        ultimaEncomendaIso = ultimaEncomenda
    )

fun List<ClienteDto>.toClientesModel(): List<Cliente> =
    map { it.toModel() }

fun ModeloDto.toModel(): ModeloMota =
    ModeloMota(
        id = idModelo ?: 0,
        nomeModelo = DtoHelpers.text(nomeModelo, "Sem modelo"),
        codigoProduto = DtoHelpers.textOrNull(codigoProduto),
        cilindrada = cilindrada,
        autonomia = autonomia,
        tipoMotorizacao = null,
        estado = DtoHelpers.text(estado, "Ativo"),
        descricao = DtoHelpers.textOrNull(descricao),
        observacoes = null,
        dataInicioProducaoIso = dataInicioProducao,
        dataLancamentoIso = dataLancamento,
        dataDescontinuacaoIso = dataDescontinuacao
    )

fun List<ModeloDto>.toModelosModel(): List<ModeloMota> =
    map { it.toModel() }

// ---------------------------------------------------------
// Checklists
// ---------------------------------------------------------

fun ChecklistDto.toModel(): Checklist =
    Checklist(
        id = idChecklist ?: 0,
        nome = DtoHelpers.text(nome, "Checklist"),
        descricao = "",
        tipo = tipoChecklistFromApi(tipo)
    )

fun List<ChecklistDto>.toChecklistsModel(): List<Checklist> =
    map { it.toModel() }

fun ChecklistItemDto.toChecklistExecucao(
    ordemId: Int,
    grupo: TipoChecklistUi
): ChecklistExecucao =
    ChecklistExecucao(
        id = idChecklist ?: 0,
        idChecklist = idChecklist ?: 0,
        idOrdemProducao = ordemId,
        idResponsavel = null,
        grupo = grupo,
        estado = DtoHelpers.checklistValueToEstado(value),
        concluido = DtoHelpers.checklistValueToBoolean(value),
        dataCriacaoIso = null,
        dataModificacaoIso = null
    )

fun ChecklistsStatusDto.toChecklistExecucoes(): List<ChecklistExecucao> {
    val ordemIdSeguro = ordemId ?: 0

    val montagemList = montagem.map {
        it.toChecklistExecucao(
            ordemId = ordemIdSeguro,
            grupo = TipoChecklistUi.MONTAGEM
        )
    }

    val embalagemList = embalagem.map {
        it.toChecklistExecucao(
            ordemId = ordemIdSeguro,
            grupo = TipoChecklistUi.EMBALAGEM
        )
    }

    val controloList = controlo.map {
        it.toChecklistExecucao(
            ordemId = ordemIdSeguro,
            grupo = TipoChecklistUi.CONTROLO
        )
    }

    return montagemList + embalagemList + controloList
}

fun ChecklistsStatusDto.toChecklistExecucoesPorGrupo(): Map<TipoChecklistUi, List<ChecklistExecucao>> =
    mapOf(
        TipoChecklistUi.MONTAGEM to montagem.map {
            it.toChecklistExecucao(ordemId ?: 0, TipoChecklistUi.MONTAGEM)
        },
        TipoChecklistUi.EMBALAGEM to embalagem.map {
            it.toChecklistExecucao(ordemId ?: 0, TipoChecklistUi.EMBALAGEM)
        },
        TipoChecklistUi.CONTROLO to controlo.map {
            it.toChecklistExecucao(ordemId ?: 0, TipoChecklistUi.CONTROLO)
        }
    )

// ---------------------------------------------------------
// Helpers úteis para viewmodels
// ---------------------------------------------------------

fun List<ChecklistExecucao>.todosConcluidos(grupo: TipoChecklistUi): Boolean {
    val itemsGrupo = filter { it.grupo == grupo }
    return itemsGrupo.isNotEmpty() && itemsGrupo.all { it.concluido }
}

fun List<ChecklistExecucao>.countConcluidos(grupo: TipoChecklistUi): Int =
    count { it.grupo == grupo && it.concluido }

fun List<ChecklistExecucao>.countTotal(grupo: TipoChecklistUi): Int =
    count { it.grupo == grupo }