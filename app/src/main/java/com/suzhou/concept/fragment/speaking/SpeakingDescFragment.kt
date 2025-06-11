package com.suzhou.concept.fragment.speaking

import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.databinding.SpeakingDescLayoutBinding
import com.suzhou.concept.fragment.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SpeakingDescFragment : BaseFragment<SpeakingDescLayoutBinding>() {
    override fun SpeakingDescLayoutBinding.initBinding() {
        lifecycleScope.launch {
            speakingDesc.text = young.youngItem.first().name
        }
    }
}