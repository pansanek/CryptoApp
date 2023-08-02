package com.example.cryptoapp.data.workers

import android.content.Context
import androidx.work.*
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.network.ApiFactory
import kotlinx.coroutines.delay

class RefreshDataWorker(context:Context,workerParameters: WorkerParameters):
    CoroutineWorker(context,workerParameters) {
    private val coinInfoDao = AppDatabase.getInstance(context).coinPriceInfoDao()

    private val mapper = CoinMapper()

    private val apiService = ApiFactory.apiService
    override suspend fun doWork(): Result {
        while(true) {
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

    companion object{
        const val NAME ="RefreshDataWorker"


        fun makeRequest():OneTimeWorkRequest{
            return OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
        }
    }

}