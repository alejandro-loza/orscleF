package com.mx.finerio.dto

class ResumeCsvRow extends CsvRow {
    Long finerioCustomerId
    Long finerioAccountId

    ResumeCsvRow() {}

    ResumeCsvRow(CsvRow csvRow) {
        this.customerName = csvRow.customerName
        this.accountNumber = csvRow.accountNumber
        this.accountName = csvRow.accountName
        this.accountNature = csvRow.accountNature
        this.accountBalance = csvRow.accountBalance
        this.accountFinancialEntityId = csvRow.accountFinancialEntityId
    }

}
