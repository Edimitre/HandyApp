package com.edimitre.handyapp.adapters.recycler_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.Reminder


import java.text.DateFormat
import java.text.SimpleDateFormat


class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ReminderHolder>() {

    private var allReminders: List<Reminder>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderHolder {


        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)

        return ReminderHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderHolder, position: Int) {


        val reminder: Reminder? = allReminders?.getOrNull(position)

        if (reminder != null) {

            holder.bind(reminder)

        }

    }


    override fun getItemCount(): Int {
        return allReminders?.size ?: 0
    }

    class ReminderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val isReminderActiveText: TextView =
            itemView.findViewById(R.id.is_reminder_active_text)
        private val reminderDateText: TextView = itemView.findViewById(R.id.reminder_date_text)
        private val reminderTimeText: TextView = itemView.findViewById(R.id.reminder_time_text)
        private val reminderDescriptionText: TextView =
            itemView.findViewById(R.id.reminder_description_text)


        fun bind(reminder: Reminder) {

            @SuppressLint("SimpleDateFormat")
            val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")

            @SuppressLint("SimpleDateFormat")
            val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss")


            val miDate = dateFormat.format(reminder.alarmTimeInMillis)

            val miTime = timeFormat.format(reminder.alarmTimeInMillis)



            if (reminder.isActive) {

                isReminderActiveText.text = "Still active"
            } else {

                isReminderActiveText.text = "Expired"
            }


            reminderDateText.text = miDate
            reminderTimeText.text = miTime
            reminderDescriptionText.text = reminder.description
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun putReminders(reminderList: List<Reminder>) {
        allReminders = reminderList
        notifyDataSetChanged()
    }

    fun getReminderByPos(pos: Int): Reminder? {
        return this.allReminders?.get(pos)
    }
}