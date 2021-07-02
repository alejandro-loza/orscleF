package com.mx.finerio.services

import com.mx.finerio.dto.CsvRow

interface ReadFileService {
    List<CsvRow> processInputFileOneByOne(String inputFilePath) throws Exception
    List<CsvRow> processInputFile(String inputFilePath) throws Exception
}