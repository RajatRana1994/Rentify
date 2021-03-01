package com.rentify.appcode.charts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.inputDataEntity
import com.rentify.appcode.charts.chartclass.*
import com.rentify.appcode.dashboard.DashboardActivity
import com.rentify.appcode.dashboard.propertyinfo.PropertyInfoActivity
import com.rentify.appcode.dashboard.userinfo.UserInfoActivity
import com.rentify.database.theme.SessionManager
import com.rentify.local.ChartFLowModel
import com.rentify.local.ChartLegend
import com.rentify.local.PDFDataModel
import com.rentify.local.UserInfo
import com.rentify.util.Extensions.Companion.addFormatDecimal
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import kotlinx.android.synthetic.main.activity_property_info.*

class ChartFlowActivity : AppCompatActivity(), NextChartPreparation {

    companion object {
        val CashRequirement_TYPE = 1
        val IntRateOfReturn_TYPE = 2
        val TaxBenefitSchedule_TYPE = 3
        val AnnualRentalIncreases_TYPE = 4
        val ImpoundAccount_TYPE = 5
        val AnnualPrincipalReduction_TYPE = 6
        val AppreciationGrowth_TYPE = 7
        val chartFlowList by lazy { mutableListOf<ChartFLowModel>() }
        var TYPE = CashRequirement_TYPE

        ///Used for calculation
        var loanIntrestV = 0.0
        var propertyTaxV = 0.0
        var homeownersV = 0.0
        var repairMaintV = 0.0
        var d2 = 0.0
        var principalPaymentV = 0.0
        var impoundAccountV = 0.0
        var vacancyV = 0.0
        var upratingV = 0.0
        var structureV = 0.0
        var annDepreciationV = 0.0
        var expenses = 0.0
        var cashReqV = 0.0
        var cashReturnV = 0.0
        var operatingExpenseV = 0.0
    }

    var summary_class=false
    val legendsList by lazy { mutableListOf<ChartLegend>() }

    /*    val handler: Handler by lazy { Handler() }
        val runnable by lazy {
            Runnable {
                chartFLowModel?.let {
                    when (TYPE) {
                        CashRequirement_TYPE -> CashRequirement(this, this, it)
                        IntRateOfReturn_TYPE -> IntRateOfReturn(this, this, it)
                        TaxBenefitSchedule_TYPE -> TaxBenefitSchedule(this, this, it)
                        AnnualRentalIncreases_TYPE -> AnnualRentalIncreases(this, this, it)
                        ImpoundAccount_TYPE -> ImpoundAccount(this, this, it)
                        AnnualPrincipalReduction_TYPE -> AnnualPrincipalReduction(this, this, it)
                        AppreciationGrowth_TYPE -> AppreciationGrowth(this, this, it)
                        else -> Toast.makeText(this, "Chart FLow Done!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

            val sumList = mutableListOf<String>()
        var cashReq = ""*/
    var chartFLowModel: ChartFLowModel? = null


    fun prepareValues() {
        loanIntrestV =
            (inputDataEntity.LOANAMOUNT!!.times(inputDataEntity.Loan_Interest!!)).div(100)//
        propertyTaxV =
            (inputDataEntity.Purchase_Price!!.times(inputDataEntity.Property_Tax!!)).div(100)//
        homeownersV =
            (inputDataEntity.Purchase_Price!!.times(inputDataEntity.Homeowners_Insurance!!)).div(100)//
        repairMaintV =
            (inputDataEntity.Purchase_Price!!.times(inputDataEntity.Maintenance_Expense!!)).div(100)//
        d2 =
            (inputDataEntity.Property_Value_after_renovation!!.minus(inputDataEntity.Purchase_Price!!)).minus(
                inputDataEntity.Improvements!!.plus(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!))
            )
        principalPaymentV = (loanIntrestV.times(inputDataEntity.Principal_Payments!!)).div(100)//
        impoundAccountV =
            (inputDataEntity.Purchase_Price!!.times(inputDataEntity.Impound_Account!!)).div(100)//
        vacancyV =
            (inputDataEntity.Market_Rent!!.times(12 * inputDataEntity.Vacancy_Factor!!)).div(100)//management +other exp
        upratingV =
            loanIntrestV + propertyTaxV + homeownersV + repairMaintV + inputDataEntity.Annual_Management!!
        structureV = (inputDataEntity.Purchase_Price!!.times(62.5)).div(100)
        expenses =
            principalPaymentV + impoundAccountV + vacancyV + loanIntrestV + propertyTaxV + homeownersV + repairMaintV + inputDataEntity.Annual_Management!!.plus(inputDataEntity.Annual_other_expenses!!)
        operatingExpenseV =
            loanIntrestV + propertyTaxV + homeownersV + repairMaintV + inputDataEntity.Annual_Management!!.plus(inputDataEntity.Annual_other_expenses!!)/*.div(inputDataEntity.Purchase_Price!!)*/
//        annDepreciationV = ((inputDataEntity.Purchase_Price!!.times(27.5)).div(62.5)).plus(operatingExpenseV)
        annDepreciationV = ((structureV.div(39).plus(operatingExpenseV)).div(inputDataEntity.Purchase_Price!!)).times(100)

        cashReqV =
            inputDataEntity!!.Loan_Down_Payment!! + inputDataEntity.Loan_Cost!! + inputDataEntity.Improvements!!.plus(inputDataEntity.Annual_other_expenses!!)
//        cashReturnV = inputDataEntity.Market_Rent!!.times(12).minus(expenses)
        cashReturnV = inputDataEntity.Cash_on_Cash_return!!

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.statusBarTheme()
//        setContentView(R.layout.activity_chart_flow)
        prepareValues()
        prepareNextData(TYPE)
        chartFLowModel?.let {
            when (TYPE) {
                CashRequirement_TYPE -> CashRequirement(this, this, it)
                IntRateOfReturn_TYPE -> IntRateOfReturn(this, this, it)
                TaxBenefitSchedule_TYPE -> TaxBenefitSchedule(this, this, it)
                AnnualRentalIncreases_TYPE -> AnnualRentalIncreases(this, this, it)
                ImpoundAccount_TYPE -> ImpoundAccount(this, this, it)
                AnnualPrincipalReduction_TYPE -> AnnualPrincipalReduction(this, this, it)
                AppreciationGrowth_TYPE -> AppreciationGrowth(this, this, it)
                else -> Toast.makeText(this, "Chart FLow Done!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun prepareNext(next: Int) {
//        prepareNextData(next)
    }

    override fun triggerNext(next: Int) {
        prepareNextData(next)
        chartFLowModel?.let {
            TYPE = when (next) {
                CashRequirement_TYPE -> CashRequirement_TYPE
                IntRateOfReturn_TYPE -> IntRateOfReturn_TYPE
                TaxBenefitSchedule_TYPE -> TaxBenefitSchedule_TYPE
                AnnualRentalIncreases_TYPE -> AnnualRentalIncreases_TYPE
                ImpoundAccount_TYPE -> ImpoundAccount_TYPE
                AnnualPrincipalReduction_TYPE -> AnnualPrincipalReduction_TYPE
                AppreciationGrowth_TYPE -> AppreciationGrowth_TYPE
                else -> {
                    showDialogAddInfo(getString(R.string.generate_summary_add_property_info_photo),
                        true)
                    8
                }
            }
            if (next != 8) {
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }


    private fun prepareNextData(next: Int, needToTrigger: Boolean = false) {
        when (next) {
            CashRequirement_TYPE -> {
                legendsList.clear()
                legendsList.add(ChartLegend(amount = "$${inputDataEntity.Loan_Down_Payment}", name = getString(R.string.down_payment)))
                legendsList.add(ChartLegend(amount = "$${inputDataEntity.Loan_Cost}", name = getString(R.string.loan_cost)))
                legendsList.add(ChartLegend(amount = "${inputDataEntity.Annual_other_expenses!!}", name = getString(
                                    R.string.other_expensive)))
                legendsList.add(ChartLegend(amount = "$${inputDataEntity.Improvements}", name = getString(R.string.capital_improvement)))
                chartFLowModel = ChartFLowModel(
                    title = getString(R.string.cash_req),
                    pageCount = "$next of 7",
                    chartLegend = legendsList
                )
            }
            IntRateOfReturn_TYPE -> {
                legendsList.clear()
                legendsList.add(ChartLegend(amount = "${inputDataEntity.Percentage_Cash_on_return}%", name = getString(R.string.cash_on_cash_return)))
                legendsList.add(ChartLegend(amount = "${principalPaymentV.div(inputDataEntity.Purchase_Price!!).times(100)}%", name = getString(R.string.principal_reduction)))
                legendsList.add(ChartLegend(amount = "${inputDataEntity.Area_Appreciation_Growth!!.div(4)}%", name = getString(R.string.appreciation_growth)))
                legendsList.add(ChartLegend(amount = "${inputDataEntity.Annual_Rental_Increase!!.times(0.37)}%", name = getString(R.string.annual_rental_increase)))
                legendsList.add(ChartLegend(amount = "$annDepreciationV", name = getString(R.string.annual_depreciation)))
                chartFLowModel = ChartFLowModel(
                    title = getString(R.string.internal_rate_of_return),
                    pageCount = "$next of 7",
                    chartLegend = legendsList,
                    descBelowChart = "${getString(R.string.initial_upside_benefit)}\n(${d2.toString().addFormatDecimal()})"
                )
            }
            TaxBenefitSchedule_TYPE -> {
                legendsList.clear()
                legendsList.add(ChartLegend(amount = "${structureV.div(39)}", name = getString(R.string.sttucture_depreciation)))
                legendsList.add(ChartLegend(amount = "$operatingExpenseV", name = getString(R.string.operating_exp)))
                legendsList.add(ChartLegend(amount = "${(inputDataEntity.Loan_Cost!!.plus(inputDataEntity.Annual_other_expenses!!)).plus(inputDataEntity.Improvements!!)}", name = getString(R.string.loan_cost_other_exp)))
                chartFLowModel = ChartFLowModel(
                    title = getString(R.string.tax_benefit_schedule),
                    pageCount = "$next of 7",
                    chartLegend = legendsList
                )
            }
            AnnualRentalIncreases_TYPE -> {
                legendsList.clear()
                var rentalIncV = inputDataEntity.Market_Rent
                legendsList.add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year1)))
                rentalIncV = rentalIncV!!.plus(rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100))
                legendsList.add(ChartLegend(amount = "${rentalIncV}", name = getString(R.string.year2)))
                rentalIncV = rentalIncV.plus(rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100))
                legendsList.add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year3)))
                rentalIncV = rentalIncV.plus(rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100))
                legendsList.add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year4)))
                rentalIncV = rentalIncV.plus(rentalIncV.times(inputDataEntity.Annual_Rental_Increase!!).div(100))
                legendsList.add(ChartLegend(amount = "$rentalIncV", name = getString(R.string.year5)))
                chartFLowModel = ChartFLowModel(
                    title = getString(R.string.annual_rental_increase),
                    pageCount = "$next of 7",
                    chartLegend = legendsList
                )
            }
            ImpoundAccount_TYPE -> {
                legendsList.clear()//RENTAL VALUE ABOVE = Rental_Deposit
                var impoundV = (impoundAccountV + vacancyV).times(1).plus(inputDataEntity.Rental_Deposit!!)
                legendsList.add(ChartLegend(amount = "$impoundV", name = getString(R.string.year1)))
                impoundV = (impoundAccountV + vacancyV).times(2).plus(inputDataEntity.Rental_Deposit!!)
                legendsList.add(ChartLegend(amount = "$impoundV", name = getString(R.string.year2)))
                impoundV = (impoundAccountV + vacancyV).times(3).plus(inputDataEntity.Rental_Deposit!!)
                legendsList.add(ChartLegend(amount = "$impoundV", name = getString(R.string.year3)))
                impoundV = (impoundAccountV + vacancyV).times(4).plus(inputDataEntity.Rental_Deposit!!)
                legendsList.add(ChartLegend(amount = "$impoundV", name = getString(R.string.year4)))
                impoundV = (impoundAccountV + vacancyV).times(5).plus(inputDataEntity.Rental_Deposit!!)
                legendsList.add(ChartLegend(amount = "$impoundV", name = getString(R.string.year5)))
                chartFLowModel = ChartFLowModel(
                    title = getString(R.string.impound_account),
                    pageCount = "$next of 7",
                    chartLegend = legendsList,
                    descAboveChart = "${inputDataEntity.Rental_Deposit.toString()
                        .addFormatDecimal()}\n${getString(R.string.rental_deposit_on_hold)}"
                )
            }
            AnnualPrincipalReduction_TYPE -> {
                legendsList.clear()
                var principalReductv = (principalPaymentV).times(1)
                legendsList.add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year1)))
                principalReductv = (principalPaymentV).times(2)
                legendsList.add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year2)))
                principalReductv = (principalPaymentV).times(3)
                legendsList.add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year3)))
                principalReductv = (principalPaymentV).times(4)
                legendsList.add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year4)))
                principalReductv = (principalPaymentV).times(5)
                legendsList.add(ChartLegend(amount = "$principalReductv", name = getString(R.string.year5)))
                chartFLowModel = ChartFLowModel(
                    title = getString(R.string.annual_principal_reduction),
                    pageCount = "$next of 7",
                    chartLegend = legendsList
                )
            }
            AppreciationGrowth_TYPE -> {
                legendsList.clear()
                var annualEquityV = inputDataEntity.Property_Value_after_renovation!! + (inputDataEntity.Property_Value_after_renovation!!.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
                legendsList.add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year1)))
                annualEquityV += (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
                legendsList.add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year2)))
                annualEquityV += (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
                legendsList.add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year3)))
                annualEquityV += (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
                legendsList.add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year4)))
                annualEquityV += (annualEquityV.times(inputDataEntity.Area_Appreciation_Growth!!).div(100))
                legendsList.add(ChartLegend(amount = "$annualEquityV", name = getString(R.string.year5)))
                chartFLowModel = ChartFLowModel(title = getString(R.string.appreciation_growth), pageCount = "$next of 7", chartLegend = legendsList)
            }
            else -> {
            }
        }
        if (needToTrigger) {
            triggerNext(next)
        }
    }

    var alertDialog: AlertDialog? = null
//    private fun generateSummaryReport() {
//        var isPropertyPhotos = false
//        GlobalScope.launch {
//            launch(Dispatchers.IO) {
//                isPropertyPhotos =
//                    getPhotoDB(this@ChartFlowActivity).photoDao().getAll().isEmpty().not()
//            }
//        }
        ///check property data
//        GlobalScope.launch {
//            delay(500)
//            launch(Dispatchers.Main) {
//                if (session(this@ChartFlowActivity).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO)!!
//                        .isEmpty() && isPropertyPhotos.not()
//                )
//                    showDialogAddInfo(
//                        getString(R.string.generate_summary_add_property_info_photo),
//                        2
//                    )
//                else if (isPropertyPhotos.not()) showDialogAddInfo(
//                    getString(R.string.generate_summary_add_property_photo),
//                    0
//                )
//                else if (session(this@ChartFlowActivity).getPrefValue(SessionManager.ADDRESS_PROPERTY_INFO)!!
//                        .isEmpty()
//                )
//                    showDialogAddInfo(getString(R.string.generate_summary_add_property_info), 1)
//                else {
//                    showDialogAddInfo(
//                        getString(R.string.generate_summary_add_property_info_photo),
//                        2
//                    )
//                    startActivity(Intent(this@ChartFlowActivity, SummaryActivity::class.java))
//                }
//            }
//        }

//    }

    fun showDialogAddInfo(message: String, addinfo: Boolean) {
        val builder = AlertDialog.Builder(this)
        val viewDialog = layoutInflater.inflate(R.layout.dialog_generate_report, null)
        builder.setView(viewDialog)
        alertDialog = builder.create()

        viewDialog.findViewById<TextView>(R.id.tvKeyStatus).text = message
        viewDialog.findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            alertDialog!!.cancel()
        }
        viewDialog.findViewById<TextView>(R.id.btnConfirm).setOnClickListener {
            if (summary_class==true){
                val intent = Intent(this, PropertyInfoActivity::class.java)
            // To pass any data to next activity
                intent.putExtra("summary", true)
                intent.putExtra("addinfo", true)
                startActivity(intent)
            // start your next act
//                startActivity(
//                    PropertyInfoActivity.createIntent(
//                        this@ChartFlowActivity,
//                        true,
//                        addinfo
//                    )
//                )
            }else {
                if (intent.hasExtra("pdf_data")) {
                    val pdfdata = (Gson().fromJson<PDFDataModel>(
                        intent.getStringExtra("pdf_data"),
                        PDFDataModel::class.java
                    )!!)
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.ADDRESS_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.ADDRESS_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.SQUARE_FEET_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.SQUARE_FEET_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.BED_ROOMS_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.BED_ROOMS_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.BATH_ROOMS_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.BATH_ROOMS_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.LOT_SIZE_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.LOT_SIZE_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.CITY_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.CITY_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.STATE_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.STATE_PROPERTY_INFO
                    )
                    RentifyApp.session(this).savePrefValue(
                        SessionManager.POSTCODE_PROPERTY_INFO,
                        pdfdata.propertyInfo!!.POSTCODE_PROPERTY_INFO
                    )

                    RentifyApp.session(this@ChartFlowActivity).apply {
                        savePrefValue(
                            SessionManager.PROPERTY_COVER,
                            pdfdata.photos!!.PROPERTY_COVER
                        )
                        savePrefValue(
                            SessionManager.PROPERTY_IMAGE_1,
                            pdfdata.photos!!.PROPERTY_IMAGE_1
                        )
                        savePrefValue(
                            SessionManager.PROPERTY_IMAGE_2,
                            pdfdata.photos!!.PROPERTY_IMAGE_2
                        )
                        savePrefValue(
                            SessionManager.PROPERTY_IMAGE_3,
                            pdfdata.photos!!.PROPERTY_IMAGE_3
                        )
                        savePrefValue(
                            SessionManager.PROPERTY_IMAGE_4,
                            pdfdata.photos!!.PROPERTY_IMAGE_4
                        )
                        savePrefValue(
                            SessionManager.PROPERTY_IMAGE_5,
                            pdfdata.photos!!.PROPERTY_IMAGE_5
                        )
                    }
                    startActivity(
                        UserInfoActivity.createIntent(
                            this@ChartFlowActivity,
                            false,
                            false
                        )
                    )
                } else {startActivity(
                    PropertyInfoActivity.createIntent(
                        this@ChartFlowActivity,
                        true,
                        addinfo
                    )
                )}
//            if (addinfo != 1)
//                startActivity(PhotoActivity.createIntent(this@ChartFlowActivity, true, addinfo))
//            else if (addinfo == 1)
//                startActivity(
//                    PropertyInfoActivity.createIntent(
//                        this@ChartFlowActivity,
//                        true,
//                        addinfo
//                    )
//                )
            }
            alertDialog!!.cancel()
        }
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()

        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog!!.window?.getAttributes())
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        layoutParams.width = dialogWindowWidth
        alertDialog!!.window?.attributes = layoutParams
    }


    override fun onBackPressed() {
        if (TYPE == CashRequirement_TYPE || TYPE < 0)
            super.onBackPressed()
        else {
            if (alertDialog != null && alertDialog!!.isShowing) {
                TYPE = AppreciationGrowth_TYPE
                alertDialog!!.dismiss()
            } else {
                TYPE -= 1
                prepareNextData(TYPE, true)
            }
        }
    }

    fun openSetting() {
        if (intent.hasExtra("pdf_data")){
            showDoneDialog(msg =getString(R.string.cant_open_in_edit_mode)) {}
        }else
        startActivity(Intent(this@ChartFlowActivity, DashboardActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
//        handler.removeCallbacks(runnable)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broardReceiver,
            IntentFilter("GO_BACK_SUMMARY")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broardReceiver)
    }

    val broardReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            summary_class=true
        }
    }
}