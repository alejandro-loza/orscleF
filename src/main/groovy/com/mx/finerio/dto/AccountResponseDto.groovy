package com.mx.finerio.dto

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeSuperProperties = true)
class AccountResponseDto extends PfmStatusDto {
    String id
    String dateCreated
    String lastUpdated
    String nature
    String name
    String number
    String balance
    String chargeable
}
