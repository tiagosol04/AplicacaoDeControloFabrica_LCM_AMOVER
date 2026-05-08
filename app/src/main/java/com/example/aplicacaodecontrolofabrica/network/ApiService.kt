package com.example.aplicacaodecontrolofabrica.core.network

import com.example.aplicacaodecontrolofabrica.data.dto.*
import retrofit2.http.*

interface ApiService {

    // ── Auth ──
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/auth/me")
    suspend fun me(): MeResponse

    // ── Ordens de Produção ──
    @GET("api/ordens")
    suspend fun getOrdens(@Query("estado") estado: Int? = null): List<OrdemProducaoDto>

    @GET("api/ordens/{id}")
    suspend fun getOrdem(@Path("id") id: Int): OrdemProducaoDto

    @GET("api/ordens/{id}/resumo")
    suspend fun getResumo(@Path("id") id: Int): OrdemResumoDto

    @GET("api/ordens/{id}/motas")
    suspend fun getMotasDaOrdem(@Path("id") id: Int): List<MotaDto>

    @POST("api/ordens/{id}/motas")
    suspend fun criarMota(@Path("id") id: Int, @Body body: CriarMotaRequest): IdResponse

    @POST("api/ordens/from-encomenda/{encomendaId}")
    suspend fun criarOrdensFromEncomenda(@Path("encomendaId") encomendaId: Int): List<IdResponse>

    @PUT("api/ordens/{id}/estado")
    suspend fun updateEstadoOrdem(@Path("id") id: Int, @Body body: UpdateEstadoRequest)

    @POST("api/ordens/{id}/iniciar")
    suspend fun iniciarOrdem(@Path("id") id: Int): IniciarOrdemResponse

    @POST("api/ordens/{id}/finalizar")
    suspend fun finalizarOrdem(@Path("id") id: Int): FinalizarOrdemResponse

    @GET("api/ordens/{id}/ficha")
    suspend fun getOrdemFicha(@Path("id") id: Int): OrdemFichaDto

    @POST("api/ordens/{id}/bloquear")
    suspend fun bloquearOrdem(@Path("id") id: Int, @Body body: BloquearOrdemRequest): BloquearOrdemResponse

    @POST("api/ordens/{id}/desbloquear")
    suspend fun desbloquearOrdem(@Path("id") id: Int, @Body body: DesbloquearOrdemRequest): DesbloquearOrdemResponse

    @GET("api/ordens/{id}/historico")
    suspend fun getOrdemHistorico(@Path("id") id: Int): HistoricoOrdemResponse

    @POST("api/ordens/{id}/marcar-embalada")
    suspend fun marcarEmbalada(@Path("id") id: Int): MarcarEmbalagemResponse

    @POST("api/ordens/{id}/marcar-enviada")
    suspend fun marcarEnviada(@Path("id") id: Int): MarcarEnviadaResponse

    @GET("api/ordens/prontos-expedicao")
    suspend fun getProntosExpedicao(): ProntosExpedicaoResponse

    @GET("api/ordens/{id}/utilizadores")
    suspend fun getOrdemUtilizadores(@Path("id") id: Int): OrdemUtilizadoresResponse

    @GET("api/dashboard/resumo")
    suspend fun getDashboardResumo(): DashboardResumoDto

    @GET("api/alertas")
    suspend fun getAlertas(): AlertasApiResponse

    // ── Motas / VIN / peças SN ──
    @GET("api/motas")
    suspend fun getMotas(@Query("estado") estado: Int? = null, @Query("ordemId") ordemId: Int? = null, @Query("semVin") semVin: Boolean? = null): List<MotaDto>

    @POST("api/motas")
    suspend fun criarMotaDireto(@Body body: CriarMotaRequest): IdResponse

    @GET("api/motas/{id}")
    suspend fun getMota(@Path("id") id: Int): MotaDto

    @GET("api/motas/by-vin/{vin}")
    suspend fun getMotaByVin(@Path("vin") vin: String): MotaDto

    @PUT("api/motas/{id}/estado")
    suspend fun updateEstadoMota(@Path("id") id: Int, @Body body: UpdateEstadoRequest)

    @PUT("api/motas/{id}/identificacao")
    suspend fun updateVin(@Path("id") id: Int, @Body body: UpdateVinRequest): UpdateVinResponse

    @GET("api/motas/{id}/pecas-sn")
    suspend fun getMotaPecasSn(@Path("id") id: Int): List<MotaPecaSnDto>

    @GET("api/motas/{id}/pecas-sn/resumo")
    suspend fun getMotaPecasSnResumo(@Path("id") id: Int): PecasSnResumoResponse

    @GET("api/motas/{id}/pecas-fixas")
    suspend fun getMotaPecasFixas(@Path("id") id: Int): PecaFixaResponse

    @PUT("api/motas/{id}")
    suspend fun updateMota(@Path("id") id: Int, @Body body: CriarMotaRequest): MotaDto

    @POST("api/motas/{id}/pecas-sn")
    suspend fun addPecaSn(@Path("id") id: Int, @Body body: AddPecaSnRequest): IdResponse

    @DELETE("api/motas/pecas-sn/{idMotaPecaSn}")
    suspend fun deleteMotaPecaSn(@Path("idMotaPecaSn") id: Int)

    // ── Peças ──
    @GET("api/pecas")
    suspend fun getPecas(): List<PecaDto>

    // ── Checklists ──
    @GET("api/checklists")
    suspend fun getChecklists(): List<ChecklistDto>

    @GET("api/ordens/{ordemId}/checklists")
    suspend fun getChecklistsDaOrdem(@Path("ordemId") ordemId: Int): ChecklistsStatusDto

    @PUT("api/ordens/{ordemId}/checklists/montagem/{checklistId}")
    suspend fun updateChecklistMontagem(@Path("ordemId") ordemId: Int, @Path("checklistId") checklistId: Int, @Body body: UpdateChecklistRequest)

    @PUT("api/ordens/{ordemId}/checklists/embalagem/{checklistId}")
    suspend fun updateChecklistEmbalagem(@Path("ordemId") ordemId: Int, @Path("checklistId") checklistId: Int, @Body body: UpdateChecklistRequest)

    @PUT("api/ordens/{ordemId}/checklists/controlo/{checklistId}")
    suspend fun updateChecklistControlo(@Path("ordemId") ordemId: Int, @Path("checklistId") checklistId: Int, @Body body: UpdateChecklistRequest)

    // ── Serviços / Manutenção / Garantias ──
    @GET("api/servicos/meta")
    suspend fun getServicosMeta(): ServicosMetaResponse

    @GET("api/servicos")
    suspend fun getServicos(@Query("estado") estado: Int? = null, @Query("motaId") motaId: Int? = null, @Query("modeloId") modeloId: Int? = null, @Query("tipo") tipo: Int? = null, @Query("vin") vin: String? = null, @Query("emAberto") emAberto: Boolean? = null, @Query("q") q: String? = null): List<ServicoDto>

    @GET("api/servicos/em-aberto")
    suspend fun getServicosEmAberto(): ServicosEmAbertoResponse

    @GET("api/servicos/{id}")
    suspend fun getServico(@Path("id") id: Int): ServicoDto

    @POST("api/servicos")
    suspend fun criarServico(@Body body: CreateServicoRequest): ServicoDto

    @PUT("api/servicos/{id}/estado")
    suspend fun updateServicoEstado(@Path("id") id: Int, @Body body: UpdateServicoEstadoRequest): ServicoDto

    @GET("api/servicos/{id}/pecas-alteradas")
    suspend fun getPecasAlteradas(@Path("id") id: Int): List<PecaAlteradaDto>

    @POST("api/servicos/{id}/pecas-alteradas")
    suspend fun addPecaAlterada(@Path("id") id: Int, @Body body: AddPecaAlteradaRequest)

    @DELETE("api/servicos/pecas-alteradas/{idAssoc}")
    suspend fun deletePecaAlterada(@Path("idAssoc") idAssoc: Int)

    @GET("api/servicos/motas/{motaId}/historico")
    suspend fun getHistoricoByMota(@Path("motaId") motaId: Int): HistoricoMotaResponse

    @GET("api/servicos/by-vin/{vin}/historico")
    suspend fun getHistoricoByVin(@Path("vin") vin: String): HistoricoMotaResponse

    @GET("api/servicos/modelos/{idModelo}/historico")
    suspend fun getHistoricoByModelo(@Path("idModelo") idModelo: Int): HistoricoModeloResponse

    @GET("api/servicos/modelos/{idModelo}/problemas-frequentes")
    suspend fun getProblemasFrequentes(@Path("idModelo") idModelo: Int): ProblemasFrequentesResponse

    @GET("api/servicos/modelos/{idModelo}/garantias")
    suspend fun getGarantiasByModelo(@Path("idModelo") idModelo: Int): GarantiasModeloResponse

    // ── Encomendas ──
    @GET("api/encomendas")
    suspend fun getEncomendas(@Query("clienteId") clienteId: Int? = null, @Query("estado") estado: Int? = null): List<EncomendaDto>

    @GET("api/encomendas/{id}")
    suspend fun getEncomenda(@Path("id") id: Int): EncomendaDto

    @POST("api/encomendas")
    suspend fun criarEncomenda(@Body body: CreateEncomendaRequest): IdResponse

    @PUT("api/encomendas/{id}")
    suspend fun updateEncomenda(@Path("id") id: Int, @Body body: CreateEncomendaRequest)

    // ── Utilizadores / Modelos / Clientes ──
    @GET("api/utilizadores")
    suspend fun getUtilizadores(): List<UtilizadorDto>

    @GET("api/utilizadores/{id}")
    suspend fun getUtilizadorDetalhe(@Path("id") id: Int): UtilizadorDetailResponseDto

    @PUT("api/utilizadores/{id}/status")
    suspend fun updateUtilizadorStatus(@Path("id") id: Int, @Body body: UpdateUserStatusRequest)

    @GET("api/utilizadores/{id}/motas")
    suspend fun getMotasDoUtilizador(@Path("id") id: Int, @Query("ativasOnly") ativasOnly: Boolean = true): UtilizadorMotasResponseDto

    @GET("api/modelos")
    suspend fun getModelos(): List<ModeloDto>

    @GET("api/modelos/{id}")
    suspend fun getModelo(@Path("id") id: Int): ModeloDto

    @GET("api/clientes")
    suspend fun getClientes(): List<ClienteDto>

    @GET("api/clientes/{id}")
    suspend fun getCliente(@Path("id") id: Int): ClienteDto
}
