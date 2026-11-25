
package cl.tacticaldev.visitas

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.tacticaldev.visitas.ui.EquiposScreen
import cl.tacticaldev.visitas.ui.HomeScreen
import cl.tacticaldev.visitas.ui.SplashScreen
import cl.tacticaldev.visitas.ui.historial.DetalleVisitaScreen
import cl.tacticaldev.visitas.ui.historial.HistorialScreen
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val HISTORIAL = "historial"
    const val DETALLE = "detalle/{visitaId}"
    const val NUEVA_VISITA = "nueva_visita"
    const val EQUIPOS = "equipos"
    const val GESTION_USUARIOS = "gestion_usuarios"

    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNavigation(
    equipoViewModel: EquipoViewModel,
    historialViewModel: HistorialViewModel
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val todas by historialViewModel.todas.collectAsState()
    val equipos by equipoViewModel.equipos.collectAsState()
    val errorEquipos by equipoViewModel.error.collectAsState()
    val errorHistorial by historialViewModel.error.collectAsState()

    var usuarioLogueado by remember {
        mutableStateOf<Usuario?>(null)
    }

    // ERRORES DE EQUIPOS
    LaunchedEffect(errorEquipos) {
        errorEquipos?.let { mensaje ->
            Toast.makeText(
                context,
                mensaje,
                Toast.LENGTH_SHORT
            ).show()

            equipoViewModel.limpiarError()
        }
    }

    // ERRORES DE HISTORIAL
    LaunchedEffect(errorHistorial) {
        errorHistorial?.let { mensaje ->
            Toast.makeText(
                context,
                mensaje,
                Toast.LENGTH_SHORT
            ).show()

            historialViewModel.limpiarError()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // INICIO DE SESIÓN
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    usuarioLogueado = usuario

                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // PANTALLA PRINCIPAL
        composable(Rutas.HOME) {

            val pendientesCount = todas.count {
                !it.visita.sincronizada
            }

            val equiposFallaCount = equipos.count {
                it.estado.equals("Con falla", ignoreCase = true)
            }

            HomeScreen(
                usuario = usuarioLogueado,
                visitasPendientesCount = pendientesCount,
                equiposConFallaCount = equiposFallaCount,

                onRegistrarVisita = {
                    navController.navigate(Rutas.NUEVA_VISITA)
                },

                // SOLO ADMINISTRADOR PUEDE ENTRAR A EQUIPOS
                onVerEquipos = {
                    if (usuarioLogueado?.cargo == "Administrador") {
                        navController.navigate(Rutas.EQUIPOS)
                    } else {
                        Toast.makeText(
                            context,
                            "No tienes permisos para gestionar equipos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                onVerHistorial = {
                    navController.navigate(Rutas.HISTORIAL)
                },

                // SOLO ADMINISTRADOR PUEDE GESTIONAR USUARIOS
                onGestionarUsuarios = {
                    if (usuarioLogueado?.cargo == "Administrador") {
                        navController.navigate(Rutas.GESTION_USUARIOS)
                    } else {
                        Toast.makeText(
                            context,
                            "No tienes permisos para gestionar usuarios",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                onCerrarSesion = {
                    usuarioLogueado = null

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // GESTIÓN DE USUARIOS: SOLO ADMINISTRADOR
        composable(Rutas.GESTION_USUARIOS) {
            if (usuarioLogueado?.cargo == "Administrador") {
                GestionUsuariosScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    Toast.makeText(
                        context,
                        "Acceso restringido al administrador",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.popBackStack()
                }
            }
        }

        // PANTALLA DE BIENVENIDA
        composable(Rutas.SPLASH) {
            SplashScreen(
                onTerminar = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // NUEVA VISITA
        composable(Rutas.NUEVA_VISITA) {

            var fotoUri by remember {
                mutableStateOf<Uri?>(null)
            }

            var latitud by remember {
                mutableStateOf<Double?>(null)
            }

            var longitud by remember {
                mutableStateOf<Double?>(null)
            }

            NuevaVisitaScreen(
                equipos = equipos,

                onFotoCapturada = {
                    fotoUri = it
                },

                onFotoEliminada = {
                    fotoUri = null
                },

                onUbicacionObtenida = { lat, lon ->
                    latitud = lat
                    longitud = lon
                },

                onUbicacionEliminada = {
                    latitud = null
                    longitud = null
                },

                onGuardar = {
                        equipo,
                        estado,
                        procedencia,
                        telefono,
                        cargo,
                        rut,
                        observaciones ->

                    val rutaFoto = fotoUri?.toString()
                    val lat = latitud
                    val lon = longitud

                    // DATOS DEL USUARIO QUE INICIÓ SESIÓN
                    val nombreUsuario = usuarioLogueado?.nombre
                        ?.takeIf { it.isNotBlank() }
                        ?: "Usuario desconocido"

                    val identificadorUsuario = usuarioLogueado?.rut
                        ?.takeIf { it.isNotBlank() }
                        ?: ""

                    // CREAR VISITA CON EL USUARIO REGISTRADOR
                    historialViewModel.crearVisita(
                        equipoNombre = equipo,
                        cargo = cargo,
                        rut = rut,
                        observaciones = observaciones,
                        rutaFoto = rutaFoto,
                        latitud = lat,
                        longitud = lon,
                        usuarioRegistro = nombreUsuario,
                        usuarioRegistroId = identificadorUsuario
                    )

                    Toast.makeText(
                        context,
                        "Visita guardada",
                        Toast.LENGTH_SHORT
                    ).show()

                    scope.launch {
                        delay(1500)
                        navController.popBackStack()
                    }
                }
            )
        }

        // EQUIPOS: ACCESO EXCLUSIVO DEL ADMINISTRADOR
        composable(Rutas.EQUIPOS) {
            if (usuarioLogueado?.cargo == "Administrador") {

                EquiposScreen(
                    viewModel = equipoViewModel,
                    onVolver = {
                        navController.popBackStack()
                    }
                )

            } else {

                LaunchedEffect(Unit) {
                    Toast.makeText(
                        context,
                        "No tienes permisos para gestionar equipos",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.popBackStack()
                }
            }
        }

        // HISTORIAL
        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                viewModel = historialViewModel,

                onVolver = {
                    navController.popBackStack()
                },

                onNuevaVisita = {
                    navController.navigate(Rutas.NUEVA_VISITA)
                },

                // PROTECCIÓN TAMBIÉN DESDE EL HISTORIAL
                onVerEquipos = {
                    if (usuarioLogueado?.cargo == "Administrador") {
                        navController.navigate(Rutas.EQUIPOS)
                    } else {
                        Toast.makeText(
                            context,
                            "No tienes permisos para gestionar equipos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                onVisitaClick = { item ->
                    navController.navigate(
                        Rutas.detalle(item.visita.id)
                    )
                }
            )
        }

        // DETALLE DE VISITA
        composable(Rutas.DETALLE) { entrada ->

            val id = entrada.arguments
                ?.getString("visitaId")
                ?.toIntOrNull()

            val item = todas.firstOrNull {
                it.visita.id == id
            }

            val temperatura by historialViewModel.temperatura.collectAsState()

            if (item != null) {
                DetalleVisitaScreen(
                    item = item,
                    temperatura = temperatura,

                    onCargarTemperatura = { lat, lon ->
                        historialViewModel.cargarTemperatura(lat, lon)
                    },

                    onVolver = {
                        navController.popBackStack()
                    },

                    onCompletar = {
                        historialViewModel.marcarCompletada(item.visita)
                        navController.popBackStack()
                    },

                    onEliminar = {
                        historialViewModel.eliminar(item.visita.id)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
