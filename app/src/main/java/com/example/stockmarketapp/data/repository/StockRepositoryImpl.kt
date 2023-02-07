package com.example.stockmarketapp.data.repository

import com.example.stockmarketapp.data.csv.CSVParser
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.mapper.toCompanyInfo
import com.example.stockmarketapp.data.mapper.toCompanyListing
import com.example.stockmarketapp.data.mapper.toCompanyListingEntity
import com.example.stockmarketapp.data.remote.StockApi
import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntradayInfo
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// local db 에 Flow<Result> 를 wrapping 한 예시
// paging 과 remoteMediator 를 사용 하지 않고 local caching SSOT 를 구현 하는 방법
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>,
    db: StockDatabase,
) : StockRepository {

    private val dao = db.dao

    //TODO internet error handling 참고
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Result<List<CompanyListing>>> {
        return flow {
            emit(Result.Loading(true))

            val localListings = dao.searchCompanyListings(query)

            emit(Result.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            // 코드의 가독성을 높히기 위해 조건부를 변수로 변수화
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Result.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                // response 가 csv 의 형태
                // SOLID 의 단일 책임 원칙을 만족 하려면 csv 파일을 파싱하는 작업은 다른 함수에서 진행 해야 함
                // 이 함수는 리스트를 가져 오는 역할 만을 수행 해야 함
                // val csvReader = CSVReader(InputStreamReader(response.byteStream()))
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                // SSOT 원칙을 준수 하기 위함
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Result.Success(
                    data = dao.searchCompanyListings("").map { it.toCompanyListing() }
                ))
                emit(Result.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Result<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Result.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(
                message = "Couldn't load intraday info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Result.Error(
                message = "Coundn't load intraday info "
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Result<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Result.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(
                message = "Couldn't load company info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Result.Error(
                message = "Coundn't load company info "
            )
        }
    }
}