package com.mx.finerio.services

import com.mx.finerio.dto.AccountCreateDto
import com.mx.finerio.dto.AccountResponseDto
import com.mx.finerio.dto.UserCreateDto
import com.mx.finerio.dto.UserResponseDto

interface PFMService {
    UserResponseDto createUser(UserCreateDto userDto )
    AccountResponseDto createAccount(AccountCreateDto accountCreateDto )
}
