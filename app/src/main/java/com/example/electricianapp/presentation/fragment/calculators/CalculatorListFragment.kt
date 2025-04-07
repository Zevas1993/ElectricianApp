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
            CalculatorListItem("dwelling_load", "Dwelling Load Calculator", R.id.action_calculatorListFragment_to_dwellingLoadFragment), // Action ID needs to be created
            CalculatorListItem("conduit_fill", "Conduit Fill Calculator", R.id.action_calculatorListFragment_to_conduitFillFragment), // Action ID needs to be created
            CalculatorListItem("box_fill", "Box Fill Calculator", R.id.action_calculatorListFragment_to_boxFillFragment) // Action ID needs to be created
            // Add more calculators here as they are implemented
        )
        calculatorListAdapter.submitList(calculators)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
