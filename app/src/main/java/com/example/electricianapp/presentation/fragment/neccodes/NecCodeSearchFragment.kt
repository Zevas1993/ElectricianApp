package com.example.electricianapp.presentation.fragment.neccodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentNecCodeSearchBinding
import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.example.electricianapp.domain.model.neccodes.NecCategory
import com.example.electricianapp.presentation.adapter.NecArticleAdapter
import com.example.electricianapp.presentation.viewmodel.neccodes.NecCodeUiState
import com.example.electricianapp.presentation.viewmodel.neccodes.NecCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NecCodeSearchFragment : Fragment() {

    private var _binding: FragmentNecCodeSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NecCodeViewModel by activityViewModels()
    private lateinit var articleAdapter: NecArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNecCodeSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupDropdowns()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        articleAdapter = NecArticleAdapter { article ->
            // Handle article click
            viewModel.selectArticle(article.id)
            // Navigate to article detail
            // TODO: Implement navigation to article detail
        }
        binding.recyclerViewResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }
    }

    private fun setupDropdowns() {
        // Category dropdown
        val categories = NecCategory.values().map { it.name.replace("_", " ") }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        (binding.autoCompleteCategory as? AutoCompleteTextView)?.setAdapter(categoryAdapter)

        // Year dropdown
        val years = listOf("2020", "2017", "2014", "2011", "2008")
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, years)
        (binding.autoCompleteYear as? AutoCompleteTextView)?.setAdapter(yearAdapter)
        (binding.autoCompleteYear as? AutoCompleteTextView)?.setText("2020", false)
    }

    private fun setupListeners() {
        binding.editTextSearch.doAfterTextChanged { text ->
            viewModel.updateSearchText(text.toString())
        }

        binding.autoCompleteCategory.setOnItemClickListener { _, _, position, _ ->
            val categoryName = NecCategory.values()[position].name
            viewModel.updateSelectedCategory(NecCategory.valueOf(categoryName))
        }

        binding.autoCompleteYear.setOnItemClickListener { _, _, position, _ ->
            val year = when (position) {
                0 -> 2020
                1 -> 2017
                2 -> 2014
                3 -> 2011
                4 -> 2008
                else -> 2020
            }
            viewModel.updateSelectedYear(year)
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.searchArticles()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.codeUiState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: NecCodeUiState) {
        when (state) {
            is NecCodeUiState.Initial -> {
                binding.progressBar.isVisible = false
                binding.recyclerViewResults.isVisible = false
                binding.textViewNoResults.isVisible = false
            }
            is NecCodeUiState.Loading -> {
                binding.progressBar.isVisible = true
                binding.recyclerViewResults.isVisible = false
                binding.textViewNoResults.isVisible = false
            }
            is NecCodeUiState.Success -> {
                binding.progressBar.isVisible = false
                binding.recyclerViewResults.isVisible = state.articles.isNotEmpty()
                binding.textViewNoResults.isVisible = state.articles.isEmpty()
                articleAdapter.submitList(state.articles)
            }
            is NecCodeUiState.Error -> {
                binding.progressBar.isVisible = false
                binding.recyclerViewResults.isVisible = false
                binding.textViewNoResults.isVisible = true
                binding.textViewNoResults.text = state.message
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
