package com.rentify.local

import com.rentify.RentifyApp


data class AllEntryModel(
    var title: Int,
    var entry: String,
    var showPurchase: Boolean=false,
    var amountType: RentifyApp.INPUT=RentifyApp.INPUT.AMOUNT,
    var amount: String="0.00",
    var percent: String="0.00%"
)

data class AllInputModel(
    var title: Int,
    var type: String,
    var amountType: RentifyApp.INPUT=RentifyApp.INPUT.AMOUNT,
    var amount: String="0.00",
    var percent: String="0.00%"
)

data class DefaultSettingsModel(
    var title:String,
    var percent:String,
    var sessionType:String
)


data class InputDataEntity(
    var Purchase_Price:Double?=0.0,
    var Property_Value_after_renovation:Double?=0.0,
    var Market_Rent:Double?=0.0,
    var Rental_Deposit:Double?=0.0,
    var Loan_Down_Payment:Double?=0.0,
    var Loan_Cost:Double?=0.0,
    var Improvements:Double?=0.0,
    var Annual_Rental_Increase:Double?=0.0,
    var Area_Appreciation_Growth:Double?=0.0,
    var Principal_Payments:Double?=0.0,
    var Impound_Account:Double?=0.0,
    var Vacancy_Factor:Double?=0.0,
    var Loan_Interest:Double?=0.0,
    var Property_Tax:Double?=0.0,
    var Homeowners_Insurance:Double?=0.0,
    var Maintenance_Expense:Double?=0.0,
    var Annual_Management:Double?=0.0,
    var Annual_other_expenses:Double?=0.0,
    var Annual_Expenses:Double?=0.0,
    var Monthly_cash_requirement:Double?=0.0,
    var Annual_Income:Double?=0.0,
    var Cash_on_Cash_return:Double?=0.0,
    var Percentage_Cash_on_return:Double?=0.0,
    var Annual_Net_Operating_Income:Double?=0.0,
    var LOANAMOUNT:Double?=0.0
)


