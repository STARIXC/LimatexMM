package g3org3.limatexmm;

import java.util.Date;

/**
 * Created by meg3o on 3/20/2018.
 */

public class additionalList {
    private Integer orderDeliver;
    private String orderStatus;
    private Date orderDate;

    public additionalList(Integer orderDeliver, String orderStatus,Date orderDate){
        this.orderDeliver = orderDeliver;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public additionalList() {

    }

    public Integer getOrderDeliver() {
        return orderDeliver;
    }

    public void setOrderDeliver(Integer orderDeliver) {
        this.orderDeliver = orderDeliver;
    }
}
