package com.edimitre.handyapp.fragment.shops_expenses

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.recycler_adapter.ShopAdapter
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.view_model.ShopsViewModel
import com.edimitre.handyapp.databinding.SelectShopFormBinding


import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectShopForm : BottomSheetDialogFragment(), ShopAdapter.OnShopClickListener {

    private lateinit var listener: ShopSelectedListener

    private lateinit var myAdapter: ShopAdapter

    private lateinit var _shopViewModel: ShopsViewModel

    private var binding: SelectShopFormBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectShopFormBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        initAdapterAndRecyclerView()

        initToolbar()


        showPagedShops()
    }

    private fun initToolbar() {

        binding!!.myToolbar.inflateMenu(R.menu.toolbar_menu)



        val settingButton = binding!!.myToolbar.menu.findItem(R.id.btn_settings)
        settingButton.isVisible = false

        val search = binding!!.myToolbar.menu.findItem(R.id.btn_search_db)

        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled = true

        val btnPickDate = binding!!.myToolbar.menu.findItem(R.id.btn_calendar_pick)
        btnPickDate.isVisible = false

        val btnCloseSearch = binding!!.myToolbar.menu.findItem(R.id.btn_close_date_search)
        btnCloseSearch.isVisible = false



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showPagedShopsByName(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                showPagedShopsByName(newText)
                return false
            }
        })
    }

    private fun initViewModel() {
        _shopViewModel = ViewModelProvider(this)[ShopsViewModel::class.java]

    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = ShopAdapter(this)
        binding!!.selectShopRecyclerView.setHasFixedSize(true)
        binding!!.selectShopRecyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.selectShopRecyclerView.adapter = myAdapter


    }


    private fun showPagedShops() {

        lifecycleScope.launch {
            _shopViewModel.getAllShopsPaged().collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun showPagedShopsByName(name: String) {

        lifecycleScope.launch {
            _shopViewModel.getAllShopsByNamePaged(name).collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    // comes from shop adapter
    override fun onShopClicked(shop: Shop) {

        Log.e(HandyAppEnvironment.TAG, "shop to send  $shop ", )
        listener.addShopToExpense(shop)
        dismiss()

    }

    interface ShopSelectedListener {
        fun addShopToExpense(shop: Shop)
    }
//
    override fun onAttach(context: Context) {
        listener = try {
            context as ShopSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }


}