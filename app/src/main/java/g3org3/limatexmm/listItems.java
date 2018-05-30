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
    private Integer itemQuantity;
    private String itemMore;
    private Double itemMorevalue;
    private Integer itemPrepare;
    private Integer imgURL;

    public listItems(String itemTitle, String itemSubtitle, Double itemPrice,Integer itemQuantity, String itemMore, Double itemMorevalue, Integer itemPrepare,Integer imgURL) {
        this.itemTitle = itemTitle;
        this.itemSubtitle = itemSubtitle;
        this.itemPrice =  itemPrice;
        this.itemQuantity = itemQuantity;
        this.itemMore = itemMore;
        this.itemMorevalue = itemMorevalue;
        this.itemPrepare = itemPrepare;
        this.imgURL = imgURL;
    }


    public listItems() {

    }

    public Integer getImgURL() {
        return imgURL;
    }

    public void setImgURL(Integer imgURL) {
        this.imgURL = imgURL;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
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
        parcel.writeInt(itemQuantity);
        parcel.writeString(itemMore);
        parcel.writeDouble(itemMorevalue);
        parcel.writeInt(itemPrepare);
        parcel.writeInt(imgURL);
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
        itemQuantity = in.readInt();
        itemMore = in.readString();
        itemMorevalue = in.readDouble();
        itemPrepare = in.readInt();
        imgURL = in.readInt();
    }

}
