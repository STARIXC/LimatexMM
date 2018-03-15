package g3org3.limatexmm;

import android.text.SpannableString;

/**
 * Created by meg3o on 3/14/2018.
 */

public class commentList {
    
    private SpannableString comName;
    private Double comPrice;
    
    public commentList(SpannableString comName,Double comPrice){
        this.comName = comName;
        this.comPrice = comPrice;
    }

    public SpannableString getComName() {
        return comName;
    }

    public void setComName(SpannableString comName) {
        this.comName = comName;
    }

    public Double getComPrice() {
        return comPrice;
    }

    public void setComPrice(Double comPrice) {
        this.comPrice = comPrice;
    }

}
