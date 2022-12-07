package com.edimitre.handyapp.activity

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.adapters.tabs_adapter.ShopExpensesAdapter
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.view_model.ExpenseViewModel
import com.edimitre.handyapp.data.view_model.ShopsViewModel
import com.edimitre.handyapp.databinding.ActivityExpenseBinding
import com.edimitre.handyapp.fragment.shops_expenses.AddExpenseValuesForm
import com.edimitre.handyapp.fragment.shops_expenses.ExpensesFragment
import com.edimitre.handyapp.fragment.shops_expenses.SelectShopForm
import com.edimitre.handyapp.fragment.shops_expenses.ShopFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ShopsExpensesActivity : AppCompatActivity(), SelectShopForm.ShopSelectedListener {


    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: ShopExpensesAdapter

    private lateinit var viewPager: ViewPager2

    private lateinit var shopsViewModel: ShopsViewModel

    private lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var binding: ActivityExpenseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initActivity()

    }

    private fun initActivity() {
        initFragmentTabs()

        initViewModel()

        setButtonListeners()

    }

    private fun initFragmentTabs() {
        // get adapter
        pagerAdapter = ShopExpensesAdapter(getListOfFragments(), this)
        // get tabs
        tabs = binding.shopExpenseTabs

        // get viewPager
        viewPager = binding.viewPager

        // remove slide functionality
        viewPager.isUserInputEnabled = false
        // set view pager adapter
        viewPager.adapter = pagerAdapter


        //
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    private fun getListOfFragments(): ArrayList<Fragment> {
        return arrayListOf(
            ShopFragment(),
            ExpensesFragment(),

            )
    }

    private fun initViewModel() {

        shopsViewModel = ViewModelProvider(this)[ShopsViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

    }

    private fun setButtonListeners() {

        binding.btnAdd.setOnClickListener {


            if (viewPager.currentItem == 0) {
                openAddShopDialog()
            } else {
                openAddExpenseFormDialog()
            }
        }


    }

    private fun openAddExpenseFormDialog() {
        val selectShopForm = SelectShopForm()
        selectShopForm.show(supportFragmentManager, "add expense form")

    }

    private fun openAddShopDialog() {
        val inputName = EditText(this)
        inputName.hint = "Shop name"
        inputName.inputType = InputType.TYPE_CLASS_TEXT

        val dialog = MaterialAlertDialogBuilder(this)
//        dialog.background = ColorDrawable(resources.getColor(R.color.teal_700))
        dialog.setView(inputName)
        dialog.setTitle(HandyAppEnvironment.TITLE)

        dialog.setMessage(
            "Please set a name for the shop \n" +
                    "example :BIG MARKET, GAS STATION etc"
        )
        dialog.setPositiveButton("Add") { _, _ ->

            val name = inputName.text.toString().trim().uppercase(Locale.getDefault())

            when {
                name.isNotEmpty() -> {
                    val shop = Shop(0, name)
                    saveShop(shop)
                    showToast("${shop.shop_name} inserted successfully")
                }
                else -> {
                    showToast("Name can't be empty")
                }

            }

        }


        dialog.setNegativeButton("Close") { _, _ ->

        }
        dialog.show()
    }

    private fun saveShop(shop: Shop) {
        shopsViewModel.saveShop(shop)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

    override fun addShopToExpense(shop: Shop) {
        Log.e(HandyAppEnvironment.TAG, "shop selected $shop ")

        openAddExpenseValuesForm(shop)

    }

    private fun openAddExpenseValuesForm(shop: Shop) {
        val addExpenseValuesForm = AddExpenseValuesForm()

        val mBundle = Bundle()
        mBundle.putSerializable("shop", shop)

        addExpenseValuesForm.arguments = mBundle
        addExpenseValuesForm.show(supportFragmentManager, "add expense values")

    }


}