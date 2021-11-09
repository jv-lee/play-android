package com.lee.playandroid.project.ui

import android.os.Bundle
import com.lee.library.base.BaseFragment
import com.lee.library.extensions.binding
import com.lee.playandroid.project.R
import com.lee.playandroid.project.databinding.FragmentProjectListBinding

/**
 * @author jv.lee
 * @data 2021/11/9
 * @description
 */
class ProjectListFragment : BaseFragment(R.layout.fragment_project_list) {

    companion object {
        const val ARG_PARAMS_ID = "arg_params_id"

        fun newInstance(id: Long) = ProjectListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PARAMS_ID, id) }
        }
    }

    private val binding by binding(FragmentProjectListBinding::bind)

    override fun bindView() {

    }

    override fun bindData() {

    }
}