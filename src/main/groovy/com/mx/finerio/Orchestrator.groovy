package com.mx.finerio

import com.mx.finerio.dto.AccountDto
import com.mx.finerio.dto.ArgumentsDto
import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.dto.UserDto
import com.mx.finerio.parallel.PfmTask
import com.mx.finerio.services.FileService
import com.mx.finerio.services.PFMService
import com.mx.finerio.services.ReadFileService
import com.mx.finerio.services.impl.ReadFileServiceImpl
import com.mx.finerio.services.ValidatorService
import com.mx.finerio.services.impl.FileServiceImpl
import com.mx.finerio.services.impl.PFMServiceImpl
import com.mx.finerio.services.impl.ValidatorServiceImpl

import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

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
       ThreadPoolExecutor executor =
               (ThreadPoolExecutor) Executors.newFixedThreadPool( arguments.threads as Integer )

       listCsv.each {
           def data = [   pfmService: pfmService,
                          fileService: fileService,
                          csvRow: it,
                          host: arguments.host,
                          incomeToken: arguments.incomeToken

           ]
           def task = new PfmTask( data )
           executor.execute(task)
       }
       executor.shutdown()
       executor.awaitTermination(arguments.awaitTerminationMinutes as Long, TimeUnit.MINUTES)
       fileService.createResume()
   }

    private static ArgumentsDto buildArguments(String[] args) {
        ArgumentsDto arguments = new ArgumentsDto()
        if (args.size() > 0) {
            arguments.with {
                filePath = args.first()
                host = args[ 1 ]
                incomeToken = args[ 2 ]
                threads = args[ 3 ]
                awaitTerminationMinutes = args[ 4 ]
            }
        }
        arguments
    }
}
