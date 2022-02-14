package com.lee.playandroid.system.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.mvvm.ui.UiState
import com.lee.library.mvvm.ui.uiState
import com.lee.library.mvvm.ui.stateUpdate
import com.lee.library.mvvm.viewmodel.CoroutineViewModel
import com.lee.playandroid.library.common.entity.ParentTab
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.system.constants.Constants.CACHE_KEY_SYSTEM_CONTENT
import com.lee.playandroid.system.model.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * @author jv.lee
 * @date 2021/11/10
 * @description 体系内容列表 ViewModel
 */
class SystemContentViewModel : CoroutineViewModel() {

    private val cacheManager = CacheManager.getDefault()

    private val repository = ApiRepository()

    private val _parentTabFlow = MutableStateFlow<UiState>(UiState.Default)
    val parentTabFlow: StateFlow<UiState> = _parentTabFlow.asStateFlow()

    fun requestParentTabFlow() {
        launchIO {
            _parentTabFlow.stateUpdate({
                repository.api.getParentTabAsync().checkData().filter {
                    it.children.isNotEmpty()
                }
            }, {
                cacheManager.getCache(CACHE_KEY_SYSTEM_CONTENT)
            }, {
                cacheManager.putCache(CACHE_KEY_SYSTEM_CONTENT, it)
            })
        }
    }

    // init时获取导航parentTab数据 flow方式
    val parentTabFlow2 = repository.api.getParentTabFlow()
        // 请求响应数据校验
        .map { response ->
            response.checkData().filter { it.children.isNotEmpty() }
        }
        // 校验完毕后数据缓存处理
        .map { response ->
            response.also {
                cacheManager.putCache(CACHE_KEY_SYSTEM_CONTENT, it)
            }
        }
        // 请求开始时先获取缓存数据填充
        .onStart {
            cacheManager.getCache<List<ParentTab>>(CACHE_KEY_SYSTEM_CONTENT)?.let {
                emit(it)
            }
        }
        .uiState()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Default)

    fun requestParentTab() {
        launchIO {
            repository.api.getParentTabFlow()
                // 请求响应数据校验
                .map { response ->
                    response.checkData().filter { it.children.isNotEmpty() }
                }
                // 校验完毕后数据缓存处理
                .map { response ->
                    response.also {
                        cacheManager.putCache(CACHE_KEY_SYSTEM_CONTENT, it)
                    }
                }
                // 请求开始时先获取缓存数据填充
                .onStart {
                    cacheManager.getCache<List<ParentTab>>(CACHE_KEY_SYSTEM_CONTENT)?.let {
                        emit(it)
                    }
                }
                .uiState()
                .collect { response ->
                    _parentTabFlow.update { response }
                }
        }
    }

    init {
        requestParentTabFlow()
    }

}