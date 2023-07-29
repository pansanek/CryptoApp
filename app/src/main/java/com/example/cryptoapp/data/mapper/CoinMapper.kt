package com.example.cryptoapp.data.mapper

import com.example.cryptoapp.data.database.CoinInfoDBModel
import com.example.cryptoapp.data.network.model.CoinInfoDto
import com.example.cryptoapp.data.network.model.CoinInfoJsonContainterDto
import com.example.cryptoapp.data.network.model.CoinNamesListDto
import com.example.cryptoapp.domain.CoinInfo
import com.google.gson.Gson
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CoinMapper {


    fun mapDtoToDbModel(dto: CoinInfoDto) = CoinInfoDBModel(
        dto.fromSymbol,
        dto.toSymbol,
        dto.price,
        dto.lastUpdate,
        dto.highDay,
        dto.lowDay,
        dto.lastMarket,
        dto.imageUrl
    )

    fun mapJsonContainerToListCoinInfo(jsonContainter: CoinInfoJsonContainterDto):List<CoinInfoDto>{
        val result = mutableListOf<CoinInfoDto>()
        val jsonObject = jsonContainter.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinInfoDto::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    fun mapNamesListToString(namesList: CoinNamesListDto):String{
        return namesList.names?.map { it.coinName?.name }?.joinToString(",") ?:""
    }

    fun mapDbModelToEntity(dbModel: CoinInfoDBModel)=CoinInfo(
        dbModel.fromSymbol,
        dbModel.toSymbol,
        dbModel.price,
        convertTimestampToTime(dbModel.lastUpdate),
        dbModel.highDay,
        dbModel.lowDay,
        dbModel.lastMarket,
        BASE_IMAGE_URL+dbModel.imageUrl
    )


    private fun convertTimestampToTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val stamp = Timestamp(timestamp * 1000)
        val date = Date(stamp.time)
        val pattern = "HH:mm:ss"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    companion object{
        const val BASE_IMAGE_URL = "https://cryptocompare.com"
    }
}