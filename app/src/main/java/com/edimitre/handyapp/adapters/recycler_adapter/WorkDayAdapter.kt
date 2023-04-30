package com.edimitre.handyapp.adapters.recycler_adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.TimeUtils


class WorkDayAdapter :
    PagingDataAdapter<WorkDay, WorkDayAdapter.WorkDayViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work_day, parent, false)


        return WorkDayViewHolder(view)

    }


    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        holder.bind(getItem(position)!!)

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<WorkDay>() {

        override fun areItemsTheSame(oldItem: WorkDay, newItem: WorkDay): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkDay, newItem: WorkDay): Boolean {
            return oldItem == newItem
        }

    }

    class WorkDayViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


//        private val noteContent: TextView = itemView.findViewById(R.id.note_content_text)

        private val date: TextView = itemView.findViewById(R.id.work_day_date)


        private val workHours: TextView = itemView.findViewById(R.id.work_day_hours)

        private val activity: TextView = itemView.findViewById(R.id.work_day_activity)

        @SuppressLint("SetTextI18n")
        fun bind(workDay: WorkDay) {

            val timeInMilliSeconds =
                TimeUtils().getTimeInMilliSeconds(workDay.year, workDay.month, workDay.day)

            date.text = "date ${TimeUtils().getDateStringFromMilliSeconds(timeInMilliSeconds)}"

            workHours.text = "hours ${workDay.workHours}"

            activity.text = workDay.activity
        }
    }

    fun getWorkDayByPos(pos: Int): WorkDay? {
        return getItem(pos)
    }
}