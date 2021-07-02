package com.mx.finerio

import com.mx.finerio.services.ReadFileService
import spock.lang.Specification

class ReadFileServiceSpec extends Specification{


    public static final String TEST_FILE = '/home/pinky/Proyectos/finerio/oracleDinnCharge/src/test/resources/test.csv'
    public static final String DINN_CSV_FILE = '/home/pinky/Proyectos/finerio/oracleDinnCharge/src/test/resources/dinnDb.csv'

    void setup(){
        createFile()
    }

    void cleanup(){
        boolean fileSuccessfullyDeleted =  new File(TEST_FILE).delete()
        println "DELETED" + fileSuccessfullyDeleted
    }


    def "Should read a csv file all at once"(){
        given:
        ReadFileService reader = new ReadFileService()

        when:
        def response =reader.processInputFile(DINN_CSV_FILE)

        then:
        assert response
        assert response.size() == 4818
    }

    def "Should read a csv file one by one"(){
        given:
        ReadFileService reader = new ReadFileService()

        when:
        def response =reader.processInputFileOneByOne(DINN_CSV_FILE)

        then:
        assert response
        assert response.size() == 4818

        def group = response.groupBy {
            it.userName
        }

        assert group.size() == 755

        assert group["ABELARDO SANCHEZ ORTA"].size() == 3044

        def group2 = response.groupBy {
            it.accountNumber
        }
        assert group2.size() == 4818
    }


    def createFile(){
        def file = new File(TEST_FILE)

        if (!file.isFile() && !file.createNewFile()) {
            println "Failed to create file: $file"
            return
        }

      //  1048576.times {
        10.times {
            def row = [
                    "REGINA ORTIZ LOPEZ",
                    4,
                    "ZDINN_NIV2",
                    8104416,
                    "Debit",
                    63613.67
            ]
            file.append row.join(',')
            file.append '\n'

        }

    }
}
