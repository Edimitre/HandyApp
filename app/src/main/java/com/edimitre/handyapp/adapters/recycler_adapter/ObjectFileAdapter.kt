package com.edimitre.handyapp.adapters.recycler_adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.FileObject
import kotlinx.coroutines.CoroutineScope


class ObjectFileAdapter(
    private val listFileObject: ArrayList<FileObject>,
    private val onFileClickListener: OnObjectFileClickListener
) :
    RecyclerView.Adapter<ObjectFileAdapter.FileObjectViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileObjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileObjectViewHolder(view)

    }


    override fun onBindViewHolder(holder: FileObjectViewHolder, position: Int) {
        holder.bind(getFileByPos(position), onFileClickListener)

    }


    class FileObjectViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        private val fileName: TextView = itemView.findViewById(R.id.file_name)
        private val shareButton: CardView = itemView.findViewById(R.id.button_share)

        fun bind(fileObject: FileObject, onFileClickListener: OnObjectFileClickListener) {

            fileName.text = fileObject.name

            shareButton.setOnClickListener {
                onFileClickListener.onFileShareClicked(fileObject)
            }


        }
    }

    private fun getFileByPos(pos: Int): FileObject {
        return listFileObject[pos]
    }


    override fun getItemCount(): Int {
        return listFileObject.size
    }

    interface OnObjectFileClickListener {
        fun onFileShareClicked(fileObject: FileObject)
    }


}