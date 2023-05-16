package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.murgierasmus.myapplication.databinding.FragmentTransporteBinding
import com.murgierasmus.myapplication.ui.activities.CiudadActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentTransporteBinding? = null
private val binding get() = bindingA!!

@SuppressLint("StaticFieldLeak")
private lateinit var btnRuta: CardView
@SuppressLint("StaticFieldLeak")
private lateinit var btnAreopuerto: CardView

class TransporteFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingA = FragmentTransporteBinding.inflate(inflater, container, false)

        btnRuta = binding.cardMapa
        btnAreopuerto = binding.cardAreopuerto
        listeners()
        return binding.root
    }

    private fun listeners() {
        val actividad = requireActivity() as CiudadActivity
        btnRuta.setOnClickListener{
            actividad.enviarMapa()
        }
        btnAreopuerto.setOnClickListener{
            actividad.enviarAeropuerto()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransporteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}