package com.example.oasis.logica.comprador

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.oasis.MainActivity
import com.example.oasis.R

class SeleccionarDireccion : DialogFragment() {

    interface SeleccionarDireccionListener {
        fun onDireccionSeleccionada(direccion: String)
    }

    private lateinit var listener: SeleccionarDireccionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SeleccionarDireccionListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.activity_seleccionar_direccion, null)

        val etNuevaDireccion = view.findViewById<EditText>(R.id.etNuevaDireccion)
        val btnAgregarDireccion = view.findViewById<Button>(R.id.btnGuardarDireccion)
        val lvDirecciones = view.findViewById<ListView>(R.id.lvDirecciones)

        lvDirecciones.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, MainActivity.direccionesList)
        lvDirecciones.setOnItemClickListener { parent, _, position, _ ->
            val direccion = parent.getItemAtPosition(position) as String
            listener.onDireccionSeleccionada(direccion)
            dismiss()
        }

        btnAgregarDireccion.setOnClickListener {
            val nuevaDireccion = etNuevaDireccion.text.toString()
            if (nuevaDireccion.isNotEmpty()){
                MainActivity.direccionesList.add(nuevaDireccion)
                listener.onDireccionSeleccionada(nuevaDireccion)
                dismiss()
            }
        }

        builder.setView(view)
        return builder.create()
    }
}