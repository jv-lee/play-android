package com.lee.playandroid.me

import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.extensions.delayBackEvent
import com.lee.playandroid.me.databinding.FragmentMeBinding

/**
 * @author jv.lee
 * @data 2021/11/2
 * @description
 */
class MeFragment : BaseFragment(R.layout.fragment_me) {

    private val binding by binding(FragmentMeBinding::bind)

    override fun bindView() {
        delayBackEvent()
    }

    override fun bindData() {

    }
}