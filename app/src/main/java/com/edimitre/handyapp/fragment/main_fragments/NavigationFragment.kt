package com.edimitre.handyapp.fragment.main_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.edimitre.handyapp.activity.NewsActivity
import com.edimitre.handyapp.activity.ReminderNotesActivity
import com.edimitre.handyapp.activity.ShopsExpensesActivity
import com.edimitre.handyapp.databinding.FragmentNavigationBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NavigationFragment : Fragment() {


    private lateinit var binding: FragmentNavigationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

    }


    private fun setListeners() {

        binding.expensesCard.setOnClickListener {

            activity?.let {
                it.startActivity(Intent(it, ShopsExpensesActivity::class.java))
            }
        }

        binding.remindersAndNotesCard.setOnClickListener {
            activity?.let {
                it.startActivity(Intent(it, ReminderNotesActivity::class.java))
            }
        }


        binding.newsCard.setOnClickListener {
            activity?.let {
                it.startActivity(Intent(it, NewsActivity::class.java))
            }
        }
    }


}