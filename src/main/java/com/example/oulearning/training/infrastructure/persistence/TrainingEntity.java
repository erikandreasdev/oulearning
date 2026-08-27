package com.example.oulearning.training.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

class TrainingEntity {

    private Long id;
    private Long requestedByEmployeeId;
    private Long organizationalUnitId;
    private String name;
    private BigDecimal costAmount;
    private String costCurrency;
    private Integer hours;
    private String purposeType;
    private String purposeOther;
    private Long typeId;
    private String status;
    private String managerReviewComments;
    private String managerReviewModality;
    private Instant managerReviewStartDate;
    private Instant managerReviewEndDate;
    private Long managerReviewExternalProviderId;
    private Instant managerReviewReviewedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean active;

    private Set<Long> attendeeIds = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getRequestedByEmployeeId() {
        return requestedByEmployeeId;
    }

    public void setRequestedByEmployeeId(final Long requestedByEmployeeId) {
        this.requestedByEmployeeId = requestedByEmployeeId;
    }

    public Long getOrganizationalUnitId() {
        return organizationalUnitId;
    }

    public void setOrganizationalUnitId(final Long organizationalUnitId) {
        this.organizationalUnitId = organizationalUnitId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(final BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public String getCostCurrency() {
        return costCurrency;
    }

    public void setCostCurrency(final String costCurrency) {
        this.costCurrency = costCurrency;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(final Integer hours) {
        this.hours = hours;
    }

    public String getPurposeType() {
        return purposeType;
    }

    public void setPurposeType(final String purposeType) {
        this.purposeType = purposeType;
    }

    public String getPurposeOther() {
        return purposeOther;
    }

    public void setPurposeOther(final String purposeOther) {
        this.purposeOther = purposeOther;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(final Long typeId) {
        this.typeId = typeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getManagerReviewComments() {
        return managerReviewComments;
    }

    public void setManagerReviewComments(final String managerReviewComments) {
        this.managerReviewComments = managerReviewComments;
    }

    public String getManagerReviewModality() {
        return managerReviewModality;
    }

    public void setManagerReviewModality(final String managerReviewModality) {
        this.managerReviewModality = managerReviewModality;
    }

    public Instant getManagerReviewStartDate() {
        return managerReviewStartDate;
    }

    public void setManagerReviewStartDate(final Instant managerReviewStartDate) {
        this.managerReviewStartDate = managerReviewStartDate;
    }

    public Instant getManagerReviewEndDate() {
        return managerReviewEndDate;
    }

    public void setManagerReviewEndDate(final Instant managerReviewEndDate) {
        this.managerReviewEndDate = managerReviewEndDate;
    }

    public Long getManagerReviewExternalProviderId() {
        return managerReviewExternalProviderId;
    }

    public void setManagerReviewExternalProviderId(final Long managerReviewExternalProviderId) {
        this.managerReviewExternalProviderId = managerReviewExternalProviderId;
    }

    public Instant getManagerReviewReviewedAt() {
        return managerReviewReviewedAt;
    }

    public void setManagerReviewReviewedAt(final Instant managerReviewReviewedAt) {
        this.managerReviewReviewedAt = managerReviewReviewedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public Set<Long> getAttendeeIds() {
        return attendeeIds;
    }

    public void setAttendeeIds(final Set<Long> attendeeIds) {
        this.attendeeIds = attendeeIds;
    }
}
