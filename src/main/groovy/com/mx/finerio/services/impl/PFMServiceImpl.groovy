package com.mx.finerio.services.impl

import com.mx.finerio.dto.AccountCreateDto
import com.mx.finerio.dto.AccountDto
import com.mx.finerio.dto.UserCreateDto
import com.mx.finerio.dto.UserDto
import com.mx.finerio.services.PFMService
import groovy.json.JsonSlurper
import wslite.rest.RESTClient
import static java.nio.charset.StandardCharsets.*
import java.time.ZonedDateTime

class PFMServiceImpl implements  PFMService{

    String oauth_path = '/oauth/accesstoken?grant-type=client_credentials'
    String accountPath = '/finerio-management-sandbox/accounts'
    String userPath = '/finerio-management-sandbox/users'
    String host
    String incomeToken
    String token
    ZonedDateTime tokenTime = ZonedDateTime.now().minusMinutes( 59 )
    Integer tokenMinutesDuration = 59

    PFMServiceImpl (String host, String incomeToken) {
        this.host = host
        this.incomeToken = incomeToken
    }

    private String getAccessToken() throws Exception {

        def minusOneHour = ZonedDateTime.now().minusMinutes( tokenMinutesDuration )
        if( minusOneHour.isBefore( tokenTime ) ) { return this.token }
        def response
        def loginClient=new RESTClient( host )

        try{

            response = loginClient.post( path: oauth_path,
                    headers: [ 'Authorization': "Basic $incomeToken" ] ) {
                urlenc grant_type: 'client_credentials'
            }

        }catch( wslite.rest.RESTClientException e ){
            throw new Exception(
                    'PFMServiceImpl.getAccessToken.error.onCall')
        }

        def jsonMap = new JsonSlurper().parseText( new String( response.data ) )

        this.token = jsonMap.accessToken
        this.tokenTime = ZonedDateTime.now()
        this.token
    }

    @Override
    UserDto createUser(UserCreateDto userCreateDto) {

        def data =  [ 'name': userCreateDto.name ]
        def client = new RESTClient( host )
        def response
        def userDto = new UserDto()
        def bodyResponse
        try{

            response = client.post( path: userPath,
                    headers: [ 'Authorization': "Bearer ${getAccessToken()}" ] ) {
                json data
            }

        }catch( wslite.rest.RESTClientException e ){

                bodyResponse = new JsonSlurper().parseText( new String( e.response.data, UTF_8) )
                userDto.isSucces = false
                userDto.statusCode = e.response.statusCode
                userDto.errorMessage = bodyResponse['description']
                userDto.errorDetail = bodyResponse['detail']
        }

        bodyResponse = new JsonSlurper().parseText( new String( response.data, UTF_8) )
        userDto.isSucces = true
        userDto.id = bodyResponse['id']
        userDto.dateCreated = bodyResponse['dateCreated']
        userDto.name = bodyResponse['name']
        userDto
    }

    @Override
    AccountDto createAccount( AccountCreateDto accountCreateDto ) {

        def data =  [  userId: accountCreateDto.userId,
                       financialEntityId: accountCreateDto.financialEntityId,
                       nature: accountCreateDto.nature,
                       name: accountCreateDto.name,
                       number: accountCreateDto.number,
                       balance: accountCreateDto.balance ]

        def client = new RESTClient( host )
        def response
        def accountDto = new AccountDto()
        def bodyResponse
        try{

            response = client.post( path: accountPath,
                    headers: [ 'Authorization': "Bearer ${getAccessToken()}" ] ) {
                json data
            }

        }catch( wslite.rest.RESTClientException e ){

            bodyResponse = new JsonSlurper().parseText( new String( e.response.data, UTF_8) )
            accountDto.isSucces = false
            accountDto.statusCode = e.response.statusCode
            accountDto.errorMessage = bodyResponse['description']
            accountDto.errorDetail = bodyResponse['detail']
        }

        bodyResponse = new JsonSlurper().parseText( new String( response.data, UTF_8) )
        accountDto.isSucces = true
        accountDto.id = bodyResponse['id']
        accountDto.dateCreated = bodyResponse['dateCreated']
        accountDto.lastUpdated = bodyResponse['lastUpdated']
        accountDto.nature = bodyResponse['nature']
        accountDto.name = bodyResponse['name']
        accountDto.number = bodyResponse['number']
        accountDto.balance = bodyResponse['balance']
        accountDto.chargeable = bodyResponse['chargeable']
        accountDto
    }
}
