package com.mx.finerio

import com.mx.finerio.dto.AccountCreateDto
import com.mx.finerio.dto.UserCreateDto
import com.mx.finerio.services.PFMService
import com.mx.finerio.services.impl.PFMServiceImpl
import spock.lang.Specification

class PFMServiceSpec extends Specification{
    String host
    String incomeToken
    PFMService pFMService

    void setup(){
         host = 'https://qa-api.actinver.com.mx'
         incomeToken = 'Y0VlZnozV1FlVVNnRWJGdTJzdkhiQTFsa1c2ejdYZFo6aHFLQzhSUU9VbkhDS0Z3bQ=='
         pFMService = new PFMServiceImpl( host, incomeToken)
    }

    void cleanup(){

    }

    def "Should create user"(){
        given:
        def userCreateDto = new UserCreateDto()
        def name='145156159'
        userCreateDto.name=name


        when:
        def response = pFMService.createUser( userCreateDto )
        then:
        assert response.isSucces == true
        assert response.name == name
    }

    def "Should create account"(){
        given:
        def accountCreateDto = new AccountCreateDto()
        Long userIdd=371746
        Long financialEntityIdd=1
        String naturee='Debit'
        String namee='Account'
        String numberr="sdfdsfsd"
        BigDecimal balancee = BigDecimal.ZERO

        accountCreateDto.with {
            userId=userIdd
            financialEntityId=financialEntityIdd
            nature=naturee
            name=namee
            number=numberr
            balance=balancee
        }

        when:
        def response = pFMService.createAccount( accountCreateDto )
        then:
        assert response.isSucces == true
        assert response.name == namee
    }




}
