package com.mx.finerio.services

import com.mx.finerio.dto.StatusDto

interface FileService {
    void createFileRecord(StatusDto statusDto)
    void createResume()

}