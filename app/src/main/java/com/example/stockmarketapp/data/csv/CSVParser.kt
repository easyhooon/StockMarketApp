package com.example.stockmarketapp.data.csv

import java.io.InputStream

//TODO generic 의 대한 이해
interface CSVParser<T> {
    // suspend fun <T> parse(stream: InputStream): List<T>
    suspend fun parse(stream: InputStream): List<T>
}