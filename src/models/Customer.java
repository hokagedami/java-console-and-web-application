package models;

public class Customer {
    int id;
    private Address address;
    private String telephone;
    private String businessName;

    public Customer(Address address, String telephone, String businessName) {
        this.address = address;
        this.telephone = telephone;
        this.businessName = businessName;
    }

    public Customer(int id, Address address, String telephone, String businessName) {
        this.id = id;
        this.address = address;
        this.telephone = telephone;
        this.businessName = businessName;
    }
    public Customer(){}


    public int getId() {
        return id;
    }

    public String getAddress() {
        // return string concatenation of address fields
        String addressLine1 = address.getAddressLine1();
        String addressLine2 = address.getAddressLine2();
        String addressLine3 = address.getAddressLine3();
        String country = address.getCountry();
        String postCode = address.getPostCode();

        if(addressLine2.isBlank() && addressLine3.isBlank()){
            return String.format("%s, %s, %s", addressLine1, postCode, country);
        }
        else if(addressLine3.isBlank()){
            return String.format("%s, %s, %s, %s", addressLine1, addressLine2, postCode, country);
        }
        else if(addressLine2.isBlank()){
            return String.format("%s, %s, %s, %s", addressLine1, addressLine3, postCode, country);
        }
        return String.format("%s, %s, %s, %s, %s",
                addressLine1, addressLine2, addressLine3, postCode, country);
    }

    public Address getAddressObject() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
