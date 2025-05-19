package com.example.mapsapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.mapsapp.BuildConfig
import com.example.mapsapp.utils.AuthState
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MySupabaseClient {
    lateinit var client: SupabaseClient
    lateinit var storage: Storage

    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    constructor() {
        client = createSupabaseClient(supabaseUrl = supabaseUrl, supabaseKey = supabaseKey) {
            install(Postgrest)
            install(Storage)
            install(Auth){
                autoLoadFromStorage = true
            }
        }
        storage = client.storage
    }


    suspend fun getAllMarkers(): List<Marker> {
        return client.from("Student").select().decodeList<Marker>()
    }

    suspend fun getMarker(id: String): Marker{
        return client.from("Student").select {
            filter {
                eq("id", id)
            }
        }.decodeSingle<Marker>()
    }

    suspend fun insertMarker(newMarker: Marker) {
        client.from("Student").insert(newMarker)
    }

    suspend fun signUpWithEmail(emailValue: String, passwordValue: String): AuthState {
        try {
            client.auth.signUpWith(Email) {
                email = emailValue
                password = passwordValue
            }
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage!!)
        }
    }

    suspend fun signInWithEmail(emailValue: String, passwordValue: String): AuthState {
        try {
            client.auth.signInWith(Email) {
                email = emailValue
                password = passwordValue
            }
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage!!)
        }
    }

    fun retrieveCurrentSession(): UserSession?{
        val session = client.auth.currentSessionOrNull()
        return session
    }

    fun refreshSession(): AuthState {
        try {
            client.auth.currentSessionOrNull()
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage!!)
        }
    }


    /*suspend fun updateMarker0(id: String, name: String, coordenades: String, imageName: String, imageFile: ByteArray) {
        val imageName = storage.from("images").update(path = imageName, data = imageFile)
        client.from("Student").update({
            set("name", name)
            set("coordenades", coordenades)
            set("image", buildImageUrl(imageFileName = imageName.path))
        }) {
            filter {
                eq("id", id)
            }
        }
    }*/

    // Eh actualizado el registro Marker con los campos, y sube imagen si imageFile no vacío
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateMarker(id: String, name: String, coordenades: String, imageName: String, imageFile: ByteArray) {
        var finalImageUrl = imageName

        if (imageFile.isNotEmpty()) {
            try {
                // Eliminar imagen anterior
                deleteImage(imageName)

                // Subir nueva imagen y obtener su URL
                finalImageUrl = uploadImage(imageFile)
            } catch (e: Exception) {
                throw Exception("Error actualizando imagen: ${e.message}", e)
            }
        }

        try {
            val updateMap = mapOf(
                "name" to name,
                "coordenades" to coordenades,
                "image" to finalImageUrl
            )

            client.from("Student").update(updateMap) {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            throw Exception("Error actualizando Student: ${e.message}", e)
        }
    }


    suspend fun getBitmapDescriptorFromUrl(context: Context, imageUrl: String): BitmapDescriptor? {
        return try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val originalBitmap = (result as BitmapDrawable).bitmap

            // Escalar el bitmap a un tamaño adecuado para el mapa
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 96, 96, true)

            BitmapDescriptorFactory.fromBitmap(scaledBitmap)
        } catch (e: Exception) {
            null
        }
    }



    suspend fun deleteMarker(id: String){
        client.from("Student").delete{ filter { eq("id", id) } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadImage(imageFile: ByteArray): String {
        val fechaHoraActual = LocalDateTime.now()
        val formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val imageName = storage.from("images").upload(path = "image_${fechaHoraActual.format(formato)}.png", data = imageFile)
        return buildImageUrl(imageFileName = imageName.path)
    }

    fun buildImageUrl(imageFileName: String) = "${this.supabaseUrl}/storage/v1/object/public/images/${imageFileName}"

    suspend fun deleteImage(imageName: String){
        val imgName = imageName.removePrefix("https://aobflzinjcljzqpxpcxs.supabase.co/storage/v1/object/public/images/")
        client.storage.from("images").delete(imgName)
    }

}