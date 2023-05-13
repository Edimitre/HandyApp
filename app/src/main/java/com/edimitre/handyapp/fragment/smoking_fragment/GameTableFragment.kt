package com.edimitre.handyapp.fragment.smoking_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.view_model.CigaretteViewModel
import com.edimitre.handyapp.databinding.FragmentGameTableBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameTableFragment : Fragment() {


    lateinit var binding:FragmentGameTableBinding

    private val _cigarViewModel: CigaretteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameTableBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeGameTable()

        setListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun observeGameTable(){

        _cigarViewModel.gameTable.observe(viewLifecycleOwner){

            if (it?.pointsWon != null){
                binding.winPoints.text = "Points won ${it.pointsWon}"
            }else{

                binding.winPoints.text = "No Points"

            }


            if (it?.pointsLose != null){
                binding.losePoints.text = "Points lose ${it.pointsLose}"
            }else{

                binding.losePoints.text = "No Points"

            }



            when (it?.isWinning) {
                true -> {
                    binding.winningText.text = "WINNING"
                }
                false -> {

                    binding.winningText.text = "LOSING"

                }
                else -> {

                    binding.winningText.text = "No Points"

                }
            }

        }
    }

    private fun setListeners(){

        binding.btnClearPoints.setOnClickListener {

            _cigarViewModel.clearGamePoints()
        }
    }
}