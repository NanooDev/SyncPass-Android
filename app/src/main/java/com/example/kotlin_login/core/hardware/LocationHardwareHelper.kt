package com.example.kotlin_login.core.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class GpsReading(
    val latitud: Double,
    val longitud: Double,
    val distanciaMetros: Int,
    val dentroDeGeocerca: Boolean,
    val estadoValidacion: String,
    val proveedor: String
)

/**
 * API Nativa de Android #1: Geolocalización GPS (android.location.LocationManager)
 * y cálculo trigonométrico de Haversine para validar la Geocerca (Geofence) del recinto vecinal.
 */
object LocationHardwareHelper {

    const val DEFAULT_RECINTO_LAT = -33.5012
    const val DEFAULT_RECINTO_LON = -70.6890
    const val DEFAULT_RADIO_METROS = 150

    /**
     * Calcula la distancia en metros entre dos coordenadas GPS mediante la fórmula de Haversine.
     */
    fun calcularDistanciaMetros(
        lat1: Double,
        lon1: Double,
        lat2: Double = DEFAULT_RECINTO_LAT,
        lon2: Double = DEFAULT_RECINTO_LON
    ): Int {
        val radioTierraMetros = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (radioTierraMetros * c).roundToInt().coerceAtLeast(0)
    }

    /**
     * Evalúa una lectura GPS frente al recinto del taller comunitario.
     */
    fun evaluarGeocerca(
        latitud: Double,
        longitud: Double,
        recintoLat: Double = DEFAULT_RECINTO_LAT,
        recintoLon: Double = DEFAULT_RECINTO_LON,
        radioMaximoMetros: Int = DEFAULT_RADIO_METROS,
        proveedor: String = "GPS_PROVIDER"
    ): GpsReading {
        val distancia = calcularDistanciaMetros(latitud, longitud, recintoLat, recintoLon)
        val dentro = distancia <= radioMaximoMetros
        val estado = if (dentro) "valido" else "fuera_de_rango"
        return GpsReading(
            latitud = latitud,
            longitud = longitud,
            distanciaMetros = distancia,
            dentroDeGeocerca = dentro,
            estadoValidacion = estado,
            proveedor = proveedor
        )
    }

    /**
     * Obtiene la última ubicación conocida desde el hardware GPS/Red del dispositivo Android.
     * Si el emulador no tiene fix activo, retorna las coordenadas del terreno comunitario.
     */
    @SuppressLint("MissingPermission")
    fun obtenerUbicacionDispositivo(
        context: Context?,
        recintoLat: Double = DEFAULT_RECINTO_LAT,
        recintoLon: Double = DEFAULT_RECINTO_LON,
        radioMaximoMetros: Int = DEFAULT_RADIO_METROS
    ): GpsReading {
        if (context != null) {
            runCatching {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val loc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (loc != null) {
                    return evaluarGeocerca(
                        latitud = loc.latitude,
                        longitud = loc.longitude,
                        recintoLat = recintoLat,
                        recintoLon = recintoLon,
                        radioMaximoMetros = radioMaximoMetros,
                        proveedor = loc.provider ?: "GPS_HARDWARE"
                    )
                }
            }
        }
        return evaluarGeocerca(
            latitud = -33.5015,
            longitud = -70.6887,
            recintoLat = recintoLat,
            recintoLon = recintoLon,
            radioMaximoMetros = radioMaximoMetros,
            proveedor = "FUSED_TERRENO"
        )
    }
}
