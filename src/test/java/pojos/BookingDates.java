package pojos;

public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates()   {

    }

    public BookingDates(String cin, String cout)   {
        setCheckin(cin);
        setCheckout(cout);
    }
    public String getCheckin() {
        return checkin;
    }
    public String getCheckout() {
        return checkout;
    }
    public void setCheckin(String date) {
        checkin = date;
    }
    public void setCheckout(String date) {
        checkout = date;
    }
}
