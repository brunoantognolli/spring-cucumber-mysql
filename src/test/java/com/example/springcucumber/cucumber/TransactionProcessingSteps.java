package com.example.springcucumber.cucumber;

import com.example.springcucumber.config.ProcessingItemProcessor;
import com.example.springcucumber.entity.DayTransaction;
import com.example.springcucumber.entity.ProcessedDayTransaction;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionProcessingSteps {

    private DayTransaction dayTransaction;
    private ProcessedDayTransaction processedTransaction;

    @Autowired
    private ProcessingItemProcessor processor;

    @Given("there is a day transaction with following details")
    public void thereIsADayTransactionWithFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> data = rows.get(0);

        dayTransaction = new DayTransaction();
        dayTransaction.setId(Long.parseLong(data.get("id")));
        dayTransaction.setDescription(data.get("description"));
        dayTransaction.setAmount(new BigDecimal(data.get("amount")));
        dayTransaction.setStatus(data.get("status"));
        dayTransaction.setTransactionDate(LocalDateTime.now());
    }

    @When("the transaction is processed")
    public void theTransactionIsProcessed() throws Exception {
        processedTransaction = processor.process(dayTransaction);
    }

    @Then("the processed transaction should have following details")
    public void theProcessedTransactionShouldHaveFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> data = rows.get(0);

        assertEquals(data.get("processedStatus"), processedTransaction.getProcessedStatus());
        assertEquals(new BigDecimal(data.get("processedAmount")), processedTransaction.getProcessedAmount());
    }
} 