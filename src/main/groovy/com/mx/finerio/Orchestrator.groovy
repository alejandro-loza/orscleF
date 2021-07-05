package com.mx.finerio

import com.mx.finerio.dto.ArgumentsDto
import com.mx.finerio.dto.CsvRow
import com.mx.finerio.parallel.PfmTask
import com.mx.finerio.services.FileService
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
    FileService fileService = new FileServiceImpl()

    void run(String[] args) {
        ArgumentsDto arguments = buildArguments(args)
        List<CsvRow> listCsv = readFileService.processInputFile( arguments.filePath )

        if ( !validatorService.areRecordsUniqueValid( listCsv )) {//todo verify if throws or continue to migrating
            println "Records are invalid"
            return
        }
        def resultFilePath = PfmTask.getFileName(arguments.filePath)
        migrateRowsToPfmApi(arguments, listCsv, resultFilePath)
        fileService.createResume(listCsv, resultFilePath )
        println "Migration finished for file: $resultFilePath"
    }

    private static ArgumentsDto buildArguments(String[] args) {
        ArgumentsDto arguments = new ArgumentsDto()//todo validate command args
        if (args.size() > 0) {
            arguments.with {
                filePath = args[0]
                host = args[ 1 ]
                incomeToken = args[ 2 ]
                threads = args[ 3 ]
                awaitTerminationMinutes = args[ 4 ]
            }
        }
        arguments
    }

    private void migrateRowsToPfmApi(ArgumentsDto arguments, List<CsvRow> listCsv, String resultFilePath) {
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(arguments.threads as Integer)

        listCsv.each {//todo check for an optimal loop and start point!!
            if( !fileService.existFile(resultFilePath, it.accountNumber as String) ){
                println "Processing record with accountNmber $it.accountNumber"
                def data = [pfmService  :  new PFMServiceImpl(arguments.host, arguments.incomeToken),
                            fileService : fileService,
                            csvRow      : it,
                            host        : arguments.host,
                            incomeToken : arguments.incomeToken,
                            seedFileName: arguments.filePath
                ]
                executor.execute( new PfmTask(data))
        }else{
                println "Found accountNumber $it.accountNumber record"
            }
        }
        executor.shutdown()
        executor.awaitTermination(arguments.awaitTerminationMinutes as Long, TimeUnit.MINUTES)
    }
}
