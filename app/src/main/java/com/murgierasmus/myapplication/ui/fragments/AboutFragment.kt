package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentAboutBinding
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.dialogs.Dialogs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentAboutBinding? = null
private val binding get() = bindingA!!


class AboutFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        bindingA = FragmentAboutBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.profilefragment_perfil))
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.aboutfragment_about))
        var dialogs = Dialogs(requireContext())
        binding.cardJuan.setOnClickListener {


            dialogs.dialogDesarrolladorJuan()
        }
        binding.cardDani.setOnClickListener {

            dialogs.dialogDesarrolladorDani()
        }

        binding.cardInstagram.setOnClickListener{

            val url = "https://www.instagram.com/eoielejido/?igshid=YmMyMTA2M2Y%3D"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireContext().startActivity(intent)

        }
        binding.cardInstagram2.setOnClickListener{

            val url = "https://www.instagram.com/sepie_gob/?igshid=YmMyMTA2M2Y%3D"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireContext().startActivity(intent)

        }

        binding.cardInstagram3.setOnClickListener{

            val url = "https://www.instagram.com/pluri_almeria/?igshid=YmMyMTA2M2Y%3D"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireContext().startActivity(intent)

        }

        binding.cardTwitter.setOnClickListener{

            val url = "https://twitter.com/pluri_almeria?lang=es"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireContext().startActivity(intent)

        }
        binding.cardPluri.setOnClickListener{

            val url = "https://blogsaverroes.juntadeandalucia.es/plurilinguismoalmeria/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            requireContext().startActivity(intent)

        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}