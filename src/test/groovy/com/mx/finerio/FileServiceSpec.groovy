package com.mx.finerio

import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto
import com.mx.finerio.services.FileService
import com.mx.finerio.services.impl.FileServiceImpl
import groovy.json.JsonOutput
import spock.lang.Specification

class FileServiceSpec extends Specification {
    public static final String TEST_FILES_PATH = 'testFiles'
    FileService service = new FileServiceImpl()

    /*void cleanup(){
        boolean fileSuccessfullyDeleted =  new File(TEST_FILES_PATH).deleteDir()
        println "DELETED" + fileSuccessfullyDeleted
    }*/

    def "Should save a customer file response"() {
        given:
        StatusDto statusDto = new StatusDto()
        statusDto.with {
            data = '{data:testResponse}'
            accountNumber = 1234
        }

        when:
        service.createFileRecord(statusDto, TEST_FILES_PATH)

        then:
        File file = new File(TEST_FILES_PATH + "/responses/${statusDto.accountNumber}.txt")
        assert file.exists()
        assert file.text == statusDto.data
        assert file.name == "${statusDto.accountNumber}.txt"

    }

    def "Should create resume files"() {
        given: 'a row set'
        def happyAccountNumber = 1111
        CsvRow happyRow = generateRow(happyAccountNumber)
        def badAccountNumber = 2222
        CsvRow badRow = generateRow(badAccountNumber)
        def partialAccountNumber = 3333
        CsvRow partialRow = generateRow(partialAccountNumber)
        List<CsvRow> csvRows = [happyRow,badRow,partialRow]

        and: ' a  happyPath file'
        StatusDto happyStatus = new StatusDto()
        happyStatus.with {
            data = generateDataJson(123, true, 456, true)
            accountNumber = happyAccountNumber
        }

        service.createFileRecord(happyStatus, TEST_FILES_PATH)

        and: ' a  bad path file'
        StatusDto badStatus = new StatusDto()
        badStatus.with {
            data = generateDataJson(null, false, null, false)
            accountNumber = badAccountNumber
        }

        service.createFileRecord(badStatus, TEST_FILES_PATH)

        and: ' a  partial path file'
        StatusDto partialStatus = new StatusDto()
        partialStatus.with {
            data = generateDataJson(4567, true, null, false)
            accountNumber = partialAccountNumber
        }

        service.createFileRecord(partialStatus, TEST_FILES_PATH)

        when:
        service.createResume(csvRows, TEST_FILES_PATH)

        then:
        File success = new File(TEST_FILES_PATH + "/resumen/successMigrated.csv")
        assert success.exists()


    }

    private def generateDataJson(Long userId, Boolean userSuccess, Long accountId, Boolean accountSucces) {
        JsonOutput.toJson(
                [success: userSuccess ,
                 user: [success: userSuccess, id: userId],
                 account: [success: accountSucces, id: accountId]])
    }

    private CsvRow generateRow(Long accountNumberToSet){
        CsvRow row = new CsvRow()
        row.with {
            customerName = "REGINA ORTIZ LOPEZ"
            accountFinancialEntityId = 4
            accountName = "ZDINN_NIV2"
            accountNumber = accountNumberToSet
            accountNature = "Debit"
            accountBalance = 63613.67
        }
        row
    }


}
