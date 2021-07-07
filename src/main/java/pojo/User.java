package pojo;

public class User {
    private String userName;         //用户名
    private String payId;      //账单Id
    private String amount;      //账单
    private String bandWidth;      //月95峰值

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", payId='" + payId + '\'' +
                ", amount='" + amount + '\'' +
                ", bandWidth='" + bandWidth + '\'' +
                '}';
    }

    public String getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(String bandWidth) {
        this.bandWidth = bandWidth;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
