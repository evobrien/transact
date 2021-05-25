package com.obregon.countryflags.ui.saved_image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.obregon.countryflags.R
import com.obregon.countryflags.databinding.SearchImageFragmentBinding
import com.obregon.countryflags.databinding.SearchSectionLayoutBinding
import com.obregon.countryflags.domain.usecase.FlagData
import com.obregon.countryflags.ui.common.FlagDataRecyclerAdapter
import com.obregon.countryflags.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFlagScreen : Fragment() {
    private val savedFlagViewModel: SavedFlagViewModel by viewModels()
    private lateinit var binding: SearchImageFragmentBinding
    private lateinit var searchBinding: SearchSectionLayoutBinding

    companion object {
        private const val MIN_VALID_SEARCH_TERM_LEN = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchImageFragmentBinding.inflate(inflater, container, false)
        searchBinding = binding.searchSection
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupHintText()
        setFilterButton()
        setAllButton()
        handleListUpdates()
        handleErrors()
        observeChanges()
    }

    private fun setupHintText() {
        searchBinding.textInputLayout.hint = getString(R.string.enter_country_name)
    }

    private fun setFilterButton() {
        searchBinding.btnFetch.text = getString(R.string.filter_country)
        searchBinding.btnFetch.setOnClickListener {
            searchLocalStore()
        }
    }

    private fun setAllButton() {
        searchBinding.btnShowAll.visibility = View.VISIBLE
        searchBinding.btnShowAll.setOnClickListener {
            savedFlagViewModel.fetchAllFlagData()
        }
    }

    private fun handleListUpdates() {
        savedFlagViewModel.savedFlagList.observeForever {
            loadList(it)
        }
    }

    private fun handleErrors() {
        savedFlagViewModel.error.observeForever {
            showError(it)
        }
    }

    private fun observeChanges() {
        savedFlagViewModel.savedFlagCount.observeForever {
            savedFlagViewModel.fetchAllFlagData()
        }
    }

    private fun searchLocalStore() {
        val searchTerm = searchBinding.edtCountryCode.text.toString()
        if (isValidSearchTermLength(searchTerm)) {
            savedFlagViewModel.fetchFlagData(searchTerm)
        } else {
            showError(
                getString(
                    R.string.err_not_exceed_count,
                    MIN_VALID_SEARCH_TERM_LEN.toString()
                )
            )
        }
    }

    private fun isValidSearchTermLength(searchTerm: String) =
        searchTerm.length >= MIN_VALID_SEARCH_TERM_LEN

    private fun loadList(flagData: List<FlagData>) {
        if (flagData.isNotEmpty()) {
            binding.tvNoSavedResults.visibility = View.GONE
            binding.savedImageList.visibility = View.VISIBLE
            binding.savedImageList.adapter = FlagDataRecyclerAdapter(flagData)
            binding.savedImageList.layoutManager = LinearLayoutManager(this.context)
        } else {
            binding.tvNoSavedResults.visibility = View.VISIBLE
            binding.savedImageList.visibility = View.GONE
        }
    }

    private fun showError(error: String) {
        this.context?.showToast(error)
    }
}

