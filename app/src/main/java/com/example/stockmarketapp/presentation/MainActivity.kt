package com.example.stockmarketapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.stockmarketapp.presentation.ui.theme.StockMarketAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

// TODO compose-desinations 변경 사항 적용
// TODO 나나공에 많은 개선을 이뤄줄 프로젝트
// TODO 주석들 확인하여 나나공에도 적용
// side effect 관련은 borutoApp 과 calorieTracker 확인
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockMarketAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // TODO api 변경점 적용
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}