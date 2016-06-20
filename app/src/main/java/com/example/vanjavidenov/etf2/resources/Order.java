package com.example.vanjavidenov.etf2.resources;

/**
 * Created by vanjavidenov on 6/20/16.
 */
public class Order {
    private int id;
    private String item;
    private String quantity;
    private String time;
    private String table;
    private String payment;
    private String sum;

    public Order(String it, String qu, String ti, String ta, String pa, String su, int i){
        item = it;
        quantity = qu;
        time = ti;
        table = ta;
        payment = pa;
        sum = su;
        id = i;
    }

    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
