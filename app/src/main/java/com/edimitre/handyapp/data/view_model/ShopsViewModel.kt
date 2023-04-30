package com.edimitre.handyapp.data.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.service.ShopService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(private val shopService: ShopService) :
    ViewModel() {



    fun saveShop(shop: Shop): Job = viewModelScope.launch {

        shopService.saveShop(shop)
    }

    fun deleteShop(shop: Shop): Job = viewModelScope.launch {

        shopService.deleteShop(shop)
    }

//    fun deleteAllShop(): Job = viewModelScope.launch {
//
//        shopService.deleteAllShops()
//    }

    fun getAllShopsPaged(): Flow<PagingData<Shop>> {


        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 50,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { shopService.getAllShops()!! })
            .flow
            .cachedIn(viewModelScope)
    }


    fun getAllShopsByNamePaged(name: String): Flow<PagingData<Shop>> {


        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { shopService.getAllShopsByNamePaged(name)!! })
            .flow
            .cachedIn(viewModelScope)
    }

}



