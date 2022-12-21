package com.edimitre.handyapp.fragment.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.recycler_adapter.NewsAdapter
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.NewsViewModel
import com.edimitre.handyapp.databinding.FragmentBotaAlBinding
import com.edimitre.handyapp.databinding.FragmentJoqBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class JoqFragment : Fragment() {


    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: FragmentJoqBinding

    private lateinit var myAdapter: NewsAdapter

    private val _newsViewModel: NewsViewModel by activityViewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoqBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        val t = isWorkEverScheduledBefore(requireContext(), "scrap_work")
//        Log.e(HandyAppEnvironment.TAG, "is scheduled: $t")

        initAdapterAndRecyclerView()

        initToolbar()

        showAllNews()

        enableTouchFunctions()

    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = NewsAdapter()

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.newsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myAdapter

            addItemDecoration(dividerItemDecoration)

        }
    }

    private fun initToolbar() {

        binding.nToolbar.inflateMenu(R.menu.toolbar_menu)


        val btnPickDate = binding.nToolbar.menu.findItem(R.id.btn_calendar_pick)
        btnPickDate.isVisible = false

        val btnCloseSearch = binding.nToolbar.menu.findItem(R.id.btn_close_date_search)
        btnCloseSearch.isVisible = false

        val settingButton = binding.nToolbar.menu.findItem(R.id.btn_settings)
        settingButton.isVisible = false

        val search = binding.nToolbar.menu.findItem(R.id.btn_search_db)

        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled = true



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showAllNewsByContent(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                showAllNewsByContent(newText)
                return false
            }
        })
    }

    private fun showAllNews() {

        lifecycleScope.launch {
            _newsViewModel.getAllNewsBySourcePaged("joq.al").collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun showAllNewsByContent(source: String) {

        lifecycleScope.launch {
            _newsViewModel.getAllNewsBySourcePaged(source).collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun enableTouchFunctions() {
        itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val news = myAdapter.getNewsByPos(viewHolder.absoluteAdapterPosition)
                    openDeleteDialog(news!!, viewHolder.absoluteAdapterPosition)

                }
            })

        itemTouchHelper.attachToRecyclerView(binding.newsRecyclerView)
    }

    private fun openDeleteDialog(news: News, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "are you sure you want to delete ${news.title} \n" +
                    "this action can't be undone"

        )
        dialog.setPositiveButton("Delete") { _, _ ->


            _newsViewModel.deleteNews(news)
            myAdapter.notifyItemChanged(pos)


        }

        dialog.setNegativeButton("Close") { _, _ ->


        }


        dialog.setOnDismissListener {

            myAdapter.notifyItemChanged(pos)
        }


        dialog.show()
    }


}