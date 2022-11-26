package com.edimitre.handyapp.fragment.reminder_and_notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.recycler_adapter.NoteAdapter
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.view_model.NoteViewModel
import com.edimitre.handyapp.databinding.FragmentNotesBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private lateinit var myAdapter: NoteAdapter

    private lateinit var _noteVieModel: NoteViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper


    private lateinit var binding: FragmentNotesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViewModel()

        initAdapterAndRecyclerView()

        initToolbar()

        showAllNotes()

        enableTouchFunctions()
    }


    private fun initViewModel() {

        _noteVieModel= ViewModelProvider(this)[NoteViewModel::class.java]
    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = NoteAdapter()

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.notesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myAdapter

            addItemDecoration(dividerItemDecoration)

        }
    }

    private fun initToolbar(){

        binding.nToolbar.inflateMenu(R.menu.toolbar_menu)



        val btnPickDate = binding.nToolbar.menu.findItem(R.id.btn_calendar_pick)
        btnPickDate.isVisible = false

        val btnCloseSearch = binding.nToolbar.menu.findItem(R.id.btn_close_date_search)
        btnCloseSearch.isVisible = false

        val settingButton = binding.nToolbar.menu.findItem(R.id.btn_settings)
        settingButton.isVisible = false

        val search = binding.nToolbar.menu.findItem(R.id.btn_search_db)

        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled = true



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showAllNotesByContent(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {

                showAllNotesByContent(newText)
                return false
            }
        })
    }


    private fun showAllNotes() {

        lifecycleScope.launch {
            _noteVieModel.getAllNotesPaged().collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun showAllNotesByContent(content:String) {

        lifecycleScope.launch {
            _noteVieModel.getAllNotesPagedByContent(content).collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun enableTouchFunctions() {
        itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val note = myAdapter.getNoteByPos(viewHolder.absoluteAdapterPosition)

                    openDeleteDialog(note!!, viewHolder.absoluteAdapterPosition)

                }
            })

        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerView)
    }


    private fun openDeleteDialog(note: Note, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "are you sure you want to delete ${note.content} \n" +
                    "this action can't be undone"

        )
        dialog.setPositiveButton("Delete") { _, _ ->


            _noteVieModel.deleteNote(note)
            myAdapter.notifyItemChanged(pos)


        }

        dialog.setNegativeButton("Close") { _, _ ->


        }


        dialog.setOnDismissListener {

            myAdapter.notifyItemChanged(pos)
        }


        dialog.show()
    }



}