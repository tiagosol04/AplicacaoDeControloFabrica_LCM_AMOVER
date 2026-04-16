package com.example.aplicacaodecontrolofabrica.data.model

data class OrdemResumo(
    val ordemId: Int,
    val motas: Int = 0,
    val servicos: Int = 0,
    val montagemOk: Boolean = false,
    val embalagemOk: Boolean = false,
    val controloOk: Boolean = false
) {
    val checklistsOk: Boolean
        get() = montagemOk && embalagemOk && controloOk
}