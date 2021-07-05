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

    static String getFileName( String seedFileName){
        def file=new File(seedFileName)
        def name=file.name.split("\\.")[0]
        name
    }

    @Override
    void run() {
        UserResponseDto userCreateResponse = pfmService.createUser(generateUserCreateBodyDto(csvRow))
        AccountResponseDto accountCreateResponse

        if( userCreateResponse.isSucces) {
            accountCreateResponse = pfmService.createAccount(
                    generateAccountCreateBodyDto(csvRow, userCreateResponse))
        }

        StatusDto statusDto = fromPfmResponseToFileRecord(
                userCreateResponse,accountCreateResponse, csvRow.accountNumber.toString())
        println "statusDto: $statusDto"
       fileService.createFileRecord(statusDto, getFileName(seedFileName))
    }

    private StatusDto fromPfmResponseToFileRecord( UserResponseDto userResponseDto, AccountResponseDto accountResponseDto, String customerNumber ){

        def user
        def account
        def resMap

        if( userResponseDto.isSucces ) { //todo if account fails why not all row fails? if( userDto.isSucces && accountDto.isSucces )
            user = [ success:true,
                     id:userResponseDto.id,
                     dateCreated:userResponseDto.dateCreated,
                     name:userResponseDto.name ]
            if(  accountResponseDto.isSucces ) {
                account = [ success: true,
                            id: accountResponseDto.id,
                            dateCreated: accountResponseDto.dateCreated,
                            lastUpdate:accountResponseDto.lastUpdated,
                            nature: accountResponseDto.nature,
                            name: accountResponseDto.number,
                            balance:accountResponseDto.balance,
                            chargeable: accountResponseDto.chargeable]
                resMap = [ success: true, user: user, account: account ] //todo migrate to DTOS to use on resumen csv
            }else{
                def accountError = [ success: false,
                                     statusCode: accountResponseDto.statusCode,
                                     errorMessage:accountResponseDto.errorMessage ,
                                     errorDetail: accountResponseDto.errorDetail  ]
                resMap = [ success: false, user: user, account: accountError  ]
            }
        }else{
                resMap = [ success: false, user: [ success: false,
                                             statusCode: userResponseDto.statusCode,
                                             errorMessage:userResponseDto.errorMessage ,
                                             errorDetail: userResponseDto.errorDetail  ],
                     account: null  ]
        }

        StatusDto statusDto = new StatusDto()
        statusDto.accountNumber = customerNumber
        statusDto.data = JsonOutput.toJson(resMap)
        statusDto
    }

    private UserCreateDto generateUserCreateBodyDto(CsvRow csvRow){
        new UserCreateDto(csvRow.accountNumber.toString())
    }

    private AccountCreateDto generateAccountCreateBodyDto(CsvRow csvRow, UserResponseDto userResponse){
        AccountCreateDto cmd = new AccountCreateDto()
        cmd.with {
            userId = userResponse.id as Long
            financialEntityId = csvRow.accountFinancialEntityId
            nature = getCapitalString (csvRow.accountNature)
            name = csvRow.accountName
            number = csvRow.accountNumber
            balance = csvRow.accountBalance
        }
        cmd
    }

    private String getCapitalString(String nature){
        nature.substring(0,1)+nature.substring(1,nature.length()).toLowerCase()
    }
}