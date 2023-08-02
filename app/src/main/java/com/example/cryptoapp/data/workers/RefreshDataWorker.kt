package com.example.cryptoapp.data.workers

import android.content.Context
import androidx.work.*
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.database.CoinInfoDao
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.network.ApiFactory
import com.example.cryptoapp.data.network.ApiService
import kotlinx.coroutines.delay

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val coinInfoDao: CoinInfoDao,
    private val apiService: ApiService,
    private val mapper: CoinMapper,
):
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        while (true) {
            try {
                val topCoins = apiService.getTopCoinsInfo(limit = 50)
                val fSymbol = mapper.mapNamesListToString(topCoins)
                val jsonContainter = apiService.getFullPriceList(fSyms = fSymbol)
                val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainter)
                val dbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
                coinInfoDao.insertPriceList(dbModelList)
            } catch (e: Exception) {
            }
            delay(1000)
        }
    }

    companion object {
        const val NAME = "RefreshDataWorker"


        fun makeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
        }
    }

}