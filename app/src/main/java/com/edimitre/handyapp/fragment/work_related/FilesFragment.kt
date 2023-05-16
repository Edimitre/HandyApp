package com.edimitre.handyapp.fragment.work_related

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.edimitre.handyapp.adapters.recycler_adapter.ObjectFileAdapter
import com.edimitre.handyapp.data.model.FileObject
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.FilesViewModel
import com.edimitre.handyapp.databinding.FragmentFilesBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class FilesFragment : Fragment(), ObjectFileAdapter.OnObjectFileClickListener {


    private lateinit var myAdapter: ObjectFileAdapter

    private val _fileViewModel: FilesViewModel by activityViewModels()

    private lateinit var binding: FragmentFilesBinding

    @Inject
    lateinit var systemService: SystemService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initAdapterAndRecyclerView()

        setListeners()
    }

    private fun setListeners() {

        if (!TimeUtils().isFriday()) {
            binding.btnGenerateFile.visibility = View.GONE
        }else{
            binding.btnGenerateFile.visibility = View.VISIBLE
        }
        binding.btnGenerateFile.setOnClickListener {
            createXlsFile()
        }
    }


    private fun initAdapterAndRecyclerView() {


        myAdapter = ObjectFileAdapter(this)
        myAdapter.setContent(_fileViewModel.getAllFiles())

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

    override fun onFileOpenClicked(fileObject: FileObject) {

        openOnOtherApp(fileObject.actualFile!!)
    }

    private fun shareOnOtherApp(file: File) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND

            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                file
            )
            putExtra(Intent.EXTRA_STREAM, uri)
            type = requireContext().contentResolver.getType(uri)
        }

        activity.let {
            it!!.startActivity(sendIntent)
        }

    }


    private fun openOnOtherApp(file: File) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_VIEW

            val myMime: MimeTypeMap = MimeTypeMap.getSingleton()
            val fileMime = myMime.getMimeTypeFromExtension(file.extension).toString()
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                file
            )
            setDataAndType(uri, fileMime)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        activity.let {
            it!!.startActivity(sendIntent)
        }

    }

    private fun createXlsFile() {


        val uuid = systemService.startCreateFileWorker()

        setLoading(true)

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(uuid)
            .observe(viewLifecycleOwner) {

                if (it.state.name === "SUCCEEDED") {

                    val list = _fileViewModel.getAllFiles()
                    myAdapter.setContent(list)
                    myAdapter.notifyDataSetChanged()

                    setLoading(false)
                }
            }

    }


    private fun setLoading(value: Boolean) {
        _fileViewModel.setIsFilesFragmentRefreshing(value)
    }

}