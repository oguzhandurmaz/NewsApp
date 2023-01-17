package com.example.newsapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsDetailBinding
import com.example.newsapp.network.Result
import com.example.newsapp.network.models.Article
import com.example.newsapp.utils.Utils
import com.example.newsapp.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

class NewsDetailFragment : Fragment(R.layout.fragment_news_detail) {

    private var _binding: FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private val args: NewsDetailFragmentArgs by navArgs()

    private var successDialog: AlertDialog? = null
    private var loadingDialog: Dialog? = null
    private lateinit var menuHost: MenuHost
    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.news_detail_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                android.R.id.home -> {
                    findNavController().navigateUp()
                }
                R.id.add_favorite -> {
                    viewModel.addToFavorite(args.model)
                }
            }
            return true
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewsDetailBinding.bind(view)
        binding.composeView.apply {
            val typedValue = TypedValue();
            requireContext().theme.resolveAttribute(
                android.R.attr.textColorPrimary,
                typedValue,
                true
            );
            val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Log.d("Repo", args.model.toString())
                DetailScreen(news = args.model, color)
            }
        }
        successDialog = Utils.getSuccessDialog(requireContext())
        loadingDialog = Utils.getLoadingDialog(requireContext())
        menuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addFavoriteResult.collectLatest {
                when (it) {
                    is Result.Success -> {
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuHost.removeMenuProvider(menuProvider)
        loadingDialog = null
        successDialog = null
        _binding = null
    }
}

@Composable
fun DetailScreen(news: Article, color: Int) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = news.urlToImage, contentDescription = "", modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = news.title ?: "",
            fontSize = 20.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = Color(color)
        )
        news.author?.let {
            Text(
                modifier = Modifier
                    .padding(8.dp, 2.dp)
                    .fillMaxWidth(),
                text = news.author ?: "", color = MaterialTheme.colors.primary, fontSize = 16.sp
            )
        }
        Text(
            modifier = Modifier.padding(10.dp, 6.dp),
            text = news.description ?: ""
            ?: "", fontSize = 16.sp,
            color = Color(color)
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = dateFormat.parse(news.publishedAt ?: "")?.let { dateFormat2.format(it) } ?: "",
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(color))
    }
}