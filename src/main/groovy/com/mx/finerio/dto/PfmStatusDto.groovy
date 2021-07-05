package com.mx.finerio.dto

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class PfmStatusDto {
    Boolean isSucces
    String statusCode
    String errorMessage
    String errorDetail
}
