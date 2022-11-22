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
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.util.TimeUtils

import java.text.DateFormat
import java.text.SimpleDateFormat


class ExpenseAdapter(private val onExpenseClickListener: OnExpenseClickListener) :
    PagingDataAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)

    }


    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

        holder.bind(getItem(position)!!, onExpenseClickListener)

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Expense>() {

        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.expense_id == newItem.expense_id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }

    }

    class ExpenseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val expenseDescription: TextView =
            itemView.findViewById(R.id.expense_description_text)
        private val expenseShopName: TextView = itemView.findViewById(R.id.expense_shop_name_text)
        private val expenseValue: TextView = itemView.findViewById(R.id.expense_value_text)
        private val expenseDate: TextView = itemView.findViewById(R.id.expense_date_text)
        private val expenseTime: TextView = itemView.findViewById(R.id.expense_hour_text)


        fun bind(expense: Expense, onExpenseClickListener: OnExpenseClickListener) {

            val expenseTimeInMillis = TimeUtils().getTimeInMilliSeconds(
                expense.year, expense.month,
                expense.date, expense.hour, expense.minute
            )


            @SuppressLint("SimpleDateFormat")
            val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")

            @SuppressLint("SimpleDateFormat")
            val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss")


            val eDate = dateFormat.format(expenseTimeInMillis)

            val eTime = timeFormat.format(expenseTimeInMillis)



            expenseDescription.text = expense.description
            expenseShopName.text = expense.shop.shop_name
            expenseValue.text = expense.spentValue.toString()
            expenseDate.text = eDate
            expenseTime.text = eTime


            itemView.setOnClickListener {
                onExpenseClickListener.onExpenseClick(expense)
            }

        }

    }


    fun getExpenseByPos(pos: Int): Expense? {
        return getItem(pos)
    }

    interface OnExpenseClickListener {
        fun onExpenseClick(expense: Expense)

    }
}