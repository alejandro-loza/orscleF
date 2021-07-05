package com.mx.finerio.dto

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class CsvRow {
    String customerName
    Long accountFinancialEntityId
    String accountName
    Long accountNumber
    String accountNature
    BigDecimal accountBalance
}
