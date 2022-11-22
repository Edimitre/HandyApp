package com.edimitre.handyapp.fragment.shops_expenses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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

import com.edimitre.handyapp.databinding.FragmentShopBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopFragment : Fragment(),ShopAdapter.OnShopClickListener {

    private lateinit var myAdapter: ShopAdapter

    private lateinit var shopsViewModel: ShopsViewModel

    private lateinit var binding: FragmentShopBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment(){
        initViewModel()
        initToolbar()
        setToolbarItems()
        initAdapterAndRecyclerView()
        observeShops()
    }

    private fun initViewModel() {
        shopsViewModel = ViewModelProvider(this)[ShopsViewModel::class.java]
    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = ShopAdapter(this)
        binding.shopsRecyclerView.setHasFixedSize(true)
        binding.shopsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.shopsRecyclerView.adapter = myAdapter


    }

    private fun initToolbar() {

        binding.toolbar.inflateMenu(R.menu.toolbar_menu)


        val searchButton = binding.toolbar.menu.findItem(R.id.btn_search_db)

        val searchView = searchButton.actionView as SearchView
        searchView.isSubmitButtonEnabled = true



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

    private fun setToolbarItems(){

        val calendarButton = binding.toolbar.menu.findItem(R.id.btn_calendar_pick)
        val closeSearchButton = binding.toolbar.menu.findItem(R.id.btn_close_date_search)

        calendarButton.isVisible = false
        closeSearchButton.isVisible = false
    }

    private fun showPagedShopsByName(query: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            shopsViewModel.getAllShopsByNamePaged(query).collectLatest {
                myAdapter.submitData(it)
            }
        }
    }

    private fun observeShops(){
        viewLifecycleOwner.lifecycleScope.launch {
            shopsViewModel.getAllShopsPaged().collectLatest {
                myAdapter.submitData(it)
                Log.e(HandyAppEnvironment.TAG, "observeShops: ", )
            }
        }
    }

    override fun onShopClicked(shop: Shop) {
        Log.e(HandyAppEnvironment.TAG, "onShopClicked: $shop")
    }

}