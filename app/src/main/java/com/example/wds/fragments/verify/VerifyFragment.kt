package com.example.wds.fragments.verify

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val cardStackList = ArrayList<Entry>()
    private val acceptList = ArrayList<Entry>()
    private val rejectList = ArrayList<Entry>()
    private val actionList = ArrayList<Boolean>()
    private var topEntry: Entry? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        println("DEBUG onCreateView()")
        val root = inflater.inflate(R.layout.fragment_verify, container, false)
        entryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        initialize(root)
        setupButtons()
        return root
    }

    override fun onPause() {
        while (acceptList.size > 0) {
            entryViewModel.insert(acceptList[0])
            acceptList.removeAt(0)
        }
        acceptList.clear()
        rejectList.clear()
        actionList.clear()
        super.onPause()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
//        println("DEBUG onCardDragging: d=${direction?.name} ratio=$ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        println("DEBUG onCardSwiped: d = $direction")
        if (direction == Direction.Right) {
//            accept()
            actionList.add(0, true)
            topEntry?.let { acceptList.add(0, it) }
            cardStackList.removeAt(0)
            adapter.notifyDataSetChanged()
        }
        if (direction == Direction.Left) {
//            reject()
            actionList.add(0, false)
            topEntry?.let { rejectList.add(0, it) }
            cardStackList.removeAt(0)
            adapter.notifyDataSetChanged()
        }
        bgtvVisibility()
    }

    override fun onCardRewound() {
//        println("DEBUG onCardRewound: ${manager.topPosition}")
//        if (actionList[0]) acceptList.removeAt(0)
//        else rejectList.removeAt(0)
//        actionList.removeAt(0)
//        bgtvVisibility()
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

    private fun initialize(root: View) {
//        println("DEBUG initialize()")
        cardStackView = root.findViewById(R.id.card_stack_view)
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
        btnAdd = root.findViewById(R.id.btnAdd)!!
        undoBtn = root.findViewById(R.id.undoBtn)!!
        acceptBtn = root.findViewById(R.id.acceptBtn)!!
        rejectBtn = root.findViewById(R.id.rejectBtn)!!
        tvVerify = root.findViewById(R.id.tvVerify)!!
        bgtvVisibility()
    }

    private fun setupButtons() {
//        println("DEBUG setupButtons()")
        btnAdd.setOnClickListener {
            cardStackList.add(addNewEntry())
        }

        undoBtn.setOnClickListener {
            if (actionList.size > 0) {
                println("DEBUG undoBtn clicked")
                val setting : RewindAnimationSetting
                if (actionList[0]) {
                    setting = RewindAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(DecelerateInterpolator())
                        .build()
                    cardStackList.add(0, acceptList[0])
                    acceptList.removeAt(0)
                    actionList.removeAt(0)
                } else {
                    setting = RewindAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(DecelerateInterpolator())
                        .build()
                    cardStackList.add(0, rejectList[0])
                    rejectList.removeAt(0)
                    actionList.removeAt(0)
                }
                manager.setRewindAnimationSetting(setting)
                cardStackView.rewind()
                adapter.notifyDataSetChanged()
            }
        }

        acceptBtn.setOnClickListener {
            if (cardStackList.size > 0) {
                println("DEBUG acceptBtn clicked")
//                accept()
//                actionList.add(0, true)
//                topEntry?.let { acceptList.add(0, it) }
//                cardStackList.removeAt(0)
//                adapter.notifyDataSetChanged()

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
//                reject()
//                actionList.add(0, false)
//                topEntry?.let { rejectList.add(0, it) }
//                cardStackList.removeAt(0)
//                adapter.notifyDataSetChanged()

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

//    private fun accept() {
//        println("DEBUG accept()")
//        actionList.add(0, true)
//        topEntry?.let { acceptList.add(0, it) }
//        cardStackList.removeAt(0)
//        adapter.notifyDataSetChanged()
//    }

//    private fun reject() {
//        println("DEBUG reject()")
//        actionList.add(0, false)
//        topEntry?.let { rejectList.add(0, it) }
//        cardStackList.removeAt(0)
//        adapter.notifyDataSetChanged()
//    }

    // New Dummy Instance
    private fun addNewEntry(): Entry {
//        println("DEBUG addNewEntry()")
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\nHH:mm:ss.SSS")
        val textTime = current.format(formatter)
        val num = Random.nextInt(1084)
        val imgSrc = "https://picsum.photos/400/300/?image=$num"
        val textAnimal = "Pic $num"
        adapter.notifyDataSetChanged()
        return Entry(0, imgSrc, textAnimal, textTime)
    }

    private fun bgtvVisibility() {
        tvVerify.visibility = if (cardStackList.size == 0) View.VISIBLE else View.INVISIBLE
    }
}
