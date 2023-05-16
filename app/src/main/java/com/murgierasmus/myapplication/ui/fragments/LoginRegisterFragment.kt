package com.murgierasmus.myapplication.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import com.murgierasmus.myapplication.R
import com.murgierasmus.myapplication.databinding.FragmentInitialLoginRegisterBinding
import com.murgierasmus.myapplication.ui.activities.MainActivity
import com.murgierasmus.myapplication.utils.methods.Methods


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@SuppressLint("StaticFieldLeak")
private lateinit var btnLogin: Button


@SuppressLint("StaticFieldLeak")
private lateinit var btnRegister: Button

@SuppressLint("StaticFieldLeak")
private var bindingA: FragmentInitialLoginRegisterBinding? = null
private val binding get() = bindingA!!

@SuppressLint("StaticFieldLeak")
private lateinit var methods: Methods

class LoginRegisterFragment : Fragment() {
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
        (requireActivity() as MainActivity).setToolbarTitle(requireContext().getString(R.string.loginregistrerfragment_acceso))
        bindingA = FragmentInitialLoginRegisterBinding.inflate(inflater, container, false)
        methods = Methods(requireContext())
        btnRegister = binding.btnHomeRegister
        btnLogin = binding.btnHomeLogin


        listeners()
        return binding.root
    }

    private fun listeners() {
        btnRegister.setOnClickListener {
            methods.replaceFragment(RegisterFragment(), requireActivity().supportFragmentManager)

        }
        btnLogin.setOnClickListener{
            methods.replaceFragment(LoginFragment(), requireActivity().supportFragmentManager)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginRegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}