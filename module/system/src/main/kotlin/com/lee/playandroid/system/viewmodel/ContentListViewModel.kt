package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.lee.library.mvvm.livedata.LoadStatus
import com.lee.library.mvvm.ui.UiStatePage
import com.lee.library.mvvm.ui.applyData
import com.lee.library.mvvm.ui.pageLaunch
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.system.model.repository.ApiRepository
import com.lee.playandroid.system.ui.ContentListFragment

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容 子内容列表 ViewModel
 */
class ContentListViewModel(handle: SavedStateHandle) : CoroutineViewModel() {

    private val id: Long by lazy { handle[ContentListFragment.ARG_PARAMS_ID] ?: 0 }

    private val repository = ApiRepository()

    private val _contentListLive = MutableLiveData<UiStatePage>(UiStatePage.Loading(0))
    val contentListLive: LiveData<UiStatePage> = _contentListLive

    fun requestContentList(@LoadStatus status: Int) {
        launchIO {
            _contentListLive.pageLaunch(status, { page ->
                applyData { repository.api.getContentDataAsync(page, id).checkData() }
            })
        }
    }

}