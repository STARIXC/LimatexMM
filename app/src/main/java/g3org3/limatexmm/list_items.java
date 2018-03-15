package g3org3.limatexmm;

/**
 * Created by meg3o on 3/12/2018.
 */

public class list_items {

    private String title;
    private String subTitle;
    private Double price;
    private String more;
    private Double more_value;

    public list_items(String title, String subTitle, Double price, String more, Double more_value) {
        this.title = title;
        this.subTitle = subTitle;
        this.price =  price;
        this.more = more;
        this.more_value = more_value;
    }

    public Double getMore_value() {
        return more_value;
    }

    public void setMore_value(Double more_value) {
        this.more_value = more_value;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
