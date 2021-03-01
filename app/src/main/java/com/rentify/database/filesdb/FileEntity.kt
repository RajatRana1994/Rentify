package com.rentify.database.filesdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "filename") var filename: String,
    @ColumnInfo(name = "folder") var folder: String,
    @ColumnInfo(name = "purchaseprice") var Purchase_Price: Double? = 0.0,
    @ColumnInfo(name = "propertyvalueafterrenovation") var Property_Value_after_renovation: Double? = 0.0,
    @ColumnInfo(name = "marketrent") var Market_Rent: Double? = 0.0,
    @ColumnInfo(name = "rentaldeposit") var Rental_Deposit: Double? = 0.0,
    @ColumnInfo(name = "loandownpayment") var Loan_Down_Payment: Double? = 0.0,
    @ColumnInfo(name = "loancost") var Loan_Cost: Double? = 0.0,
    @ColumnInfo(name = "improvements") var Improvements: Double? = 0.0,
    @ColumnInfo(name = "annualrentalincrease") var Annual_Rental_Increase: Double? = 0.0,
    @ColumnInfo(name = "areaappreciationgrowth") var Area_Appreciation_Growth: Double? = 0.0,
    @ColumnInfo(name = "principalpayments") var Principal_Payments: Double? = 0.0,
    @ColumnInfo(name = "impoundaccount") var Impound_Account: Double? = 0.0,
    @ColumnInfo(name = "vacancyfactor") var Vacancy_Factor: Double? = 0.0,
    @ColumnInfo(name = "loaninterest") var Loan_Interest: Double? = 0.0,
    @ColumnInfo(name = "propertytax") var Property_Tax: Double? = 0.0,
    @ColumnInfo(name = "homeownersinsurance") var Homeowners_Insurance: Double? = 0.0,
    @ColumnInfo(name = "maintenanceexpense") var Maintenance_Expense: Double? = 0.0,
    @ColumnInfo(name = "annualmanagement") var Annual_Management: Double? = 0.0,
    @ColumnInfo(name = "annualotherexpenses") var Annual_other_expenses: Double? = 0.0,
    @ColumnInfo(name = "annualexpenses") var Annual_Expenses: Double? = 0.0,
    @ColumnInfo(name = "monthlycashrequirement") var Monthly_cash_requirement: Double? = 0.0,
    @ColumnInfo(name = "annualincome") var Annual_Income: Double? = 0.0,
    @ColumnInfo(name = "cashoncashreturn") var Cash_on_Cash_return: Double? = 0.0,
    @ColumnInfo(name = "percentagecashonreturn") var Percentage_Cash_on_return: Double? = 0.0,
    @ColumnInfo(name = "annualnetoperatingincome") var Annual_Net_Operating_Income: Double? = 0.0,
    @ColumnInfo(name = "loanamount") var LOANAMOUNT: Double? = 0.0
)