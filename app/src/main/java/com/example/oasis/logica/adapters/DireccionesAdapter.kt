import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.oasis.R
import com.example.oasis.model.Ubicacion

class DireccionesAdapter(context: Context, private val direcciones: List<Ubicacion>) :
    ArrayAdapter<Ubicacion>(context, 0, direcciones) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_direccion, parent, false)
        val direccion = getItem(position)
        val textView = view.findViewById<TextView>(R.id.tvDireccion)
        if (direccion != null) {
            textView.text = direccion.getDireccion()
        }
        return view
    }
}