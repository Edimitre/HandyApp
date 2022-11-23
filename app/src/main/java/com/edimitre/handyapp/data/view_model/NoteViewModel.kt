package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.service.NoteService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(private val noteService: NoteService) : ViewModel() {


    fun saveNote(note: Note): Job = viewModelScope.launch {


        noteService.saveNote(note)

    }

    fun deleteNote(note: Note): Job = viewModelScope.launch {


        noteService.deleteNote(note)

    }


    fun getAllNotesPaged(): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { noteService.getAllNotesPaged()!! })
            .flow
            .cachedIn(viewModelScope)
    }

    fun getAllNotesPagedByContent(content: String): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { noteService.getNotesByContentPaged(content)!! })
            .flow
            .cachedIn(viewModelScope)
    }

}