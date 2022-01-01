package com.lee.playandroid

import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.banBackEvent
import com.lee.library.extensions.binding
import com.lee.playandroid.databinding.FragmentSplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author jv.lee
 * @date 2021/12/31
 * @description
 */
class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val binding by binding(FragmentSplashBinding::bind)

    private lateinit var backCallback: OnBackPressedCallback

    override fun bindView() {
        backCallback = requireActivity().banBackEvent()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            backCallback.remove()
            findNavController().popBackStack()
        }
    }

    override fun bindData() {

    }
}