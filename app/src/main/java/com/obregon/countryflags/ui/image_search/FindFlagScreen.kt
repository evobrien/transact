package com.obregon.countryflags.ui.image_search

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
class FindFlagScreen : Fragment() {
    private val findFlagViewModel: FindFlagViewModel by viewModels()
    private lateinit var binding: SearchImageFragmentBinding
    private lateinit var searchBinding: SearchSectionLayoutBinding

    companion object {
        private const val FLAG_STYLE_FLAT = "flat"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchImageFragmentBinding.inflate(inflater, container, false)
        searchBinding = binding.searchSection
        searchBinding.btnFetch.setOnClickListener {
            fetchFlag()
        }
        handleErrors()
        handleUpdates()
        return binding.root
    }

    private fun fetchFlag() {
        val countryCode = searchBinding.edtCountryCode.text.toString()
        if (countryCode.isNotBlank()) {
            findFlagViewModel.fetchFlag(countryCode, FLAG_STYLE_FLAT)
        } else {
            searchBinding.edtCountryCode.error =
                context?.getString(R.string.err_validate_country_code)
        }
    }

    private fun handleUpdates() {
        findFlagViewModel.flag.observeForever {
            loadList(it)
        }
    }

    private fun loadList(flagData: List<FlagData>) {
        if (flagData.isNotEmpty()) {
            binding.tvNoSavedResults.visibility = View.GONE
            binding.savedImageList.visibility = View.VISIBLE
            binding.savedImageList.adapter = FlagDataRecyclerAdapter(
                flagData,
                true,
                findFlagViewModel
            )
            binding.savedImageList.layoutManager = LinearLayoutManager(this.context)
        } else {
            binding.tvNoSavedResults.visibility = View.VISIBLE
            binding.savedImageList.visibility = View.GONE
        }
    }

    private fun handleErrors() {
        findFlagViewModel.error.observeForever {
            context?.showToast(it)
            binding.tvNoSavedResults.visibility = View.VISIBLE
            binding.savedImageList.visibility = View.GONE
        }

    }

}


