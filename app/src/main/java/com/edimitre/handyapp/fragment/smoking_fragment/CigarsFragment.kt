package com.edimitre.handyapp.fragment.smoking_fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.adapters.recycler_adapter.CigaretteAdapter
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.CigaretteViewModel
import com.edimitre.handyapp.databinding.FragmentCigarsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CigarsFragment : Fragment(), CigaretteAdapter.OnCigarClickListener {

    private lateinit var myAdapter: CigaretteAdapter

    private val _cigarViewModel: CigaretteViewModel by activityViewModels()

    private lateinit var binding: FragmentCigarsBinding

    lateinit var dropdown: Spinner

    private val items = arrayOf("", "30m", "60m", "1h/30m", "2h", "2h/30m", "3h")

    lateinit var adapter: ArrayAdapter<String>

    @Inject
    lateinit var systemService:SystemService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCigarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapterAndRecyclerView()

        loadSpinner()

        showAllCigars()

        setListeners()
    }


    private fun loadSpinner() {

        adapter = ArrayAdapter(requireActivity(), R.layout.simple_spinner_dropdown_item, items)
        dropdown = binding.timeDistanceSpinner

        dropdown.adapter = adapter

        dropdown.onItemSelectedListener


    }

    private fun setListeners(){

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {

                val millisFromSelection = getMillisFromSelection(items[position])
                if (millisFromSelection != null) {
                    val minutesSelected = TimeUtils().getMinutesFromMillis(millisFromSelection)
                    _cigarViewModel.setSelectedTime(minutesSelected)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.btnClearCigars.setOnClickListener {
            _cigarViewModel.deleteAllCigars()
            systemService.cancelAllCigarAlarms()
            myAdapter.notifyDataSetChanged()
        }
    }

    private fun initAdapterAndRecyclerView() {

        myAdapter = CigaretteAdapter(this)

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.cigarsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myAdapter

            addItemDecoration(dividerItemDecoration)

        }
    }


    private fun showAllCigars() {


        _cigarViewModel.allCigars?.observe(viewLifecycleOwner) {
            myAdapter.setCigarList(it)
            myAdapter.notifyDataSetChanged()
        }


    }

    private fun getMillisFromSelection(selection: String): Long? {
        return when (selection) {

            "30m" -> {
                TimeUtils().getMillisFromMinutes(30)
            }
            "60m" -> {
                TimeUtils().getMillisFromMinutes(60)
            }
            "1h/30m" -> {
                TimeUtils().getMillisFromMinutes(90)
            }
            "2h" -> {
                TimeUtils().getMillisFromMinutes(120)
            }
            "2h/30m" -> {
                TimeUtils().getMillisFromMinutes(150)
            }
            "3h" -> {
                TimeUtils().getMillisFromMinutes(180)

            }
            else -> {
                null
            }
        }
    }


    override fun onCigarClicked(cigar: Cigar) {
        TODO("Not yet implemented")
    }

}