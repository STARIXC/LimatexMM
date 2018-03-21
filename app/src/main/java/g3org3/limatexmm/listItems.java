package g3org3.limatexmm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by meg3o on 3/12/2018.
 */

public class listItems implements Parcelable{

    private String itemTitle;
    private String itemSubtitle;
    private Double itemPrice;
    private String itemMore;
    private Double itemMorevalue;
    private Integer itemPrepare;

    public listItems(String itemTitle, String itemSubtitle, Double itemPrice, String itemMore, Double itemMorevalue, Integer itemPrepare) {
        this.itemTitle = itemTitle;
        this.itemSubtitle = itemSubtitle;
        this.itemPrice =  itemPrice;
        this.itemMore = itemMore;
        this.itemMorevalue = itemMorevalue;
        this.itemPrepare = itemPrepare;
    }


    public listItems() {

    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemSubtitle() {
        return itemSubtitle;
    }

    public void setItemSubtitle(String itemSubtitle) {
        this.itemSubtitle = itemSubtitle;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemMore() {
        return itemMore;
    }

    public void setItemMore(String itemMore) {
        this.itemMore = itemMore;
    }

    public Double getItemMorevalue() {
        return itemMorevalue;
    }

    public void setItemMorevalue(Double itemMorevalue) {
        this.itemMorevalue = itemMorevalue;
    }

    public Integer getItemPrepare() {
        return itemPrepare;
    }

    public void setItemPrepare(Integer itemPrepare) {
        this.itemPrepare = itemPrepare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemTitle);
        parcel.writeString(itemSubtitle);
        parcel.writeDouble(itemPrice);
        parcel.writeString(itemMore);
        parcel.writeDouble(itemMorevalue);
        parcel.writeInt(itemPrepare);
    }

    public static final Creator<listItems> CREATOR = new Creator<listItems>() {
        @Override
        public listItems createFromParcel(Parcel in) {
            return new listItems(in);
        }

        @Override
        public listItems[] newArray(int size) {
            return new listItems[size];
        }
    };

    private listItems(Parcel in) {
        itemTitle = in.readString();
        itemSubtitle = in.readString();
        itemPrice = in.readDouble();
        itemMore = in.readString();
        itemMorevalue = in.readDouble();
        itemPrepare = in.readInt();
    }

}
