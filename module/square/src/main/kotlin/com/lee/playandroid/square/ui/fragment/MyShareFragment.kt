package com.lee.playandroid.square.ui.fragment

import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.library.widget.toolbar.TitleToolbar
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentMyShareBinding

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 我的分享页面
 */
class MyShareFragment : BaseFragment(R.layout.fragment_my_share) {

    private val binding by binding(FragmentMyShareBinding::bind)

    override fun bindView() {
        binding.toolbar.setClickListener(object : TitleToolbar.ClickListener() {
            override fun moreClick() {
                findNavController().navigate(R.id.action_my_share_fragment_to_create_share_fragment)
            }
        })
    }

    override fun bindData() {

    }
}