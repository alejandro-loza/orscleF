package com.mx.finerio.services.impl

import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.ResumeCsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.services.FileService
import groovy.json.JsonSlurper

class FileServiceImpl implements  FileService {
    @Override
    void createFileRecord(StatusDto statusDto, String filePath ) {
        filePath = "$filePath/responses"
        File file = new File( "${createDirectory(filePath).path}/${statusDto.accountNumber}.txt")

        if (!file.isFile() && !file.createNewFile()) {
            println "Failed to create file: $file" //todo verify if need exception
            return
        }
        file.append(statusDto.data)//todo verify if it needs to be closed
    }

    @Override
    void createResume(List<CsvRow> cvsRows, String resultPath) {
        def resumeDirectory = createDirectory("${resultPath}/resumen")
        File successMigratedFile = new File( "${resumeDirectory.path}/successMigrated.csv")
        File failMigratedFile = new File( "${resumeDirectory.path}/failMigrated.csv")

        if(successMigratedFile.exists()){successMigratedFile.delete()}
        if(failMigratedFile.exists()){failMigratedFile.delete()}

        cvsRows.each {cvsRow ->
            generateRowResumen(resultPath, cvsRow, failMigratedFile, successMigratedFile)
        }
    }


    @Override
    boolean existFile(String resultPath, String accountNumber ){
        File file = new File("${resultPath}/responses/${accountNumber}.txt")
        file.exists()
    }

    private void generateRowResumen(String resultPath, CsvRow cvsRow, File failMigrated, File successMigrated) {
        File file = new File("${resultPath}/responses/${cvsRow.accountNumber}.txt")
        if (file.exists()) {
            Map fileContent = convertFileContentToMap(file)
            if (fileContent && !fileContent.isEmpty()) {
                ResumeCsvRow resumeCsvRow = new ResumeCsvRow(cvsRow)
                if (fileContent.success) {
                       // createRowOnFile(resumeCsvRow, failMigrated)//todo what happen with user ok and account bad?
                        resumeCsvRow.finerioCustomerId = fileContent.user.id as Long
                        resumeCsvRow.finerioAccountId = fileContent?.account?.id as Long
                        createRowOnFile(resumeCsvRow, successMigrated)

                } else {
                    createRowOnFailedFile(resumeCsvRow, failMigrated)
                }

            } else {
                println "Currupt File: $file"
            }
        }
    }

    private void createRowOnFailedFile(ResumeCsvRow resumeCsvRow, File failMigrated) {
        def row = [
                resumeCsvRow.customerName,
                resumeCsvRow.accountFinancialEntityId,
                resumeCsvRow.accountName,
                resumeCsvRow.accountNumber,
                resumeCsvRow.accountNature,
                resumeCsvRow.accountBalance,
        ]
        failMigrated.append row.join(',')
        failMigrated.append '\n'
    }

    private void createRowOnFile(ResumeCsvRow resumeCsvRow, File failMigrated) {
        def row = [
                resumeCsvRow.customerName,
                resumeCsvRow.finerioCustomerId,
                resumeCsvRow.accountName,
                resumeCsvRow.finerioAccountId,
                resumeCsvRow.accountNumber,
        ]
        failMigrated.append row.join(',')
        failMigrated.append '\n'
    }

    private Map convertFileContentToMap(File file){
        def slurper = new JsonSlurper()
        slurper.parseText(file.text) as Map
    }

    private File createDirectory(String filePath) {
        def dir = new File(filePath)
        dir.mkdirs()
        dir
    }
}
