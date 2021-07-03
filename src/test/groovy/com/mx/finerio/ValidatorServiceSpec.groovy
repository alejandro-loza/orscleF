package com.mx.finerio

import com.mx.finerio.dto.CsvRow
import com.mx.finerio.services.ValidatorService
import com.mx.finerio.services.impl.ValidatorServiceImpl
import spock.lang.Specification

class ValidatorServiceSpec extends Specification {
    ValidatorService service = new ValidatorServiceImpl()
    def "Should not validate that a list duplicated"(){
        given:
        CsvRow row = new CsvRow()
        row.with {
            customerName = 'username'
            accountNature = 'Debit'
            accountNumber = 123
        }
        List<CsvRow> rows = [row,row]

        when:
        def result = service.areRecordsUniqueValid([row, row])

        then:
        assert result == false
    }

    def "Should validate that a list duplicated"(){
        given:
        CsvRow row1 = new CsvRow()
        row1.with {
            customerName = 'username'
            accountNature = 'Debit'
            accountNumber = 123
        }

        CsvRow row2 = new CsvRow()
        row2.with {
            customerName = 'username'
            accountNature = 'Debit'
            accountNumber = 456
        }
        List<CsvRow> rows = [row1,row2]

        when:
        def result = service.areRecordsUniqueValid(rows)

        then:
        assert result == true
    }

}
