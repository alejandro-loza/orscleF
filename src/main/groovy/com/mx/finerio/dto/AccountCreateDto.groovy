package com.mx.finerio.dto

import groovy.transform.ToString


@ToString(includeNames = true, includePackage = false)
class AccountCreateDto {
    Long userId
    Long financialEntityId
    String nature
    String name
    String number
    boolean chargeable
    BigDecimal balance = 0
}

