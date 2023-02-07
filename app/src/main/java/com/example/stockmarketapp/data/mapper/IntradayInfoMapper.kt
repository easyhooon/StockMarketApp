package com.example.stockmarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.example.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// TODO mapper 의 진정한 역할..
// 이런 방법으로 lesson site 와 category 를 변환할 수 있겠다!
@RequiresApi(Build.VERSION_CODES.O)
fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}