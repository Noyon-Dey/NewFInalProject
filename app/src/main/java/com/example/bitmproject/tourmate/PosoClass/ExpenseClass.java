package com.example.bitmproject.tourmate.PosoClass;

import java.io.Serializable;



public class ExpenseClass implements Serializable {
    String userId;
    String eventId;
    String expenseId;
    double expenseAmount;
    String expenseTittle;
    String expenseDate;

    public ExpenseClass(String userId, String eventId, String expenseId, double expenseAmount, String expenseTittle) {
        this.userId = userId;
        this.eventId = eventId;
        this.expenseId = expenseId;
        this.expenseAmount = expenseAmount;
        this.expenseTittle = expenseTittle;
    }

    public ExpenseClass(String userId, String eventId, String expenseId, double expenseAmount, String expenseTittle, String expenseDate) {
        this.userId = userId;
        this.eventId = eventId;
        this.expenseId = expenseId;
        this.expenseAmount = expenseAmount;
        this.expenseTittle = expenseTittle;
        this.expenseDate = expenseDate;
    }

    public ExpenseClass() {
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseTittle() {
        return expenseTittle;
    }

    public void setExpenseTittle(String expenseTittle) {
        this.expenseTittle = expenseTittle;
    }
}
