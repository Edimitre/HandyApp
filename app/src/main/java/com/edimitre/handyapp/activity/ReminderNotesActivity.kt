package com.edimitre.handyapp.activity

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.adapters.tabs_adapter.RemindersAndNotesPagerAdapter
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.NoteViewModel
import com.edimitre.handyapp.data.view_model.ReminderViewModel
import com.edimitre.handyapp.databinding.ActivityReminderNotesBinding
import com.edimitre.handyapp.fragment.reminder_and_notes.AddReminderForm
import com.edimitre.handyapp.fragment.reminder_and_notes.NotesFragment
import com.edimitre.handyapp.fragment.reminder_and_notes.RemindersFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ReminderNotesActivity : AppCompatActivity(), AddReminderForm.AddReminderListener {
    @Inject
    lateinit var systemService: SystemService


    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: RemindersAndNotesPagerAdapter

    private lateinit var viewPager: ViewPager2

    private lateinit var _reminderViewModel: ReminderViewModel

    private lateinit var _noteViewModel: NoteViewModel

    private lateinit var binding: ActivityReminderNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReminderNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        systemService.createNotificationChannel()

        initViewModel()

        loadPageNavigation()

        setListeners()
    }

    private fun initViewModel() {
        _reminderViewModel = ViewModelProvider(this)[ReminderViewModel::class.java]
        _noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

    }

    private fun loadPageNavigation() {
        // get adapter
        pagerAdapter = RemindersAndNotesPagerAdapter(getReminderAndNotesFragments(), this)
        // get tabs
        tabs = binding.remindersAndNotesTabs

        // get viewPager
        viewPager = binding.reminderAndNoteViewPager

        // remove slide functionality
        viewPager.isUserInputEnabled = false
        // set view pager adapter
        viewPager.adapter = pagerAdapter


        //
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun getReminderAndNotesFragments(): ArrayList<Fragment> {
        return arrayListOf(
            RemindersFragment(),
            NotesFragment()
        )
    }

    private fun setListeners() {

        binding.btnAdd.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> {
                    openAddReminderForm()
                }
                1 -> {
                    openAddNoteDialog()
                }
            }
        }
    }

    private fun openAddNoteDialog() {
        val inputContent = EditText(this)
        inputContent.hint = "write your note"
        inputContent.inputType = InputType.TYPE_CLASS_TEXT

        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setView(inputContent)
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "please enter some content"
        )
        dialog.setPositiveButton("Add") { _, _ ->

            val content = inputContent.text.toString().trim()

            when {
                content.isNotEmpty() -> {
                    val note = Note(0, content)
                    _noteViewModel.saveNote(note)
                    Toast.makeText(
                        this,
                        "note added successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                else -> {
                    Toast.makeText(this, "Content can't be empty", Toast.LENGTH_SHORT).show()

                }

            }

        }

        dialog.setNegativeButton("Close") { _, _ ->

        }
        dialog.show()
    }

    private fun openAddReminderForm() {

        val reminderForm = AddReminderForm()
        reminderForm.show(supportFragmentManager, "add reminder")
    }

    override fun addReminder(reminder: Reminder?) {
        _reminderViewModel.saveReminder(reminder!!)
        systemService.setAlarm(reminder.alarmTimeInMillis)
    }
}