package com.edimitre.handyapp.adapters.recycler_adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.Cigar


class CigaretteAdapter(private val onFileClickListener: OnCigarClickListener) :
    RecyclerView.Adapter<CigaretteAdapter.CigarViewHolder>() {

    private var cigarList: List<Cigar> = ArrayList<Cigar>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CigarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cigar, parent, false)
        return CigarViewHolder(view)

    }


    override fun onBindViewHolder(holder: CigarViewHolder, position: Int) {
        holder.bind(getCigarByPos(position), onFileClickListener)

    }


    class CigarViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        private val cigarText: TextView = itemView.findViewById(R.id.cigar_text)


        fun bind(cigar: Cigar, onCigarClickListener: OnCigarClickListener) {

            cigarText.text = cigar.id.toString()

        }
    }


    fun setCigarList(listCigars: List<Cigar>) {

        this.cigarList = listCigars

    }

    private fun getCigarByPos(pos: Int): Cigar {
        return cigarList[pos]
    }


    override fun getItemCount(): Int {
        return cigarList.size
    }

    interface OnCigarClickListener {
        fun onCigarClicked(cigar: Cigar)

    }


}