package com.example.kotlin_login

import com.example.kotlin_login.core.config.AppConfig
import com.example.kotlin_login.core.config.CognitoAppConfig
import com.example.kotlin_login.core.hardware.ActaStorageHelper
import com.example.kotlin_login.core.hardware.LocationHardwareHelper
import com.example.kotlin_login.core.hardware.QrCameraHardwareHelper
import com.example.kotlin_login.core.jwt.JwtDecoder
import com.example.kotlin_login.data.datasource.SyncPassLocalDataSourceImpl
import com.example.kotlin_login.data.datasource.SyncPassRemoteDataSource
import com.example.kotlin_login.data.datasource.SyncPassRemoteDataSourceImpl
import com.example.kotlin_login.data.local.AppDatabase
import com.example.kotlin_login.data.local.toEntity
import com.example.kotlin_login.data.remote.AsistenciaRemoteDto
import com.example.kotlin_login.data.remote.AsistenciasResponseDto
import com.example.kotlin_login.data.remote.AuthApiService
import com.example.kotlin_login.data.remote.CreateTallerRequestDto
import com.example.kotlin_login.data.remote.EncuestaRequestDto
import com.example.kotlin_login.data.remote.EncuestaResponseDto
import com.example.kotlin_login.data.remote.LoginRequest
import com.example.kotlin_login.data.remote.LoginResponse
import com.example.kotlin_login.data.remote.SyncAsistenciasRequestDto
import com.example.kotlin_login.data.remote.SyncPassApiService
import com.example.kotlin_login.data.remote.TallerRemoteDto
import com.example.kotlin_login.data.remote.TalleresResponseDto
import com.example.kotlin_login.data.remote.UserDto
import com.example.kotlin_login.data.remote.UsuarioRemoteDto
import com.example.kotlin_login.data.remote.UsuariosResponseDto
import com.example.kotlin_login.data.repository.AuthRepository
import com.example.kotlin_login.data.repository.RemoteUserItem
import com.example.kotlin_login.data.repository.SyncPassRepositoryImpl
import com.example.kotlin_login.di.DatabaseModule
import com.example.kotlin_login.di.NetworkModule
import com.example.kotlin_login.di.RepositoryModule
import com.example.kotlin_login.domain.model.AsistenciaRegistro
import com.example.kotlin_login.domain.model.EncuestaSatisfaccion
import com.example.kotlin_login.domain.model.ProyectoComunitario
import com.example.kotlin_login.domain.model.TallerEvento
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.feature.notificaciones.NtfyTopics
import com.example.kotlin_login.ui.admin.AdminViewModel
import com.example.kotlin_login.ui.beneficiario.BeneficiarioViewModel
import com.example.kotlin_login.ui.docente.DocenteViewModel
import com.example.kotlin_login.ui.home.HomeViewModel
import com.example.kotlin_login.ui.home.HomeViewModelFactory
import com.example.kotlin_login.ui.login.LoginViewModel
import com.example.kotlin_login.ui.voluntario.VoluntarioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.Base64

@OptIn(ExperimentalCoroutinesApi::class)
class SyncPassUnitTests {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakeSyncPassApiService : SyncPassApiService {
        var talleres = mutableListOf(
            TallerRemoteDto(
                id = "00000000-0000-0000-0002-000000000001",
                nombre = "Taller: Uso de WhatsApp y videollamadas",
                lugar = "Sede Social Villa El Roble",
                fechaHora = "15 oct, 09:00 hrs",
                cuposOcupados = 5,
                cuposMaximos = 40,
                estado = "En curso",
                codigoQr = "EVT-2026-001"
            )
        )
        var asistencias = mutableListOf<AsistenciaRemoteDto>()

        override suspend fun getTalleres(): TalleresResponseDto =
            TalleresResponseDto(exito = true, datos = talleres, talleres = talleres)

        override suspend fun createTaller(request: CreateTallerRequestDto): TalleresResponseDto {
            val nuevo = TallerRemoteDto(
                id = "00000000-0000-0000-0002-000000000099",
                nombre = request.nombre,
                lugar = request.lugar,
                fechaHora = request.fechaHora,
                cuposOcupados = 0,
                cuposMaximos = request.cuposMaximos,
                estado = request.estado,
                codigoQr = "EVT-NEW-99"
            )
            talleres.add(0, nuevo)
            return TalleresResponseDto(exito = true, datos = talleres, talleres = talleres)
        }

        override suspend fun deleteTaller(id: String): TalleresResponseDto {
            talleres.removeIf { it.id == id }
            return TalleresResponseDto(exito = true, datos = talleres, talleres = talleres)
        }

        override suspend fun getAsistencias(): AsistenciasResponseDto =
            AsistenciasResponseDto(exito = true, sincronizados = asistencias.size, datos = asistencias)

        override suspend fun syncAsistencias(request: SyncAsistenciasRequestDto): AsistenciasResponseDto {
            asistencias.addAll(0, request.registros)
            return AsistenciasResponseDto(
                exito = true,
                mensaje = "Sincronizado",
                sincronizados = request.registros.size,
                datos = asistencias
            )
        }

        override suspend fun submitEncuesta(request: EncuestaRequestDto): EncuestaResponseDto =
            EncuestaResponseDto(exito = true, mensaje = "Encuesta guardada")

        override suspend fun getUsuarios(): UsuariosResponseDto =
            UsuariosResponseDto(
                exito = true,
                datos = listOf(
                    UsuarioRemoteDto("u1", "Admin", "admin@duocuc.cl", "admin", true),
                    UsuarioRemoteDto("u2", "Docente", "docente@duocuc.cl", "docente", true),
                    UsuarioRemoteDto("u3", "Voluntario", "voluntario@duocuc.cl", "estudiante_voluntario", true),
                    UsuarioRemoteDto("u4", "Vecino", "vecino@comunidad.cl", "attendee", false)
                )
            )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LocationHardwareHelper calcula distancia Haversine y geocerca correctamente`() {
        val cerca = LocationHardwareHelper.evaluarGeocerca(
            latitud = -33.5015,
            longitud = -70.6887
        )
        assertTrue(cerca.dentroDeGeocerca)
        assertEquals("valido", cerca.estadoValidacion)
        assertTrue(cerca.distanciaMetros in 1..150)

        val lejos = LocationHardwareHelper.evaluarGeocerca(
            latitud = -33.4400,
            longitud = -70.6500
        )
        assertFalse(lejos.dentroDeGeocerca)
        assertEquals("fuera_de_rango", lejos.estadoValidacion)
        assertTrue(lejos.distanciaMetros > 150)

        val fallback = LocationHardwareHelper.obtenerUbicacionDispositivo(null)
        assertTrue(fallback.dentroDeGeocerca)
    }

    @Test
    fun `QrCameraHardwareHelper procesa pases QR y RUT manuales`() {
        val qrBeneficiario = QrCameraHardwareHelper.procesarCodigoEscaneado("SYNCPASS-BENEFICIARIO-12345678-9")
        assertEquals("QR", qrBeneficiario.metodo)
        assertTrue(qrBeneficiario.esQrInstitucional)
        assertEquals("María González (QR)", qrBeneficiario.nombreSugerido)

        val qrBen2 = QrCameraHardwareHelper.procesarCodigoEscaneado("QR-BEN-00102")
        assertEquals("Carlos Soto (QR)", qrBen2.nombreSugerido)

        val qrBen3 = QrCameraHardwareHelper.procesarCodigoEscaneado("QR-BEN-00103")
        assertEquals("Rosa Díaz (QR)", qrBen3.nombreSugerido)

        val manual = QrCameraHardwareHelper.procesarCodigoEscaneado("18.765.432-1")
        assertEquals("MANUAL", manual.metodo)
        assertFalse(manual.esQrInstitucional)
        assertTrue(QrCameraHardwareHelper.detectarCamaraTraseraDisponible(null))
    }

    @Test
    fun `ActaStorageHelper exporta archivo CSV en almacenamiento local`() {
        val taller = TallerEvento(
            id = "EVT-TEST-01",
            proyectoId = "P-1",
            nombre = "Taller Alfabetización",
            fechaHora = "Hoy 15:00",
            lugar = "Sede Vecinal",
            cuposMaximos = 30,
            cuposOcupados = 2,
            codigoQr = "QR-EVT-1",
            estado = "En curso"
        )
        val asistencias = listOf(
            AsistenciaRegistro("1", "EVT-TEST-01", "BEN-00101", "María González")
        )
        val tmpDir = File(System.getProperty("java.io.tmpdir"), "syncpass_test_actas")
        val file = ActaStorageHelper.guardarActaEnDispositivo(null, taller, asistencias, tmpDir)
        assertTrue(file.exists())
        val content = file.readText()
        assertTrue(content.contains("ID_REGISTRO"))
        assertTrue(content.contains("María González"))
        file.delete()
    }

    @Test
    fun `SyncPassRepositoryImpl gestiona persistencia Offline-First y sincronizacion con Supabase`() = runTest {
        val db = AppDatabase()
        val local = SyncPassLocalDataSourceImpl(db.syncPassDao())
        val fakeApi = FakeSyncPassApiService()
        val remote: SyncPassRemoteDataSource = SyncPassRemoteDataSourceImpl(fakeApi)
        val repo = SyncPassRepositoryImpl(local, remote)

        // 1. Listar talleres remotos y guardar en local
        val talleres = repo.getTalleres(forceRemote = true)
        assertTrue(talleres.isNotEmpty())
        val sampleEntity = talleres.first().copy(nombre = "Taller Editado").run {
            com.example.kotlin_login.data.local.TallerEntity(
                id = id,
                proyectoId = proyectoId,
                nombre = nombre,
                fechaHora = fechaHora,
                lugar = lugar,
                cuposMaximos = cuposMaximos,
                cuposOcupados = cuposOcupados,
                codigoQr = codigoQr,
                estado = estado
            )
        }
        db.updateTaller(sampleEntity)
        db.deleteTaller(sampleEntity.copy(id = "TEMP-DEL"))

        // 2. Crear taller online y eliminar taller
        val afterCreate = repo.createTaller("Taller Ciberseguridad", "Sede Norte", "Mañana 10:00", 25, isOffline = false)
        assertTrue(afterCreate.any { it.nombre == "Taller Ciberseguridad" })
        val afterDelete = repo.deleteTaller("00000000-0000-0000-0002-000000000099")
        assertNotNull(afterDelete)

        // 3. Registrar asistencia en modo Offline (queda pendiente en Room local)
        val regOffline = repo.registrarAsistencia(
            tallerId = "00000000-0000-0000-0002-000000000001",
            rutOrCode = "BEN-00999",
            nombreBeneficiario = "Vecino Offline",
            metodo = "QR",
            latitud = -33.5015,
            longitud = -70.6887,
            distanciaMetros = 30,
            estadoValidacion = "valido",
            isOffline = true
        )
        assertFalse(regOffline.sincronizado)
        assertTrue(repo.getPendingAsistenciasCount() >= 1)

        // 4. Sincronizar pendientes hacia el servicio remoto
        val synced = repo.syncPendingAsistencias()
        assertTrue(synced >= 1)
        assertEquals(0, repo.getPendingAsistenciasCount())

        // 5. Registrar encuesta offline y sincronizarla
        val encOffline = repo.registrarEncuesta(
            tallerId = "00000000-0000-0000-0002-000000000001",
            rutBeneficiario = "BEN-00101",
            estrellas = 5,
            utilidad = 4,
            comentarios = "Excelente taller",
            isOffline = true
        )
        assertFalse(encOffline.sincronizado)
        val syncedEnc = repo.syncPendingEncuestas()
        assertEquals(1, syncedEnc)

        // 6. Obtener usuarios RBAC
        val users = repo.getUsuarios()
        assertEquals(4, users.size)
    }

    @Test
    fun `VoluntarioViewModel maneja modo offline, escaneo QR, geocerca GPS y sincronizacion`() = runTest {
        val db = AppDatabase()
        val repo = SyncPassRepositoryImpl(
            SyncPassLocalDataSourceImpl(db.syncPassDao()),
            SyncPassRemoteDataSourceImpl(FakeSyncPassApiService())
        )
        val vm = VoluntarioViewModel(repo)

        vm.onToggleOfflineMode(true)
        assertTrue(vm.uiState.value.isOffline)

        vm.onSimulateCoordinateChange(-33.5015, -70.6887)
        assertTrue(vm.uiState.value.dentroDeGeocerca)

        vm.onRefreshHardwareSensors(null)
        vm.onCodeScanned("SYNCPASS-BENEFICIARIO-12345678-9")
        assertTrue(vm.uiState.value.pendingSyncCount >= 1)

        vm.onSyncPendingClick()
        assertEquals(0, vm.uiState.value.pendingSyncCount)
    }

    @Test
    fun `DocenteViewModel crea, selecciona, elimina talleres y exporta acta CSV`() = runTest {
        val db = AppDatabase()
        val repo = SyncPassRepositoryImpl(
            SyncPassLocalDataSourceImpl(db.syncPassDao()),
            SyncPassRemoteDataSourceImpl(FakeSyncPassApiService())
        )
        val vm = DocenteViewModel(repo)

        vm.onOpenCreateDialog()
        assertTrue(vm.uiState.value.showCreateDialog)

        vm.onNewNombreChange("Taller Adulto Mayor")
        vm.onNewLugarChange("Sede Sur")
        vm.onNewFechaChange("Viernes 11:00")
        vm.onNewCuposChange("35")
        vm.onCreateTaller()

        assertFalse(vm.uiState.value.showCreateDialog)
        assertTrue(vm.uiState.value.talleres.isNotEmpty())

        val first = vm.uiState.value.talleres.first()
        vm.onSelectTaller(first)
        vm.onExportActa(null)
        assertNotNull(vm.uiState.value.statusMessage)

        vm.onDeleteTaller(first.id)
        vm.onDismissCreateDialog()
    }

    @Test
    fun `BeneficiarioViewModel y AdminViewModel gestionan estados correctamente`() = runTest {
        val db = AppDatabase()
        val repo = SyncPassRepositoryImpl(
            SyncPassLocalDataSourceImpl(db.syncPassDao()),
            SyncPassRemoteDataSourceImpl(FakeSyncPassApiService())
        )
        val benVm = BeneficiarioViewModel(repo)
        benVm.onRatingChange(5)
        benVm.onUtilityChange(4)
        benVm.onCommentsChange("Muy claro el contenido")
        benVm.onToggleOffline(false)
        benVm.onSubmitSurvey()
        assertTrue(benVm.uiState.value.surveySent)
        benVm.onResetSurvey()
        assertFalse(benVm.uiState.value.surveySent)

        val adminVm = AdminViewModel(repo)
        adminVm.syncAdminData()
        assertTrue(adminVm.uiState.value.metrics.isNotEmpty())
        assertEquals(4, adminVm.uiState.value.usersList.size)
    }

    @Test
    fun `LoginViewModel, HomeViewModel, JwtDecoder, AuthRepository y Modulos DI funcionan correctamente`() = runTest {
        // JwtDecoder Demo & Real JWT
        val demoRes = JwtDecoder.decode(JwtDecoder.createDemoToken("coordinator"))
        assertTrue(demoRes.isSuccess)
        assertEquals("profesor.coordinador@duocuc.cl", demoRes.getOrNull()?.username)

        val payloadJson = """{"username":"test@duocuc.cl","roles":["admin"],"iss":"duoc","sub":"1","iat":100,"exp":200}"""
        val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.toByteArray())
        val realJwtRes = JwtDecoder.decode("header.$encodedPayload.sig")
        assertTrue(realJwtRes.isSuccess)
        assertEquals("test@duocuc.cl", realJwtRes.getOrNull()?.username)

        // HomeViewModel & Factory
        val homeVm = HomeViewModelFactory("DEMO_volunteer").create(HomeViewModel::class.java)
        assertEquals("voluntario.estudiante@duocuc.cl", homeVm.uiState.value.user?.username)

        // AuthRepository & LoginViewModel
        val fakeAuthApi = object : AuthApiService {
            override suspend fun login(body: LoginRequest): LoginResponse =
                LoginResponse(access_token = "DEMO_coordinator", user = UserDto(1, body.username, body.username))
        }
        val authRepo = AuthRepository(fakeAuthApi)
        val loginVm = LoginViewModel(authRepo)
        loginVm.onUsernameChange("")
        loginVm.onPasswordChange("123")
        loginVm.onLoginClick()
        assertNotNull(loginVm.uiState.value.usernameError)

        loginVm.onUsernameChange("profesor@duocuc.cl")
        loginVm.onPasswordChange("123456")
        loginVm.onTogglePasswordVisibility()
        loginVm.onLoginClick()
        assertTrue(loginVm.uiState.value.isSuccess)
        loginVm.onErrorShown()

        // Domain Models, UserRole, Configs & DI Modules
        val proyecto = ProyectoComunitario("P1", "Titulo", "Desc", "Tech", "UV1", 50, "activo")
        assertEquals("P1", proyecto.id)
        val entity = EncuestaSatisfaccion("E1", "T1", "BEN-1", 5, 4, "Ok").toEntity()
        assertEquals("E1", entity.toDomain().id)
        assertEquals(UserRole.DOCENTE, UserRole.fromSlug("coordinator"))
        assertEquals(UserRole.VOLUNTARIO, UserRole.fromSlug("volunteer"))
        assertEquals(UserRole.BENEFICIARIO, UserRole.fromSlug("attendee"))
        assertEquals(UserRole.ADMIN, UserRole.fromSlug("admin"))
        assertTrue(NtfyTopics.generate("docente@duocuc.cl").startsWith("app_vibe_"))
        assertNotNull(AppConfig.apiBaseUrl)
        assertNotNull(CognitoAppConfig.config)
        assertTrue(CognitoAppConfig.isConfigured)
        assertNotNull(DatabaseModule.provideLocalDataSource())
        assertNotNull(NetworkModule.provideRemoteDataSource())
        assertNotNull(RepositoryModule.provideSyncPassRepository())
    }
}
