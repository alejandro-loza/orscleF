package com.mx.finerio.services.impl

import com.mx.finerio.dto.CsvRow
import com.mx.finerio.services.ValidatorService

class ValidatorServiceImpl implements ValidatorService{

    //este metodo va a validar todos los registros, si encuentra repetidos los escribe en un archivo y regresa false, si todo esta bien solo regrea true
    @Override
    Boolean areRecordsValid(List<CsvRow> listCsv) {
        return null
    }

    @Override
    Boolean hasBeenProcessed(CsvRow csvRow) {
        return null
    }
}
