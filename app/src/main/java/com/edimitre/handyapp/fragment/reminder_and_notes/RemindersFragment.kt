package com.edimitre.handyapp.fragment.reminder_and_notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.adapters.recycler_adapter.ReminderAdapter
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.view_model.ReminderViewModel
import com.edimitre.handyapp.databinding.FragmentRemindersBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemindersFragment : Fragment(), AddReminderForm.AddReminderListener {

    private lateinit var adapter: ReminderAdapter

    private val _reminderViewModel: ReminderViewModel by activityViewModels()

    private lateinit var binding: FragmentRemindersBinding

    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapterAndRecyclerView()

        observeProducts()

        enableTouchFunctions()
    }


    private fun initAdapterAndRecyclerView() {

        adapter = ReminderAdapter()
        binding.reminderRecyclerView.setHasFixedSize(true)
        binding.reminderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.reminderRecyclerView.adapter = adapter


    }

    private fun observeProducts() {

        _reminderViewModel.reminderList!!.observe(viewLifecycleOwner) {
            when {
                it.isEmpty() -> {
                    binding.emptyRemindersText.visibility = View.VISIBLE
                }
                else -> {
                    adapter.putReminders(it)
                }
            }

        }
    }

    override fun addReminder(reminder: Reminder?) {
        _reminderViewModel.saveReminder(reminder!!)
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

                    val reminder = adapter.getReminderByPos(viewHolder.absoluteAdapterPosition)

                    openDeleteDialog(reminder!!, viewHolder.absoluteAdapterPosition)

                }
            })

        itemTouchHelper.attachToRecyclerView(binding.reminderRecyclerView)
    }


    private fun openDeleteDialog(reminder: Reminder, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "are you sure you want to delete ${reminder.description} \n" +
                    "this action can't be undone"

        )
        dialog.setPositiveButton("Delete") { _, _ ->


            _reminderViewModel.deleteReminder(reminder)
            adapter.notifyItemChanged(pos)


        }

        dialog.setNegativeButton("Close") { _, _ ->


        }


        dialog.setOnDismissListener {

            adapter.notifyItemChanged(pos)
        }


        dialog.show()
    }

}