package com.edimitre.handyapp.fragment.smoking_fragment

import android.R
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.databinding.FragmentCigarsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CigarsFragment : Fragment(){

    private lateinit var listener: TimeDistanceSetListener

    private lateinit var binding: FragmentCigarsBinding

    lateinit var dropdown: Spinner

    private val items = arrayOf("","30m", "60m", "1h/30m", "2h","2h/30m","3h")

    lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCigarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSpinner()
    }


    private fun loadSpinner(){

        adapter = ArrayAdapter(requireActivity(), R.layout.simple_spinner_dropdown_item, items)
        dropdown = binding.timeDistanceSpinner
        
        dropdown.adapter = adapter
        
        dropdown.onItemSelectedListener

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {

                Log.e(TAG, "onItemSelected: ${items[position]}", )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                
            }
        }
    }

    


    interface TimeDistanceSetListener {
        fun onTimeDistanceSet(timeInMillis: Int)
    }

    //
    override fun onAttach(context: Context) {
        listener = try {
            context as TimeDistanceSetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }

}