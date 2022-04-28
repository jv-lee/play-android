package com.lee.playandroid.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.lee.library.cache.CacheManager
import com.lee.library.extensions.getCache
import com.lee.library.extensions.putCache
import com.lee.library.viewstate.uiState
import com.lee.library.viewmodel.CoroutineViewModel
import com.lee.library.viewstate.UiState
import com.lee.playandroid.library.common.entity.Data
import com.lee.playandroid.library.common.entity.ParentTab
import com.lee.playandroid.library.common.extensions.checkData
import com.lee.playandroid.library.common.extensions.createApi
import com.lee.playandroid.search.model.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.http.GET

/**
 * @author jv.lee
 * @date 2022/2/14
 * @description
 */
@Deprecated("不好用")
class TestFlowViewModel : CoroutineViewModel() {

    private val api = createApi<ApiService>()

    private val cacheManager = CacheManager.getDefault()

    private val _parentTabFlow = MutableStateFlow<UiState>(UiState.Default)
    val parentTabFlow: StateFlow<UiState> = _parentTabFlow.asStateFlow()

    /**
     * retrofit api fun
     */
    @GET("tree/json")
    fun getParentTabFlow(): Flow<Data<List<ParentTab>>> {
        return flow { }
    }

    // init时获取导航parentTab数据 flow方式
    val parentTabFlow2 = getParentTabFlow()
        // 请求响应数据校验
        .map { response ->
            response.checkData().filter { it.children.isNotEmpty() }
        }
        // 校验完毕后数据缓存处理
        .map { response ->
            response.also {
                cacheManager.putCache("CACHE_KEY_SYSTEM_CONTENT", it)
            }
        }
        // 请求开始时先获取缓存数据填充
        .onStart {
            cacheManager.getCache<List<ParentTab>>("CACHE_KEY_SYSTEM_CONTENT")?.let {
                emit(it)
            }
        }
        .uiState()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Default)

    fun requestParentTab() {
        launchIO {
            getParentTabFlow()
                // 请求响应数据校验
                .map { response ->
                    response.checkData().filter { it.children.isNotEmpty() }
                }
                // 校验完毕后数据缓存处理
                .map { response ->
                    response.also {
                        cacheManager.putCache("CACHE_KEY_SYSTEM_CONTENT", it)
                    }
                }
                // 请求开始时先获取缓存数据填充
                .onStart {
                    cacheManager.getCache<List<ParentTab>>("CACHE_KEY_SYSTEM_CONTENT")?.let {
                        emit(it)
                    }
                }
                .uiState()
                .collect { response ->
                    _parentTabFlow.update { response }
                }
        }
    }
}