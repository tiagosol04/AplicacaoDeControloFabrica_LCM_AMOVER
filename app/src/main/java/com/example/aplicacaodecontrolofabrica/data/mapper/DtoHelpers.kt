package com.example.aplicacaodecontrolofabrica.data.mapper

import com.example.aplicacaodecontrolofabrica.data.model.CoberturaServicoUi
import com.example.aplicacaodecontrolofabrica.data.model.EstadoAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.EstadoChecklistExecucaoUi
import com.example.aplicacaodecontrolofabrica.data.model.EstadoServicoUi
import com.example.aplicacaodecontrolofabrica.data.model.OrigemAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.SeveridadeAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.TipoAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.TipoChecklistUi
import com.example.aplicacaodecontrolofabrica.data.model.TipoServicoUi

object DtoHelpers {

    fun intOrZero(value: Number?): Int = value?.toInt() ?: 0

    fun doubleOrZero(value: Number?): Double = value?.toDouble() ?: 0.0

    fun text(value: Any?, fallback: String = ""): String =
        value?.toString()?.trim()?.takeIf { it.isNotBlank() } ?: fallback

    fun textOrNull(value: Any?): String? =
        value?.toString()?.trim()?.takeIf { it.isNotBlank() }

    fun uppercaseOrNull(value: Any?): String? =
        textOrNull(value)?.uppercase()

    fun firstNotBlank(vararg values: Any?): String? =
        values.firstNotNullOfOrNull { textOrNull(it) }

    fun checklistValueToBoolean(value: Number?): Boolean =
        (value?.toInt() ?: 0) == 1

    fun checklistValueToEstado(value: Number?): EstadoChecklistExecucaoUi =
        if (checklistValueToBoolean(value)) {
            EstadoChecklistExecucaoUi.CONCLUIDO
        } else {
            EstadoChecklistExecucaoUi.POR_FAZER
        }

    fun isUserAtivo(ativo: Boolean?, estado: Number?): Boolean {
        return when {
            ativo != null -> ativo
            estado != null -> estado.toInt() == 1
            else -> true
        }
    }

    fun mapChecklistTipo(value: Int?): TipoChecklistUi = when (value) {
        1 -> TipoChecklistUi.MONTAGEM
        2 -> TipoChecklistUi.EMBALAGEM
        3 -> TipoChecklistUi.CONTROLO
        else -> TipoChecklistUi.DESCONHECIDO
    }

    fun mapEstadoMota(value: Int?): String = when (value) {
        0 -> "Em Produção"
        1 -> "Ativa"
        2 -> "Em Manutenção"
        3 -> "Descontinuada"
        else -> "Desconhecido"
    }

    fun mapEstadoEncomenda(value: Int?): String = when (value) {
        0 -> "Pendente"
        1 -> "Pronta"
        2 -> "Enviada"
        3 -> "Atrasada"
        else -> "Registada"
    }

    fun mapTipoServico(raw: String?): TipoServicoUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("MANUTEN") -> TipoServicoUi.MANUTENCAO
            value.contains("AVARIA") -> TipoServicoUi.AVARIA
            value.contains("GARANTIA") -> TipoServicoUi.GARANTIA
            value.contains("INSPEC") -> TipoServicoUi.INSPECAO
            value.contains("DIAGN") -> TipoServicoUi.DIAGNOSTICO
            value.contains("PREPARA") || value.contains("ENTREGA") -> TipoServicoUi.PREPARACAO_ENTREGA
            value.contains("CAMPANHA") -> TipoServicoUi.CAMPANHA_TECNICA
            else -> TipoServicoUi.OUTRO
        }
    }

    fun mapEstadoServico(raw: String?): EstadoServicoUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("CONCL") -> EstadoServicoUi.CONCLUIDO
            value.contains("CANCEL") -> EstadoServicoUi.CANCELADO
            value.contains("TRAT") || value.contains("CURSO") -> EstadoServicoUi.EM_TRATAMENTO
            else -> EstadoServicoUi.ABERTO
        }
    }

    fun mapCoberturaServico(raw: String?): CoberturaServicoUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("FORA") -> CoberturaServicoUi.FORA_GARANTIA
            value.contains("GARANTIA") -> CoberturaServicoUi.GARANTIA
            else -> CoberturaServicoUi.POR_INTEGRAR
        }
    }

    fun mapTipoAlerta(raw: String?): TipoAlertaUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("BLOQUEIO") -> TipoAlertaUi.BLOQUEIO
            value.contains("MATERIAL") -> TipoAlertaUi.MATERIAL_CRITICO
            value.contains("QUALIDADE") -> TipoAlertaUi.QUALIDADE
            value.contains("ATRASO") -> TipoAlertaUi.ATRASO
            value.contains("PRIORIDADE") -> TipoAlertaUi.PRIORIDADE
            value.contains("TECNICO") || value.contains("TÉCNICO") -> TipoAlertaUi.TECNICO
            value.contains("GARANTIA") -> TipoAlertaUi.GARANTIA
            value.contains("OPERACIONAL") || value.isBlank() -> TipoAlertaUi.OPERACIONAL
            else -> TipoAlertaUi.OUTRO
        }
    }

    fun mapSeveridadeAlerta(raw: String?): SeveridadeAlertaUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("CRIT") -> SeveridadeAlertaUi.CRITICA
            value.contains("ALTA") -> SeveridadeAlertaUi.ALTA
            value.contains("MEDIA") || value.contains("MÉDIA") -> SeveridadeAlertaUi.MEDIA
            else -> SeveridadeAlertaUi.BAIXA
        }
    }

    fun mapEstadoAlerta(raw: String?): EstadoAlertaUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("FECH") -> EstadoAlertaUi.FECHADO
            value.contains("RESOLV") -> EstadoAlertaUi.RESOLVIDO
            value.contains("TRAT") -> EstadoAlertaUi.EM_TRATAMENTO
            value.contains("ANAL") -> EstadoAlertaUi.EM_ANALISE
            else -> EstadoAlertaUi.ABERTO
        }
    }

    fun mapOrigemAlerta(raw: String?): OrigemAlertaUi {
        val value = raw?.trim()?.uppercase().orEmpty()
        return when {
            value.contains("QUALIDADE") -> OrigemAlertaUi.QUALIDADE
            value.contains("POS") || value.contains("PÓS") || value.contains("GARANTIA") -> OrigemAlertaUi.POS_VENDA
            value.contains("MANUAL") -> OrigemAlertaUi.MANUAL
            value.contains("API") -> OrigemAlertaUi.API
            value.contains("FABRICA") || value.contains("FÁBRICA") || value.contains("PRODUCAO") || value.contains("PRODUÇÃO") ->
                OrigemAlertaUi.FABRICA
            else -> OrigemAlertaUi.SISTEMA
        }
    }

    fun normalizeVin(raw: Any?): String =
        raw?.toString()?.trim()?.uppercase().orEmpty()

    fun displayName(primary: Any?, secondary: Any?, fallback: String): String =
        firstNotBlank(primary, secondary) ?: fallback
}