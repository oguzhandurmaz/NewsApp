package com.example.newsapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newsapp.INewsRecycler
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsBinding
import com.example.newsapp.network.Result
import com.example.newsapp.network.models.Article
import com.example.newsapp.ui.adapter.NewsRecyclerAdapter
import com.example.newsapp.utils.Utils
import com.example.newsapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news) {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private var adapter: NewsRecyclerAdapter? = null
    private var successDialog: Dialog? = null
    private var loadingDialog: Dialog? = null

    private val newsRecyclerCallback = object : INewsRecycler {
        override fun onClickListener(data: Article) {
            goToDetail(data)
        }

        override fun onFavoriteListener(data: Article) {
            viewModel.addToFavorite(data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewsBinding.bind(view)

        successDialog = Utils.getSuccessDialog(requireContext())
        loadingDialog = Utils.getLoadingDialog(requireContext())
        loadingDialog?.show()
        adapter = NewsRecyclerAdapter(newsRecyclerCallback)
        binding.recyclerNews.adapter = adapter

        viewModel.newsResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    setUiState(success = true)
                    val items = viewModel.getRecyclerItems(it.result)
                    adapter?.setData(items)
                }
                is Result.Error -> {
                    setUiState(error = true)
                }
                is Result.Loading -> {
                    setUiState(loading = true)
                }
            }
        }
        viewModel.favoriteIds.observe(viewLifecycleOwner) {
            getNews()
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addFavoriteResult.collectLatest {
                when (it) {
                    is Result.Success -> {
                        val adapterData = adapter?.getData()
                        val item = adapterData?.find { f -> f.data.title == it.result.title }
                        val position = adapterData?.indexOf(item)
                        item?.isFavorite = true
                        adapter?.notifyItemChanged(position ?: 0)
                        loadingDialog?.dismiss()
                        successDialog?.show()
                    }
                    is Result.Loading -> {
                        loadingDialog?.show()
                    }
                    is Result.Error -> {
                        loadingDialog?.dismiss()
                        Toast.makeText(
                            requireContext(),
                            R.string.fail_add_favorite,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.btnRetry.setOnClickListener {
            getNews()
        }
    }

    private fun setUiState(
        success: Boolean = false,
        loading: Boolean = false,
        error: Boolean = false
    ) {
        binding.recyclerNews.isVisible = success
        if(loading) loadingDialog?.show() else loadingDialog?.dismiss()
        binding.groupRetry.isVisible = error
    }

    private fun getNews() {
        viewModel.getNews("")
    }

    private fun goToDetail(data: Article) {
        val action = NewsFragmentDirections.actionNewsFragmentToNewsDetailFragment(data)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingDialog = null
        successDialog = null
        adapter = null
        _binding = null
    }
}