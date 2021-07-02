package com.mx.finerio.services

import com.mx.finerio.dto.AccountCreateDto
import com.mx.finerio.dto.AccountDto
import com.mx.finerio.dto.UserCreateDto
import com.mx.finerio.dto.UserDto

interface PFMService {
    UserDto createUser( UserCreateDto userDto )
    AccountDto createAccount( AccountCreateDto accountCreateDto )
}
