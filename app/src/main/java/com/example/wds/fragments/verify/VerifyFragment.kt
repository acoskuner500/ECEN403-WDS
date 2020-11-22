package com.example.wds.fragments.verify

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.wds.R
import com.example.wds.entry.Entry
import com.example.wds.entry.EntryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyakaido.android.cardstackview.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class VerifyFragment : Fragment(R.layout.fragment_verify), CardStackListener {
    private lateinit var entryViewModel: EntryViewModel
    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter
    private lateinit var btnAdd : Button
    private lateinit var undoBtn : FloatingActionButton
    private lateinit var acceptBtn : FloatingActionButton
    private lateinit var rejectBtn : FloatingActionButton
    private lateinit var tvVerify : TextView
    private var cardStackList = ArrayList<Entry>()
    private val acceptList = ArrayList<Entry>()
    private val rejectList = ArrayList<Entry>()
    private val actionList = ArrayList<Boolean>()
    private var topEntry: Entry? = null

    companion object {
        private const val prefsKey = "prefsKey"
        private const val cardStackKey = "cardStackKey"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        entryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        loadData()
        initialize(view)
        setupButtons()
    }

    override fun onPause() {
        while (acceptList.size > 0) {
            entryViewModel.insert(acceptList[0])
            acceptList.removeAt(0)
        }
        acceptList.clear()
        rejectList.clear()
        actionList.clear()
        saveData()
        super.onPause()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
//        println("DEBUG onCardDragging: d=${direction?.name} ratio=$ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        println("DEBUG onCardSwiped: d = $direction")
        if (direction == Direction.Right) {
            actionList.add(0, true)
            topEntry?.let { acceptList.add(0, it) }
        }
        if (direction == Direction.Left) {
            actionList.add(0, false)
            topEntry?.let { rejectList.add(0, it) }
        }
        cardStackList.removeAt(0)
        adapter.notifyDataSetChanged()
        bgtvVisibility()
    }

    override fun onCardRewound() {
//        println("DEBUG onCardRewound: ${manager.topPosition}")
        bgtvVisibility()
    }

    override fun onCardCanceled() {
//        println("DEBUG onCardRewound: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View?, position: Int) {
//        println("DEBUG onCardAppeared: $position")
        topEntry = cardStackList[position]
        bgtvVisibility()
    }

    override fun onCardDisappeared(view: View?, position: Int) {
//        println("DEBUG disappeared")
    }

    private fun initialize(view: View) {
//        println("DEBUG initialize()")
        cardStackView = view.findViewById(R.id.card_stack_view)
        cardStackView.setHasFixedSize(true)
        manager = CardStackLayoutManager(context, this)
        adapter = CardStackAdapter(cardStackList)
        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(5)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
        btnAdd = view.findViewById(R.id.btnAdd)!!
        undoBtn = view.findViewById(R.id.undoBtn)!!
        acceptBtn = view.findViewById(R.id.acceptBtn)!!
        rejectBtn = view.findViewById(R.id.rejectBtn)!!
        tvVerify = view.findViewById(R.id.tvVerify)!!
        bgtvVisibility()
    }

    private fun setupButtons() {
//        println("DEBUG setupButtons()")
        btnAdd.setOnClickListener {
            cardStackList.add(randomEntry())
            adapter.notifyDataSetChanged()
        }

        undoBtn.setOnClickListener {
            if (actionList.size > 0) {
                println("DEBUG undoBtn clicked")
                manager.setRewindAnimationSetting(
                    RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(DecelerateInterpolator())
                        .build()
                )
                if (actionList[0]) {
                    cardStackList.add(0, acceptList[0])
                    acceptList.removeAt(0)
                } else {
                    cardStackList.add(0, rejectList[0])
                    rejectList.removeAt(0)
                }
                actionList.removeAt(0)
                cardStackView.rewind()
                adapter.notifyDataSetChanged()
            }
        }

        acceptBtn.setOnClickListener {
            if (cardStackList.size > 0) {
                println("DEBUG acceptBtn clicked")
                manager.setSwipeAnimationSetting(
                    SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator())
                        .build()
                )
                cardStackView.swipe()
            }
        }

        rejectBtn.setOnClickListener {
            if (cardStackList.size > 0) {
                println("DEBUG rejectBtn clicked")
                manager.setSwipeAnimationSetting(
                    SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator())
                        .build()
                )
                cardStackView.swipe()
            }
        }
    }

    // New Dummy Instance
    private fun randomEntry(): Entry {
//        println("DEBUG addNewEntry()")
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm:ss.SSS")
        val textTime = current.format(formatter)
        val num = Random.nextInt(1084)
        val imgSrc = "https://picsum.photos/400/300/?image=$num"
        val textAnimal = "Pic $num"
        return Entry(0, imgSrc, textAnimal, textTime)
    }

    private fun bgtvVisibility() {
        tvVerify.visibility = if (cardStackList.size == 0) View.VISIBLE else View.INVISIBLE
    }

    private fun saveData() {
        prefs().edit().apply {
            putString(cardStackKey, Gson().toJson(cardStackList))
        }.apply()
    }

    private fun loadData() {
        val json = prefs().getString(cardStackKey, null)
        val type = object : TypeToken<ArrayList<Entry>>() {}.type
        cardStackList = Gson().fromJson(json,type)
    }

    private fun prefs(): SharedPreferences {
        return requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
    }
}
