package com.example.aplicacaodecontrolofabrica.data.model

enum class PerfilOperacionalUi {
    SUPERVISOR_PRODUCAO,
    RESPONSAVEL_LINHA,
    QUALIDADE,
    POS_VENDA,
    ADMINISTRACAO,
    GENERICO
}

data class RoleAccessUi(
    val perfil: PerfilOperacionalUi,

    // ── Navegação ──
    val canOpenCockpit: Boolean,
    val canOpenOperacao: Boolean,
    val canOpenProducao: Boolean,
    val canOpenAlertas: Boolean,
    val canOpenHistorico: Boolean,
    val canOpenPerfil: Boolean,
    val canOpenEncomendas: Boolean = false,
    val canOpenEquipa: Boolean = false,
    val canOpenDocumentos: Boolean = false,

    // ── Ações sobre alertas/ocorrências ──
    val canMarcarPrioridade: Boolean,
    val canGerirBloqueios: Boolean,
    val canRegistarOcorrencias: Boolean,
    val canConsultarGarantias: Boolean,

    // ── Ações na ficha operacional ──
    // Iniciar uma ordem de produção (estado 0 → 1)
    val canIniciarOrdem: Boolean = false,
    // Finalizar uma ordem (estado → 2), requer checklists OK
    val canFinalizarOrdem: Boolean = false,
    // Bloquear/desbloquear uma ordem (estado → 3 ou → 1)
    val canBloquearOrdem: Boolean = false,
    // Criar/registar mota/unidade numa ordem
    val canRegistarUnidade: Boolean = false,
    // Registar ou alterar VIN/número de quadro
    val canRegistarVin: Boolean = false,
    // Marcar/desmarcar itens de checklist (montagem, embalagem, controlo)
    val canMarcarChecklist: Boolean = false,
    // Adicionar/remover peças com número de série
    val canRegistarPecasSn: Boolean = false,
    // Criar ordem de produção a partir de uma encomenda
    val canCriarOrdemDeEncomenda: Boolean = false
)

fun RoleAccessUi.perfilLabel(): String = when (perfil) {
    PerfilOperacionalUi.SUPERVISOR_PRODUCAO -> "Supervisor de Produção"
    PerfilOperacionalUi.RESPONSAVEL_LINHA -> "Responsável de Linha"
    PerfilOperacionalUi.QUALIDADE -> "Qualidade"
    PerfilOperacionalUi.POS_VENDA -> "Pós-venda / Garantia"
    PerfilOperacionalUi.ADMINISTRACAO -> "Administração / Gestão"
    PerfilOperacionalUi.GENERICO -> "Perfil genérico"
}

private fun normalizeRole(raw: String): String =
    raw.trim().uppercase()
        .replace('Ã', 'A').replace('Â', 'A').replace('À', 'A').replace('Á', 'A')
        .replace('Ê', 'E').replace('É', 'E').replace('È', 'E')
        .replace('Í', 'I').replace('Î', 'I')
        .replace('Õ', 'O').replace('Ô', 'O').replace('Ó', 'O')
        .replace('Ú', 'U').replace('Û', 'U').replace('Ç', 'C')

private val ADMIN_ROLES = setOf(
    "ADMIN", "ADMINISTRADOR", "ADMINISTRACAO", "GESTAO", "GESTOR",
    "MANAGER", "FABRICANTE", "DIRETOR", "GERENTE"
)

private val SUPERVISOR_ROLES = setOf(
    "SUPERVISOR", "SUPERVISOR_PRODUCAO", "PRODUCAO", "CHEFE_LINHA",
    "RESPONSAVEL_FABRICA", "CHEFE_PRODUCAO", "GESTOR_PRODUCAO"
)

private val LINHA_ROLES = setOf(
    "OPERADOR", "OPERADOR_LINHA", "LINHA", "MONTAGEM", "EMBALAGEM",
    "RESPONSAVEL_LINHA", "MECANICO", "TECNICO_LINHA"
)

private val QUALIDADE_ROLES = setOf(
    "QUALIDADE", "CONTROLO", "CONTROLE", "QC",
    "QUALIDADE_PRODUCAO", "TECNICO_QUALIDADE", "INSPECTOR"
)

private val POSVENDA_ROLES = setOf(
    "POSVENDA", "POS_VENDA", "GARANTIA", "OFICINA", "SERVICO",
    "SERVICOS", "AFTERSALES", "ASSISTENCIA", "REPARACAO"
)

fun resolveRoleAccess(roles: List<String>): RoleAccessUi {
    val norm = roles.map { normalizeRole(it) }.toSet()

    val isAdmin = norm.any { it in ADMIN_ROLES }
    val isSupervisor = norm.any { it in SUPERVISOR_ROLES }
    val isQualidade = norm.any { it in QUALIDADE_ROLES }
    val isPosVenda = norm.any { it in POSVENDA_ROLES }
    val isOperadorLinha = norm.any { it in LINHA_ROLES }

    return when {
        isAdmin -> RoleAccessUi(
            perfil = PerfilOperacionalUi.ADMINISTRACAO,
            canOpenCockpit = true,
            canOpenOperacao = true,
            canOpenProducao = true,
            canOpenAlertas = true,
            canOpenHistorico = true,
            canOpenPerfil = true,
            canMarcarPrioridade = true,
            canGerirBloqueios = true,
            canRegistarOcorrencias = true,
            canConsultarGarantias = true,
            canOpenEncomendas = true,
            canOpenEquipa = true,
            canIniciarOrdem = true,
            canFinalizarOrdem = true,
            canBloquearOrdem = true,
            canRegistarUnidade = true,
            canRegistarVin = true,
            canMarcarChecklist = true,
            canRegistarPecasSn = true,
            canCriarOrdemDeEncomenda = true
        )

        isSupervisor -> RoleAccessUi(
            perfil = PerfilOperacionalUi.SUPERVISOR_PRODUCAO,
            canOpenCockpit = true,
            canOpenOperacao = true,
            canOpenProducao = true,
            canOpenAlertas = true,
            canOpenHistorico = true,
            canOpenPerfil = true,
            canMarcarPrioridade = true,
            canGerirBloqueios = true,
            canRegistarOcorrencias = true,
            canConsultarGarantias = true,
            canOpenEncomendas = true,
            canOpenEquipa = true,
            canIniciarOrdem = true,
            canFinalizarOrdem = true,
            canBloquearOrdem = true,
            canRegistarUnidade = true,
            canRegistarVin = true,
            canMarcarChecklist = true,
            canRegistarPecasSn = true,
            canCriarOrdemDeEncomenda = true
        )

        isQualidade -> RoleAccessUi(
            perfil = PerfilOperacionalUi.QUALIDADE,
            canOpenCockpit = true,
            canOpenOperacao = true,
            canOpenProducao = true,
            canOpenAlertas = true,
            canOpenHistorico = true,
            canOpenPerfil = true,
            canMarcarPrioridade = false,
            canGerirBloqueios = true,
            canRegistarOcorrencias = true,
            canConsultarGarantias = true,
            canOpenEquipa = true,
            canIniciarOrdem = false,
            canFinalizarOrdem = false,
            canBloquearOrdem = true,
            canRegistarUnidade = false,
            canRegistarVin = false,
            canMarcarChecklist = true,  // Qualidade pode marcar checklist de controlo
            canRegistarPecasSn = false,
            canCriarOrdemDeEncomenda = false
        )

        isPosVenda -> RoleAccessUi(
            perfil = PerfilOperacionalUi.POS_VENDA,
            canOpenCockpit = false,
            canOpenOperacao = true,
            canOpenProducao = true,
            canOpenAlertas = true,
            canOpenHistorico = true,
            canOpenPerfil = true,
            canMarcarPrioridade = false,
            canGerirBloqueios = false,
            canRegistarOcorrencias = true,
            canConsultarGarantias = true,
            canOpenEquipa = true,
            canIniciarOrdem = false,
            canFinalizarOrdem = false,
            canBloquearOrdem = false,
            canRegistarUnidade = false,
            canRegistarVin = false,
            canMarcarChecklist = false,
            canRegistarPecasSn = false,
            canCriarOrdemDeEncomenda = false
        )

        isOperadorLinha -> RoleAccessUi(
            perfil = PerfilOperacionalUi.RESPONSAVEL_LINHA,
            canOpenCockpit = false,
            canOpenOperacao = true,
            canOpenProducao = true,
            canOpenAlertas = true,
            canOpenHistorico = false,
            canOpenPerfil = true,
            canMarcarPrioridade = false,
            canGerirBloqueios = false,
            canRegistarOcorrencias = true,
            canConsultarGarantias = false,
            canOpenEquipa = true,
            canIniciarOrdem = true,
            canFinalizarOrdem = false,
            canBloquearOrdem = false,
            canRegistarUnidade = true,
            canRegistarVin = true,
            canMarcarChecklist = true,
            canRegistarPecasSn = true,
            canCriarOrdemDeEncomenda = false
        )

        else -> RoleAccessUi(
            perfil = PerfilOperacionalUi.GENERICO,
            canOpenCockpit = true,
            canOpenOperacao = true,
            canOpenProducao = true,
            canOpenAlertas = true,
            canOpenHistorico = true,
            canOpenPerfil = true,
            canMarcarPrioridade = false,
            canGerirBloqueios = false,
            canRegistarOcorrencias = true,
            canConsultarGarantias = true,
            canOpenEquipa = true,
            canIniciarOrdem = false,
            canFinalizarOrdem = false,
            canBloquearOrdem = false,
            canRegistarUnidade = false,
            canRegistarVin = false,
            canMarcarChecklist = false,
            canRegistarPecasSn = false,
            canCriarOrdemDeEncomenda = false
        )
    }
}