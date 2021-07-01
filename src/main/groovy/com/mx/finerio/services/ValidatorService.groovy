package com.mx.finerio.services

import com.mx.finerio.dto.CsvRow

interface ValidatorService {
    Boolean areRecordsValid( List<CsvRow> listCsv )
    Boolean hasBeenProcessed( CsvRow csvRow )

}