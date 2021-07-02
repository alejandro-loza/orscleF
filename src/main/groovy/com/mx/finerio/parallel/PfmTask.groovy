package com.mx.finerio.parallel

import com.mx.finerio.dto.AccountCreateDto
import com.mx.finerio.dto.AccountDto
import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.dto.UserCreateDto
import com.mx.finerio.dto.UserDto
import com.mx.finerio.services.FileService
import com.mx.finerio.services.PFMService

class PfmTask implements  Runnable {

    public static final String FILES_PATH = "files"

    PFMService pfmService
    FileService fileService
    CsvRow csvRow
    String host
    String incomeToken
    Map data

    PfmTask(  Map data ) {
        this.pfmService= data.pfmService
        this.fileService= data.fileService
        this.csvRow= data.csvRow
        this.host = data.host
        this.incomeToken = data.incomeToken
    }

    @Override
    void run() {
        UserDto userCreateResponse = pfmService.createUser(generateUserCreateBodyDto(csvRow))//todo si falla?
        AccountDto accountCreateResponse = pfmService.createAccount(
                generateAccountCreateBodyDto(csvRow, userCreateResponse))
        StatusDto statusDto = fromPfmResponseToFileRecord(userCreateResponse,accountCreateResponse)
        fileService.createFileRecord(statusDto, FILES_PATH)
    }

    private StatusDto fromPfmResponseToFileRecord(UserDto userDto, AccountDto accountDto){
        return null
    }

    private UserCreateDto generateUserCreateBodyDto(CsvRow csvRow){
        new UserCreateDto(csvRow.customerNumber.toString())
    }

    private AccountCreateDto generateAccountCreateBodyDto(CsvRow csvRow, UserDto userResponse){
        AccountCreateDto cmd = new AccountCreateDto()
        cmd.with {
            userId = userResponse.id
            financialEntityId = csvRow.financialEntityId
            nature = csvRow.accountNature
            name = csvRow.accountName
            number = csvRow.customerNumber
            balance = csvRow.balance
        }
        cmd
    }
}