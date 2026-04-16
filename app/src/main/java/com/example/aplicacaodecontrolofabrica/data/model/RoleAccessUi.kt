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

    val canOpenCockpit: Boolean,
    val canOpenOperacao: Boolean,
    val canOpenProducao: Boolean, // compatibilidade temporária
    val canOpenAlertas: Boolean,
    val canOpenHistorico: Boolean,
    val canOpenPerfil: Boolean,

    val canMarcarPrioridade: Boolean,
    val canGerirBloqueios: Boolean,
    val canRegistarOcorrencias: Boolean,
    val canConsultarGarantias: Boolean,

    val canOpenEncomendas: Boolean = false,
    val canOpenEquipa: Boolean = false,
    val canOpenDocumentos: Boolean = false
)

fun RoleAccessUi.perfilLabel(): String = when (perfil) {
    PerfilOperacionalUi.SUPERVISOR_PRODUCAO -> "Supervisor de Produção"
    PerfilOperacionalUi.RESPONSAVEL_LINHA -> "Responsável de Linha"
    PerfilOperacionalUi.QUALIDADE -> "Qualidade"
    PerfilOperacionalUi.POS_VENDA -> "Pós-venda / Garantia"
    PerfilOperacionalUi.ADMINISTRACAO -> "Administração / Gestão"
    PerfilOperacionalUi.GENERICO -> "Perfil genérico"
}

fun resolveRoleAccess(roles: List<String>): RoleAccessUi {
    val norm = roles.map { it.trim().uppercase() }

    fun hasAny(vararg expected: String): Boolean {
        return expected.any { exp ->
            norm.any { role -> role == exp || role.contains(exp) }
        }
    }

    val isAdmin = hasAny(
        "ADMIN",
        "ADMINISTRADOR",
        "ADMINISTRACAO",
        "ADMINISTRAÇÃO",
        "GESTAO",
        "GESTÃO",
        "GESTOR",
        "MANAGER",
        "FABRICANTE"
    )

    val isSupervisor = hasAny(
        "SUPERVISOR",
        "PRODUCAO",
        "PRODUÇÃO",
        "CHEFE_LINHA",
        "RESPONSAVEL_FABRICA",
        "RESPONSÁVEL_FÁBRICA"
    )

    val isOperadorLinha = hasAny(
        "OPERADOR",
        "LINHA",
        "MONTAGEM",
        "EMBALAGEM",
        "RESPONSAVEL_LINHA",
        "RESPONSÁVEL_LINHA"
    )

    val isQualidade = hasAny(
        "QUALIDADE",
        "CONTROLO",
        "CONTROLE",
        "QC"
    )

    val isPosVenda = hasAny(
        "POSVENDA",
        "PÓS",
        "GARANTIA",
        "OFICINA",
        "SERVICO",
        "SERVIÇO"
    )

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
            canOpenEquipa = true
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
            canOpenEquipa = true
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
            canOpenEquipa = true
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
            canOpenEquipa = true
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
            canOpenEquipa = true
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
            canOpenEquipa = true
        )
    }
}