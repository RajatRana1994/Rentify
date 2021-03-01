package com.rentify.appcode.summary

import android.content.ComponentName
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.rentify.R
import com.rentify.RentifyApp.Companion.inputDataEntity
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.charts.ChartFlowActivity
import com.rentify.appcode.charts.ChartFlowActivity.Companion.homeownersV
import com.rentify.appcode.charts.ChartFlowActivity.Companion.impoundAccountV
import com.rentify.appcode.charts.ChartFlowActivity.Companion.loanIntrestV
import com.rentify.appcode.charts.ChartFlowActivity.Companion.propertyTaxV
import com.rentify.appcode.charts.ChartFlowActivity.Companion.repairMaintV
import com.rentify.appcode.charts.ChartFlowActivity.Companion.vacancyV
import com.rentify.appcode.charts.LegendAdapter
import com.rentify.appcode.dashboard.DashboardActivity
import com.rentify.appcode.dashboard.files.FilesActivity
import com.rentify.appcode.dashboard.settings.SettingsActivity
import com.rentify.database.theme.SessionManager
import com.rentify.local.*
import com.rentify.util.ColorUtil
import com.rentify.util.ConstUtils.Companion.appFolderPath
import com.rentify.util.Extensions.Companion.addFormatDecimal
import com.rentify.util.Extensions.Companion.clearFormat
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.showYesNoDialog
import com.rentify.util.ProgressDialogs
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import kotlinx.android.synthetic.main.activity_summary.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class SummaryActivity : AppCompatActivity(), View.OnClickListener, PDFUtil.PDFUtilListener {

    companion object {

    }

    var titleHeaderWidth = 0
    var titleHeaderHeight = 0
    var userPropertyLayoutWidth = 0
    var userPropertyLayoutHeight = 0
    var cashReqLayoutWidth = 0
    var cashReqLayoutHeight = 0
    var chartLayoutWidth = 0
    var chartLayoutHeight = 0
    var chart1LayoutWidth = 0
    var chart1LayoutHeight = 0
    var chart2LayoutWidth = 0
    var chart2LayoutHeight = 0
    var chart3LayoutWidth = 0
    var chart3LayoutHeight = 0
    var chart4LayoutWidth = 0
    var chart4LayoutHeight = 0
    var chart5LayoutWidth = 0
    var chart5LayoutHeight = 0
    var chart6LayoutWidth = 0
    var chart6LayoutHeight = 0
    var propertyBigLayoutWidth = 0
    var propertyBigLayoutHeight = 0

    var list = mutableListOf<String>()

    fun chartBg() = ContextCompat.getDrawable(
        this@SummaryActivity, when (session(this).getPrefValue(SessionManager.THEME_COLOR)) {
            ColorUtil.BLUE.color -> R.drawable.ic_splash_bg
            ColorUtil.ORANGE.color -> R.drawable.orange_splash
            ColorUtil.DARKORANGE.color -> R.drawable.orange_splash
            ColorUtil.GREEN.color -> R.drawable.green_splash
            ColorUtil.PURPLE.color -> R.drawable.purple_splash
            ColorUtil.RED.color -> R.drawable.red_splash
            else -> R.drawable.ic_splash_bg
        }
    )!!

    //    val legendsDataList by lazy { mutableListOf<ChartLegend>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        list.clear()
        setEntryData()
        btnBack.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        ivMsg.setOnClickListener(this)
        chartLayout.background = chartBg()
        chart1Layout.background = chartBg()
        chart2Layout.background = chartBg()
        chart3Layout.background = chartBg()
        chart4Layout.background = chartBg()
        chart5Layout.background = chartBg()
        chart6Layout.background = chartBg()
        setUserData()
        setPropertyData()
        setAllChartData()
        setAllImages()
        getHeights()
    }

    private fun setEntryData() {
        mutableListOf<Pair<String, String>>().apply {
            add(Pair(getString(R.string.cash_req_caps), ""))
            add(
                Pair(
                    getString(R.string.property_value),
                    "${inputDataEntity.Property_Value_after_renovation!!.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.purchase_price),
                    "${inputDataEntity.Purchase_Price!!.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.market_rent),
                    "${inputDataEntity.Market_Rent!!.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.loan_down_payment),
                    "${inputDataEntity.Loan_Down_Payment!!.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.loan_cost),
                    "${inputDataEntity.Loan_Cost!!.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.improvements),
                    "${inputDataEntity.Improvements!!.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.annual_rental_increase_),
                    "${inputDataEntity.Annual_Rental_Increase.toString().clearFormat()
                        .addFormatDecimal(false)}%"
                )
            )
            add(
                Pair(
                    getString(R.string.area_appreciation_growth),
                    "${inputDataEntity.Area_Appreciation_Growth.toString().clearFormat()
                        .addFormatDecimal(false)}%"
                )
            )
            add(Pair(getString(R.string.annual_exp_flow_caps), ""))
            add(
                Pair(
                    getString(R.string.loan_amount),
                    "${inputDataEntity.Purchase_Price!!.minus(inputDataEntity.Loan_Down_Payment!!)
                        .toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.principal_payments),
                    "${ChartFlowActivity.principalPaymentV.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.impound_account),
                    "${impoundAccountV.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.vacancy_factor),
                    "${vacancyV.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.loan_intrest),
                    "${loanIntrestV.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.property_tax),
                    "${propertyTaxV.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.homeowners_insurance),
                    "${homeownersV.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.repair_and_maintance),
                    "${repairMaintV.toString().clearFormat().addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.management),
                    "${inputDataEntity.Annual_Management.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.annual_other_expenses_),
                    "${inputDataEntity.Annual_other_expenses.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.annual_expenses),
                    "${inputDataEntity.Annual_Expenses.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.monthly_cash_requirement),
                    "${inputDataEntity.Monthly_cash_requirement.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.annual_income),
                    "${inputDataEntity.Annual_Income.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.cash_on_cash_return),
                    "${inputDataEntity.Cash_on_Cash_return.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            add(
                Pair(
                    getString(R.string.percentage_cash_on_return),
                    "${inputDataEntity.Percentage_Cash_on_return.toString().clearFormat()
                        .addFormatDecimal(false)}%"
                )
            )
            add(
                Pair(
                    getString(R.string.annual_net_operating_income),
                    "${inputDataEntity.Annual_Net_Operating_Income.toString().clearFormat()
                        .addFormatDecimal(true)}"
                )
            )
            rvCashExpenseFlow.adapter = SummaryEntryAdapter(this)
        }
    }

    private fun setAllImages() {
//        GlobalScope.launch {
//            launch(Dispatchers.IO) {
//                getFileData()
//            }
//        }
        setImage(
            ivPropertyImg1,
            session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_COVER)!!
        )

        session(this@SummaryActivity).apply {
            if (getPrefValue(SessionManager.PROPERTY_IMAGE_1)!!.isNotEmpty()) list.add(
                getPrefValue(
                    SessionManager.PROPERTY_IMAGE_1
                )!!
            )
            if (getPrefValue(SessionManager.PROPERTY_IMAGE_2)!!.isNotEmpty()) list.add(
                getPrefValue(
                    SessionManager.PROPERTY_IMAGE_2
                )!!
            )
            if (getPrefValue(SessionManager.PROPERTY_IMAGE_3)!!.isNotEmpty()) list.add(
                getPrefValue(
                    SessionManager.PROPERTY_IMAGE_3
                )!!
            )
            if (getPrefValue(SessionManager.PROPERTY_IMAGE_4)!!.isNotEmpty()) list.add(
                getPrefValue(
                    SessionManager.PROPERTY_IMAGE_4
                )!!
            )
            if (getPrefValue(SessionManager.PROPERTY_IMAGE_5)!!.isNotEmpty()) list.add(
                getPrefValue(
                    SessionManager.PROPERTY_IMAGE_5
                )!!
            )
            rvPropertyImgs.adapter = PropertyImgAdapter(list)
        }
    }

    private suspend fun getFileData() {
//        if (RentifyApp.getPhotoDB(this@SummaryActivity).photoDao().getAll().isNotEmpty()) {
//            list = RentifyApp.getPhotoDB(this@SummaryActivity).photoDao().getAll() as MutableList<PhotoEntity>
//            val listimg = mutableListOf<Pair<String?, String?>>()
//            list.forEachIndexed { index, fIleEntity ->
//                when (index) {
//                    0 -> setImage(ivPropertyImg1, list[index].photoname!!, list[index].foldername!!)
//                    1, 2, 3, 4, 5 -> {
//                        if (File(list[index].foldername!!).exists()) {
//                            if (File(list[index].photoname!!).exists()) {
//                                listimg.add(Pair(list[index].photoname!!, list[index].foldername!!))
//                            }
//                        }
//                        if (index == list.size - 1) {
//                            GlobalScope.launch {
//                                launch(Dispatchers.Main) {
//                                    rvPropertyImgs.adapter = PropertyImgAdapter(listimg)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    fun setImage(view: ImageView, file: String, folder: String = "") {
//        GlobalScope.launch {
//            launch(Dispatchers.Main) {
//                try {
//                    if (File(folder).exists()) {
        if (File(file).exists()) {
            view.setImageURI(Uri.fromFile(File(file)))
        }
//                    }
//                } catch (e: FileNotFoundException) {
//                }
//            }
//        }
    }

    private fun setUserData() {
        if (File(session(this).getPrefValue(SessionManager.USER_NAME_USERINFO)!!).exists())
            ivLogo.setImageURI(Uri.fromFile(File(session(this).getPrefValue(SessionManager.USER_NAME_USERINFO)!!)))
        tvUserName.text = session(this).getPrefValue(SessionManager.USER_NAME_USERINFO)
        tvUserCompanyName.text = session(this).getPrefValue(SessionManager.COMPANY_NAME_USERINFO)
        tvUserPhone.text = session(this).getPrefValue(SessionManager.PHONE_USERINFO)
        tvUserAddress.text = getUserAddress()
    }

    private fun setPropertyData() {
        propertySqFeet.text = session(this).getPrefValue(SessionManager.SQUARE_FEET_PROPERTY_INFO)
        propertyBedroom.text = session(this).getPrefValue(SessionManager.BED_ROOMS_PROPERTY_INFO)
        propertyLotSize.text = session(this).getPrefValue(SessionManager.LOT_SIZE_PROPERTY_INFO)
        propertyBethroom.text = session(this).getPrefValue(SessionManager.BATH_ROOMS_PROPERTY_INFO)
        tvPropertyAddress.text = getPropertyAddress()
    }

    fun setAllChartData() {
        //chart cash requirement
        mutableListOf<ChartLegend>().apply {
            var total = 0.0
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Loan_Down_Payment}",
                    name = getString(R.string.down_payment)
                )
            )
            total = total.plus(inputDataEntity.Loan_Down_Payment!!)
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Loan_Cost}",
                    name = getString(R.string.loan_cost)
                )
            )
            total = total.plus(inputDataEntity.Loan_Cost!!)
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Annual_other_expenses!!}", name = getString(
                        R.string.other_expensive
                    )
                )
            )
            total = total.plus(inputDataEntity.Annual_other_expenses!!)
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Improvements}",
                    name = getString(R.string.capital_improvement)
                )
            )
            total = total.plus(inputDataEntity.Improvements!!)
            setChartData(
                chart, rvLegends, this, "$total", """${getString(R.string.cash_req)}
            |${total.toString().clearFormat().addFormatDecimal()}
        """.trimMargin()
            )
        }
        //chart rate of return
        mutableListOf<ChartLegend>().apply {
            var total = 0.0
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Percentage_Cash_on_return!!}%",
                    name = getString(R.string.cash_on_cash_return)
                )
            )
            total =
                total.plus(inputDataEntity.Percentage_Cash_on_return!!)
            add(
                ChartLegend(
                    amount = "${ChartFlowActivity.principalPaymentV.div(inputDataEntity.Purchase_Price!!)
                        .times(100)}%", name = getString(R.string.principal_reduction)
                )
            )
            total = total.plus(
                ChartFlowActivity.principalPaymentV.div(inputDataEntity.Purchase_Price!!).times(100)
            )
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Area_Appreciation_Growth!!.div(
                        4
                    )}%",
                    name = getString(R.string.appreciation_growth)
                )
            )
            total = total.plus(
                inputDataEntity.Area_Appreciation_Growth!!.div(
                    4
                )
            )
            add(
                ChartLegend(
                    amount = "${inputDataEntity.Annual_Rental_Increase!!.times(
                        0.37
                    )}%",
                    name = getString(R.string.annual_rental_increase)
                )
            )
            total = total.plus(
                inputDataEntity.Annual_Rental_Increase!!.times(
                    0.37
                )
            )
            add(
                ChartLegend(
                    amount = "${ChartFlowActivity.annDepreciationV}",
                    name = getString(R.string.annual_depreciation)
                )
            )
            total = total.plus(ChartFlowActivity.annDepreciationV)
            setChartData(
                chart1, rvLegends1, this, "$total",
                """${getString(R.string.internal_rate_of_return)}
            |${total.toString().clearFormat().addFormatDecimal().replace("$", "")}%
        """.trimMargin(), false
            )
        }
        // chart tax benefit
        mutableListOf<ChartLegend>().apply {
            var total = 0.0
            add(
                ChartLegend(
                    amount = "${ChartFlowActivity.structureV.div(39)}",
                    name = getString(R.string.sttucture_depreciation)
                )
            )
            total = total.plus(ChartFlowActivity.structureV.div(39))
            add(
                ChartLegend(
                    amount = "${ChartFlowActivity.operatingExpenseV}",
                    name = getString(R.string.operating_exp)
                )
            )
            total = total.plus(ChartFlowActivity.operatingExpenseV)
            add(
                ChartLegend(
                    amount = "${(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!)).plus(
                        inputDataEntity.Improvements!!
                    )}",
                    name = getString(R.string.loan_cost_other_exp)
                )
            )
            total = total.plus(
                (inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!)).plus(
                    inputDataEntity.Improvements!!
                )
            )
            tvAmountYear1.text = "${total.toString().clearFormat().addFormatDecimal()}"
            tvAmountYear2.text =
                "${total.minus(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!))
                    .toString().clearFormat().addFormatDecimal()}"
            tvAmountYear3.text =
                "${total.minus(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!))
                    .toString().clearFormat().addFormatDecimal()}"
            tvAmountYear4.text =
                "${total.minus(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!))
                    .toString().clearFormat().addFormatDecimal()}"
            tvAmountYear5.text =
                "${total.minus(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!))
                    .toString().clearFormat().addFormatDecimal()}"
            setChartData(
                chart2, rvLegends2, this, "$total",
                """${getString(R.string.tax_benefit_schedule)}
            |${total.toString().clearFormat().addFormatDecimal()}
        """.trimMargin()
            )
        }
        // chart rental increase////////////////
        mutableListOf<ChartLegend>().apply {
            var rentalIncV = inputDataEntity.Market_Rent
            add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year1)))
            rentalIncV = rentalIncV!!.plus(
                rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100)
            )
            add(ChartLegend(amount = "${rentalIncV}", name = getString(R.string.year2)))
            rentalIncV = rentalIncV.plus(
                rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100)
            )
            add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year3)))
            rentalIncV = rentalIncV.plus(
                rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100)
            )
            add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year4)))
            rentalIncV = rentalIncV.plus(
                rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100)
            )
            add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year5)))
            setChartData(
                chart3, rvLegends3, this, "$rentalIncV",
                """${getString(R.string.annual_rental_increase)}
            |${inputDataEntity.Annual_Rental_Increase!!.toString().clearFormat()
                    .addFormatDecimal(false)}%
        """.trimMargin()
            )
        }
        // chart impound account
        mutableListOf<ChartLegend>().apply {
            var total = 0.0
            var impoundV =
                (ChartFlowActivity.impoundAccountV + ChartFlowActivity.vacancyV).times(1)
                    .plus(inputDataEntity.Rental_Deposit!!)
            add(ChartLegend(amount = "$impoundV", name = getString(R.string.year1)))
            total = total.plus(impoundV)
            impoundV =
                (ChartFlowActivity.impoundAccountV + ChartFlowActivity.vacancyV).times(2)
                    .plus(inputDataEntity.Rental_Deposit!!)
            add(ChartLegend(amount = "$impoundV", name = getString(R.string.year2)))
//            total = total.plus(impoundV)
            impoundV =
                (ChartFlowActivity.impoundAccountV + ChartFlowActivity.vacancyV).times(3)
                    .plus(inputDataEntity.Rental_Deposit!!)
            add(ChartLegend(amount = "$impoundV", name = getString(R.string.year3)))
//            total = total.plus(impoundV)
            impoundV =
                (ChartFlowActivity.impoundAccountV + ChartFlowActivity.vacancyV).times(4)
                    .plus(inputDataEntity.Rental_Deposit!!)
            add(ChartLegend(amount = "$impoundV", name = getString(R.string.year4)))
//            total = total.plus(impoundV)
            impoundV =
                (ChartFlowActivity.impoundAccountV + ChartFlowActivity.vacancyV).times(5)
                    .plus(inputDataEntity.Rental_Deposit!!)
            add(ChartLegend(amount = "$impoundV", name = getString(R.string.year5)))
//            total = total.plus(impoundV)
            aboveDesc.text = "${inputDataEntity.Rental_Deposit.toString()
                .addFormatDecimal()}\n${getString(R.string.rental_deposit_on_hold)}"
            setChartData(
                chart4, rvLegends4, this, "$total",
                """${getString(R.string.impound_account)}
            |${total.toString().clearFormat().addFormatDecimal()}
        """.trimMargin()
            )
        }
        // chart principle reduction
        mutableListOf<ChartLegend>().apply {
            var total = 0.0
            var principalReductv = (ChartFlowActivity.principalPaymentV).times(1)
            add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year1)))
            total = total.plus(principalReductv)
            principalReductv = (ChartFlowActivity.principalPaymentV).times(2)
            add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year2)))
//            total = total.plus(principalReductv)
            principalReductv = (ChartFlowActivity.principalPaymentV).times(3)
            add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year3)))
//            total = total.plus(principalReductv)
            principalReductv = (ChartFlowActivity.principalPaymentV).times(4)
            add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year4)))
//            total = total.plus(principalReductv)
            principalReductv = (ChartFlowActivity.principalPaymentV).times(5)
            add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year5)))
//            total = total.plus(principalReductv)
            setChartData(
                chart5, rvLegends5, this, "$total",
                """${getString(R.string.annual_principal_reduction)}
            |${total.toString().clearFormat().addFormatDecimal()}
        """.trimMargin()
            )
        }
        //chart appreciation growth
        mutableListOf<ChartLegend>().apply {
            var total = 0.0
            var annualEquityV =
                inputDataEntity.Property_Value_after_renovation!! + (inputDataEntity.Property_Value_after_renovation!!.times(
                    inputDataEntity.Area_Appreciation_Growth!!
                )
                    .div(100))
            add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year1)))
            total = total.plus(annualEquityV)
            annualEquityV +=
                (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!)
                    .div(100))
            add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year2)))
//            total = total.plus(annualEquityV)
            annualEquityV +=
                (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
            add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year3)))
//            total = total.plus(annualEquityV)
            annualEquityV +=
                (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
            add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year4)))
//            total = total.plus(annualEquityV)
            annualEquityV +=
                (ChartFlowActivity.propertyTaxV.times(inputDataEntity.Area_Appreciation_Growth!!)
                    .div(100))
            add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year5)))
//            total = total.plus(annualEquityV)
            setChartData(
                chart6, rvLegends6, this, "$total",
                """${getString(R.string.appreciation_growth)}
            |${total.toString().clearFormat().addFormatDecimal()}
        """.trimMargin()
            )
        }
    }


    private fun setChartData(
        chartview: PieChart,
        rview: RecyclerView,
        legendsList: MutableList<ChartLegend>,
        total: String,
        centerText: String,
        isAmount: Boolean = true
    ) {
        val entries = ArrayList<PieEntry>()

        chartview.setUsePercentValues(true)
        chartview.description.isEnabled = false
        chartview.setExtraOffsets(20f, 30f, 20f, 5f)
        chartview.dragDecelerationFrictionCoef = 0.55f
        chartview.setCenterTextSize(14f)
        chartview.isDrawHoleEnabled = true
        chartview.setHoleColor(Color.WHITE)
        chartview.setTransparentCircleColor(Color.WHITE)
        chartview.setTransparentCircleAlpha(70)
        chartview.holeRadius = 65f
        chartview.transparentCircleRadius = 70f

        chartview.setDrawCenterText(true)
        val l = chart.legend
        l.isEnabled = false
        chartview.setEntryLabelColor(Color.WHITE)
        chartview.setEntryLabelTextSize(0f)

        legendsList.forEach {
            entries.add(PieEntry(it.amount!!.clearFormat().toFloat(), ""))
        }
        chart1.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        chartview.centerText = centerText

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

        rview.adapter = LegendAdapter(legendsList, isAmount)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(0f)
        data.setValueTextColor(Color.WHITE)
        chartview.data = data
        chartview.highlightValues(null)
        chartview.invalidate()
    }

    fun getUserAddress() =
        """${session(this).getPrefValue(SessionManager.ADDRESS_USERINFO)}, ${session(this).getPrefValue(
            SessionManager.CITY_USERINFO
        )}, ${session(this).getPrefValue(SessionManager.STATE_USERINFO)} ${session(this).getPrefValue(
            SessionManager.POSTCODE_USERINFO
        )}"""

    fun getPropertyAddress() =
        """${session(this).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO)}, ${session(this).getPrefValue(
            SessionManager.CITY_PROPERTY_INFO
        )}, ${session(this).getPrefValue(SessionManager.STATE_PROPERTY_INFO)} ${session(this).getPrefValue(
            SessionManager.POSTCODE_PROPERTY_INFO
        )}"""


    fun captureBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveFile(v1: View, type: String) {
        val now = Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mFolderPath =
                "${Environment.DIRECTORY_DOCUMENTS}${File.separator}Rentify${File.separator}"
            val mFilePath = "${mFolderPath}$now.jpg"
            if (File(mFolderPath).exists().not()) {
                val created = File(mFolderPath).mkdir()
                Log.e("created", created.toString())
            }
            val file = File(mFolderPath, mFilePath)
//            val document = Document()

            // create bitmap screen capture
//            val v1 = window.decorView.rootView;
            v1.isDrawingCacheEnabled = true;
            when (type) {
                "0" -> {
                    Log.e("ERROR_IN", "0")
                    val bitmap = Bitmap.createBitmap(v1.drawingCache);
                    v1.isDrawingCacheEnabled = false;
//                    PDFUtil.getInstance().apply {
//                        generatePDF(mutableListOf<View>().apply {
//                            add(v1)
//                        },mFolderPath,this@SummaryActivity)
//                    }
//                    val  outputStream = FileOutputStream(file);
//                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                    outputStream.flush();
//                    outputStream.close();
//                    createMyPDF(bitmap)
                }
                "1" -> {
                    Log.e("ERROR_IN", "1")
                    val bm1 = Bitmap.createBitmap(v1.drawingCache);
                    v1.isDrawingCacheEnabled = false;
                    val outputStream = FileOutputStream(file);
                    bm1!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
                "2" -> {
                    Log.e("ERROR_IN", "2")
                    val bm2 = Bitmap.createBitmap(v1.drawingCache);
                    v1.isDrawingCacheEnabled = false;
                    val outputStream = FileOutputStream(file);
                    bm2!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
                "3" -> {
                    Log.e("ERROR_IN", "3")
                    val bm3 = Bitmap.createBitmap(v1.drawingCache);
                    v1.isDrawingCacheEnabled = false;
                    val outputStream = FileOutputStream(file);
                    bm3!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
            Toast.makeText(this@SummaryActivity, "SAVED", Toast.LENGTH_SHORT).show()
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private fun captureScreens(share: Boolean) {
        ProgressDialogs.showLoading(this@SummaryActivity)
        var allHeight = 0
        try {
            allHeight = 0
            titleHeader.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    Log.e("HEIGHT", "${titleHeader!!.height}") //height is ready
                    titleHeader.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
            val bitmapTitle = loadBitmapFromView(titleHeader, titleHeaderWidth, titleHeaderHeight)!!
            allHeight += bitmapTitle.height
            val bitmap1 = loadBitmapFromView(
                userPropertyLayout,
                userPropertyLayoutWidth,
                userPropertyLayoutHeight
            )!!
            allHeight += bitmap1.height
            val bitmap2 =
                loadBitmapFromView(cashReqLayout, cashReqLayoutWidth, cashReqLayoutHeight)!!
            allHeight += bitmap2.height
            val bitmap3 = loadBitmapFromView(chartLayout, chartLayoutWidth, chartLayoutHeight)!!
            allHeight += bitmap3.height
            val bitmap4 = loadBitmapFromView(chart1Layout, chart1LayoutWidth, chart1LayoutHeight)!!
            allHeight += bitmap4.height
            val bitmap5 = loadBitmapFromView(chart2Layout, chart2LayoutWidth, chart2LayoutHeight)!!
            allHeight += bitmap5.height
            val bitmap6 = loadBitmapFromView(chart3Layout, chart3LayoutWidth, chart3LayoutHeight)!!
            allHeight += bitmap6.height
            val bitmap7 = loadBitmapFromView(chart4Layout, chart4LayoutWidth, chart4LayoutHeight)!!
            allHeight += bitmap7.height
            val bitmap8 = loadBitmapFromView(chart5Layout, chart5LayoutWidth, chart5LayoutHeight)!!
            allHeight += bitmap8.height
            val bitmap9 = loadBitmapFromView(chart6Layout, chart6LayoutWidth, chart6LayoutHeight)!!
            allHeight += bitmap9.height
            val bitmap10 = loadBitmapFromView(
                propertyBigLayout,
                propertyBigLayoutWidth,
                propertyBigLayoutHeight
            )!!
            allHeight += bitmap10.height
            ProgressDialogs.showLoading(this@SummaryActivity, "Creating PDF...")
            createMyPDF(
                bitmapTitle,
                bitmap1,
                bitmap2,
                bitmap3,
                bitmap4,
                bitmap5,
                bitmap6,
                bitmap7,
                bitmap8,
                bitmap9,
                bitmap10,
                allHeight,
                share
            )

        } catch (e: Throwable) {
            ProgressDialogs.hideLoading()
            try {
                titleHeader.isDrawingCacheEnabled = true;
                val bitmapTitle = Bitmap.createBitmap(titleHeader.drawingCache);
                titleHeader.isDrawingCacheEnabled = false;
                allHeight += bitmapTitle.height
                userPropertyLayout.isDrawingCacheEnabled = true;
                val bitmap1 = Bitmap.createBitmap(userPropertyLayout.drawingCache);
                userPropertyLayout.isDrawingCacheEnabled = false;
                allHeight += bitmap1.height
                cashReqLayout.isDrawingCacheEnabled = true;
                val bitmap2 = Bitmap.createBitmap(cashReqLayout.drawingCache);
                cashReqLayout.isDrawingCacheEnabled = false;
                allHeight += bitmap2.height
                chartLayout.isDrawingCacheEnabled = true;
                val bitmap3 = Bitmap.createBitmap(chartLayout.drawingCache);
                chartLayout.isDrawingCacheEnabled = false;
                allHeight += bitmap3.height
                chart1Layout.isDrawingCacheEnabled = true;
                val bitmap4 = Bitmap.createBitmap(chart1Layout.drawingCache);
                chart1Layout.isDrawingCacheEnabled = false;
                allHeight += bitmap4.height
                chart2Layout.isDrawingCacheEnabled = true;
                val bitmap5 = Bitmap.createBitmap(chart2Layout.drawingCache);
                chart2Layout.isDrawingCacheEnabled = false;
                allHeight += bitmap5.height
                chart3Layout.isDrawingCacheEnabled = true;
                val bitmap6 = Bitmap.createBitmap(chart3Layout.drawingCache);
                chart3Layout.isDrawingCacheEnabled = false;
                allHeight += bitmap6.height
                chart4Layout.isDrawingCacheEnabled = true;
                val bitmap7 = Bitmap.createBitmap(chart4Layout.drawingCache);
                chart4Layout.isDrawingCacheEnabled = false;
                allHeight += bitmap7.height
                chart5Layout.isDrawingCacheEnabled = true;
                val bitmap8 = Bitmap.createBitmap(chart5Layout.drawingCache);
                chart5Layout.isDrawingCacheEnabled = false;
                allHeight += bitmap8.height
                chart6Layout.isDrawingCacheEnabled = true;
                val bitmap9 = Bitmap.createBitmap(chart6Layout.drawingCache);
                chart6Layout.isDrawingCacheEnabled = false;
                allHeight += bitmap9.height
                propertyBigLayout.isDrawingCacheEnabled = true;
                val bitmap10 = Bitmap.createBitmap(propertyBigLayout.drawingCache);
                propertyBigLayout.isDrawingCacheEnabled = false;
                allHeight += bitmap10.height
                ProgressDialogs.showLoading(this@SummaryActivity, "Creating PDF...")

                createMyPDF(
                    bitmapTitle,
                    bitmap1,
                    bitmap2,
                    bitmap3,
                    bitmap4,
                    bitmap5,
                    bitmap6,
                    bitmap7,
                    bitmap8,
                    bitmap9,
                    bitmap10,
                    allHeight,
                    share
                )
            } catch (e: Throwable) {
                Toast.makeText(
                    this@SummaryActivity,
                    "Something went wrong,Please try later!",
                    Toast.LENGTH_SHORT
                ).show()
                // Several error may come out with file handling or DOM
                e.printStackTrace();
            }
        }
    }


    fun getHeights() {

        titleHeader.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${titleHeader!!.height}") //height is ready
                titleHeaderWidth = titleHeader!!.width
                titleHeaderHeight = titleHeader!!.height
                titleHeader.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        userPropertyLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${userPropertyLayout!!.height}") //height is ready
                userPropertyLayoutWidth = userPropertyLayout!!.width
                userPropertyLayoutHeight = userPropertyLayout!!.height
                userPropertyLayout.viewTreeObserver!!.removeOnGlobalLayoutListener(this)
            }
        })
        cashReqLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${cashReqLayout!!.height}") //height is ready
                cashReqLayoutWidth = cashReqLayout!!.width
                cashReqLayoutHeight = cashReqLayout!!.height
                cashReqLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chartLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "chartLayout{titleHeader!!.height}") //height is ready
                chartLayoutWidth = chartLayout!!.width
                chartLayoutHeight = chartLayout!!.height
                chartLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chart1Layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${chart1Layout!!.height}") //height is ready
                chart1LayoutWidth = chart1Layout!!.width
                chart1LayoutHeight = chart1Layout!!.height
                chart1Layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chart2Layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${chart2Layout!!.height}") //height is ready
                chart2LayoutWidth = chart2Layout!!.width
                chart2LayoutHeight = chart2Layout!!.height
                chart2Layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chart3Layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${chart3Layout!!.height}") //height is ready
                chart3LayoutWidth = chart3Layout!!.width
                chart3LayoutHeight = chart3Layout!!.height
                chart3Layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chart4Layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${chart4Layout!!.height}") //height is ready
                chart4LayoutWidth = chart4Layout!!.width
                chart4LayoutHeight = chart4Layout!!.height
                chart4Layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chart5Layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${chart5Layout!!.height}") //height is ready
                chart5LayoutWidth = chart5Layout!!.width
                chart5LayoutHeight = chart5Layout!!.height
                chart5Layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        chart6Layout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${chart6Layout!!.height}") //height is ready
                chart6LayoutWidth = chart6Layout!!.width
                chart6LayoutHeight = chart6Layout!!.height
                chart6Layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        propertyBigLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                Log.e("HEIGHT", "${propertyBigLayout!!.height}") //height is ready
                propertyBigLayoutWidth = propertyBigLayout!!.width
                propertyBigLayoutHeight = propertyBigLayout!!.height
                propertyBigLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap? {
        val b = Bitmap.createBitmap(
            if ((v.layoutParams.width ?: 0) > 0) v.layoutParams.width else width,
            if ((v.layoutParams.height ?: 0) > 0) v.layoutParams.height else height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(
            0,
            0,
            if ((v.layoutParams.width ?: 0) > 0) v.layoutParams.width else width,
            if ((v.layoutParams.height ?: 0) > 0) v.layoutParams.height else height
        )
        v.draw(c)
        return b
    }

    override fun onClick(p0: View?) {
        when (p0!!) {
            btnBack -> {
                LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("GO_BACK"))
                finish()
            }
            ivMsg -> {
                captureScreens(true)
            }
            btnNext -> {
                showYesNoDialog(getString(R.string.sure_to_save_file)) {
                    captureScreens(false)
                }
            }
        }
    }


    override fun pdfGenerationSuccess(savedPDFFile: File?) {
        Toast.makeText(this@SummaryActivity, "Save", Toast.LENGTH_SHORT).show()
    }

    override fun pdfGenerationFailure(exception: Exception?) {
        Toast.makeText(this@SummaryActivity, "fail", Toast.LENGTH_SHORT).show()
    }

    val displayMetrics by lazy { DisplayMetrics() }

    private fun createMyPDF(
        bTitle: Bitmap,
        b1: Bitmap,
        b2: Bitmap,
        b3: Bitmap,
        b4: Bitmap,
        b5: Bitmap,
        b6: Bitmap,
        b7: Bitmap,
        b8: Bitmap,
        b9: Bitmap,
        b10: Bitmap,
        allHeight: Int,
        share: Boolean
    ) {
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val myPdfDocument = PdfDocument()
        val myPageInfo = PdfDocument.PageInfo.Builder(
            (b1.width ?: displayMetrics.widthPixels) + 20,
            allHeight + 50,
            1
        ).create()
        val myPage = myPdfDocument.startPage(myPageInfo)
        val logoPaint = Paint()
        var plusHeight = 10
        val screenTitle = Bitmap.createScaledBitmap(
            bTitle,
            if (bTitle.width == 0) titleHeaderWidth else bTitle.width,
            bTitle.height,
            false
        )
        myPage.canvas.drawBitmap(screenTitle, 10f, 10f, logoPaint)
        plusHeight += screenTitle.height
        val screen1 = Bitmap.createScaledBitmap(
            b1,
            if (b1.width == 0) userPropertyLayoutWidth else b1.width,
            b1.height,
            false
        )
        myPage.canvas.drawBitmap(screen1, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b1.height + 10
        val screen2 = Bitmap.createScaledBitmap(
            b2,
            if (b2.width == 0) cashReqLayoutWidth else b2.width,
            b2.height,
            false
        )
        myPage.canvas.drawBitmap(screen2, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b2.height + 10
        val screen3 = Bitmap.createScaledBitmap(
            b3,
            if (b3.width == 0) chartLayoutWidth else b3.width,
            b3.height,
            false
        )
        myPage.canvas.drawBitmap(screen3, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b3.height + 10
        val screen4 = Bitmap.createScaledBitmap(
            b4,
            if (b4.width == 0) chart1LayoutWidth else b4.width,
            b4.height,
            false
        )
        myPage.canvas.drawBitmap(screen4, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b4.height + 10
        val screen5 = Bitmap.createScaledBitmap(
            b5,
            if (b5.width == 0) chart2LayoutWidth else b5.width,
            b5.height,
            false
        )
        myPage.canvas.drawBitmap(screen5, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b5.height + 10
        val screen6 = Bitmap.createScaledBitmap(
            b6,
            if (b6.width == 0) chart3LayoutWidth else b6.width,
            b6.height,
            false
        )
        myPage.canvas.drawBitmap(screen6, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b6.height + 10
        val screen7 = Bitmap.createScaledBitmap(
            b7,
            if (b7.width == 0) chart4LayoutWidth else b7.width,
            b7.height,
            false
        )
        myPage.canvas.drawBitmap(screen7, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b7.height + 10
        val screen8 = Bitmap.createScaledBitmap(
            b8,
            if (b8.width == 0) chart5LayoutWidth else b8.width,
            b8.height,
            false
        )
        myPage.canvas.drawBitmap(screen8, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b8.height + 10
        val screen9 = Bitmap.createScaledBitmap(
            b9,
            if (b9.width == 0) chart6LayoutWidth else b9.width,
            b9.height,
            false
        )
        myPage.canvas.drawBitmap(screen9, 10f, plusHeight.toFloat(), logoPaint)
        plusHeight += b9.height + 10
        val screen10 = Bitmap.createScaledBitmap(
            b10,
            if (b10.width == 0) propertyBigLayoutWidth else b10.width,
            b10.height,
            false
        )
        myPage.canvas.drawBitmap(screen10, 10f, plusHeight.toFloat(), logoPaint)
//        Toast.makeText(this@SummaryActivity, "Created!", Toast.LENGTH_SHORT).show()

        myPdfDocument.finishPage(myPage)
        if (appFolderPath()!!.exists().not()) {
            val created = appFolderPath()!!.mkdir()
//            Log.e("Created = ", created.toString())
        }
        val myFile = File(
            appFolderPath()!!.path + File.separator + "${session(this).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO)}.pdf"
        ).apply {
            createNewFile()
//            Toast.makeText(this@SummaryActivity, "Saved!", Toast.LENGTH_SHORT).show()
        }
//        val myFile = File(appFolderPath()!!.path +File.separator +"Rentify$time.pdf").apply { createNewFile() }
        try {
            myPdfDocument.writeTo(FileOutputStream(myFile))
            session(this@SummaryActivity).savePrefValue(myFile.name, savePdfData())
        } catch (e: Exception) {
            ProgressDialogs.hideLoading()
            e.printStackTrace();
        }
        myPdfDocument.close();

        ProgressDialogs.hideLoading()

        if (share) {
            composeMail(myFile)
        } else {
            showDoneDialog(getString(R.string.file_saved_successfully)) {
                startActivities(
                    arrayOf(
                        Intent(this@SummaryActivity, SettingsActivity::class.java),
                        Intent(this@SummaryActivity, DashboardActivity::class.java),
                        Intent(this@SummaryActivity, FilesActivity::class.java)
                    )
                )
                finishAffinity()
            }
        }
    }

    private fun composeMail(file: File) {
        val path = Uri.fromFile(file)
        var isFound = false
        val shareIntent = Intent(Intent.ACTION_SENDTO);
        shareIntent.type = "*/*";
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent by Android device")
        shareIntent.putExtra(Intent.EXTRA_STREAM, path)

        try {
            val pm = packageManager;
            val activityList = pm.queryIntentActivities(shareIntent, 0);
            for (app in activityList) {
                if ((app.activityInfo.name).contains("google.android.gm") || (app.activityInfo.name).contains(
                        "gmail"
                    )
                ) {
                    isFound = true
                    val activity = app.activityInfo
                    val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    shareIntent.component = name
                    //reset
                    list.clear()
                    setEntryData()
                    btnBack.setOnClickListener(this)
                    btnNext.setOnClickListener(this)
                    ivMsg.setOnClickListener(this)
                    chartLayout.background = chartBg()
                    chart1Layout.background = chartBg()
                    chart2Layout.background = chartBg()
                    chart3Layout.background = chartBg()
                    chart4Layout.background = chartBg()
                    chart5Layout.background = chartBg()
                    chart6Layout.background = chartBg()
                    setUserData()
                    setPropertyData()
                    setAllChartData()
                    setAllImages()
                    getHeights()
                    startActivity(shareIntent)
                }
            }
            if (!isFound) {
                try {
                    val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntent(arrayOfNulls<Parcelable>(shareIntent.size())))
                    //reset
                    list.clear()
                    setEntryData()
                    btnBack.setOnClickListener(this)
                    btnNext.setOnClickListener(this)
                    ivMsg.setOnClickListener(this)
                    chartLayout.background = chartBg()
                    chart1Layout.background = chartBg()
                    chart2Layout.background = chartBg()
                    chart3Layout.background = chartBg()
                    chart4Layout.background = chartBg()
                    chart5Layout.background = chartBg()
                    chart6Layout.background = chartBg()
                    setUserData()
                    setPropertyData()
                    setAllChartData()
                    setAllImages()
                    getHeights()
                    startActivity(chooserIntent)
                } catch (e: java.lang.Exception) {
                    Toast.makeText(this@SummaryActivity, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
                //reset
                list.clear()
                setEntryData()
                btnBack.setOnClickListener(this)
                btnNext.setOnClickListener(this)
                ivMsg.setOnClickListener(this)
                chartLayout.background = chartBg()
                chart1Layout.background = chartBg()
                chart2Layout.background = chartBg()
                chart3Layout.background = chartBg()
                chart4Layout.background = chartBg()
                chart5Layout.background = chartBg()
                chart6Layout.background = chartBg()
                setUserData()
                setPropertyData()
                setAllChartData()
                setAllImages()
                getHeights()
                startActivity(chooserIntent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(this@SummaryActivity, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    fun savePdfData(): String {
        return Gson().toJson(
            PDFDataModel().apply {
                inputData = inputDataEntity
                photos = PhotoData().apply {
                    PROPERTY_COVER =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_COVER)!!
                    PROPERTY_IMAGE_1 =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_1)!!
                    PROPERTY_IMAGE_2 =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_2)!!
                    PROPERTY_IMAGE_3 =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_3)!!
                    PROPERTY_IMAGE_4 =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_4)!!
                    PROPERTY_IMAGE_5 =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_IMAGE_5)!!
                }
                userInfo = UserInfo().apply {
                    USER_NAME_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.USER_NAME_USERINFO)!!
                    COMPANY_NAME_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.COMPANY_NAME_USERINFO)!!
                    EMAIL_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.EMAIL_USERINFO)!!
                    PHONE_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PHONE_USERINFO)!!
                    ADDRESS_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.ADDRESS_USERINFO)!!
                    CITY_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.CITY_USERINFO)!!
                    STATE_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.STATE_USERINFO)!!
                    POSTCODE_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.POSTCODE_USERINFO)!!
                    LOGO_USERINFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.LOGO_USERINFO)!!
                }
                propertyInfo = PropertyInfo().apply {
                    ADDRESS_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO)!!
                    SQUARE_FEET_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.SQUARE_FEET_PROPERTY_INFO)!!
                    BED_ROOMS_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.BED_ROOMS_PROPERTY_INFO)!!
                    BATH_ROOMS_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.BATH_ROOMS_PROPERTY_INFO)!!
                    LOT_SIZE_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.LOT_SIZE_PROPERTY_INFO)!!
                    CITY_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.CITY_PROPERTY_INFO)!!
                    STATE_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.STATE_PROPERTY_INFO)!!
                    POSTCODE_PROPERTY_INFO =
                        session(this@SummaryActivity).getPrefValue(SessionManager.POSTCODE_PROPERTY_INFO)!!
                }
                dafaultData = DefaultData().apply {
                    RENTAL_INCREASES =
                        session(this@SummaryActivity).getPrefValue(SessionManager.RENTAL_INCREASES)!!
                    APPRECIATION_GROWTH =
                        session(this@SummaryActivity).getPrefValue(SessionManager.APPRECIATION_GROWTH)!!
                    PRINCIPAL_PAYMENT =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)!!
                    IMPOUND_ACCOUNT =
                        session(this@SummaryActivity).getPrefValue(SessionManager.IMPOUND_ACCOUNT)!!
                    VACANCY_FACTOR =
                        session(this@SummaryActivity).getPrefValue(SessionManager.VACANCY_FACTOR)!!
                    LOAN_INTREST =
                        session(this@SummaryActivity).getPrefValue(SessionManager.LOAN_INTREST)!!
                    PROPERTY_TAX =
                        session(this@SummaryActivity).getPrefValue(SessionManager.PROPERTY_TAX)!!
                    HOMEOWNERS_INSURANCE =
                        session(this@SummaryActivity).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)!!
                    MAINTENANCE_EXP =
                        session(this@SummaryActivity).getPrefValue(SessionManager.MAINTENANCE_EXP)!!
                }
            }, PDFDataModel::class.java
        )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("GO_BACK"))
    }

}