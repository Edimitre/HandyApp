package com.edimitre.handyapp.fragment.news

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.adapters.recycler_adapter.NewsAdapter
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.NewsViewModel
import com.edimitre.handyapp.databinding.FragmentBotaAlBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class BotaAlFragment : Fragment(), NewsAdapter.OnNewsClickListener {


    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: FragmentBotaAlBinding

    private lateinit var myAdapter: NewsAdapter

    private val _newsViewModel: NewsViewModel by activityViewModels()

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


//        val t = isWorkEverScheduledBefore(requireContext(), "scrap_work")
//        Log.e(TAG, "is scheduled: $t")

        initAdapterAndRecyclerView()

        setRefreshListener()

        checkIfNewsEmpty()

        showAllNews()

        observeWork()

        enableTouchFunctions()

    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = NewsAdapter(this)

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.newsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myAdapter

            addItemDecoration(dividerItemDecoration)

        }
    }

    private fun checkIfNewsEmpty(){

        lifecycleScope.launch {

            when (_newsViewModel.getOneBySource("bota.al")) {
                null -> {
                    val job = _newsViewModel.deleteAllBySource("bota.al")

                    runBlocking {
                        job.start()
                        systemService.startScrapBotaAl()
                    }

                }
                else -> {

                }
            }
        }
    }

    private fun setRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {

            lifecycleScope.launch {
                val job = _newsViewModel.deleteAllBySource("bota.al")
                runBlocking {
                    job.start()
                    systemService.startScrapBotaAl()
                }

            }

        }
    }


    private fun showAllNews() {

        lifecycleScope.launch {
            _newsViewModel.getAllNewsBySourcePaged("bota.al").collectLatest {
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

    private fun observeWork() {

        val workManager = WorkManager.getInstance(requireContext())

        val workList = workManager.getWorkInfosByTagLiveData("scrap_botaal_work")

        workList.observe(viewLifecycleOwner) { listWorkInfo ->

            listWorkInfo.forEach {


                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {

                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    WorkInfo.State.RUNNING -> {

                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    else -> {

                    }
                }

            }
        }


    }


//    private fun isWorkEverScheduledBefore(context: Context, tag: String): Boolean {
//        val instance = WorkManager.getInstance(context)
//        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosForUniqueWork(tag)
//        var workScheduled = false
//        statuses.get()?.let {
//            for (workStatus in it) {
//                workScheduled = (
//                        workStatus.state == WorkInfo.State.ENQUEUED
//                                || workStatus.state == WorkInfo.State.RUNNING
//                                || workStatus.state == WorkInfo.State.BLOCKED
//                                || workStatus.state.isFinished // It checks SUCCEEDED, FAILED, CANCELLED already
//                        )
//            }
//        }
//        return workScheduled
//    }

    override fun onLikeClicked(news: News) {

        _newsViewModel.likeNews(news)
    }

    override fun onShareClicked(news: News) {
        shareOnOtherApp(news.link)
    }

    private fun shareOnOtherApp(link: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        activity.let {
            it!!.startActivity(shareIntent)
        }

    }
}