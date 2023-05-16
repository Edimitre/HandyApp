package com.edimitre.handyapp.adapters.recycler_adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.MemeTemplate
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.service.FileService
import com.google.android.material.card.MaterialCardView
import java.io.File


class MemeTemplateAdapter(private val templateOpenListener:OnTemplateOpenListener) :
    PagingDataAdapter<MemeTemplate, MemeTemplateAdapter.MemeTemplateViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeTemplateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meme_template, parent, false)
        return MemeTemplateViewHolder(view)

    }


    override fun onBindViewHolder(holder: MemeTemplateViewHolder, position: Int) {
        holder.bind(getItem(position)!!, templateOpenListener)

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<MemeTemplate>() {

        override fun areItemsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate): Boolean {
            return oldItem == newItem
        }

    }

    class MemeTemplateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        private val templateImg: ImageView = itemView.findViewById(R.id.template_img_view)
        private val templateName: TextView = itemView.findViewById(R.id.template_name_view)
        private val btnOpenTemplate:MaterialCardView = itemView.findViewById(R.id.btn_open_template)

        fun bind(memeTemplate: MemeTemplate,onTemplateOpenListener: OnTemplateOpenListener) {

            val fileService = FileService()
            val bitmap = fileService.getBitmapFromBase64(memeTemplate.imgBase64)
            templateImg.setImageBitmap(bitmap)

            templateName.text = memeTemplate.name


            btnOpenTemplate.setOnClickListener {

                onTemplateOpenListener.onTemplateOpen(memeTemplate)
            }

        }
    }

    fun getTemplateByPos(pos: Int): MemeTemplate? {
        return getItem(pos)
    }



    interface OnTemplateOpenListener {
        fun onTemplateOpen(memeTemplate: MemeTemplate)
    }

}