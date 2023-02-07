package com.example.stockmarketapp.presentation.companyinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketapp.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.stockmarketapp.util.Result
import javax.inject.Inject


@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val repository: StockRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    //TODO category, site 도 이렇게 async 하면 될 것 같다.
    init {
        viewModelScope.launch {
            // navigation 을 통해 전달 받은 값
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)

            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            val intradayInfoResult = async { repository.getIntradayInfo(symbol) }

            when (val result = companyInfoResult.await()) {
                is Result.Success -> {
                    state = state.copy(
                        company = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message,
                        company = null
                    )
                }
                is Result.Loading -> Unit
            }

            when (val result = intradayInfoResult.await()) {
                is Result.Success -> {
                    state = state.copy(
                        stockInfos = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message,
                        company = null
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }
}