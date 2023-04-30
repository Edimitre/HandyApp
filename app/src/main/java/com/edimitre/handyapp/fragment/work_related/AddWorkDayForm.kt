package com.edimitre.handyapp.fragment.work_related

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.databinding.AddWorkDayFormBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddWorkDayForm : BottomSheetDialogFragment() {


    private var listener: AddWorkDayListener? = null

    private lateinit var binding: AddWorkDayFormBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = AddWorkDayFormBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }


    private fun setListeners() {

        binding.btnCancel.setOnClickListener {

            dismiss()
        }

        binding.btnAddWorkday.setOnClickListener {

            when {
                inputIsOk() -> {
                    val year = TimeUtils().getCurrentYear()
                    val month = TimeUtils().getCurrentMonth()
                    val day = TimeUtils().getCurrentDate()

                    val hours = binding.inputHours.text.toString().toInt()
                    val activity = binding.inputActivity.text.toString()

                    listener?.addWorkDay(WorkDay(0, year, month, day, hours, activity))
                    dismiss()
                }
            }


        }
    }

    private fun inputIsOk(): Boolean {


        val hours = binding.inputHours.text.toString()

        val activity = binding.inputActivity.text.toString()

        return when {

            hours.isEmpty() -> {

                binding.inputHours.error = "hours can't be empty"
                false
            }

            activity.isEmpty() -> {

                binding.inputActivity.error = "activity can't be empty"
                false
            }

            else -> {
                true
            }
        }
    }


    interface AddWorkDayListener {
        fun addWorkDay(workDay: WorkDay)
    }

    override fun onAttach(context: Context) {
        listener = try {
            context as AddWorkDayListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }

}