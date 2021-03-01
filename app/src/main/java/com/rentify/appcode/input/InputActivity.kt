package com.rentify.appcode.input

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.rentify.R
import com.rentify.RentifyApp.Companion.inputDataEntity
import com.rentify.RentifyApp.Companion.session
import com.rentify.appcode.charts.ChartFlowActivity
import com.rentify.appcode.dashboard.DashboardActivity
import com.rentify.database.theme.SessionManager
import com.rentify.local.PDFDataModel
import com.rentify.util.Extensions.Companion.addAmountWatcher
import com.rentify.util.Extensions.Companion.addFormatDecimal
import com.rentify.util.Extensions.Companion.addPercentWatcher
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.clearFormat
import com.rentify.util.Extensions.Companion.emptyIfZero
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.Extensions.Companion.viewTheme
import com.rentify.util.Extensions.Companion.zeroIfEmpty
import kotlinx.android.synthetic.main.activity_input.*

@RequiresApi(Build.VERSION_CODES.M)
class InputActivity : AppCompatActivity(), View.OnScrollChangeListener {
    val clear_extra: Boolean by lazy { intent.getBooleanExtra("clear_extra", false) }
    val from_novice: Boolean by lazy { intent.getBooleanExtra("from_novice", false) }

    companion object {
        ///Used for calculation
        var otherExpV = 0.0
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun getValue(edt: EditText): Double {
        return if (edt.text.toString().clearFormat().isEmpty()) 0.0 else edt.text.toString()
            .clearFormat().toDouble()
    }

    fun prepareValues() {
        try {
            val LOANAMOUNT = getValue(edtPurchasePrice).minus(getValue(edtLoanDownPayment))
            loanIntrestV = (LOANAMOUNT.times(getValue(edtLoanInterest))).div(100)//
            propertyTaxV = (getValue(edtPurchasePrice).times(getValue(edtPropertyTax))).div(100)//
            homeownersV =
                (getValue(edtPurchasePrice).times(getValue(edtHomeownersInsurance))).div(100)//
            repairMaintV =
                (getValue(edtPurchasePrice).times(getValue(edtMaintenanceExpense))).div(100)//
            principalPaymentV = (loanIntrestV.times(getValue(edtPrincipalPayments))).div(100)//
            impoundAccountV =
                (getValue(edtPurchasePrice).times(getValue(edtImpoundAccount))).div(100)//
            vacancyV = (getValue(edtMarketRent).times(12 * getValue(edtVacancyFactor))).div(100)

            expenses =
                principalPaymentV + impoundAccountV + vacancyV + loanIntrestV + propertyTaxV + homeownersV + repairMaintV + getValue(
                    edtAnnualManagement
                ).plus(otherExpV)
            cashReqV =
                getValue(edtLoanDownPayment) + getValue(edtLoanCost) + getValue(edtImprovements).plus(
                    otherExpV
                )
            cashReturnV = getValue(edtMarketRent).times(12).minus(expenses)
            val allexp =
                loanIntrestV + propertyTaxV + homeownersV + repairMaintV + principalPaymentV + impoundAccountV + vacancyV + otherExpV + getValue(
                    edtAnnualManagement
                )
            edtAnnualExpenses.text = allexp.toString().addFormatDecimal()
            edtMonthlyCashRequirement.text = (allexp.div(12)).toString().addFormatDecimal()
            edtAnnualIncome.text = getValue(edtMarketRent).times(12).toString().addFormatDecimal()
            edtCashonCashreturn.text = cashReturnV.toString().addFormatDecimal()
            edtPercentageCashonReturn.text =
                "${cashReturnV.div(cashReqV).times(100).toString().addFormatDecimal(false)}%"
            edtAnnualNetOperatingIncome.text = cashReturnV.toString().addFormatDecimal()
            edtCashonCashreturn.setTextColor(
                ContextCompat.getColor(
                    this@InputActivity,
                    if (edtCashonCashreturn.text.toString().clearFormat()
                            .toDouble() < 0
                    ) R.color.red else R.color.forest_green
                )
            )
            edtPercentageCashonReturn.setTextColor(
                ContextCompat.getColor(
                    this@InputActivity,
                    if (edtPercentageCashonReturn.text.toString().clearFormat()
                            .toDouble() < 0
                    ) R.color.red else R.color.forest_green
                )
            )
            edtAnnualNetOperatingIncome.setTextColor(
                ContextCompat.getColor(
                    this@InputActivity,
                    if (edtAnnualNetOperatingIncome.text.toString().clearFormat()
                            .toDouble() < 0
                    ) R.color.red else R.color.forest_green
                )
            )
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun defualtValues() {
        //default values
        edtAnnualRentalIncrease.setText(
            session(this).getPrefValue(SessionManager.RENTAL_INCREASES)!!.emptyIfZero()
        )
        edtAreaAppreciationGrowth.setText(
            session(this).getPrefValue(SessionManager.APPRECIATION_GROWTH)!!.emptyIfZero()
        )
        edtPrincipalPayments.setText(
            session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)!!.emptyIfZero()
        )
        edtImpoundAccount.setText(
            session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)!!.emptyIfZero()
        )
        edtVacancyFactor.setText(
            session(this).getPrefValue(SessionManager.VACANCY_FACTOR)!!.emptyIfZero()
        )
        edtLoanInterest.setText(
            session(this).getPrefValue(SessionManager.LOAN_INTREST)!!.emptyIfZero()
        )
        edtPropertyTax.setText(
            session(this).getPrefValue(SessionManager.PROPERTY_TAX)!!.emptyIfZero()
        )
        edtHomeownersInsurance.setText(
            session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)!!.emptyIfZero()
        )
        edtMaintenanceExpense.setText(
            session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)!!.emptyIfZero()
        )
        //default values

        edtAnnualRentalIncrease.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtAnnualRentalIncrease.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        edtAreaAppreciationGrowth.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtAreaAppreciationGrowth.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        edtPrincipalPayments.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtPrincipalPayments.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        edtImpoundAccount.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtImpoundAccount.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }
        edtVacancyFactor.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtVacancyFactor.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        edtLoanInterest.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtLoanInterest.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }
        edtPropertyTax.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtPropertyTax.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        edtHomeownersInsurance.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtHomeownersInsurance.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }
        edtMaintenanceExpense.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode==KeyEvent.KEYCODE_DEL){
                edtMaintenanceExpense.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        if (clear_extra) {
            edtPurchasePrice.setText("")
            edtPropertyValueRenovation.setText("")
            edtMarketRent.setText("")
            edtRentalDeposit.setText("")
            edtLoanDownPayment.setText("")
            edtLoanCost.setText("")
            edtImprovements.setText("")
            edtAnnualManagement.setText("")
            edtAnnualOtherexpenses.setText("")
            edtLoanInterest.setText("")
            edtPropertyTax.setText("")
            edtHomeownersInsurance.setText("")
            edtMaintenanceExpense.setText("")
            edtAnnualRentalIncrease.setText("")
            edtAreaAppreciationGrowth.setText("")
            edtPrincipalPayments.setText("")
            edtVacancyFactor.setText("")
        }

        headerTool.setOnClickListener { }
        if (intent.hasExtra("pdf_data")) {
            inputDataEntity = (Gson().fromJson<PDFDataModel>(
                intent.getStringExtra("pdf_data"),
                PDFDataModel::class.java
            )!!).inputData!!
            setNoviceData(true)
        } else {
            setNoviceData(false)
        }
        if (from_novice.not()) defualtValues()
        edtPurchasePrice.addAmountWatcher {

        }

        edtPurchasePrice.addTextChangedListener(edtPurchasePrice.addAmountWatcher {
            prepareValues()
            lblPurchasePrice.visibility =
                if (edtPurchasePrice.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        })
        edtPropertyValueRenovation.addTextChangedListener(edtPropertyValueRenovation.addAmountWatcher {
            prepareValues()
            lblPropertyValueRenovation.visibility =
                if (edtPropertyValueRenovation.text.toString().trim()
                        .equals("$")
                ) View.VISIBLE else View.GONE
        })
        edtMarketRent.addTextChangedListener(edtMarketRent.addAmountWatcher {
            prepareValues()
            lblMarketRent.visibility =
                if (edtMarketRent.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        })
        edtRentalDeposit.addTextChangedListener(edtRentalDeposit.addAmountWatcher {
            prepareValues()
            lblRentalDeposit.visibility =
                if (edtRentalDeposit.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        })
        edtLoanDownPayment.addTextChangedListener(edtLoanDownPayment.addAmountWatcher {
            prepareValues()
            lblLoanDownPayment.visibility = if (edtLoanDownPayment.text.toString().trim()
                    .equals("$")
            ) View.VISIBLE else View.GONE
        })
        edtLoanCost.addTextChangedListener(edtLoanCost.addAmountWatcher {
            prepareValues()
            lblLoanCost.visibility =
                if (edtLoanCost.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        })
        edtImprovements.addTextChangedListener(edtImprovements.addAmountWatcher {
            prepareValues()
            lblImprovements.visibility =
                if (edtImprovements.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        })
        edtAnnualManagement.addTextChangedListener(edtAnnualManagement.addAmountWatcher {
            prepareValues()
            lblAnnualManagement.visibility = if (edtAnnualManagement.text.toString().trim()
                    .equals("$")
            ) View.VISIBLE else View.GONE
        })
        edtAnnualOtherexpenses.addTextChangedListener(edtAnnualOtherexpenses.addAmountWatcher {
            prepareValues()
            lblAnnualOtherexpenses.visibility = if (edtAnnualOtherexpenses.text.toString().trim()
                    .equals("$")
            ) View.VISIBLE else View.GONE
        })
        edtAnnualRentalIncrease.addTextChangedListener(edtAnnualRentalIncrease.addPercentWatcher { prepareValues() })
        edtAreaAppreciationGrowth.addTextChangedListener(edtAreaAppreciationGrowth.addPercentWatcher { prepareValues() })
        edtPrincipalPayments.addTextChangedListener(edtPrincipalPayments.addPercentWatcher { prepareValues() })
        edtImpoundAccount.addTextChangedListener(edtImpoundAccount.addPercentWatcher { prepareValues() })
        edtVacancyFactor.addTextChangedListener(edtVacancyFactor.addPercentWatcher { prepareValues() })
        edtLoanInterest.addTextChangedListener(edtLoanInterest.addPercentWatcher { prepareValues() })
        edtPropertyTax.addTextChangedListener(edtPropertyTax.addPercentWatcher { prepareValues() })
        edtHomeownersInsurance.addTextChangedListener(edtHomeownersInsurance.addPercentWatcher { prepareValues() })
        edtMaintenanceExpense.addTextChangedListener(edtMaintenanceExpense.addPercentWatcher { prepareValues() })
        lblPurchasePrice.visibility =
            if (edtPurchasePrice.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblPropertyValueRenovation.visibility =
            if (edtPropertyValueRenovation.text.toString().trim()
                    .equals("$")
            ) View.VISIBLE else View.GONE
        lblMarketRent.visibility =
            if (edtMarketRent.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblRentalDeposit.visibility =
            if (edtRentalDeposit.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblLoanDownPayment.visibility =
            if (edtLoanDownPayment.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblLoanCost.visibility =
            if (edtLoanCost.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblImprovements.visibility =
            if (edtImprovements.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblAnnualManagement.visibility =
            if (edtAnnualManagement.text.toString().trim().equals("$")) View.VISIBLE else View.GONE
        lblAnnualOtherexpenses.visibility = if (edtAnnualOtherexpenses.text.toString().trim()
                .equals("$")
        ) View.VISIBLE else View.GONE
//        edtAnnualExpenses.addTextChangedListener(edtAnnualExpenses.addAmountWatcher())  9 var
//        edtMonthlyCashRequirement.addTextChangedListener(edtMonthlyCashRequirement.addAmountWatcher())    //edtAnnualExpenses/12
//        edtAnnualIncome.addTextChangedListener(edtAnnualIncome.addAmountWatcher())    // rent*12
//        edtCashonCashreturn.addTextChangedListener(edtCashonCashreturn.addAmountWatcher()) //         cashReturnV              (if - red + forest green)
//        edtPercentageCashonReturn.addTextChangedListener(edtPercentageCashonReturn.addPercentWatcher()) //           cocr%     (if - red + forest green)
//        edtAnnualNetOperatingIncome.addTextChangedListener(edtAnnualNetOperatingIncome.addAmountWatcher())//   cashReturnV     (if - red + forest green)


        btnNext.setOnSingleClickListener {
            if (edtPurchasePrice.text!!.isEmpty()) {
                inputDataEntity.Purchase_Price = 0.0
            } else {
                inputDataEntity.Purchase_Price =
                    edtPurchasePrice.text.toString().clearFormat().toDouble()
            }
            if (!edtPropertyValueRenovation.text!!.isEmpty()) {
                inputDataEntity.Property_Value_after_renovation =
                    edtPropertyValueRenovation.text.toString().clearFormat().toDouble()
            }
            if (!edtMarketRent.text!!.isEmpty()) {
                inputDataEntity.Market_Rent = edtMarketRent.text.toString().clearFormat().toDouble()
            }
            if (!edtRentalDeposit.text!!!!.isEmpty()) {
                inputDataEntity.Rental_Deposit =
                    edtRentalDeposit.text.toString().clearFormat().toDouble()
            }
            if (!edtLoanDownPayment.text!!!!.isEmpty()) {
                inputDataEntity.Loan_Down_Payment =
                    edtLoanDownPayment.text.toString().clearFormat().toDouble()
            }
            if (!edtLoanCost.text!!.isEmpty()) {
                inputDataEntity.Loan_Cost = edtLoanCost.text.toString().clearFormat().toDouble()
            }
            if (!edtImprovements.text!!.isEmpty()) {
                inputDataEntity.Improvements =
                    edtImprovements.text.toString().clearFormat().toDouble()
            }
            if (!edtAnnualRentalIncrease.text.isEmpty()) {
                inputDataEntity.Annual_Rental_Increase =
                    if (edtAnnualRentalIncrease.text.toString().clearFormat().zeroIfEmpty().toDouble() > 0.0) edtAnnualRentalIncrease.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.RENTAL_INCREASES)!!.clearFormat()
                        .toDouble()
            }
            if (!edtAreaAppreciationGrowth.text.isEmpty()) {
                inputDataEntity.Area_Appreciation_Growth =
                    if (edtAreaAppreciationGrowth.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtAreaAppreciationGrowth.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.APPRECIATION_GROWTH)!!
                        .clearFormat().toDouble()
            }
            if (!edtPrincipalPayments.text.isEmpty()) {
                inputDataEntity.Principal_Payments =
                    if (edtPrincipalPayments.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtPrincipalPayments.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)!!
                        .clearFormat().toDouble()
            }
            if (!edtImpoundAccount.text.isEmpty()) {
                inputDataEntity.Impound_Account =
                    if (edtImpoundAccount.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtImpoundAccount.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)!!.clearFormat()
                        .toDouble()
            }
            if (!edtVacancyFactor.text.isEmpty()) {
                inputDataEntity.Vacancy_Factor =
                    if (edtVacancyFactor.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtVacancyFactor.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.VACANCY_FACTOR)!!.clearFormat()
                        .toDouble()
            }
            if (!edtLoanInterest.text.isEmpty()) {
                inputDataEntity.Loan_Interest =
                    if (edtLoanInterest.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtLoanInterest.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.LOAN_INTREST)!!.clearFormat()
                        .toDouble()
            }
            if (!edtPropertyTax.text.isEmpty()) {
                inputDataEntity.Property_Tax =
                    if (edtPropertyTax.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtPropertyTax.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.PROPERTY_TAX)!!.clearFormat()
                        .toDouble()
            }
            if (!edtHomeownersInsurance.text.isEmpty()) {
                inputDataEntity.Homeowners_Insurance =
                    if (edtHomeownersInsurance.text.toString().clearFormat().zeroIfEmpty()
                            .toDouble() > 0.0
                    ) edtHomeownersInsurance.text.toString().clearFormat().zeroIfEmpty().toDouble()
                    else session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)!!
                        .clearFormat().toDouble()
            }
            if (!edtMaintenanceExpense.text.isEmpty()) {
                inputDataEntity.Maintenance_Expense =
                    edtMaintenanceExpense.text.toString().clearFormat().toDouble()
            }
            if (!edtAnnualManagement.text!!.isEmpty()) {
                inputDataEntity.Annual_Management =
                    edtAnnualManagement.text.toString().clearFormat().toDouble()
            }
            if (!edtAnnualOtherexpenses.text!!.isEmpty()) {
                inputDataEntity.Annual_other_expenses =
                    edtAnnualOtherexpenses.text.toString().clearFormat().toDouble()
            }
            if (!edtAnnualExpenses.text.isEmpty()) {
                inputDataEntity.Annual_Expenses =
                    edtAnnualExpenses.text.toString().clearFormat().toDouble()
            }
            if (!edtMonthlyCashRequirement.text.isEmpty()) {
                inputDataEntity.Monthly_cash_requirement =
                    edtMonthlyCashRequirement.text.toString().clearFormat().toDouble()
            }
            if (!edtAnnualIncome.text.isEmpty()) {
                inputDataEntity.Annual_Income =
                    edtAnnualIncome.text.toString().clearFormat().toDouble()
            }
            if (!edtCashonCashreturn.text.isEmpty()) {
                inputDataEntity.Cash_on_Cash_return =
                    edtCashonCashreturn.text.toString().clearFormat().toDouble()
            }
            if (!edtPercentageCashonReturn.text.isEmpty()) {
                inputDataEntity.Percentage_Cash_on_return =
                    edtPercentageCashonReturn.text.toString().clearFormat().replace("∞", "0")
                        .replace(
                            "-∞",
                            "0"
                        ).toDouble()
            }
            if (!edtAnnualNetOperatingIncome.text.isEmpty()) {
                inputDataEntity.Annual_Net_Operating_Income =
                    edtAnnualNetOperatingIncome.text.toString().clearFormat().toDouble()
            }
            inputDataEntity.LOANAMOUNT = inputDataEntity.Purchase_Price!!.minus(
                inputDataEntity.Loan_Down_Payment!!
            )

            ChartFlowActivity.TYPE = ChartFlowActivity.CashRequirement_TYPE
            startActivity(Intent(this@InputActivity, ChartFlowActivity::class.java).apply {
                if (intent.hasExtra("pdf_data")) putExtra(
                    "pdf_data",
                    intent.getStringExtra("pdf_data")
                )
            })

        }


        btnBack.setOnClickListener { onBackPressed() }
        ivSetting.setOnSingleClickListener {
            if (intent.hasExtra("pdf_data")) {
                showDoneDialog(msg = getString(R.string.cant_open_in_edit_mode)) {}
            } else
                startActivity(
                    Intent(
                        this,
                        DashboardActivity::class.java
                    )
                )
        }

    }

    private fun setNoviceData(pdfData: Boolean) {
        edtPurchasePrice.setText(
            if (inputDataEntity.Purchase_Price!! > 0) inputDataEntity.Purchase_Price.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtPropertyValueRenovation.setText(
            if (inputDataEntity.Property_Value_after_renovation!! > 0) inputDataEntity.Property_Value_after_renovation.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtMarketRent.setText(
            if (inputDataEntity.Market_Rent!! > 0) inputDataEntity.Market_Rent.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtRentalDeposit.setText(
            if (inputDataEntity.Rental_Deposit!! > 0) inputDataEntity.Rental_Deposit.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtLoanDownPayment.setText(
            if (inputDataEntity.Loan_Down_Payment!! > 0) inputDataEntity.Loan_Down_Payment.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtLoanCost.setText(
            if (inputDataEntity.Loan_Cost!! > 0) inputDataEntity.Loan_Cost.toString().clearFormat()
                .addFormatDecimal() else ""
        )
        edtImprovements.setText(
            if (inputDataEntity.Improvements!! > 0) inputDataEntity.Improvements.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtAnnualRentalIncrease.setText(
            if (inputDataEntity.Annual_Rental_Increase!! > 0) inputDataEntity.Annual_Rental_Increase.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtAreaAppreciationGrowth.setText(
            if (inputDataEntity.Area_Appreciation_Growth!! > 0) inputDataEntity.Area_Appreciation_Growth.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtPrincipalPayments.setText(
            if (inputDataEntity.Principal_Payments!! > 0) inputDataEntity.Principal_Payments.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtImpoundAccount.setText(
            if (inputDataEntity.Impound_Account!! > 0) inputDataEntity.Impound_Account.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtVacancyFactor.setText(
            if (inputDataEntity.Vacancy_Factor!! > 0) inputDataEntity.Vacancy_Factor.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtLoanInterest.setText(
            if (inputDataEntity.Loan_Interest!! > 0) inputDataEntity.Loan_Interest.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtPropertyTax.setText(
            if (inputDataEntity.Property_Tax!! > 0) inputDataEntity.Property_Tax.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtHomeownersInsurance.setText(
            if (inputDataEntity.Homeowners_Insurance!! > 0) inputDataEntity.Homeowners_Insurance.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtMaintenanceExpense.setText(
            if (inputDataEntity.Maintenance_Expense!! > 0) inputDataEntity.Maintenance_Expense.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtAnnualManagement.setText(
            if (inputDataEntity.Annual_Management!! > 0) inputDataEntity.Annual_Management.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        edtAnnualOtherexpenses.setText(
            if (inputDataEntity.Annual_other_expenses!! > 0) inputDataEntity.Annual_other_expenses.toString()
                .clearFormat().addFormatDecimal() else ""
        )
        if (pdfData) {
            edtAnnualExpenses.setText(
                if (inputDataEntity.Annual_Expenses!! > 0) inputDataEntity.Annual_Expenses.toString()
                    .clearFormat().addFormatDecimal() else ""
            )
            edtMonthlyCashRequirement.setText(
                if (inputDataEntity.Monthly_cash_requirement!! > 0) inputDataEntity.Monthly_cash_requirement.toString()
                    .clearFormat().addFormatDecimal() else ""
            )
            edtAnnualIncome.setText(
                if (inputDataEntity.Annual_Income!! > 0) inputDataEntity.Annual_Income.toString()
                    .clearFormat().addFormatDecimal() else ""
            )
            edtCashonCashreturn.setText(
                if (inputDataEntity.Cash_on_Cash_return!! > 0) inputDataEntity.Cash_on_Cash_return.toString()
                    .clearFormat().addFormatDecimal() else ""
            )
            edtPercentageCashonReturn.setText(
                if (inputDataEntity.Percentage_Cash_on_return!! > 0) inputDataEntity.Percentage_Cash_on_return.toString()
                    .clearFormat().addFormatDecimal() else ""
            )
            edtAnnualNetOperatingIncome.setText(
                if (inputDataEntity.Annual_Net_Operating_Income!! > 0) inputDataEntity.Annual_Net_Operating_Income.toString()
                    .clearFormat().addFormatDecimal() else ""
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView2.setOnScrollChangeListener(this)
        }else{
            if ("$0.00".equals(edtCashonCashreturn.text.toString())) prepareValues()
        }
    }


//    private fun addInputs(input: AllInputModel) {
//        val view = LayoutInflater.from(this).inflate(R.layout.item_input_edit, llInflater, false)
//        view.findViewById<TextView>(R.id.edtTitle).text = input.title
//
//        view.findViewById<EditText>(R.id.edtAmount).apply {
//            if (input.amountType == RentifyApp.INPUT.AMOUNT) {
//                setText(if (input.amount.equals("0.00").not()) input.amount else "")
//                if (input.amount.equals("0.00")) hint = "$0.00"
//                addTextChangedListener(addAmountWatcher())
//            } else {
//                setText(if (input.percent.equals("0").not()) "${input.percent}%" else "")
//                if (input.percent.equals("0")) hint = "0%"
//                addTextChangedListener(addPercentWatcher())
//            }
//            viewTheme()
//        }
//        llInflater.addView(view)
//    }

    override fun onResume() {
        super.onResume()
        refreshTheme()
//        defualtValues()
//        edtPurchasePrice.setText("")
//        edtPropertyValueRenovation.setText("")
//        edtMarketRent.setText("")
//        edtRentalDeposit.setText("")
//        edtLoanDownPayment.setText("")
//        edtLoanCost.setText("")
//        edtImprovements.setText("")
//        edtAnnualManagement.setText("")
//        edtAnnualOtherexpenses.setText("")
    }

    fun refreshTheme() {
        window!!.statusBarTheme()
        headerTool.headerTheme()
        btnNext.bgTheme()
        btnBack.bgTheme()
        edtPurchasePrice.viewTheme()
        edtPropertyValueRenovation.viewTheme()
        edtMarketRent.viewTheme()
        edtRentalDeposit.viewTheme()
        edtLoanDownPayment.viewTheme()
        edtLoanCost.viewTheme()
        edtImprovements.viewTheme()
        edtAnnualRentalIncrease.viewTheme()
        edtAreaAppreciationGrowth.viewTheme()
        edtPrincipalPayments.viewTheme()
        edtImpoundAccount.viewTheme()
        edtVacancyFactor.viewTheme()
        edtLoanInterest.viewTheme()
        edtPropertyTax.viewTheme()
        edtHomeownersInsurance.viewTheme()
        edtMaintenanceExpense.viewTheme()
        edtAnnualManagement.viewTheme()
        edtAnnualOtherexpenses.viewTheme()
        edtAnnualExpenses.viewTheme()
        edtMonthlyCashRequirement.viewTheme()
        edtAnnualIncome.viewTheme()
        edtCashonCashreturn.viewTheme()
        edtPercentageCashonReturn.viewTheme()
        edtAnnualNetOperatingIncome.viewTheme()
    }

    override fun onBackPressed() {
        if (intent.hasExtra("pdf_data")) super.onBackPressed() else {
            if (session(this).getPrefValue(SessionManager.WORKINGMODE).equals("0").not())
                finishAffinity()
            else
                super.onBackPressed()
        }
    }

    override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
        if ("$0.00" == edtCashonCashreturn.text.toString()) prepareValues()
    }


}