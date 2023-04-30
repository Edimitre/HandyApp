package com.edimitre.handyapp.data.service

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.model.Shop
import javax.inject.Inject

class ShopService @Inject constructor(private val shopExpenseDao: ShopExpenseDao) {


    suspend fun saveShop(shop: Shop) {

        shopExpenseDao.saveOrUpdateShop(shop)
    }

    suspend fun deleteShop(shop: Shop) {

        shopExpenseDao.deleteShop(shop)
    }

    fun getTestShop(): LiveData<Shop>? {


        return shopExpenseDao.getTestShop()
    }

//    fun getShopById(id: Long): LiveData<Shop>? {
//
//        return shopExpenseDao.getShopById(id)
//    }

    fun getAllShops(): PagingSource<Int, Shop>? {


        return shopExpenseDao.getAllShops()
    }

    suspend fun deleteAllShops() {

        shopExpenseDao.deleteAllShops()
    }

    fun getAllShopsByNamePaged(name: String): PagingSource<Int, Shop>? {


        return shopExpenseDao.getByNamePaged(name)
    }


}