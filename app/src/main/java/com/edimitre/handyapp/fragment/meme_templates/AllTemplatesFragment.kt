package com.edimitre.handyapp.fragment.meme_templates

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.recycler_adapter.MemeTemplateAdapter
import com.edimitre.handyapp.data.model.MemeTemplate
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.view_model.MemeTemplateViewModel
import com.edimitre.handyapp.databinding.FragmentAllTemplatesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AllTemplatesFragment : Fragment(), MemeTemplateAdapter.OnTemplateOpenListener {

    private lateinit var myAdapter: MemeTemplateAdapter

    private val _memeTemplateViewModel: MemeTemplateViewModel by activityViewModels()


    private lateinit var itemTouchHelper: ItemTouchHelper

    lateinit var binding: FragmentAllTemplatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllTemplatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapterAndRecyclerView()

        initToolbar()

        showAllTemplates()

        enableTouchFunctions()
    }


    private fun initAdapterAndRecyclerView() {

        myAdapter = MemeTemplateAdapter(this)

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.templatesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myAdapter

            addItemDecoration(dividerItemDecoration)

        }
    }

    private fun initToolbar() {

        binding.templatesToolbar.inflateMenu(R.menu.toolbar_menu)


        val btnPickDate = binding.templatesToolbar.menu.findItem(R.id.btn_calendar_pick)
        btnPickDate.isVisible = false

        val btnCloseSearch = binding.templatesToolbar.menu.findItem(R.id.btn_close_date_search)
        btnCloseSearch.isVisible = false

        val settingButton = binding.templatesToolbar.menu.findItem(R.id.btn_settings)
        settingButton.isVisible = false

        val search = binding.templatesToolbar.menu.findItem(R.id.btn_search_db)

        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled = true



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showAllTemplatesByName(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                showAllTemplatesByName(newText)
                return false
            }
        })
    }

    private fun showAllTemplates() {

        lifecycleScope.launch {
            _memeTemplateViewModel.getAllTemplatesPaged().collectLatest {
                myAdapter.submitData(it)
            }
        }

    }

    private fun showAllTemplatesByName(name: String) {

        lifecycleScope.launch {
            _memeTemplateViewModel.getAllTemplatesByName(name).collectLatest {
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

                    val memeTemplate =
                        myAdapter.getTemplateByPos(viewHolder.absoluteAdapterPosition)

                    openDeleteDialog(memeTemplate!!, viewHolder.absoluteAdapterPosition)

                }
            })

        itemTouchHelper.attachToRecyclerView(binding.templatesRecyclerView)
    }


    private fun openDeleteDialog(memeTemplate: MemeTemplate, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "are you sure you want to delete ${memeTemplate.name} \n" +
                    "this action can't be undone"

        )
        dialog.setPositiveButton("Delete") { _, _ ->


            _memeTemplateViewModel.deleteMemeTemplate(memeTemplate)
            myAdapter.notifyItemChanged(pos)


        }

        dialog.setNegativeButton("Close") { _, _ ->


        }


        dialog.setOnDismissListener {

            myAdapter.notifyItemChanged(pos)
        }


        dialog.show()
    }

    override fun onTemplateOpen(memeTemplate: MemeTemplate) {

        val bitmap = FileService().getBitmapFromBase64(memeTemplate.imgBase64)

        FileService().createTempFile(bitmap!!)

        val file = FileService().getTempFile()
        
        val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file!!)
        
        val mime = MimeTypeMap.getSingleton()
        val ext: String = file.name.substring(file.name.lastIndexOf(".") + 1)
        val type = mime.getMimeTypeFromExtension(ext)
        val sharingIntent = Intent("android.intent.action.SEND")
        sharingIntent.type = type
        sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sharingIntent.putExtra("android.intent.extra.STREAM", uri)

        activity.let {
            it!!.startActivity(Intent.createChooser(sharingIntent, "open image"))
        }


    }


}