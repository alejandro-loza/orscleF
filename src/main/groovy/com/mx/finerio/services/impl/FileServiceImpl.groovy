package com.mx.finerio.services.impl

import com.mx.finerio.dto.StatusDto
import com.mx.finerio.services.FileService

class FileServiceImpl implements  FileService {
    @Override
    void createFileRecord(StatusDto statusDto, String filePath ) {
        File file = new File(filePath + "/${statusDto.customerNumber}.txt")

        if (!file.isFile() && !file.createNewFile()) {
            println "Failed to create file: $file"
            return
        }
        file.append(statusDto.data)
    }

    @Override
    void createResume() {

    }
}
