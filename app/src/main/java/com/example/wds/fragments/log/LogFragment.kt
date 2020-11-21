package com.example.wds.fragments.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wds.R
import com.example.wds.entry.EntryViewModel

class LogFragment : Fragment(R.layout.fragment_log) {
    private lateinit var entryViewModel: EntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        println("DEBUG LogFragment")
        val root = inflater.inflate(R.layout.fragment_log,container,false)
        val adapter = LogListAdapter()
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_log)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        entryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        entryViewModel.allEntries.observe(viewLifecycleOwner, { entries ->
            entries?.let { adapter.setEntries(it) }
        })
        return root
    }
}