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


class ObjectFileAdapter(private val onFileClickListener: OnObjectFileClickListener) :
    RecyclerView.Adapter<ObjectFileAdapter.FileObjectViewHolder>() {

    var fileList:List<FileObject> = ArrayList<FileObject>()

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
        private val openButton: CardView = itemView.findViewById(R.id.button_open)


        fun bind(fileObject: FileObject, onFileClickListener: OnObjectFileClickListener) {

            fileName.text = fileObject.name

            shareButton.setOnClickListener {
                onFileClickListener.onFileShareClicked(fileObject)
            }


            openButton.setOnClickListener {
                onFileClickListener.onFileOpenClicked(fileObject)
            }
        }
    }


    fun setContent(listFile:List<FileObject>){

        this.fileList = listFile

    }

    private fun getFileByPos(pos: Int): FileObject {
        return fileList[pos]
    }


    override fun getItemCount(): Int {
        return fileList.size
    }

    interface OnObjectFileClickListener {
        fun onFileShareClicked(fileObject: FileObject)

        fun onFileOpenClicked(fileObject: FileObject)
    }


}