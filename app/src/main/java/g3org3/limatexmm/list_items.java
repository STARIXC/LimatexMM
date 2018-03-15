package g3org3.limatexmm;

/**
 * Created by meg3o on 3/12/2018.
 */

public class list_items {

    private String title;
    private String subTitle;
    private String price;
    private String more;

    public list_items(String title, String subTitle, String price, String more) {
        this.title = title;
        this.subTitle = subTitle;
        this.price = price;
        this.more = more;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
