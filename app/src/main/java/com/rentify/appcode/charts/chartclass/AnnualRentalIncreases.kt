package com.rentify.appcode.charts.chartclass

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.inputDataEntity
import com.rentify.appcode.charts.ChartFlowActivity
import com.rentify.appcode.charts.LegendAdapter
import com.rentify.appcode.charts.NextChartPreparation
import com.rentify.local.ChartFLowModel
import com.rentify.util.Extensions.Companion.addFormatDecimal
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.clearFormat
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_chart_flow.*
import java.util.ArrayList

class AnnualRentalIncreases  (
    val context: Context,
    val listener: NextChartPreparation,
    val chartFlowData: ChartFLowModel
) {

    init {
        (context as ChartFlowActivity).apply {
//            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            setContentView(R.layout.activity_chart_flow)
            headerTool.headerTheme()
            btnNext.bgTheme()
            btnBack.bgTheme()
            titleHeader.apply {
                text = chartFLowModel!!.title
                textSize= if (chartFLowModel!!.title!!.length<20) 20f else if (chartFLowModel!!.title!!.length<24) 19f else 18f
            }
            tvPageCount.text = chartFLowModel!!.pageCount
            if (chartFLowModel!!.descBelowChart.isNullOrEmpty().not())
                belowDesc.apply {
                    text=chartFLowModel!!.descBelowChart!!.replace("-","")
                    visibility= View.VISIBLE
                }
            if (chartFLowModel!!.descAboveChart.isNullOrEmpty().not())
                aboveDesc.apply {
                    text=chartFLowModel!!.descAboveChart
                    visibility= View.VISIBLE
                }
            setChartData()

            btnBack.setOnSingleClickListener { onBackPressed() }
            ivSetting.setOnSingleClickListener { openSetting() }
            btnNext.setOnSingleClickListener { listener.triggerNext(ChartFlowActivity.ImpoundAccount_TYPE) }
            listener.prepareNext(ChartFlowActivity.ImpoundAccount_TYPE)
        }
    }

    private fun setChartData() {
        (context as ChartFlowActivity).apply {
            val entries = ArrayList<PieEntry>()
            chart1.setUsePercentValues(true)
            chart1.description.isEnabled = false
            chart1.setExtraOffsets(20f, 30f, 20f, 5f)
            chart1.dragDecelerationFrictionCoef = 0.55f
            chart1.setCenterTextSize(14f)

            chart1.isDrawHoleEnabled = true
            chart1.setHoleColor(Color.WHITE)

            chart1.setTransparentCircleColor(Color.WHITE)
            chart1.setTransparentCircleAlpha(70)

            chart1.holeRadius = 65f
            chart1.transparentCircleRadius = 70f

            chart1.setDrawCenterText(true)
            val l = chart1.legend
            l.isEnabled = false
            /*     l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.textSize = 16f
            l.setDrawInside(false)
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 0f*/
            chart1.setEntryLabelColor(Color.WHITE)
            chart1.setEntryLabelTextSize(0f)
            legendsList.forEach {
//                total = total.plus(it.amount!!.clearFormat().toFloat())
                entries.add(PieEntry(it.amount!!.clearFormat().toFloat(), ""))
            }
            chart1.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            chart1.centerText = """${chartFLowModel!!.title}
            |${inputDataEntity.Annual_Rental_Increase!!.toString().clearFormat().addFormatDecimal(false)}%
        """.trimMargin()


            val dataSet = PieDataSet(entries, "")
            dataSet.setDrawIcons(false)
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f
            val colors = ArrayList<Int>()


            colors.addAll(ColorTemplate.VORDIPLOM_COLORS.asList())
            colors.addAll(ColorTemplate.JOYFUL_COLORS.asList())
            colors.addAll(ColorTemplate.COLORFUL_COLORS.asList())
            colors.addAll(ColorTemplate.LIBERTY_COLORS.asList())

            // for custom legend list
//            colors.forEachIndexed { index, c ->
//                if (index < legendsList.size)
//                    legendsList[index].color = c
//            }

            rvLegends.adapter = LegendAdapter(legendsList)
            colors.add(ColorTemplate.getHoloBlue())
            dataSet.colors = colors
            //dataSet.setSelectionShift(0f);

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(chart1))
            data.setValueTextSize(0f)
            data.setValueTextColor(Color.WHITE)
            chart1.setData(data)
            // undo all highlights
            chart1.highlightValues(null)
            chart1.animateY(500, Easing.EaseInOutQuad);

            chart1.invalidate()
        }
    }

}