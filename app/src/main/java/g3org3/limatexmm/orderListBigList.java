package g3org3.limatexmm;

import java.util.HashMap;
import java.util.List;

/**
 * Created by meg3o on 3/29/2018.
 */

public class orderListBigList {

    private HashMap<String, orderListBig> orders;

    public orderListBigList() {

    }

    public orderListBigList(HashMap<String, orderListBig> orders) {
        this.orders = orders;
    }

    public HashMap<String, orderListBig> getOrders() {
        return orders;
    }

    public void setOrders(HashMap<String, orderListBig> orders) {
        this.orders = orders;
    }
}
