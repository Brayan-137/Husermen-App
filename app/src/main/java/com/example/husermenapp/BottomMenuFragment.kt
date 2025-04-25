package com.example.husermenapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.husermenapp.databinding.FragmentBottomMenuBinding

class BottomMenuFragment : Fragment() {
    private var _binding: FragmentBottomMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnInventory.setOnClickListener { handleClickBtnInventory() }
        binding.btnTopVentas.setOnClickListener { handleClickBtnTopVentas() }
        binding.btnMercadoLibre.setOnClickListener { handleClickBtnMercadoLibre() }
        binding.btnTutorials.setOnClickListener { handleClickBtnTutorials() }
    }

    private fun handleClickBtnTutorials() {
        startActivity(Intent(requireContext(), Tutorials::class.java))
        requireActivity().finish()
    }

    private fun handleClickBtnMercadoLibre() {
        startActivity(Intent(requireContext(), MercadoLibre::class.java))
        requireActivity().finish()
    }

    private fun handleClickBtnTopVentas() {
        startActivity(Intent(requireContext(), TopVentas::class.java))
        requireActivity().finish()
    }

    private fun handleClickBtnInventory() {
        startActivity(Intent(requireContext(), Inventory::class.java))
        requireActivity().finish()
    }

    companion object {
//        private const val CONTEXT_BUNDLE = ""
//        private const val ARG_PARAM2 = "param2"
//
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            BottomMenuFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}