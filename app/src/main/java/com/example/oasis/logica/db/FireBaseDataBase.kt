package com.example.oasis.logica.db

import android.util.Log
import com.example.oasis.datos.Data
import com.example.oasis.model.Comprador
import com.example.oasis.model.Repartidor
import com.example.oasis.model.Solicitud
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FireBaseDataBase {
    companion object{
        val auth = FirebaseAuth.getInstance()
        val storage = FirebaseStorage.getInstance()
        val database = FirebaseDatabase.getInstance()
    }

    fun getCurrentComprador() = auth.currentUser

    fun logout(){
        auth.signOut()
    }

    suspend fun createSolicitud(solicitud: Solicitud): Boolean {
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_SOLICITUDES)
            val key = ref.push().key ?: throw Exception("Failed to generate key")
            solicitud.setId(key)
            ref.child(key).setValue(solicitud).await()
            Log.d("FireBaseDataBase", "Solicitud creada exitosamente")
            true
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al crear la solicitud: $exception")
            false
        }
    }

    suspend fun getSolicitudesByUserID(userID: String): MutableList<Solicitud> {
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_SOLICITUDES)
            val snapshot = ref.get().await()
            val solicitudes = mutableListOf<Solicitud>()
            snapshot.children.forEach {
                val solicitud = it.getValue(Solicitud::class.java)
                if (solicitud?.getComprador()?.getId() == userID ||
                    solicitud?.getRepartidor()?.getId() == userID)
                {
                    solicitudes.add(solicitud)
                }
            }
            solicitudes
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al obtener las solicitudes: $exception")
            mutableListOf()
        }
    }

    suspend fun getSolicitudesActivas(): MutableList<Solicitud> {
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_SOLICITUDES)
            val snapshot = ref.get().await()
            val solicitudes = mutableListOf<Solicitud>()
            snapshot.children.forEach {
                val solicitud = it.getValue(Solicitud::class.java)
                if (solicitud?.getEstado() == "No entregado" && solicitud.getRepartidor() == null)
                {
                    solicitudes.add(solicitud)
                }
            }
            solicitudes
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al obtener las solicitudes activas: $exception")
            mutableListOf()
        }
    }

    suspend fun updateSolicitud(solicitud: Solicitud): Boolean {
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_SOLICITUDES)
            ref.child(solicitud.getId()).setValue(solicitud).await()
            Log.d("FireBaseDataBase", "Solicitud actualizada exitosamente")
            true
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al actualizar la solicitud: $exception")
            false
        }
    }

    suspend fun registerUser(email: String, contrasena: String){
        try {
            auth.createUserWithEmailAndPassword(email, contrasena).await()
            Log.d("FireBaseDataBase", "Usuario creado exitosamente")
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al crear el usuario: $exception")
        }
    }

    suspend fun loginUser(email: String, contrasena: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, contrasena).await()
            Log.d("FireBaseDataBase", "Usuario logueado exitosamente")
            true
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al loguear el usuario: $exception")
            false
        }
    }

    suspend fun createComprador(comprador: Comprador){
        try {
            val ref = database.getReference(Data.PATH_DATABASE_COMPRADORES)
            val key = auth.currentUser?.uid ?: throw Exception("Failed to get user id")
            comprador.setId(key)
            ref.child(key).setValue(comprador).await()
            Log.d("FireBaseDataBase", "Comprador creado exitosamente")
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al crear el comprador: $exception")
        }
    }

    suspend fun getComprador(): Comprador? {
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_COMPRADORES)
            val snapshot = ref.child(auth.currentUser?.uid ?: throw Exception("Failed to get user id")).get().await()
            snapshot.getValue(Comprador::class.java)
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al obtener el comprador: $exception")
            null
        }
    }

    suspend fun updateComprador(comprador: Comprador): Boolean{
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_COMPRADORES)
            ref.child(auth.currentUser?.uid ?: throw Exception("Failed to get user id")).setValue(comprador).await()
            Log.d("FireBaseDataBase", "Comprador actualizado exitosamente")
            true
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al actualizar el comprador: $exception")
            false
        }
    }

    suspend fun createRepartidor(repartidor: Repartidor){
        try {
            val ref = database.getReference(Data.PATH_DATABASE_REPARTIDORES)
            val key = auth.currentUser?.uid ?: throw Exception("Failed to get user id")
            repartidor.setId(key)
            ref.child(key).setValue(repartidor).await()
            Log.d("FireBaseDataBase", "Repartidor creado exitosamente")
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al crear el repartidor: $exception")
        }
    }

    suspend fun getRepartidor(): Repartidor? {
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_REPARTIDORES)
            val snapshot = ref.child(auth.currentUser?.uid ?: throw Exception("Failed to get user id")).get().await()
            snapshot.getValue(Repartidor::class.java)
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al obtener el repartidor: $exception")
            null
        }
    }

    suspend fun updateRepartidor(repartidor: Repartidor): Boolean{
        return try {
            val ref = database.getReference(Data.PATH_DATABASE_REPARTIDORES)
            ref.child(auth.currentUser?.uid ?: throw Exception("Failed to get user id")).setValue(repartidor).await()
            Log.d("FireBaseDataBase", "Repartidor actualizado exitosamente")
            true
        } catch (exception: Exception) {
            Log.e("FireBaseDataBase", "Error al actualizar el repartidor: $exception")
            false
        }
    }
}