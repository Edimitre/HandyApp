package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.model.Reminder


@Dao
interface ReminderNotesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReminderOnThread(reminder: Reminder?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: Reminder?)

    @Query("SELECT * FROM reminder_table where isActive like 1 ORDER BY alarmTimeInMillis ASC LIMIT 1")
    fun getFirstReminderOnThread(): Reminder?

    @Query("SELECT * FROM reminder_table where isActive like 1 ORDER BY alarmTimeInMillis ASC LIMIT 1")
    suspend fun getFirstReminderOnCoroutine(): Reminder?

    @Query("SELECT * FROM reminder_table")
    fun getAllRemindersLiveData(): LiveData<List<Reminder>>?

    @Delete
    suspend fun deleteReminder(reminder: Reminder?)

    @Query("SELECT * FROM reminder_table where isActive like 1 ORDER BY alarmTimeInMillis ASC LIMIT 1")
    fun getFirstReminderLiveData(): LiveData<Reminder?>

    @Query("SELECT * FROM reminder_table")
    suspend fun getAllRemindersForBackUp(): List<Reminder>?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveNoteOnThread(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note_table WHERE id =:id")
    fun getByIdLiveData(id: Int): LiveData<Note>?

    @Query("SELECT * FROM note_table WHERE content LIKE '%' || :content || '%'")
    fun getByContentLiveData(content: String): LiveData<List<Note>>?

    @Query("SELECT * FROM note_table")
    fun getAllNotesLiveData(): LiveData<List<Note>>?

    @Query("SELECT * FROM note_table")
    suspend fun getAllNotesForBackUp(): List<Note>?


    @Query("SELECT * FROM note_table")
    fun getAllNotesPaged(): PagingSource<Int, Note>?

    @Query("SELECT * FROM note_table WHERE content LIKE '%' || :content || '%'")
    fun getNotesByContentPaged(content: String): PagingSource<Int, Note>?

}