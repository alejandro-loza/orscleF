package com.mx.finerio.services

import com.mx.finerio.dto.CsvRow
import com.mx.finerio.dto.StatusDto

interface FileService {
    boolean existFile(String resultPath, String accountNumber )
    void createFileRecord(StatusDto statusDto, String filePath)
    void createResume(List<CsvRow> cvsRows, String path)


}