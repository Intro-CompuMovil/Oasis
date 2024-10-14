package com.example.oasis.logica.comprador

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.location.Geocoder
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.model.Ubicacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeleccionarDireccion : DialogFragment() {

    interface SeleccionarDireccionListener {
        fun onDireccionSeleccionada(ubicacion: Ubicacion)
    }

    private lateinit var listener: SeleccionarDireccionListener
    private lateinit var geoCoder: Geocoder

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SeleccionarDireccionListener
        geoCoder = Geocoder(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.activity_seleccionar_direccion, null)

        val etNuevaDireccion = view.findViewById<EditText>(R.id.etNuevaDireccion)
        val btnAgregarDireccion = view.findViewById<Button>(R.id.btnGuardarDireccion)
        val lvDirecciones = view.findViewById<ListView>(R.id.lvDirecciones)

        val ubicaciones = CompradorInicio.comprador.getDirecciones()
        val direcciones = ubicaciones.map { it.getDireccion() }
        lvDirecciones.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, direcciones)
        lvDirecciones.setOnItemClickListener { _, _, position, _ ->
            listener.onDireccionSeleccionada(ubicaciones[position])
            dismiss()
        }

        btnAgregarDireccion.setOnClickListener {
            val nuevaDireccion = etNuevaDireccion.text.toString()
            if (nuevaDireccion.isNotEmpty()){
                val adressList = geoCoder.getFromLocationName(nuevaDireccion, 1)
                agregarDireccion(nuevaDireccion)
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun agregarDireccion(direccion: String){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val adressList = geoCoder.getFromLocationName(direccion, 1)
                if (!adressList.isNullOrEmpty()){
                    val address = adressList[0]
                    withContext(Dispatchers.Main){
                        val ubicacion = Ubicacion(address.latitude, address.longitude, direccion)
                        CompradorInicio.agregarDireccion(ubicacion, DataBaseSimulator(requireContext()))
                        listener.onDireccionSeleccionada(ubicacion)
                    }
                }
                else{
                    withContext(Dispatchers.Main){
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setTitle("Error")
                        builder.setMessage("No se pudo encontrar la dirección")
                        builder.setPositiveButton("Aceptar", null)
                        builder.show()
                    }
                }
                dismiss()
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}