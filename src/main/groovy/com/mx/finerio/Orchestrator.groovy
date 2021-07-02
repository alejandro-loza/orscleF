package com.mx.finerio

import com.mx.finerio.dto.AccountDto
import com.mx.finerio.dto.ArgumentsDto
import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.dto.UserDto
import com.mx.finerio.services.FileService
import com.mx.finerio.services.PFMService
import com.mx.finerio.services.ReadFileService
import com.mx.finerio.services.impl.ReadFileServiceImpl
import com.mx.finerio.services.ValidatorService
import com.mx.finerio.services.impl.FileServiceImpl
import com.mx.finerio.services.impl.PFMServiceImpl
import com.mx.finerio.services.impl.ValidatorServiceImpl

class Orchestrator {

    ReadFileService readFileService = new ReadFileServiceImpl()
    ValidatorService validatorService = new ValidatorServiceImpl()
    PFMService pfmService = new PFMServiceImpl()
    FileService fileService = new FileServiceImpl()

   void run(String[] args) {
       ArgumentsDto arguments = buildArguments(args)

        List<CsvRow> listCsv = readFileService.processInputFile( arguments.filePath )
         if ( !validatorService.areRecordsUniqueValid( listCsv )) {
             return
         }

       listCsv.each {

           if( !validatorService.hasBeenProcessed(it) ){
               def userCreateDto= getUserDto(it)
               def accountCreateDto=getAccountDto(it)
               def userDto = pfmService.createUser(userCreateDto)
               def accountDto = pfmService.createAccount(accountCreateDto)
               def statusDto = fromPfmResponseToFileRecord(userDto,accountDto)
               fileService.createFileRecord(statusDto)
           }

       }
       fileService.createResume()

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

    private static ArgumentsDto buildArguments(String[] args) {
        ArgumentsDto arguments = new ArgumentsDto()
        if (args.size() > 0) {
            arguments.with {
                filePath = args.first()
            }
        }
        arguments
    }
}
