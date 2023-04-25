package com.edimitre.handyapp.fragment.work_related

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.adapters.recycler_adapter.ObjectFileAdapter
import com.edimitre.handyapp.data.model.FileObject
import com.edimitre.handyapp.data.view_model.FilesViewModel
import com.edimitre.handyapp.databinding.FragmentFilesBinding
import java.io.File
import java.net.URLConnection


class FilesFragment : Fragment(), ObjectFileAdapter.OnObjectFileClickListener {


    private lateinit var myAdapter: ObjectFileAdapter

    private lateinit var _fileViewModel: FilesViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper


    private lateinit var binding: FragmentFilesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initAdapterAndRecyclerView()
    }

    private fun initViewModel() {

        _fileViewModel = ViewModelProvider(this)[FilesViewModel::class.java]
    }

    private fun initAdapterAndRecyclerView() {


        myAdapter = ObjectFileAdapter(_fileViewModel.getAllFiles(), this)

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)


        binding.filesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())

            adapter = myAdapter

            addItemDecoration(dividerItemDecoration)

        }


    }

    override fun onFileShareClicked(fileObject: FileObject) {

        shareOnOtherApp(fileObject.actualFile!!)
    }

    private fun shareOnOtherApp(file: File) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND

            val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)
            putExtra(Intent.EXTRA_STREAM, uri)
            type = requireContext().contentResolver.getType(uri)
        }

        activity.let {
            it!!.startActivity(sendIntent)
        }

    }




}