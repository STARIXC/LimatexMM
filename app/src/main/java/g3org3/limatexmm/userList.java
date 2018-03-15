package g3org3.limatexmm;


import java.util.Date;

/**
 * Created by meg3o on 3/13/2018.
 */

class userList {
    private String userAddr;
    private String userName;
    private String userPhone;
    private String userOrders;
    private Date userJoin;
    private String userDeny;


    public userList(String userAddr, String userName, String userPhone, String userOrders, Date userJoin, String userDeny) {
        this.userAddr = userAddr;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userOrders = userOrders;
        this.userJoin = userJoin;
        this.userDeny = userDeny;
    }

    public userList() {

    }

    public Date getUserJoin() {
        return userJoin;
    }

    public void setUserJoin(Date userJoin) {
        this.userJoin = userJoin;
    }

    public String getUserOrders() {
        return userOrders;
    }

    public void setUserOrders(String userOrders) {
        this.userOrders = userOrders;
    }


    public String getUserDeny() {
        return userDeny;
    }

    public void setUserDeny(String userDeny) {
        this.userDeny = userDeny;
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
