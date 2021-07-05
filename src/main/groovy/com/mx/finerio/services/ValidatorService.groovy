package com.mx.finerio.services

import com.mx.finerio.dto.CsvRow

interface ValidatorService {
    Boolean areRecordsUniqueValid(List<CsvRow> listCsv )
    Boolean hasBeenProcessed( CsvRow csvRow )
    Boolean areNumberRecordsSupported(List<CsvRow> listCsv)

}