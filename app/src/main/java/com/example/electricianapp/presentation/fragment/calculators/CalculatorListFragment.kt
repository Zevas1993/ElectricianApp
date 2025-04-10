package com.example.electricianapp.presentation.fragment.calculators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentCalculatorListBinding
import dagger.hilt.android.AndroidEntryPoint

// Data class for calculator list items
data class CalculatorListItem(
    val id: String,
    val name: String,
    val navigationActionId: Int
)

@AndroidEntryPoint
class CalculatorListFragment : Fragment() {

    private var _binding: FragmentCalculatorListBinding? = null
    private val binding get() = _binding!!

    private lateinit var calculatorListAdapter: CalculatorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCalculators()
    }

    private fun setupRecyclerView() {
        calculatorListAdapter = CalculatorListAdapter { calculatorItem ->
            // Handle click: Navigate using the item's action ID
            try {
                findNavController().navigate(calculatorItem.navigationActionId)
            } catch (e: IllegalArgumentException) {
                android.util.Log.e("CalculatorListFragment", "Navigation failed for action ID: ${calculatorItem.navigationActionId}", e)
                // Optionally show a toast or error message
            }
        }
        binding.recyclerViewCalculators.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = calculatorListAdapter
        }
    }

    private fun loadCalculators() {
        // Define the list of available calculators
        // TODO: Get these from a more dynamic source or constants file if needed
        val calculators = listOf(
            CalculatorListItem("dwelling_load", "Dwelling Load Calculator", R.id.action_calculatorListFragment_to_dwellingLoadFragment),
            CalculatorListItem("conduit_fill", "Conduit Fill Calculator", R.id.action_calculatorListFragment_to_conduitFillFragment),
            CalculatorListItem("box_fill", "Box Fill Calculator", R.id.action_calculatorListFragment_to_boxFillFragment),
            CalculatorListItem("luminaire", "Luminaire Calculator", R.id.action_calculatorListFragment_to_luminaireCalculatorFragment),
            CalculatorListItem("pipe_bending", "Pipe Bending Calculator", R.id.action_calculatorListFragment_to_pipeBendingFragment),
            CalculatorListItem("voltage_drop", "Voltage Drop Calculator", R.id.action_calculatorListFragment_to_voltageDropFragment),
            CalculatorListItem("nec_code", "NEC Code Lookup", R.id.action_calculatorListFragment_to_necCodeLookupFragment),
            CalculatorListItem("lighting_layout", "Lighting Layout", R.id.action_calculatorListFragment_to_lightingLayoutFragment),
            CalculatorListItem("ar_view", "AR View", R.id.action_calculatorListFragment_to_arViewFragment),
            CalculatorListItem("photo_doc", "Photo Documentation", R.id.action_calculatorListFragment_to_photoDocListFragment),
            CalculatorListItem("material_management", "Material Management", R.id.action_calculatorListFragment_to_materialListFragment)
            // Add more calculators/tools here as they are implemented
        )
        calculatorListAdapter.submitList(calculators)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
