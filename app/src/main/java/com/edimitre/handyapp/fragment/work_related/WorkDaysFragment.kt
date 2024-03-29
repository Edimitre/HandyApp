package com.edimitre.handyapp.fragment.work_related

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.adapters.recycler_adapter.WorkDayAdapter
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.WorkDayViewModel
import com.edimitre.handyapp.databinding.FragmentWorkDaysBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkDaysFragment : Fragment() {

    private lateinit var myAdapter: WorkDayAdapter

    private val _workDayViewModel: WorkDayViewModel by activityViewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper

    private lateinit var binding: FragmentWorkDaysBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWorkDaysBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapterAndRecyclerView()

        showAllWorkDaysByYearAndMonth(TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth())

        enableTouchFunctions()


    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = WorkDayAdapter()

//        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.workdaysRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myAdapter

//            addItemDecoration(dividerItemDecoration)

        }
    }

//    private fun showAllWorkDaysByYear(year: Int) {
//
//        lifecycleScope.launch {
//            _workDayViewModel.getAllWorkDaysPagedByYear(year).collectLatest {
//                myAdapter.submitData(it)
//            }
//        }
//
//    }

    private fun showAllWorkDaysByYearAndMonth(year: Int, month: Int) {

        lifecycleScope.launch {
            _workDayViewModel.getAllWorkDaysPagedByYearAndMonth(year, month).collectLatest {
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

                    val workday = myAdapter.getWorkDayByPos(viewHolder.absoluteAdapterPosition)

                    openDeleteDialog(workday!!, viewHolder.absoluteAdapterPosition)


                }
            })

        itemTouchHelper.attachToRecyclerView(binding.workdaysRecyclerView)
    }

    private fun openDeleteDialog(workDay: WorkDay, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "Are you sure you want to delete ${workDay.id} \n" +
                    "this action can't be undone"

        )
        dialog.setPositiveButton("Delete") { _, _ ->


            _workDayViewModel.deleteWorkDay(workDay)
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