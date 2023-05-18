package com.edimitre.handyapp.data.service


import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.MemeTemplateDao
import com.edimitre.handyapp.data.model.MemeTemplate
import javax.inject.Inject

class MemeTemplateService @Inject constructor(private val memeTemplateDao: MemeTemplateDao) {


//    var allNotes = noteDao.getAllNotesLiveData()

    suspend fun saveMemeTemplate(memeTemplate: MemeTemplate) {
        memeTemplateDao.save(memeTemplate)

    }

    suspend fun deleteMemeTemplate(memeTemplate: MemeTemplate) {

        memeTemplateDao.delete(memeTemplate)
    }


    fun getAllMemeTemplatesPaged(): PagingSource<Int, MemeTemplate>? {

        return memeTemplateDao.getAllPaged()
    }

    fun getMemeTemplatesByName(name: String): PagingSource<Int, MemeTemplate>? {
        return memeTemplateDao.getByNamePaged(name)
    }

}