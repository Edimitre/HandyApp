package com.edimitre.handyapp.fragment.shops_expenses

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.recycler_adapter.ExpenseAdapter
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.ExpenseViewModel
import com.edimitre.handyapp.databinding.FragmentExpensesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class ExpensesFragment : Fragment(), ExpenseAdapter.OnExpenseClickListener{

    private lateinit var myAdapter: ExpenseAdapter

    private lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var binding: FragmentExpensesBinding

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment()
    }

    private fun initFragment() {
        initViewModel()
        initAdapterAndRecyclerView()
        setCheckBoxListener()
        initToolbar()
        showCloseButton(false)
        enableTouchFunctions()
    }

    private fun initViewModel() {
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
    }

    private fun setCheckBoxListener() {
        val todayCheck = binding.todayCheckBox
        val thisMonthCheck = binding.thisMonthCheckBox
        val thisYearCheck = binding.thisYearCheckBox

        if (timeNotSpecified(todayCheck, thisMonthCheck, thisYearCheck)) {
            Log.e(HandyAppEnvironment.TAG, "time not specified setting it: ")
            todayCheck.isChecked = true
            showTodayExpenses()
        }

        todayCheck.setOnCheckedChangeListener { _, b ->

            if (timeNotSpecified(todayCheck, thisMonthCheck, thisYearCheck)) {
                todayCheck.isChecked = true
            }

            if (b) {
                showTodayExpenses()
                thisMonthCheck.isChecked = false
                thisYearCheck.isChecked = false
            }
        }

        thisMonthCheck.setOnCheckedChangeListener { _, b ->

            if (timeNotSpecified(todayCheck, thisMonthCheck, thisYearCheck)) {
                thisMonthCheck.isChecked = true
            }
            if (b) {
                showThisMonthExpenses()
                todayCheck.isChecked = false
                thisYearCheck.isChecked = false
            }
        }

        thisYearCheck.setOnCheckedChangeListener { _, b ->

            if (timeNotSpecified(todayCheck, thisMonthCheck, thisYearCheck)) {
                thisYearCheck.isChecked = true
            }
            if (b) {
                showThisYearExpenses()
                todayCheck.isChecked = false
                thisMonthCheck.isChecked = false
            }
        }
    }

    private fun timeNotSpecified(
        todayBox: CheckBox,
        thisMonth: CheckBox,
        thisYear: CheckBox
    ): Boolean {
        return !todayBox.isChecked && !thisMonth.isChecked && !thisYear.isChecked
    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = ExpenseAdapter(this)
        binding.expensesRecyclerView.setHasFixedSize(true)
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.expensesRecyclerView.adapter = myAdapter


    }

    private fun initToolbar() {

        binding.toolbar.inflateMenu(R.menu.toolbar_menu)


        val searchButton = binding.toolbar.menu.findItem(R.id.btn_search_db)

        val searchView = searchButton.actionView as SearchView
        searchView.isSubmitButtonEnabled = true


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showExpensesByDescription(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                showExpensesByDescription(newText)
                return false
            }
        })


        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when {
                    binding.todayCheckBox.isChecked -> {
                        Log.e(HandyAppEnvironment.TAG, "today checked: ")
                        showTodayExpenses()
                    }
                    binding.thisMonthCheckBox.isChecked -> {
                        Log.e(HandyAppEnvironment.TAG, "this month checked: ")
                        showThisMonthExpenses()
                    }
                    binding.thisYearCheckBox.isChecked -> {
                        Log.e(HandyAppEnvironment.TAG, "this year checked: ")
                        showThisYearExpenses()
                    }
                }

            }
        }


        val closeButton: View? = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            when {
                binding.todayCheckBox.isChecked -> {
                    Log.e(HandyAppEnvironment.TAG, "today checked: ")
                    showTodayExpenses()
                }
                binding.thisMonthCheckBox.isChecked -> {
                    Log.e(HandyAppEnvironment.TAG, "this month checked: ")
                    showThisMonthExpenses()
                }
                binding.thisYearCheckBox.isChecked -> {
                    Log.e(HandyAppEnvironment.TAG, "this year checked: ")
                    showThisYearExpenses()
                }
            }
        }


        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_calendar_pick -> {
                    openDatePicker()
                    true
                }
                R.id.btn_close_date_search -> {
                    when {
                        binding.todayCheckBox.isChecked -> {
                            Log.e(HandyAppEnvironment.TAG, "today checked: ")
                            showTodayExpenses()
                            showCloseButton(false)
                        }
                        binding.thisMonthCheckBox.isChecked -> {
                            Log.e(HandyAppEnvironment.TAG, "this month checked: ")
                            showThisMonthExpenses()
                            showCloseButton(false)
                        }
                        binding.thisYearCheckBox.isChecked -> {
                            Log.e(HandyAppEnvironment.TAG, "this year checked: ")
                            showThisYearExpenses()
                            showCloseButton(false)
                        }

                    }
                    true
                }
                else -> {
                    true
                }
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

                    val expense = myAdapter.getExpenseByPos(viewHolder.absoluteAdapterPosition)

                    openDeleteDialog(expense!!,viewHolder.absoluteAdapterPosition)


                }
            })

        itemTouchHelper.attachToRecyclerView(binding.expensesRecyclerView)
    }

    private fun openDeleteDialog(expense: Expense, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "Are you sure you want to delete ${expense.spentValue} \n" +
                    "this action can't be undone"

        )
        dialog.setPositiveButton("Delete") { _, _ ->


            expenseViewModel.deleteExpense(expense)
            myAdapter.notifyItemChanged(pos)
            setSpentValueByYearMonthDate(
                TimeUtils().getCurrentYear(),
                TimeUtils().getCurrentMonth(),
                TimeUtils().getCurrentDate()
            )


        }

        dialog.setNegativeButton("Close") { _, _ ->


        }


        dialog.setOnDismissListener {

            myAdapter.notifyItemChanged(pos)
            setSpentValueByYearMonthDate(
                TimeUtils().getCurrentYear(),
                TimeUtils().getCurrentMonth(),
                TimeUtils().getCurrentDate()
            )
        }


        dialog.show()
    }

    private fun showCloseButton(show: Boolean) {

        val closeSearchButton = binding.toolbar.menu.findItem(R.id.btn_close_date_search)
        val searchButton = binding.toolbar.menu.findItem(R.id.btn_search_db)
        val calendarButton = binding.toolbar.menu.findItem(R.id.btn_calendar_pick)
        when {
            show -> {
                closeSearchButton.isVisible = true
                searchButton.isVisible = false
                calendarButton.isVisible = false
            }
            else -> {
                closeSearchButton.isVisible = false
                searchButton.isVisible = true
                calendarButton.isVisible = true
            }
        }

    }

    private fun showTodayExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.getAllExpensesBYYearMonthDate(
                TimeUtils().getCurrentYear(),
                TimeUtils().getCurrentMonth(),
                TimeUtils().getCurrentDate()
            ).collectLatest {
                myAdapter.submitData(it)

            }
        }

        setSpentValueByYearMonthDate(TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth(),
            TimeUtils().getCurrentDate())
    }

    private fun showThisYearExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.getAllExpensesByYearPaginated(
                TimeUtils().getCurrentYear()
            ).collectLatest {
                myAdapter.submitData(it)
            }
        }

        setSpentValueByYear(TimeUtils().getCurrentYear())
    }

    private fun showThisMonthExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.getAllExpensesByYearAndMonth(
                TimeUtils().getCurrentYear(),
                TimeUtils().getCurrentMonth(),

                ).collectLatest {
                myAdapter.submitData(it)
            }
        }

        setSpentValueByYearMonth(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth()
        )
    }

    private fun showExpensesByDescription(desc: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.getAllExpensesByDescription(desc).collectLatest {
                myAdapter.submitData(it)
            }
        }

        setSpentValueByDescription(desc)
    }

    private fun showBySelectedDate(year: Int, month: Int, date: Int) {

        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.getAllExpensesBYYearMonthDate(year, month, date)
                .collectLatest {
                    myAdapter.submitData(it)
                }
        }

        setSpentValueByYearMonthDate(year, month, date)

    }

    private fun openDatePicker() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val dateDialog = DatePickerDialog(requireContext())

            dateDialog.setOnDateSetListener { _, y, m, d ->

                showBySelectedDate(y, m, d)
                showCloseButton(true)
            }
            dateDialog.show()
        }

    }

    private fun setSpentValueByYearMonthDate(year: Int, month: Int, date: Int) {

        expenseViewModel.getValueOfExpensesByYearMonthDate(year, month, date)!!
            .observe(viewLifecycleOwner) {
                binding.spentValueText.text = "Spent value : $it"
            }

        setNrOfExpensesByYearMonthDate(year, month, date)
    }
    private fun setNrOfExpensesByYearMonthDate(year: Int, month: Int, date: Int){

        expenseViewModel.getNrOfExpensesByYearMonthDate(year, month, date)!!.observe(viewLifecycleOwner){
            binding.nrOfExpensesText.text = "nr of expenses $it"
        }

    }

    private fun setSpentValueByYearMonth(year: Int, month: Int) {

        expenseViewModel.getValueOfExpensesbyYearMonth(year, month)!!.observe(viewLifecycleOwner) {
            binding.spentValueText.text = "Spent value : $it"
        }

        setNrOfExpensesByYearMonth(year, month)
    }
    private fun setNrOfExpensesByYearMonth(year: Int, month: Int){

        expenseViewModel.getNrOfExpensesByYearMonth(year, month)!!.observe(viewLifecycleOwner){
            binding.nrOfExpensesText.text = "nr of expenses $it"
        }

    }


    private fun setSpentValueByYear(currentYear: Int) {

        expenseViewModel.getValueOfExpensesbyYear(currentYear)!!.observe(viewLifecycleOwner) {
            binding.spentValueText.text = "Spent value : $it"
        }

        setNrOfExpensesByYear(currentYear)
    }
    private fun setNrOfExpensesByYear(year: Int){

        expenseViewModel.getNrOfExpensesByYear(year)!!.observe(viewLifecycleOwner){
            binding.nrOfExpensesText.text = "nr of expenses $it"
        }

    }

    private fun setSpentValueByDescription(description: String) {

        expenseViewModel.getValueOfExpensesByDescription(description)!!.observe(viewLifecycleOwner) {
            binding.spentValueText.text = "Spent value : $it"
        }

        setNrOfExpensesByDescription(description)
    }
    private fun setNrOfExpensesByDescription(description:String){

        expenseViewModel.getNrOfExpensesByDescription(description)!!.observe(viewLifecycleOwner){
            binding.nrOfExpensesText.text = "nr of expenses $it"
        }

    }

    override fun onExpenseClick(expense: Expense) {
        Log.e(HandyAppEnvironment.TAG, "onExpenseClick:${expense.shop.shop_name} ")
    }


}