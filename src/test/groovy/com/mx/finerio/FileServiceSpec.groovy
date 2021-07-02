package com.mx.finerio

import com.mx.finerio.dto.StatusDto
import com.mx.finerio.services.FileService
import com.mx.finerio.services.impl.FileServiceImpl
import spock.lang.Specification

class FileServiceSpec extends Specification {
    public static final String TEST_FILES_PATH = 'testFiles'
    FileService service = new FileServiceImpl()

    def "Should save a customer file response"() {
        given:
        StatusDto statusDto = new StatusDto()
        statusDto.with {
            data = '{data:testResponse}'
            customerNumber = 1234
        }

        when:
        service.createFileRecord(statusDto, TEST_FILES_PATH)

        then:
        File file = new File(TEST_FILES_PATH + "/${statusDto.customerNumber}.txt")
        assert file.exists()
        assert file.text == statusDto.data
        assert file.name == "${statusDto.customerNumber}.txt"

        file.delete()
    }

}
