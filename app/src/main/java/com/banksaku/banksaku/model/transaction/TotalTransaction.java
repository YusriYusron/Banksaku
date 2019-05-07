package com.banksaku.banksaku.model.transaction;

public class TotalTransaction {
    private String id;
    private String typeTransaction;
    private long totalTransaction;
    private String uId;

    public TotalTransaction() {
    }

    public TotalTransaction(String id, String typeTransaction, long totalTransaction, String uId) {
        this.id = id;
        this.typeTransaction = typeTransaction;
        this.totalTransaction = totalTransaction;
        this.uId = uId;
    }

    public TotalTransaction(String id, String typeTransaction, long totalTransaction) {
        this.id = id;
        this.typeTransaction = typeTransaction;
        this.totalTransaction = totalTransaction;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public long getTotalTransaction() {
        return totalTransaction;
    }

    public String getId() {
        return id;
    }

    public String getuId() {
        return uId;
    }
}
