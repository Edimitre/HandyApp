package com.edimitre.handyapp.fragment.meme_templates

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.canhub.cropper.CropImageView
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.view_model.MemeTemplateViewModel
import com.edimitre.handyapp.databinding.FragmentCropImageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CropImageFragment : BottomSheetDialogFragment(),CropImageView.OnCropImageCompleteListener {


    lateinit var binding: FragmentCropImageBinding

    private val memeTemplateViewModel: MemeTemplateViewModel by activityViewModels()

    var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCropImageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadImage()

        setListeners()

        setCropListener()

    }


    private fun setListeners() {

        binding.btnSaveCrop.setOnClickListener {

            save()

        }

        binding.btnRestoreCrop.setOnClickListener {

            restore()
        }

        binding.btnDismiss.setOnClickListener {
            dismiss()
        }
    }

    private fun loadImage() {

        binding.cropImageView.isShowProgressBar = true
        val args = arguments
        val mUri = args!!.getString("uri", "")
        this.uri = Uri.parse(mUri)


        binding.cropImageView.setImageUriAsync(this.uri)


    }

    private fun setCropListener() {


        binding.cropImageView.setOnCropWindowChangedListener {

            val croppedImage = binding.cropImageView.getCroppedImage(
                640,
                480,
                CropImageView.RequestSizeOptions.RESIZE_INSIDE
            )

            val uri = FileService().getUriFromTempFile(croppedImage!!)

            binding.cropImageView.setImageUriAsync(uri)
        }
    }


    private fun restore() {


        binding.cropImageView.setImageUriAsync(this.uri)
    }

    private fun save() {


        val croppedImage = binding.cropImageView.getCroppedImage(
            640,
            480,
            CropImageView.RequestSizeOptions.RESIZE_INSIDE
        )

        memeTemplateViewModel.setCroppedImage(croppedImage!!)

        dismiss()
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {

        Log.e(TAG, "onCropImageComplete: ", )

        result.bitmap
    }


}