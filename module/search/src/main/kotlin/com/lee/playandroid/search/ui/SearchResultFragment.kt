package com.lee.playandroid.search.ui

import com.lee.library.base.BaseFragment
import com.lee.library.extensions.arguments
import com.lee.library.extensions.binding
import com.lee.playandroid.search.R
import com.lee.playandroid.search.constants.Constants
import com.lee.playandroid.search.databinding.FragmentSearchResultBinding

/**
 * @author jv.lee
 * @date 2021/11/22
 * @description
 */
class SearchResultFragment : BaseFragment(R.layout.fragment_search_result) {

    private val searchKey by arguments<String>(Constants.ARG_PARAMS_SEARCH_KEY)

    private val binding by binding(FragmentSearchResultBinding::bind)

    override fun bindView() {
        binding.tvKey.text = searchKey
    }

    override fun bindData() {

    }
}