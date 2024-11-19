import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Producto {
    var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    var idProducto: Int = 0
    var nombre: String? = null
    var descripcion: String? = null
    var puntaje: Double = 0.0
    var precio: Double = 0.0
    var categoria: String? = null
    var imagen: String? = null
    var productoLatitud: Double = 0.0
    var productoLongitud: Double = 0.0

    // Constructor vacío para Firebase
    constructor()

    constructor(
        idProducto: Int,
        nombre: String?,
        descripcion: String?,
        puntaje: Double,
        precio: Double,
        categoria: String?,
        imagen: String?,
        productoLatitud: Double,
        productoLongitud: Double
    ) {
        this.idProducto = idProducto
        this.nombre = nombre
        this.descripcion = descripcion
        this.puntaje = puntaje
        this.precio = precio
        this.categoria = categoria
        this.imagen = imagen
        this.productoLatitud = productoLatitud
        this.productoLongitud = productoLongitud
    }

    // Método para leer los productos desde Firebase Realtime Database
    fun obtenerProductosDesdeFirebase(completion: (List<Producto>) -> Unit) {
        val ref: DatabaseReference = db.getReference("productos") // Referencia a la colección "productos"

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productos = mutableListOf<Producto>()

                // Recorrer todos los productos en la base de datos
                for (productoSnapshot in snapshot.children) {
                    val producto = productoSnapshot.getValue(Producto::class.java)
                    if (producto != null) {
                        productos.add(producto)
                    }
                }
                // Llamamos al completion para pasar la lista de productos a la UI o donde lo necesites
                completion(productos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Error al obtener productos: $error")
            }
        })
    }
}
