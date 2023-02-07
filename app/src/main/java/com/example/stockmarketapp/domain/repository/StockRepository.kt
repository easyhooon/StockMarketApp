package com.example.stockmarketapp.domain.repository

import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntradayInfo
import com.example.stockmarketapp.util.Result
import kotlinx.coroutines.flow.Flow

// api 호출의 과정이 원샷이 아니기 때문에 flow 를 사용 (multiple result over period time)
// Loading(load, retrieve data from local cache) -> Success or Fail
interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Result<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol: String
    ): Result<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Result<CompanyInfo>
}