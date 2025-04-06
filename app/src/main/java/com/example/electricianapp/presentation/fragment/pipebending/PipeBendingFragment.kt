package com.example.electricianapp.presentation.fragment.pipebending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.electricianapp.R
// import com.example.electricianapp.databinding.FragmentPipeBendingBinding
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint // Uncomment if needed
class PipeBendingFragment : Fragment() {

    // private var _binding: FragmentPipeBendingBinding? = null
    // private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // _binding = FragmentPipeBendingBinding.inflate(inflater, container, false)
        // return binding.root
        // return inflater.inflate(R.layout.fragment_pipe_bending, container, false)
        return TextView(requireContext()).apply { text = "Pipe Bending Fragment UI Placeholder" }
    }

    // override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //     super.onViewCreated(view, savedInstanceState)
    //     // Setup UI here
    // }

    // override fun onDestroyView() {
    //     super.onDestroyView()
    //     _binding = null
    // }
}
