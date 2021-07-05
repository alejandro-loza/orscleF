package com.mx.finerio.dto

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeSuperProperties = true)
class UserResponseDto extends PfmStatusDto {
   String id
   String dateCreated
   String name
}
