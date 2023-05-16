package com.edimitre.handyapp.fragment.meme_templates

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.model.MemeTemplate
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.view_model.MemeTemplateViewModel
import com.edimitre.handyapp.databinding.FragmentSelectTemplateBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectTemplateFragment : Fragment() {


    lateinit var binding: FragmentSelectTemplateBinding

    private val memeTemplateViewModel: MemeTemplateViewModel by activityViewModels()

    private var uri: Uri? = null
    private var croppedUri: Uri? = null
    private val mutableUriImgSelected: MutableLiveData<Uri> = MutableLiveData<Uri>(null)
    private val uriSelected: LiveData<Uri> get() = mutableUriImgSelected



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectTemplateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSelectedImage()

        observeCroppedImage()

        setListeners()
    }

    private fun setListeners() {

        binding.btnLoadTemplate.setOnClickListener {

            pickImageFromGallery()
        }

        binding.btnSaveTemplate.setOnClickListener {

            val uri = if (croppedUri != null) {
                croppedUri
            } else {
                this.uri
            }


            if (isFilenameOk()) {

                val bitmap = FileService().getBitmap(
                    requireContext().contentResolver,
                    uri
                )//imageUtil.getBitmapFromUri(this.uri!!)
                val croppedBitmap = FileService().getResizedBitmap(
                    bitmap!!,
                    640,
                    480
                )
                val string64Bitmap =
                    FileService().getBase64FromBitmap(croppedBitmap!!)


                val name = binding.inputTemplateName.text.toString().trim()
                val memeTemplate = MemeTemplate(0, name, string64Bitmap!!)
                memeTemplateViewModel.saveMemeTemplate(memeTemplate)

                FileService().clearTempFile()
//                Log.e(TAG, "base 64 from bitmap $string64Bitmap", )
            }
        }


        binding.btnCrop.setOnClickListener {

            startCrop()

        }
    }

    private fun observeSelectedImage() {

        uriSelected.observe(viewLifecycleOwner) {

            if (it != null) {
                this.uri = it
                setImage(this.uri!!)
            }

            showSaveOptions(this.uri)

        }
    }

    private fun setImage(uri: Uri) {

        binding.imgView.setImageURI(uri)
    }

    private fun pickImageFromGallery() {

        try {
            val intent = Intent(Intent.ACTION_PICK)
            intent.action = "image/*"

            startForResult.launch("image/*")

        } catch (e: Exception) {

            Log.e(TAG, "pickImageFromGallery: $e")
        }
    }

    private fun showSaveOptions(uri: Uri?) {
        if (uri != null) {
            binding.inputTemplateName.visibility = View.VISIBLE
            binding.btnSaveTemplate.visibility = View.VISIBLE
        } else {

            binding.inputTemplateName.visibility = View.INVISIBLE
            binding.btnSaveTemplate.visibility = View.INVISIBLE
        }
    }


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) {

            if (it != null) {
                setSelectedImageUri(it)
            } else {
                Toast.makeText(requireActivity(), "no image selected", Toast.LENGTH_SHORT).show()
            }

        }


    private fun startCrop() {

        val nextFrag = CropImageFragment()
        val args = Bundle()
        args.putString("uri", this.uri.toString())
        nextFrag.arguments = args
        nextFrag.show(parentFragmentManager, "crop_image")

    }


    private fun setSelectedImageUri(uri: Uri) {

        mutableUriImgSelected.value = uri
    }

    private fun isFilenameOk(): Boolean {

        return if (binding.inputTemplateName.text.toString().isEmpty()) {
            binding.inputTemplateName.error = "name can't be empty"
            false
        } else {
            true
        }
    }


    private fun observeCroppedImage() {
        memeTemplateViewModel.croppedImage?.observe(viewLifecycleOwner) {
            if (it != null) {
                croppedUri = FileService().getUriFromTempFile(it)
                setImage(croppedUri!!)
            }
        }
    }

}