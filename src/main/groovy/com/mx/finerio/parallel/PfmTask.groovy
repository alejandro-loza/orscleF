package com.mx.finerio.parallel

import com.mx.finerio.dto.AccountCreateDto
import com.mx.finerio.dto.AccountResponseDto
import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.dto.UserCreateDto
import com.mx.finerio.dto.UserResponseDto
import com.mx.finerio.services.FileService
import com.mx.finerio.services.PFMService
import groovy.json.JsonOutput

class PfmTask implements  Runnable {

    public static final String FILES_PATH = "files"

    PFMService pfmService
    FileService fileService
    CsvRow csvRow
    String host
    String incomeToken
    Map data
    String seedFileName

    PfmTask(  Map data ) {
        this.pfmService= data.pfmService
        this.fileService= data.fileService
        this.csvRow= data.csvRow
        this.host = data.host
        this.incomeToken = data.incomeToken
        this.seedFileName = data.seedFileName
    }

    @Override
    void run() {
        UserResponseDto userCreateResponse = pfmService.createUser(generateUserCreateBodyDto(csvRow))
        AccountResponseDto accountCreateResponse = pfmService.createAccount(
                generateAccountCreateBodyDto(csvRow, userCreateResponse))
        StatusDto statusDto = fromPfmResponseToFileRecord(
                userCreateResponse,accountCreateResponse, csvRow.accountNumber.toString())
        fileService.createFileRecord(statusDto, FILES_PATH)
    }

    private StatusDto fromPfmResponseToFileRecord( UserResponseDto userDto, AccountResponseDto accountDto, String customerNumber ){

        def user
        def account
        def resMap

        if( userDto.isSucces ) { //todo if account fails why not all row fails? if( userDto.isSucces && accountDto.isSucces )
            user = [ success:true,
                     id:userDto.id,
                     dateCreated:userDto.dateCreated,
                     name:userDto.name ]
            if( accountDto.isSucces ) {
                account = [ success: true,
                            id: accountDto.id,
                            dateCreated: accountDto.dateCreated,
                            lastUpdate:accountDto.lastUpdated,
                            nature: accountDto.nature,
                            name: accountDto.number,
                            balance:accountDto.balance,
                            chargeable: accountDto.chargeable]
                resMap = [ success: true, user: user, account: account ] //todo migrate to DTOS to use on resumen csv
            }else{
                def accountError = [ success: false,
                                     statusCode: accountDto.statusCode,
                                     errorMessage:accountDto.errorMessage ,
                                     errorDetail: accountDto.errorDetail  ]
                resMap = [ success: false, user: user, account: accountError  ]
            }
        }else{
                resMap = [ success: false, user: [ success: false,
                                             statusCode: userDto.statusCode,
                                             errorMessage:userDto.errorMessage ,
                                             errorDetail: userDto.errorDetail  ],
                     account: null  ]
        }

        StatusDto statusDto = new StatusDto()
        statusDto.accountNumber = customerNumber
        statusDto.data = JsonOutput.toJson(data)
        statusDto
    }

    private UserCreateDto generateUserCreateBodyDto(CsvRow csvRow){
        new UserCreateDto(csvRow.accountNumber.toString())
    }

    private AccountCreateDto generateAccountCreateBodyDto(CsvRow csvRow, UserResponseDto userResponse){
        AccountCreateDto cmd = new AccountCreateDto()
        cmd.with {
            userId = userResponse.id
            financialEntityId = csvRow.accountFinancialEntityId
            nature = csvRow.accountNature
            name = csvRow.accountName
            number = csvRow.accountNumber
            balance = csvRow.accountBalance
        }
        cmd
    }
}