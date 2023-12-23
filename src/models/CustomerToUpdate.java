package models;

public class CustomerToUpdate {
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    private Address address;
    private String telephone;
    private String businessName;

    public CustomerToUpdate(Address address, String telephone, String businessName) {
        this.address = address;
        this.telephone = telephone;
        this.businessName = businessName;
    }
}
