package com.example.newsapp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.newsapp.INewsRecycler
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsFavoriteBinding
import com.example.newsapp.network.Result
import com.example.newsapp.network.models.Article
import com.example.newsapp.ui.adapter.NewsRecyclerAdapter
import com.example.newsapp.utils.Utils
import com.example.newsapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFavoriteFragment : Fragment(R.layout.fragment_news_favorite) {

    private var _binding: FragmentNewsFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private var adapter: NewsRecyclerAdapter? = null
    private var loadingDialog: Dialog? = null

    private val newsRecyclerCallback = object : INewsRecycler {
        override fun onClickListener(data: Article) {
            goToDetail(data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFavoriteNews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewsFavoriteBinding.bind(view)

        loadingDialog = Utils.getLoadingDialog(requireContext())
        adapter = NewsRecyclerAdapter(newsRecyclerCallback)
        binding.recyclerFavoriteNews.adapter = adapter

        viewModel.favoriteNewsResult.observe(viewLifecycleOwner){
            when(it){
                is Result.Success -> {
                    setUiState(success = true)
                    adapter?.setData(viewModel.getRecyclerItems(it.result))
                }
                is Result.Loading -> {
                    setUiState(loading = true)
                }
                is Result.Error -> {
                    setUiState(error = true)
                }
            }
        }
    }
    private fun goToDetail(data: Article){
        val action = NewsFavoriteFragmentDirections.actionNewsFavoriteFragmentToNewsDetailFragment(data)
        findNavController().navigate(action)
    }
    private fun setUiState(
        success: Boolean = false,
        loading: Boolean = false,
        error: Boolean = false
    ) {
        binding.recyclerFavoriteNews.isVisible = success
        if(loading) loadingDialog?.show() else loadingDialog?.dismiss()
        binding.groupRetry.isVisible = error
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingDialog = null
        adapter = null
        _binding = null
    }
}