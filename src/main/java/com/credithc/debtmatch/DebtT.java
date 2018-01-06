package com.credithc.debtmatch;

public class DebtT {
    private String id;
    private int money;

    public DebtT() {
    }

    public DebtT(String id, int money) {
        this.id = id;
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "DebtT{" +
                "id=" + id +
                ", money=" + money +
                '}';
    }
}
