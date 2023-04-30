package com.edimitre.handyapp.data.service


import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.Note

import javax.inject.Inject

class NoteService @Inject constructor(private val noteDao: ReminderNotesDao) {


//    var allNotes = noteDao.getAllNotesLiveData()

    suspend fun saveNote(note: Note) {
        noteDao.save(note)

    }

    suspend fun deleteNote(note: Note) {

        noteDao.delete(note)
    }


    fun getAllNotesPaged(): PagingSource<Int, Note>? {

        return noteDao.getAllNotesPaged()
    }

    fun getNotesByContentPaged(content: String): PagingSource<Int, Note>? {
        return noteDao.getNotesByContentPaged(content)
    }

}