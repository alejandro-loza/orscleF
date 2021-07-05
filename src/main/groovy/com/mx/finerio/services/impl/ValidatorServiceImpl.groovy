package com.mx.finerio.services.impl

import com.mx.finerio.dto.CsvRow
import com.mx.finerio.services.ValidatorService

class ValidatorServiceImpl implements ValidatorService{

    @Override
    Boolean areRecordsUniqueValid(List<CsvRow> listCsv) {
        listCsv == listCsv.toUnique { a, b -> a.accountNumber <=> b.accountNumber }
    }

    @Override
    Boolean areNumberRecordsSupported(List<CsvRow> listCsv) {
        listCsv.size() <= 10000
    }


    @Override
    Boolean hasBeenProcessed(CsvRow csvRow) {
        return null
    }
}
