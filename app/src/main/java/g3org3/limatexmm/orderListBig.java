package g3org3.limatexmm;

import java.util.List;

/**
 * Created by meg3o on 3/20/2018.
 */

public class orderListBig {

    List<listItems> orderList;
   userList userSimple;
    additionalList additionalSimple;
   String dateSimple;
   Integer countSimple;
   Boolean paid;

    public orderListBig() {

    }

    public orderListBig(List<listItems> orderList, userList userSimple, additionalList additionalSimple, String dateSimple,Integer countSimple,Boolean paid) {
        this.orderList = orderList;
        this.userSimple = userSimple;
        this.additionalSimple = additionalSimple;
        this.dateSimple = dateSimple;
        this.countSimple = countSimple;
        this.paid = paid;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getCountSimple() {
        return countSimple;
    }

    public void setCountSimple(Integer countSimple) {
        this.countSimple = countSimple;
    }

    public String getDateSimple() {
        return dateSimple;
    }

    public void setDateSimple(String dateSimple) {
        this.dateSimple = dateSimple;
    }

    public List<listItems> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<listItems> orderList) {
        this.orderList = orderList;
    }

    public userList getUserSimple() {
        return userSimple;
    }

    public void setUserSimple(userList userSimple) {
        this.userSimple = userSimple;
    }

    public additionalList getAdditionalSimple() {
        return additionalSimple;
    }

    public void setAdditionalSimple(additionalList additionalSimple) {
        this.additionalSimple = additionalSimple;
    }
}
