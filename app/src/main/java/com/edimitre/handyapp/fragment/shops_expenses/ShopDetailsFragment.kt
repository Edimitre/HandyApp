package com.edimitre.handyapp.fragment.shops_expenses

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.adapters.recycler_adapter.ExpenseAdapter
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.view_model.ExpenseViewModel
import com.edimitre.handyapp.databinding.FragmentShopDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopDetailsFragment : BottomSheetDialogFragment(), ExpenseAdapter.OnExpenseClickListener {

    private lateinit var myAdapter: ExpenseAdapter

    private lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var binding: FragmentShopDetailsBinding


    private var shop: Shop? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShopDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment()
    }

    private fun initFragment() {
        initViewModel()

        initAdapterAndRecyclerView()

        getSelectedShop()

        showShopExpenses(shop!!)
    }

    private fun initViewModel() {
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
    }


    private fun initAdapterAndRecyclerView() {

        myAdapter = ExpenseAdapter(this)
        binding.shopExpensesRecyclerView.setHasFixedSize(true)
        binding.shopExpensesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.shopExpensesRecyclerView.adapter = myAdapter


    }

    private fun getSelectedShop() {
        val bundle = arguments
        shop = bundle!!.getSerializable("shop") as Shop

        binding.nameOfShopText.text = shop!!.shop_name
    }


    private fun showShopExpenses(shop: Shop) {
        viewLifecycleOwner.lifecycleScope.launch {
            expenseViewModel.getAllExpensesByShopName(shop.shop_name).collectLatest {
                myAdapter.submitData(it)

            }
        }

        setSpentValueByShopName(shop.shop_name)
    }


    @SuppressLint("SetTextI18n")
    private fun setSpentValueByShopName(shopName: String) {

        expenseViewModel.getValueOfExpensesByShopName(shopName)!!
            .observe(viewLifecycleOwner) {
                binding.valueSpentOnShopText.text = "Total value spent on $shopName is : $it"
            }

        setNrOfExpensesByShopName(shopName)
    }

    @SuppressLint("SetTextI18n")
    private fun setNrOfExpensesByShopName(shopName: String) {

        expenseViewModel.getNrOfExpensesByShopName(shopName)!!.observe(viewLifecycleOwner) {
            binding.nrOfExpensesSpentOnShopText.text = "Nr of expenses is $it"
        }

    }

    override fun onExpenseClick(expense: Expense) {
        Log.e(HandyAppEnvironment.TAG, "onExpenseClick:${expense.shop.shop_name} ")
    }


}