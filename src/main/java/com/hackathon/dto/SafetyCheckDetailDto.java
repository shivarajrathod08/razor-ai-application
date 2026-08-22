package com.hackathon.dto;



import com.hackathon.model.enums.SafetyCheckVerdict;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SafetyCheckDetailDto {
    private boolean activeCartCheck;
    private boolean productExistenceCheck;
    private boolean productActiveCheck;
    private boolean stockAvailabilityCheck;
    private boolean serverSidePriceVerification;
    private boolean transactionLimitCheck;
    private boolean customerConfirmationCheck;
    private boolean idempotencyCheck;
    private BigDecimal calculatedAmount;
    private BigDecimal maxTransactionLimit;
    private SafetyCheckVerdict verdict;
    private List<String> reasons = new ArrayList<>();

    public SafetyCheckDetailDto() {}

    public boolean isActiveCartCheck() { return activeCartCheck; }
    public void setActiveCartCheck(boolean activeCartCheck) { this.activeCartCheck = activeCartCheck; }
    public boolean isProductExistenceCheck() { return productExistenceCheck; }
    public void setProductExistenceCheck(boolean productExistenceCheck) { this.productExistenceCheck = productExistenceCheck; }
    public boolean isProductActiveCheck() { return productActiveCheck; }
    public void setProductActiveCheck(boolean productActiveCheck) { this.productActiveCheck = productActiveCheck; }
    public boolean isStockAvailabilityCheck() { return stockAvailabilityCheck; }
    public void setStockAvailabilityCheck(boolean stockAvailabilityCheck) { this.stockAvailabilityCheck = stockAvailabilityCheck; }
    public boolean isServerSidePriceVerification() { return serverSidePriceVerification; }
    public void setServerSidePriceVerification(boolean serverSidePriceVerification) { this.serverSidePriceVerification = serverSidePriceVerification; }
    public boolean isTransactionLimitCheck() { return transactionLimitCheck; }
    public void setTransactionLimitCheck(boolean transactionLimitCheck) { this.transactionLimitCheck = transactionLimitCheck; }
    public boolean isCustomerConfirmationCheck() { return customerConfirmationCheck; }
    public void setCustomerConfirmationCheck(boolean customerConfirmationCheck) { this.customerConfirmationCheck = customerConfirmationCheck; }
    public boolean isIdempotencyCheck() { return idempotencyCheck; }
    public void setIdempotencyCheck(boolean idempotencyCheck) { this.idempotencyCheck = idempotencyCheck; }
    public BigDecimal getCalculatedAmount() { return calculatedAmount; }
    public void setCalculatedAmount(BigDecimal calculatedAmount) { this.calculatedAmount = calculatedAmount; }
    public BigDecimal getMaxTransactionLimit() { return maxTransactionLimit; }
    public void setMaxTransactionLimit(BigDecimal maxTransactionLimit) { this.maxTransactionLimit = maxTransactionLimit; }
    public SafetyCheckVerdict getVerdict() { return verdict; }
    public void setVerdict(SafetyCheckVerdict verdict) { this.verdict = verdict; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}