package com.rentify.local

data class ChartFLowModel(
    var title: String?="",
    var pageCount: String?="",
    var chartLegend: MutableList<ChartLegend>?= mutableListOf<ChartLegend>(),
    var showYearsData: Boolean=false,
    var descAboveChart: String?="",
    var descBelowChart: String?=""

    )

data class ChartLegend(
//    var color: Int?=Color.TRANSPARENT,
    val name: String?="",
    val amount: String?=""
)
