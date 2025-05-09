package com.example.mapsapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mapsapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
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

    suspend fun insertMarker(name: String, coordenades: String, imageName1: String){
        client.from("Student").insert(name, coordenades)
    }

    suspend fun updateMarker(id: String, name: String, coordenades: Double, imageName: String, imageFile: ByteArray) {
        val imageName = storage.from("images").update(path = imageName, data = imageFile)
        client.from("Student").update({
            set("name", name)
            set("marker", coordenades)
            set("image", buildImageUrl(imageFileName = imageName.path))
        }) {
            filter {
                eq("id", id)
            }
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