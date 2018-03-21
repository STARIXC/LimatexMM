package g3org3.limatexmm;


import java.util.Date;
import java.util.List;

/**
 * Created by meg3o on 3/13/2018.
 */

class orderList {
    private List<listItems> orderList;
    private userList user;
    private Date curDate;
    private Integer deliver;


    public orderList(List<listItems> orderList, userList user, Date curDate, Integer deliver) {
        this.orderList = orderList;
        this.user = user;
        this.curDate = curDate;
        this.deliver = deliver;
    }

    public Integer getDeliver() {
        return deliver;
    }

    public void setDeliver(Integer deliver) {
        this.deliver = deliver;
    }

    public orderList() {

    }

    public List<listItems> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<listItems> orderList) {
        this.orderList = orderList;
    }

    public userList getUser() {
        return user;
    }

    public void setUser(userList user) {
        this.user = user;
    }

    public Date getCurDate() {
        return curDate;
    }

    public void setCurDate(Date curDate) {
        this.curDate = curDate;
    }
}
