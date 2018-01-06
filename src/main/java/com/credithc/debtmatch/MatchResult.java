package com.credithc.debtmatch;

public class MatchResult {
    private int idF;
    private String idD;
    private int money;

    public MatchResult(int idf, String idd, int money) {
        this.idF = idf;
        this.idD = idd;
        this.money = money;
    }

    public int getIdF() {
        return idF;
    }

    public void setIdF(int idF) {
        this.idF = idF;
    }

    public String getIdD() {
        return idD;
    }

    public void setIdD(String idD) {
        this.idD = idD;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "idF=" + idF +
                ", idD=" + idD +
                ", money=" + money +
                '}';
    }
}
