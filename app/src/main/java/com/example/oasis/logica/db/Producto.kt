package com.example.oasis.logica.db

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class Producto {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var idProducto: Int = 0
    var nombre: String? = null
    var descripcion: String? = null
    var puntaje: Double = 0.0
    var precio: Double = 0.0
    var categoria: String? = null
    var imagen: String? = null
    var productoLatitud: Double = 0.0
    var productoLongitud: Double = 0.0

    // Constructor vacío para Firestore
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

    // Método para agregar un solo producto a Firestore
    fun agregarProducto(producto: Producto) {
        db.collection("productos") // La colección donde guardamos los productos
            .document(producto.idProducto.toString()) // Usamos el ID del producto como ID del documento
            .set(producto) // Subimos el objeto Producto
            .addOnSuccessListener {
                Log.d("Firestore", "Producto agregado correctamente: ${producto.nombre}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al agregar producto: ${producto.nombre}", e)
            }
    }

    // Método para agregar varios productos a Firestore
    fun agregarProductos() {
        // Creamos una lista de productos
        val productos = listOf(
            Producto(1, "Laptop", "Laptop HP", 4.7, 1000.0, "Tecnología", "https://m.media-amazon.com/images/I/91UaM92H1dL._AC_SX450_.jpg", 4.642095, -74.113714),
            Producto(2, "Smartphone", "Smartphone Samsung", 4.5, 500.0, "Tecnología", "https://m.media-amazon.com/images/I/51PtFHUPjBL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.647011, -74.115402),
            Producto(3, "Tablet", "Tablet Lenovo", 4.3, 300.0, "Tecnología", "https://m.media-amazon.com/images/I/61PnHlc0HCL.__AC_SY445_SX342_QL70_FMwebp_.jpg", 4.668909, -74.052497),
            Producto(4, "Smartwatch", "Smartwatch Xiaomi", 4.2, 200.0, "Tecnología", "https://m.media-amazon.com/images/I/711f6KLsMaL._AC_SX679_.jpg", 4.658827, -74.057860),
            Producto(5, "Audífonos", "Audífonos Sony", 4.1, 100.0, "Tecnología", "https://m.media-amazon.com/images/I/41+1Csr1pSL._AC_SY300_SX300_.jpg", 4.715869, -74.029102),
            Producto(6, "Sofá", "Sofá de 3 plazas", 4.8, 800.0, "Hogar", "https://m.media-amazon.com/images/I/61-YZAc9zeL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.701952, -74.041364),
            Producto(7, "Mesa", "Mesa de comedor", 4.6, 400.0, "Hogar", "https://m.media-amazon.com/images/I/51UMEm+pkcL._AC_SX679_.jpg", 4.712835, -74.071793),
            Producto(8, "Silla", "Silla de oficina", 4.4, 200.0, "Hogar", "https://m.media-amazon.com/images/I/71i08qnZeDL._AC_SX679_.jpg", 4.719835, -74.071793),
            Producto(9, "Cama", "Cama matrimonial", 4.3, 600.0, "Hogar", "https://m.media-amazon.com/images/I/81lTUWPc4oL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.733150, -74.066686),
            Producto(10, "Escritorio", "Escritorio de madera", 4.2, 300.0, "Hogar", "https://m.media-amazon.com/images/I/81ioD6WoCeL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.739150, -74.066686),
            Producto(11, "Camisa", "Camisa de vestir", 4.7, 50.0, "Moda", "https://m.media-amazon.com/images/I/51rkKPruYvL._AC_SX569_.jpg", 4.695037, -74.086947),
            Producto(12, "Pantalón", "Pantalón de mezclilla", 4.5, 40.0, "Moda", "https://m.media-amazon.com/images/I/71u-r5z+keL._AC_SX679_.jpg", 4.701037, -74.086947),
            Producto(13, "Zapatos", "Zapatos de piel", 4.3, 60.0, "Moda", "https://m.media-amazon.com/images/I/71k2ZobLduL._AC_SY695_.jpg", 4.680794, -74.081179),
            Producto(14, "Chamarra", "Chamarra de cuero", 4.2, 70.0, "Moda", "https://m.media-amazon.com/images/I/71zwwEe2nLL._AC_SX679_.jpg", 4.689794, -74.081179),
            Producto(15, "Reloj", "Reloj de pulsera", 4.1, 30.0, "Moda", "https://m.media-amazon.com/images/I/61TLVPYrx5L._AC_SY695_.jpg", 4.665767, -74.057975),
            Producto(16, "Balón", "Balón de fútbol", 4.8, 20.0, "Deportes", "https://m.media-amazon.com/images/I/71RyK2qVMWL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.672767, -74.057975),
            Producto(17, "Raqueta", "Raqueta de tenis", 4.6, 30.0, "Deportes", "https://m.media-amazon.com/images/I/71NjVS5BeEL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.600115, -74.078211),
            Producto(18, "Tenis", "Tenis para correr", 4.4, 40.0, "Deportes", "https://m.media-amazon.com/images/I/61J+fImqBTL._AC_SY695_.jpg", 4.613930, -74.095476),
            Producto(19, "Bicicleta", "Bicicleta de montaña", 4.3, 200.0, "Deportes", "https://m.media-amazon.com/images/I/71X+l+AirLL._AC_SX679_.jpg", 4.619930, -74.095476),
            Producto(20, "Pesas", "Pesas de 5 kg", 4.2, 50.0, "Deportes", "https://m.media-amazon.com/images/I/61jau6tusOL.__AC_SX300_SY300_QL70_FMwebp_.jpg", 4.702683, -74.041120)
        )

        // Agregar todos los productos a Firestore
        for (producto in productos) {
            agregarProducto(producto)
        }
    }
}
