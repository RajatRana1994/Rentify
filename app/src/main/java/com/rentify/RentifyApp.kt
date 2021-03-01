package com.rentify

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.room.Room
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.libraries.places.api.Places
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rentify.database.filesdb.FileDatabase
import com.rentify.database.photodb.PhotoDatabase
import com.rentify.database.theme.SessionManager
import com.rentify.local.AllEntryModel
import com.rentify.local.AllInputModel
import com.rentify.local.InputDataEntity
import com.rentify.util.ConstUtils
import com.rentify.util.Extensions.Companion.emptyIfZero
import com.rentify.util.ThemeHelper
import java.util.*


class RentifyApp : Application() {

    enum class INPUT { AMOUNT, PERCENT }

    companion object {
        val allEntries by lazy { mutableListOf<AllEntryModel>() }
        val allInputs by lazy { mutableListOf<AllInputModel>() }
        val dollar = Currency.getInstance("USD").symbol
        var selectedColor :String?= "#0001FE"
        val colors = ArrayList<Int>()
        var sessionManager: SessionManager? = null
        fun session(context: Context): SessionManager {
            if (sessionManager == null)
                sessionManager = SessionManager(context)
            return sessionManager!!
        }
        var photoDB: PhotoDatabase? = null
        fun getPhotoDB(context: Context): PhotoDatabase {
            if (photoDB == null)
                photoDB = Room.databaseBuilder(context.applicationContext, PhotoDatabase::class.java, "photos_rentify").build()
            return photoDB!!
        }

        var fileDB: FileDatabase? = null
        fun getFileDB(context: Context): FileDatabase {
            if (fileDB == null)
                fileDB = Room.databaseBuilder(context.applicationContext, FileDatabase::class.java, "files_rentify").build()
            return fileDB!!
        }

        var inputDataEntity=InputDataEntity()

    }

    operator fun get(context: Context): RentifyApp {
        return context.applicationContext as RentifyApp
    }



    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        ThemeHelper.applyTheme(session(this).getPrefValue(SessionManager.THEME_MODE)!!)
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.google_apikey))
        FirebaseCrashlytics.getInstance()
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        addEnteries()
        addInputs()
        colors.addAll(ColorTemplate.VORDIPLOM_COLORS.asList())
        colors.addAll(ColorTemplate.JOYFUL_COLORS.asList())
        colors.addAll(ColorTemplate.COLORFUL_COLORS.asList())
        colors.addAll(ColorTemplate.LIBERTY_COLORS.asList())
        colors.add(ColorTemplate.getHoloBlue())

    }


    fun addEnteries() {
//        allEntries.clear()
//        allEntries.add(AllEntryModel(R.string.purchase_price,getString(R.string.whats_purchase_price) ))
//        allEntries.add(AllEntryModel(R.string.new_property_value,getString(R.string.whats_new_property_value) ))
//        allEntries.add(AllEntryModel(R.string.market_rent,getString(R.string.whats_amount_of_rent) ))
//        allEntries.add(AllEntryModel(R.string.rental_deposit,getString(R.string.not_counting_first_rent) ))
//        allEntries.add(AllEntryModel(R.string.loan_down_payment,getString(R.string.how_much_downpayment) , showPurchase = true))
//        allEntries.add(AllEntryModel(R.string.loan_cost,getString(R.string.any_loan_fee) ))
//        allEntries.add(AllEntryModel(R.string.improvements,getString(R.string.enter_improvement_cost) ))
//        allEntries.add(AllEntryModel(R.string.yearly_rental_inc,getString(R.string.enter_yearly_rental_increase,(session(this).getPrefValue(SessionManager.RENTAL_INCREASES)?:"3.00%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.RENTAL_INCREASES)?:"3.00%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.area_appreciation,getString(R.string.yearly_appreciation_rate) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.APPRECIATION_GROWTH)?:"3.00%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.principal_payment,getString(R.string.per_loan_payment_to_principal,(session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)?:"25.00%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.PRINCIPAL_PAYMENT)?:"25.00%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.impound_account,getString(R.string.enter_impound_account,(session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)?:"0.33%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.IMPOUND_ACCOUNT)?:"0.33%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.vacancy_rate,getString(R.string.budget_for_vacancy_rate,(session(this).getPrefValue(SessionManager.VACANCY_FACTOR)?:"0.00%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.VACANCY_FACTOR)?:"0.00%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.loan_intrest,getString(R.string.intrest_rate_for_lender) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.LOAN_INTREST)?:"0.00%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.property_tax,getString(R.string.whats_property_tax_rate,(session(this).getPrefValue(SessionManager.PROPERTY_TAX)?:"1.125%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.PROPERTY_TAX)?:"1.125%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.homeowners_insurance,getString(R.string.whats_homeowners_insurance,(session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)?:"0.25%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.HOMEOWNERS_INSURANCE)?:"0.25%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.maintenance_exp,getString(R.string.cost_of_repair_maintenance,(session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)?:"0.44%")) , amountType = INPUT.PERCENT,percent = (session(this).getPrefValue(SessionManager.MAINTENANCE_EXP)?:"0.44%").emptyIfZero()))
//        allEntries.add(AllEntryModel(R.string.yearly_managemnet,getString(R.string.hire_property_management_company) ))
//        allEntries.add(AllEntryModel(R.string.yearly_other_exp,getString(R.string.add_deduction) ))
    }

    fun addInputs() {
        allInputs.add(AllInputModel(title = R.string.purchase_price,type=ConstUtils.Purchase_Price,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.property_value_after_renovation,type=ConstUtils.Property_Value_after_renovation,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.market_rent,type=ConstUtils.Market_Rent,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.rental_deposit,type=ConstUtils.Rental_Deposit,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.loan_down_payment,type=ConstUtils.Loan_Down_Payment,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.loan_cost,type=ConstUtils.Loan_Cost,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.improvements,type=ConstUtils.Improvements,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.annual_rental_increase_,type=ConstUtils.Annual_Rental_Increase,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.area_appreciation_growth,type=ConstUtils.Area_Appreciation_Growth,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.principal_payments,type=ConstUtils.Principal_Payments,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.impound_account_,type=ConstUtils.Impound_Account,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.vacancy_factor_,type=ConstUtils.Vacancy_Factor,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.loan_interest_,type=ConstUtils.Loan_Interest,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.property_tax_,type=ConstUtils.Property_Tax,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.homeowners_insurance_,type=ConstUtils.Homeowners_Insurance,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.maintenance_expense_,type=ConstUtils.Maintenance_Expense,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.annual_management_,type=ConstUtils.Annual_Management,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.annual_other_expenses_,type=ConstUtils.Annual_other_expenses,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.annual_expenses,type=ConstUtils.Annual_Expenses,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.monthly_cash_requirement,type=ConstUtils.Monthly_cash_requirement,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.annual_income,type=ConstUtils.Annual_Income,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.cash_on_cash_return,type=ConstUtils.Cash_on_Cash_return,amountType =  INPUT.AMOUNT))
        allInputs.add(AllInputModel(title = R.string.percentage_cash_on_return,type=ConstUtils.Percentage_Cash_on_return,amountType =  INPUT.PERCENT))
        allInputs.add(AllInputModel(title = R.string.annual_net_operating_income,type=ConstUtils.Annual_Net_Operating_Income,amountType =  INPUT.AMOUNT))
    }

}