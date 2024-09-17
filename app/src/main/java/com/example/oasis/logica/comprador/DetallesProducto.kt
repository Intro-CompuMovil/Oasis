package com.example.oasis.logica.comprador

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Order
import com.example.oasis.model.Product

class DetallesProducto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_producto)
        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Detalles del producto")

        val producto = intent.getSerializableExtra("product") as Product
        initUI(producto)
        initButtons(producto)
    }

    private fun initUI(product: Product){
        val productName = findViewById<TextView>(R.id.tvDetallesProductoNombreProducto)
        val productDescription = findViewById<TextView>(R.id.tvDetallesProductoDescripcion)
        val productPrice = findViewById<TextView>(R.id.tvDetallesProductoPrecio)
        val productCalificacion = findViewById<TextView>(R.id.tvDetallesProductoCalificacion)

        productName.text = product.getNombre()
        productDescription.text = product.getDescripcion()
        productPrice.text = product.getPrecio().toString()
        productCalificacion.text = product.getPuntaje().toString()
    }

    private fun initButtons(product: Product){
        val productCantidad = findViewById<TextView>(R.id.tvDetallesProductoCantidad)
        var cantidad: Int
        val btnDecrementar = findViewById<ImageButton>(R.id.btnDetallesProductoDecrementar)
        btnDecrementar.setOnClickListener {
            cantidad = productCantidad.text.toString().toInt()
            if(cantidad > 1){
                cantidad--
                productCantidad.text = cantidad.toString()
            }
        }
        val btnIncrementar = findViewById<ImageButton>(R.id.btnDetallesProductoIncrementar)
        btnIncrementar.setOnClickListener {
            cantidad = productCantidad.text.toString().toInt()
            cantidad++
            productCantidad.text = cantidad.toString()
        }
        val btnAddToCart = findViewById<ImageButton>(R.id.btnDetallesProductoAgregarCarrito)
        btnAddToCart.setOnClickListener {
            if (MainActivity.addProductToCarrito(Order(product, productCantidad.text.toString().toInt()))) {
                Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Producto ya está en el carrito, cantidad actualizada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}