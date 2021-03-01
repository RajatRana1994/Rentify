package com.rentify.appcode

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.rentify.R
import com.rentify.RentifyApp
import com.rentify.RentifyApp.Companion.inputDataEntity
import com.rentify.appcode.dashboard.DashboardActivity
import com.rentify.appcode.input.InputActivity
import com.rentify.database.theme.SessionManager
import com.rentify.local.AllEntryModel
import com.rentify.util.Extensions.Companion.addAmountWatcher
import com.rentify.util.Extensions.Companion.addPercentWatcher
import com.rentify.util.Extensions.Companion.bgTheme
import com.rentify.util.Extensions.Companion.clearFormat
import com.rentify.util.Extensions.Companion.emptyIfZero
import com.rentify.util.Extensions.Companion.headerTheme
import com.rentify.util.Extensions.Companion.setOnSingleClickListener
import com.rentify.util.Extensions.Companion.showDoneDialog
import com.rentify.util.Extensions.Companion.statusBarTheme
import com.rentify.util.Extensions.Companion.zeroIfEmpty
import kotlinx.android.synthetic.main.activity_input.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btnBack
import kotlinx.android.synthetic.main.activity_main.btnNext
import kotlinx.android.synthetic.main.activity_main.headerTool
import kotlinx.android.synthetic.main.activity_main.ivSetting
import kotlinx.android.synthetic.main.activity_main.titleHeader

class MainActivity : AppCompatActivity(), () -> Unit {
    var currentCount = -1

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        RentifyApp().addEnteries() // add list
        RentifyApp.allEntries.clear()
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.purchase_price,
                getString(R.string.whats_purchase_price)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.new_property_value,
                getString(R.string.whats_new_property_value)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.market_rent,
                getString(R.string.whats_amount_of_rent)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.rental_deposit,
                getString(R.string.not_counting_first_rent)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.loan_down_payment,
                getString(R.string.how_much_downpayment),
                showPurchase = true
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.loan_cost,
                getString(R.string.any_loan_fee)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.improvements,
                getString(R.string.enter_improvement_cost)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.yearly_rental_inc, getString(
                    R.string.enter_yearly_rental_increase, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.RENTAL_INCREASES) ?: "3.00%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.RENTAL_INCREASES) ?: "3.00%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.area_appreciation,
                getString(R.string.yearly_appreciation_rate),
                amountType = RentifyApp.INPUT.PERCENT,
                percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.APPRECIATION_GROWTH) ?: "3.00%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.principal_payment, getString(
                    R.string.per_loan_payment_to_principal, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.PRINCIPAL_PAYMENT) ?: "25.00%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.PRINCIPAL_PAYMENT) ?: "25.00%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.impound_account, getString(
                    R.string.enter_impound_account, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.IMPOUND_ACCOUNT) ?: "0.33%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.IMPOUND_ACCOUNT) ?: "0.33%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.vacancy_rate, getString(
                    R.string.budget_for_vacancy_rate, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.VACANCY_FACTOR) ?: "0.00%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.VACANCY_FACTOR) ?: "0.00%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.loan_intrest,
                getString(R.string.intrest_rate_for_lender),
                amountType = RentifyApp.INPUT.PERCENT,
                percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.LOAN_INTREST) ?: "0.00%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.property_tax, getString(
                    R.string.whats_property_tax_rate, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.PROPERTY_TAX) ?: "1.125%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.PROPERTY_TAX) ?: "1.125%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.homeowners_insurance, getString(
                    R.string.whats_homeowners_insurance, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.HOMEOWNERS_INSURANCE) ?: "0.25%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.HOMEOWNERS_INSURANCE) ?: "0.25%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.maintenance_exp, getString(
                    R.string.cost_of_repair_maintenance, (RentifyApp.session(this)
                        .getPrefValue(SessionManager.MAINTENANCE_EXP) ?: "0.44%")
                ), amountType = RentifyApp.INPUT.PERCENT, percent = (RentifyApp.session(this)
                    .getPrefValue(SessionManager.MAINTENANCE_EXP) ?: "0.44%").emptyIfZero()
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.yearly_managemnet,
                getString(R.string.hire_property_management_company)
            )
        )
        RentifyApp.allEntries.add(
            AllEntryModel(
                R.string.yearly_other_exp,
                getString(R.string.add_deduction)
            )
        )
        showNext()
        btnNext.setOnSingleClickListener {
            if (edtAmount.text.isNullOrEmpty().not() || edtPercent.text.isNullOrEmpty().not())
                if (RentifyApp.allEntries[currentCount].amountType == RentifyApp.INPUT.AMOUNT) RentifyApp.allEntries[currentCount].amount =
                    edtAmount.text.toString() else RentifyApp.allEntries[currentCount].percent =
                    edtPercent.text.toString().zeroIfEmpty()
            saveInputs()// to show in expert input screen
            showNext()
        }
        btnBack.setOnSingleClickListener { onBackPressed() }
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

    fun showNext(isNext: Boolean = true) {
        if (isNext && currentCount < RentifyApp.allEntries.size - 1) {
            edtAmount.visibility = View.GONE
            edtPercent.visibility = View.GONE
            lblAmount.visibility = if (edtAmount.text.toString().trim()
                    .equals("$") && edtAmount.visibility == View.VISIBLE
            ) View.VISIBLE else View.GONE
            tvPurchasePrice.visibility = View.GONE
            currentCount++
            setData(currentCount)
        } else if (!isNext && currentCount > 0) {
            edtAmount.visibility = View.GONE
            edtPercent.visibility = View.GONE
            lblAmount.visibility = if (edtAmount.text.toString().trim()
                    .equals("$") && edtAmount.visibility == View.VISIBLE
            ) View.VISIBLE else View.GONE
            currentCount--
            setData(currentCount)
        } else {
            Intent(this, InputActivity::class.java).apply {
                putExtra("from_novice", true)
                startActivity(this)
            }
        }
    }

    fun setData(pos: Int) {
        val model = RentifyApp.allEntries[pos]
        entryCount.text = getString(R.string.count_of_eighteen, (pos + 1).toString())
        titleHeader.text = getString(model.title)
        tvEntry.animateText(model.entry.replace("%%","%"), this)
        if (model.amountType == RentifyApp.INPUT.AMOUNT) {
            edtAmount.setText(if (model.amount.clearFormat().toDouble() > 0) model.amount else "")
            edtAmount.setOnFocusChangeListener { view, b ->
                edtAmount.addTextChangedListener(edtAmount.addAmountWatcher() {
                    lblAmount.visibility = if ((edtAmount.text.toString().trim()
                            .equals("$")||edtAmount.text.toString().trim().isEmpty()) && edtAmount.visibility == View.VISIBLE
                    ) View.VISIBLE else View.GONE
                })
            }
        } else {
            edtPercent.setText(
                (if (model.percent.clearFormat().toDouble() > 0) model.percent else "0.00%").replace("%%","%")
            )
            edtPercent.setOnFocusChangeListener { view, b ->
                edtPercent.addTextChangedListener(edtPercent.addPercentWatcher {})
            }
            edtPercent.setOnKeyListener { view, i, keyEvent ->
                    if (keyEvent.keyCode== KeyEvent.KEYCODE_DEL){
                        edtAnnualRentalIncrease.setText("")
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }

        }
//        edtAmount.addTextChangedListener(if (model.amountType == RentifyApp.INPUT.AMOUNT) edtAmount.addAmountWatcher() else edtAmount.addPercentWatcher())
        lblAmount.visibility = if ((edtAmount.text.toString().trim()
                .equals("$")||edtAmount.text.toString().trim().isEmpty()) && edtAmount.visibility == View.VISIBLE && model.amountType == RentifyApp.INPUT.AMOUNT
        ) View.VISIBLE else View.GONE
        edtPercent.visibility =
            if (model.amountType == RentifyApp.INPUT.PERCENT) View.VISIBLE else View.GONE
        edtAmount.visibility =
            if (model.amountType == RentifyApp.INPUT.AMOUNT) View.VISIBLE else View.GONE

//        if (model.amountType == RentifyApp.INPUT.PERCENT){
//            edtPercent.addTextChangedListener( edtPercent.addPercentWatcher{})
//        }
//        else edtAmount.addTextChangedListener(edtAmount.addAmountWatcher(){
//            lblAmount.visibility= if (edtAmount.text.toString().trim().equals("$")&&edtAmount.visibility == View.VISIBLE) View.VISIBLE else View.GONE
//        })
    }

    //text animate listener
    override fun invoke() {
        edtPercent.visibility =
            if (RentifyApp.allEntries[currentCount].amountType == RentifyApp.INPUT.PERCENT) View.VISIBLE else View.GONE
        edtAmount.visibility =
            if (RentifyApp.allEntries[currentCount].amountType == RentifyApp.INPUT.AMOUNT) View.VISIBLE else View.GONE
        lblAmount.visibility = if (edtAmount.text.toString().trim()
                .equals("$") && edtAmount.visibility == View.VISIBLE
        ) View.VISIBLE else View.GONE
        tvPurchasePrice.visibility = if (RentifyApp.allEntries[currentCount].showPurchase) {
            tvPurchasePrice.text = "Your Purchase Price\n${RentifyApp.allEntries[0].amount}"
            View.VISIBLE
        } else View.GONE
    }

    fun saveInputs() {
        when (currentCount) {
            0 -> {
                inputDataEntity.Purchase_Price =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            1 -> {
                inputDataEntity.Property_Value_after_renovation =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            2 -> {
                inputDataEntity.Market_Rent =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            3 -> {
                inputDataEntity.Rental_Deposit =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            4 -> {
                inputDataEntity.Loan_Down_Payment =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            5 -> {
                inputDataEntity.Loan_Cost =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            6 -> {
                inputDataEntity.Improvements =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            7 -> {
                inputDataEntity.Annual_Rental_Increase =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            8 -> {
                inputDataEntity.Area_Appreciation_Growth =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            9 -> {
                inputDataEntity.Principal_Payments =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            10 -> {
                inputDataEntity.Impound_Account =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            11 -> {
                inputDataEntity.Vacancy_Factor =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            12 -> {
                inputDataEntity.Loan_Interest =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            13 -> {
                inputDataEntity.Property_Tax =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            14 -> {
                inputDataEntity.Homeowners_Insurance =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            15 -> {
                inputDataEntity.Maintenance_Expense =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            16 -> {
                inputDataEntity.Annual_Management =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            17 -> {
                inputDataEntity.Annual_other_expenses =
                    RentifyApp.allEntries[currentCount].amount.clearFormat().toDouble()
            }
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        if (currentCount > 0) showNext(false)
        else {
            if (RentifyApp.session(this).getPrefValue(SessionManager.WORKINGMODE).equals("0")
                    .not()
            ) finishAffinity() else super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTheme()
        updateValues()
        setData(currentCount)
    }

    fun updateValues() {
        RentifyApp.allEntries.forEachIndexed { index, allEntryModel ->
            when (index) {
                0 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.purchase_price,
                    entry = getString(R.string.whats_purchase_price)
                )
                1 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.new_property_value,
                    entry = getString(R.string.whats_new_property_value)
                )
                2 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.market_rent,
                    entry = getString(R.string.whats_amount_of_rent)
                )
                3 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.rental_deposit,
                    entry = getString(R.string.not_counting_first_rent)
                )
                4 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.loan_down_payment,
                    entry = getString(R.string.how_much_downpayment),
                    showPurchase = true
                )
                5 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.loan_cost,
                    entry = getString(R.string.any_loan_fee)
                )
                6 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.improvements,
                    entry = getString(R.string.enter_improvement_cost)
                )
                7 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.yearly_rental_inc,
                    entry = getString(
                        R.string.enter_yearly_rental_increase,
                        (RentifyApp.session(this).getPrefValue(SessionManager.RENTAL_INCREASES)
                            ?: "3.00")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this)
                        .getPrefValue(SessionManager.RENTAL_INCREASES) ?: "3.00").emptyIfZero()+"%"
                )
                8 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.area_appreciation,
                    entry = getString(R.string.yearly_appreciation_rate),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this)
                        .getPrefValue(SessionManager.APPRECIATION_GROWTH) ?: "3.00").emptyIfZero()+"%"
                )
                9 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.principal_payment,
                    entry = getString(
                        R.string.per_loan_payment_to_principal,
                        (RentifyApp.session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)
                            ?: "25.00")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this)
                        .getPrefValue(SessionManager.PRINCIPAL_PAYMENT) ?: "25.00").emptyIfZero()+"%"
                )
                10 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.impound_account,
                    entry = getString(
                        R.string.enter_impound_account,
                        (RentifyApp.session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)
                            ?: "0.33")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)
                        ?: "0.33").emptyIfZero()+"%"
                )
                11 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.vacancy_rate,
                    entry = getString(
                        R.string.budget_for_vacancy_rate,
                        (RentifyApp.session(this).getPrefValue(SessionManager.VACANCY_FACTOR)
                            ?: "0.00")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this).getPrefValue(SessionManager.VACANCY_FACTOR)
                        ?: "0.00").emptyIfZero()+"%"
                )
                12 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.loan_intrest,
                    entry = getString(R.string.intrest_rate_for_lender),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this).getPrefValue(SessionManager.LOAN_INTREST)
                        ?: "0.00").emptyIfZero()+"%"
                )
                13 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.property_tax,
                    entry = getString(
                        R.string.whats_property_tax_rate,
                        (RentifyApp.session(this).getPrefValue(SessionManager.PROPERTY_TAX)
                            ?: "1.125")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this).getPrefValue(SessionManager.PROPERTY_TAX)
                        ?: "1.125").emptyIfZero()+"%"
                )
                14 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.homeowners_insurance,
                    entry = getString(
                        R.string.whats_homeowners_insurance,
                        (RentifyApp.session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)
                            ?: "0.25")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this)
                        .getPrefValue(SessionManager.HOMEOWNERS_INSURANCE) ?: "0.25").emptyIfZero()+"%"
                )
                15 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.maintenance_exp,
                    entry = getString(
                        R.string.cost_of_repair_maintenance,
                        (RentifyApp.session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)
                            ?: "0.44")+"%"
                    ),
                    amountType = RentifyApp.INPUT.PERCENT,
                    percent = (RentifyApp.session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)
                        ?: "0.44").emptyIfZero()+"%"
                )
                16 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.yearly_managemnet,
                    entry = getString(R.string.hire_property_management_company)
                )
                17 -> RentifyApp.allEntries[index] = allEntryModel.copy(
                    title = R.string.yearly_other_exp,
                    entry = getString(R.string.add_deduction)
                )
            }
        }
    }

    fun refreshTheme() {
        window!!.statusBarTheme()
        headerTool.headerTheme()
        btnNext.bgTheme()
        btnBack.bgTheme()
    }
}