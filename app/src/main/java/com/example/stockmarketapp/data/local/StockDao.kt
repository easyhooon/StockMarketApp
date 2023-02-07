package com.example.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )

    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListings()

    // name, symbol 은 column 이기 때문에 : 를 붙히지 않음
    @Query(
        """
            SELECT *
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListings(query: String): List<CompanyListingEntity>
    // ex) 검색어로 tEs 를 입력 -> Like '%' || Lower(:query) || '%' (양쪽에 % 를 붙히고 소문자화)
    // -> $tes% -> LIKE -> name 이 tes 를 포함 하는 경우
    // or query 가 symbol 과 같은 경우
}