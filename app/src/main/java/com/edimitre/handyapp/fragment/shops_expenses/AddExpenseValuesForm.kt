package com.edimitre.handyapp.fragment.shops_expenses


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.ExpenseViewModel
import com.edimitre.handyapp.databinding.AddExpenseValuesFormBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AddExpenseValuesForm : BottomSheetDialogFragment() {

    private lateinit var expenseViewModel: ExpenseViewModel

    private var binding: AddExpenseValuesFormBinding? = null

    private var shop: Shop? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddExpenseValuesFormBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadVieModel()

        getSelectedShop()

        setListeners()
    }

    private fun getSelectedShop() {
        val bundle = arguments
        shop = bundle!!.getSerializable("shop") as Shop
        binding!!.selectedShopText.text = shop!!.shop_name
    }

    private fun loadVieModel() {

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
    }

    private fun setListeners() {

        binding!!.btnAddExpenseValues.setOnClickListener {

            if (inputsAreOk()) {
                val description =
                    binding!!.inputExpenseDescription.text.trim().toString().lowercase(Locale.ROOT)
                val spentValue = binding!!.inputExpenseValue.text.trim().toString().toDouble()

                val expense = Expense(
                    0,
                    description,
                    TimeUtils().getCurrentYear(),
                    TimeUtils().getCurrentMonth(),
                    TimeUtils().getCurrentDate(),
                    TimeUtils().getCurrentHour(),
                    TimeUtils().getCurrentMinute(),
                    spentValue,
                    shop!!
                )

                expenseViewModel.saveExpense(expense)
                showToast("expense ${expense.description} saved successfully ")
                dismiss()
            }
        }

        binding!!.btnCloseExpenseValues.setOnClickListener {
            dismiss()
        }

    }

    private fun inputsAreOk(): Boolean {
        val description =
            binding!!.inputExpenseDescription.text.trim().toString().lowercase(Locale.ROOT)
        val spentValue = binding!!.inputExpenseValue.text.trim().toString()
        if (description == "") {
            binding!!.inputExpenseDescription.error = "description can't be empty"
            return false
        } else if (spentValue == "") {
            binding!!.inputExpenseValue.error = "value can't be empty"
            return false
        } else {
            return true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}