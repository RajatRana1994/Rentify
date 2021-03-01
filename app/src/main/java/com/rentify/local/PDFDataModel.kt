package com.rentify.local

import com.rentify.database.photodb.PhotoEntity

data class PDFDataModel(
    var fileName: String="",
    var inputData: InputDataEntity?=null,
    var userInfo:UserInfo?=null,
    var propertyInfo:PropertyInfo?=null,
    var dafaultData:DefaultData?=null,
    var photos:PhotoData?=null
)


data class UserInfo(
    var USER_NAME_USERINFO: String="",
    var COMPANY_NAME_USERINFO: String="",
    var EMAIL_USERINFO: String="",
    var PHONE_USERINFO: String="",
    var ADDRESS_USERINFO: String="",
    var CITY_USERINFO: String="",
    var STATE_USERINFO: String="",
    var POSTCODE_USERINFO: String="",
    var LOGO_USERINFO: String=""
)

data class PropertyInfo(
    var ADDRESS_PROPERTY_INFO: String="",
    var SQUARE_FEET_PROPERTY_INFO: String="",
    var BED_ROOMS_PROPERTY_INFO: String="",
    var BATH_ROOMS_PROPERTY_INFO: String="",
    var LOT_SIZE_PROPERTY_INFO: String="",
    var CITY_PROPERTY_INFO: String="",
    var STATE_PROPERTY_INFO: String="",
    var POSTCODE_PROPERTY_INFO: String=""
)
data class PhotoData(
    var PROPERTY_COVER: String="",
    var PROPERTY_IMAGE_1: String="",
    var PROPERTY_IMAGE_2: String="",
    var PROPERTY_IMAGE_3: String="",
    var PROPERTY_IMAGE_4: String="",
    var PROPERTY_IMAGE_5: String=""
)
data class DefaultData(
    var RENTAL_INCREASES: String="",
    var APPRECIATION_GROWTH: String="",
    var PRINCIPAL_PAYMENT: String="",
    var IMPOUND_ACCOUNT: String="",
    var VACANCY_FACTOR: String="",
    var LOAN_INTREST: String="",
    var PROPERTY_TAX: String="",
    var HOMEOWNERS_INSURANCE: String="",
    var MAINTENANCE_EXP: String=""
)