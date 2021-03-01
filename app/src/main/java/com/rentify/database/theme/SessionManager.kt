package com.rentify.database.theme

import android.content.Context
import com.rentify.util.ThemeHelper

class SessionManager(val context: Context) {
    companion object{

        val TERMSAPPLIED="TERMSAPPLIED"
        val WORKINGMODE="WORKINGMODE"


        val THEME_COLOR="THEME_COLOR"
        val THEME_MODE="THEME_MODE"
        val THEME_ACCESSIBLE_MODE="THEME_ACCESSIBLE_MODE"
        val LANGUAGE="LANGUAGE"
        //DEFAULT_SETTINGS
        val RENTAL_INCREASES="RENTAL_INCREASES"
        val APPRECIATION_GROWTH="APPRECIATION_GROWTH"
        val PRINCIPAL_PAYMENT="PRINCIPAL_PAYMENT"
        val IMPOUND_ACCOUNT="IMPOUND_ACCOUNT"
        val VACANCY_FACTOR="VACANCY_FACTOR"
        val LOAN_INTREST="LOAN_INTREST"
        val PROPERTY_TAX="PROPERTY_TAX"
        val HOMEOWNERS_INSURANCE="HOMEOWNERS_INSURANCE"
        val MAINTENANCE_EXP="MAINTENANCE_EXP"
        ///USER_INFO
        val USER_NAME_USERINFO="USER_NAME_USERINFO"
        val COMPANY_NAME_USERINFO="COMPANY_NAME_USERINFO"
        val EMAIL_USERINFO="EMAIL_USERINFO"
        val PHONE_USERINFO="PHONE_USERINFO"
        val ADDRESS_USERINFO="ADDRESS_USERINFO"
        val CITY_USERINFO="CITY_USERINFO"
        val STATE_USERINFO="STATE_USERINFO"
        val POSTCODE_USERINFO="POSTCODE_USERINFO"
        val LOGO_USERINFO="LOGO_USERINFO"
        // Property Image
        val PROPERTY_COVER="PROPERTY_COVER"
        val PROPERTY_IMAGE_1="PROPERTY_IMAGE_1"
        val PROPERTY_IMAGE_2="PROPERTY_IMAGE_2"
        val PROPERTY_IMAGE_3="PROPERTY_IMAGE_3"
        val PROPERTY_IMAGE_4="PROPERTY_IMAGE_4"
        val PROPERTY_IMAGE_5="PROPERTY_IMAGE_5"
        //PROPERTY_INFO
        val ADDRESS_PROPERTY_INFO="ADDRESS_PROPERTY_INFO"
        val SQUARE_FEET_PROPERTY_INFO="SQUARE_FEET_PROPERTY_INFO"
        val BED_ROOMS_PROPERTY_INFO="BED_ROOMS_PROPERTY_INFO"
        val BATH_ROOMS_PROPERTY_INFO="BATH_ROOMS_PROPERTY_INFO"
        val LOT_SIZE_PROPERTY_INFO="LOT_SIZE_PROPERTY_INFO"
        val CITY_PROPERTY_INFO="CITY_PROPERTY_INFO"
        val STATE_PROPERTY_INFO="STATE_PROPERTY_INFO"
        val POSTCODE_PROPERTY_INFO="POSTCODE_PROPERTY_INFO"
    }

    val pref = context.getSharedPreferences("rentify", Context.MODE_PRIVATE)

    fun savePrefValue(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }


    fun getPrefValue(key: String) = pref.getString(key, default(key))

    fun default(key:String)=
        when(key){
            THEME_MODE->ThemeHelper.DEFAULT_MODE
            RENTAL_INCREASES->"3.00%"
            APPRECIATION_GROWTH->"3.00%"
            PRINCIPAL_PAYMENT->"25.00%"
            IMPOUND_ACCOUNT->"0.33%"
            VACANCY_FACTOR->"0.00%"
            LOAN_INTREST->"0.00%"
            PROPERTY_TAX->"1.125%"
            HOMEOWNERS_INSURANCE->"0.25%"
            MAINTENANCE_EXP->"0.44%"
            WORKINGMODE->"0"
            else -> ""
        }

}