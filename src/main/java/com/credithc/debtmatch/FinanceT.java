package com.credithc.debtmatch;

public class FinanceT {
    private int id;
    private int money;
    private int type;

    public FinanceT() {
    }

    public FinanceT(int id, int money, int type) {
        this.id = id;
        this.money = money;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FinanceT{" +
                "id=" + id +
                ", money=" + money +
                ", type=" + type +
                '}';
    }
}
