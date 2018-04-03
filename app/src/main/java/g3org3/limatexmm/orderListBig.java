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

    public orderListBig() {

    }

    public orderListBig(List<listItems> orderList, userList userSimple, additionalList additionalSimple, String dateSimple){
        this.orderList = orderList;
        this.userSimple = userSimple;
        this.additionalSimple = additionalSimple;
        this.dateSimple =dateSimple;
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
