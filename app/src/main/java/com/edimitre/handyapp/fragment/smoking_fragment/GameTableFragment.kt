package com.edimitre.handyapp.fragment.smoking_fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.view_model.CigaretteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameTableFragment : Fragment() {


    private val _cigarViewModel: CigaretteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_table, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeGameTable()
    }

    private fun observeGameTable(){

        _cigarViewModel.gameTable.observe(viewLifecycleOwner){

            Log.e(TAG, "points won : ${it?.pointsWon}", )
            Log.e(TAG, "points loose : ${it?.pointsLose}", )
            Log.e(TAG, "isWinning : ${it?.isWinning}", )
        }
    }

    // todo design this game table fragment
}