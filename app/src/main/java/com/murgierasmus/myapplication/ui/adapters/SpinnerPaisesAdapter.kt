package com.murgierasmus.myapplication.ui.adapters
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.dataclass.Paises


class SpinnerPaisesAdapter(var paises: List<Paises>, var context: Context) : BaseAdapter() {


    override fun getCount(): Int {
        return paises.size
    }

    override fun getItem(position: Int): Any {
        return paises[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var vista = convertView
        var inflate: LayoutInflater = LayoutInflater.from(context)
        vista = inflate.inflate(R.layout.item_spinner, null)

        var image = vista.findViewById<ImageView>(R.id.imgPais)
        var pais = vista.findViewById<TextView>(R.id.nombrePais)

        pais.text = paises.get(position).nombre.toString()

        Glide.with(context)
            .load(paises.get(position).foto)
            .centerCrop()
            .into(image)

        return vista
    }
}
