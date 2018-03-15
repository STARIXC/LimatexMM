package g3org3.limatexmm;

/**
 * Created by meg3o on 3/14/2018.
 */

public class commentList {
    
    private String comName;
    private Integer comPrice;
    
    public commentList(String comName,Integer comPrice){
        this.comName = comName;
        this.comPrice = comPrice;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public Integer getComPrice() {
        return comPrice;
    }

    public void setComPrice(Integer comPrice) {
        this.comPrice = comPrice;
    }

}
