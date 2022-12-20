package com.edimitre.handyapp.fragment.news

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.recycler_adapter.NewsAdapter
import com.edimitre.handyapp.adapters.recycler_adapter.NoteAdapter
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.NewsViewModel
import com.edimitre.handyapp.data.view_model.NoteViewModel
import com.edimitre.handyapp.databinding.FragmentBotaAlBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BotaAlFragment : Fragment() {


    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: FragmentBotaAlBinding

    private lateinit var myAdapter: NewsAdapter

    private lateinit var _newsVieModel: NewsViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentBotaAlBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        systemService.startScrapWorker()

        val t = isWorkEverScheduledBefore(requireContext(), "scrap_work")
        Log.e(TAG, "is scheduled: $t")


        initViewModel()

        initAdapterAndRecyclerView()

        initToolbar()

        showAllNews()

        enableTouchFunctions()

    }

    private fun initViewModel() {

        _newsVieModel = ViewModelProvider(this)[NewsViewModel::class.java]
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
                showAllNotesByContent(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                showAllNotesByContent(newText)
                return false
            }
        })
    }

    private fun showAllNews() {

        lifecycleScope.launch {
            _newsVieModel.getAllNewsPaged().collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun showAllNotesByContent(source: String) {

        lifecycleScope.launch {
            _newsVieModel.getAllNewsBySourcePaged(source).collectLatest {
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


            _newsVieModel.deleteNews(news)
            myAdapter.notifyItemChanged(pos)


        }

        dialog.setNegativeButton("Close") { _, _ ->


        }


        dialog.setOnDismissListener {

            myAdapter.notifyItemChanged(pos)
        }


        dialog.show()
    }








//    private fun observeWork() {
//
//        val workManager = WorkManager.getInstance(requireContext())
//
//        val workList = workManager.getWorkInfosByTagLiveData("scrap_work")
//
//        workList.observe(viewLifecycleOwner) { listWorkInfo ->
//
//            listWorkInfo.forEach {
//
//
//                Log.e(TAG, "observingProgres: ${it.progress}")
//
//                when (it.state.name) {
//
//                    "SUCCEEDED" -> {
//
//                        showLoading(false)
//                    }
//                    "RUNNING" -> {
//
//                        showLoading(true)
//                    }
//                }
//
//            }
//        }
//
//
//    }

//    private fun showLoading(status: Boolean) {
//        when {
//            status == true -> {
//                Log.e(TAG, "showing loading")
//                binding.newsProgresBar.visibility = View.VISIBLE
//            }
//            else -> {
//                Log.e(TAG, "not showing loading")
//                binding.newsProgresBar.visibility = View.INVISIBLE
//            }
//        }
//    }

    private fun isWorkEverScheduledBefore(context: Context, tag: String): Boolean {
        val instance = WorkManager.getInstance(context)
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosForUniqueWork(tag)
        var workScheduled = false
        statuses.get()?.let {
            for (workStatus in it) {
                workScheduled = (
                        workStatus.state == WorkInfo.State.ENQUEUED
                                || workStatus.state == WorkInfo.State.RUNNING
                                || workStatus.state == WorkInfo.State.BLOCKED
                                || workStatus.state.isFinished // It checks SUCCEEDED, FAILED, CANCELLED already
                        )
            }
        }
        return workScheduled
    }
}