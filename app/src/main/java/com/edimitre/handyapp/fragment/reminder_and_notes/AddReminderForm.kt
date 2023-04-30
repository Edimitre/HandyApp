package com.edimitre.handyapp.fragment.reminder_and_notes

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.databinding.AddReminderFormBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat

@AndroidEntryPoint
class AddReminderForm : BottomSheetDialogFragment() {


    private var listener: AddReminderListener? = null

    private var year: Int? = null

    private var month: Int? = null

    private var date: Int? = null

    private var hour: Int? = null

    private var minutes: Int? = null

    private lateinit var binding: AddReminderFormBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddReminderFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setListeners()
    }

    private fun setListeners() {
        binding.btnOpenAlarmDatePicker.setOnClickListener {

            openDatePicker()

        }

        binding.btnAddReminder.setOnClickListener {

            if (inputIsOk()) {

                val threeSeconds = 3 * 1000
                val timeInMillis =
                    TimeUtils().getTimeInMilliSeconds(year!!, month!!, date!!, hour!!, minutes!!)
                val description = binding.reminderDescriptionInput.text.toString()
                val reminder = Reminder(0, timeInMillis + threeSeconds, description, true)

                // verify reminder
                if (isReminderValid(reminder)) {
                    listener!!.addReminder(reminder)

                    dismiss()
                } else {
                    Toast.makeText(
                        activity,
                        "Selected date doesn't add with selected time \n" +
                                "please set again",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

        }

        binding.btnCloseReminderDialog.setOnClickListener {
            dismiss()
        }
    }


    private fun isReminderValid(reminder: Reminder): Boolean {

        return reminder.alarmTimeInMillis >= System.currentTimeMillis()

    }

    private fun openDatePicker() {

        val dateDialog = DatePickerDialog(requireContext())

        val timeNow = System.currentTimeMillis() - 1000
        dateDialog.datePicker.minDate = timeNow

        dateDialog.setOnDateSetListener { _, y, m, d ->
            year = y
            month = m
            date = d
            showSelectedDate(
                year!!,
                month!!,
                date!!,
                TimeUtils().getCurrentHour(),
                TimeUtils().getCurrentMinute()
            )
            openTimePicker()
        }
        dateDialog.show()

    }

    private fun openTimePicker() {


        val timePickerDialog = TimePickerDialog(
            context, { _, h, m ->
                hour = h
                minutes = m
                showSelectedDate(year!!, month!!, date!!, hour!!, minutes!!)
            },
            TimeUtils().getCurrentHour(), TimeUtils().getCurrentMinute(), true
        )


        timePickerDialog.show()

    }

    private fun inputIsOk(): Boolean {


        val description = binding.reminderDescriptionInput.text.toString()

        return when {

            year == null && month == null && date == null && hour == null -> {
                Toast.makeText(
                    requireContext(),
                    "date and time can't be empty",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            description.isEmpty() -> {
                Toast.makeText(requireContext(), "please set a description", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            else -> {
                true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSelectedDate(year: Int, month: Int, day: Int, hour: Int, minute: Int) {

        @SuppressLint("SimpleDateFormat")
        val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")

        @SuppressLint("SimpleDateFormat")
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss")


        val rDate =
            dateFormat.format(TimeUtils().getTimeInMilliSeconds(year, month, day, hour, minute))

        val rTime =
            timeFormat.format(TimeUtils().getTimeInMilliSeconds(year, month, day, hour, minute))

        binding.selectedDateText.text = rDate

        binding.selectedTimeText.text = rTime
    }

    interface AddReminderListener {
        fun addReminder(reminder: Reminder?)
    }

    override fun onAttach(context: Context) {
        listener = try {
            context as AddReminderListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }

}
