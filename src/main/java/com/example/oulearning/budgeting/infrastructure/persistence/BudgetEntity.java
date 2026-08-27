package com.example.oulearning.budgeting.infrastructure.persistence;

import java.math.BigDecimal;

class BudgetEntity {

    private Long id;
    private Long organizationalUnitId;
    private Integer fiscalYear;
    private BigDecimal totalAmount;
    private BigDecimal reservedAmount;
    private BigDecimal availableAmount;
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getOrganizationalUnitId() {
        return organizationalUnitId;
    }

    public void setOrganizationalUnitId(final Long organizationalUnitId) {
        this.organizationalUnitId = organizationalUnitId;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(final Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReservedAmount() {
        return reservedAmount;
    }

    public void setReservedAmount(final BigDecimal reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(final BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }
}
