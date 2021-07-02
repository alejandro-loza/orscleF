package com.mx.finerio.parallel

import com.mx.finerio.dto.AccountDto
import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.dto.UserDto
import com.mx.finerio.services.FileService
import com.mx.finerio.services.PFMService

class PfmTask implements  Runnable{

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
        def userCreateDto = getUserDto(csvRow)
        def accountCreateDto = getAccountDto(csvRow)
        def userDto = pfmService.createUser(userCreateDto)
        def accountDto = pfmService.createAccount(accountCreateDto)
        def statusDto = fromPfmResponseToFileRecord(userDto,accountDto)
        fileService.createFileRecord(statusDto)
    }

    private StatusDto fromPfmResponseToFileRecord(UserDto userDto, AccountDto accountDto){
        return null
    }

    private UserDto getUserDto(CsvRow csvRow){
        return null
    }
    private AccountDto getAccountDto(CsvRow csvRow){
        return null
    }
}