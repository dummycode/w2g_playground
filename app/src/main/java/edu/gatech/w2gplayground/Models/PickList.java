package edu.gatech.w2gplayground.Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickList extends Model {
    private String id;
    private Date assignedAt;
    private List<Order> orders;

    public PickList(String id, Date assignedAt, List<Order> orders) {
        this.id = id;
        this.assignedAt = assignedAt;
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    /**
     * @return number of orders in pick list
     */
    public int getOrderCount() {
        return this.orders.size();
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public String getAssignedAtString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        return sdf.format(assignedAt);
    }
}
