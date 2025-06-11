package com.suzhou.concept.fragment.exercise

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.suzhou.concept.R
import com.suzhou.concept.adapter.PagerAdapter
import com.suzhou.concept.bean.PauseServiceVideoEvent
import com.suzhou.concept.databinding.ExerciseFragmentBinding
import com.suzhou.concept.fragment.BaseFragment
import org.greenrobot.eventbus.EventBus

/**
 * 练习界面
 */
class ExerciseFragment:BaseFragment<ExerciseFragmentBinding>() {
    private lateinit var mediator: TabLayoutMediator
    private val titleArray by lazy { resources.getStringArray(R.array.exercise_array) }

    override fun ExerciseFragmentBinding.initBinding() {
        val fragArray= with(mutableListOf<Fragment>()){
            add(MultipleExerciseFragment())
            add(StructureExerciseFragment())
            this
        }
        exercisePager.apply {
            offscreenPageLimit= fragArray.size
            adapter= PagerAdapter(requireActivity(),fragArray)
        }
        mediator= TabLayoutMediator(exerciseTab,exercisePager,true,true,tabConfigurationStrategy)
        mediator.attach()
        EventBus.getDefault().post(PauseServiceVideoEvent())

        exercise.getConceptExercise()
    }

    private val tabConfigurationStrategy= TabLayoutMediator.TabConfigurationStrategy { tab, position -> tab.text = titleArray[position] }

    override fun onDestroy() {
        super.onDestroy()

        if (::mediator.isInitialized){
            mediator.detach()
        }
    }
}