package com.mfino.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class Subscriber {

    String MDN, OriginalMDN, NewMDN;
    String email;
    String Currency;
    String Gender;
    String Address;
    String City;
    String BirthPlace;
    String DateOfBirth;
    String IDNumber;

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getBirthPlace() {
        return BirthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        BirthPlace = birthPlace;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOriginalMDN() {
        return OriginalMDN;
    }

    public void setOriginalMDN(String originalMDN) {
        OriginalMDN = originalMDN;
    }

    public String getNewMDN() {
        return NewMDN;
    }

    public void setNewMDN(String newMDN) {
        NewMDN = newMDN;
    }
    String IMSI, product, marketingCaterogy, first_Name, last_Name, language,
            active_Account_Type;
    String MDN_Status;
    //int Service_Type;
    String IDType;

    public String getIDType() {
        return IDType;
    }

    public void setIDType(String iDType) {
        IDType = iDType;
    }

//	public int getService_Type() {
//		return Service_Type;
//	}
//
//	public void setService_Type(int serviceType) {
//		Service_Type = serviceType;
//	}
    public String getMDN() {
        return MDN;
    }

    public void setMDN(String mDN) {
        MDN = mDN;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String iMSI) {
        IMSI = iMSI;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getMarketingCaterogy() {
        return marketingCaterogy;
    }

    public void setMarketingCaterogy(String marketingCaterogy) {
        this.marketingCaterogy = marketingCaterogy;
    }

    public String getFirst_Name() {
        return first_Name;
    }

    public void setFirst_Name(String firstName) {
        first_Name = firstName;
    }

    public String getLast_Name() {
        return last_Name;
    }

    public void setLast_Name(String lastName) {
        last_Name = lastName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getActive_Account_Type() {
        return active_Account_Type;
    }

    public void setActive_Account_Type(String activeAccountType) {
        active_Account_Type = activeAccountType;
    }

    public String getMDN_Status() {
        return MDN_Status;
    }

    public void setMDN_Status(String mDNStatus) {
        MDN_Status = mDNStatus;
    }
    String Balance;

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }
}

public class GenerateSubscribers {

    public static void fillsubscriber1001(Subscriber s, int k) {
        int mdn = 628820200 + k;
        String MDNStatus[] = {"Active"};
        String product[] = {"1", "2", "3", "4"};
        String currency[] = {"IDR", "USD"};
        String activeAccountType[] = {"PREPAID", "POSTPAID"};
        String language[] = {"en_US", "id_ID"};
        String firstName = "subscriber" + k;
        String lastName = "last" + k;
        String marketingCaterogy[] = {            
            "SMART_Employee_Prepaid",
            "VIP",
            "M_finance_retailer_Prepaid",
            "SMG_FnF_subscriber_Prepaid",
            "SMG_FnF_subscriber_Postpaid",
            "SMART_Employee_Postpaid",
            "SMART_TESTER",
            "KTNA_Prepaid",
            "BISSTEL_Prepaid",
            "HIPMI_Prepaid",
            "CCS_Prepaid_CB_with_NBG",
            "M_Finance_Retailer_Postpaid",
            "REVA_test",
            "SMG_Clients_Postpaid",
            "Postpaid_Corporate_Paid",
            "Postpaid _Corporate_Guarantee",
            "Postpaid_Consumer_AYCE_25",
            "E_Load_Retailers_Prepaid",
            "SMG_Corporate_Postpaid",
            "Regular_Prepaid_Fixed_Wireless",
            "Regular_Postpaid_Fixed_Wireless",
            "Corporate_Postpaid_Fixed_Wireless",
            "Corporate_Prepaid_Fixed_Wireless",
            "CCS_Postpaid_Guarantee_50K_60K_75K",
            "Prepaid_Tarbiyah",
            "Prepaid_EVDO",
            "Postpaid_EVDO",
            "CCS_Prepaid_180K",
            "CCS_Prepaid_180KPlus",
            "Dompet_Merchants",
            "SMG_Postpaid_EVDO",
            "Chatterbox_190",
            "Prepaid_EVDO_Reseller",
            "Corporate_Artatel",
            "BlackBerry_Prepaid_Hebat",
            "BlackBerry_Prepaid_Hemat",
            "Postpaid_EvDO_Silver_Banking_Upfront",
            "SMART_TESTER_Operational"
        };
        //String marketingCaterogy[] = {"3", "2", "3"};
        //s.setService_Type(1001);
        s.setMDN(mdn + "");
        s.setIMSI(mdn + "");
        s.setMarketingCaterogy(marketingCaterogy[k % 39]);
        s.setProduct(product[k % 3]);
        s.setFirst_Name(firstName);
        s.setLast_Name(lastName);
        s.setLanguage(language[k % 2]);
        s.setActive_Account_Type(activeAccountType[k % 2]);
        s.setMDN_Status(MDNStatus[0]);
        s.setOriginalMDN("");
        s.setNewMDN("");
        s.setEmail("raju@mfino.com");
        s.setCurrency(currency[k % 2]);
        s.setAddress("");
        s.setCity("");
        s.setBirthPlace("");
        s.setDateOfBirth("");
        s.setIDType("");
        s.setGender("1");
        s.setBalance("");
        s.setIDNumber("");
    }

//    public static void fillsubscriber1002(Subscriber s, int k) {
//        int mdn = 621245120 + k;
//        //	s.setService_Type(1002);
//        s.setOriginalMDN(s.getMDN());
//        s.setMDN(mdn + "");
//        s.setNewMDN(mdn + "");
//    }

    public static void main(String[] args) throws IOException {
        int k = 1; // String filePath = System.getProperty("user.dir") // +
        // "\\Subscribers\\GenerateSubscribers" + k + ".csv";
        String filePath = "E:\\BulkUploadSubscribers" + 1001 + ".csv";
        File SubscriberFile = new File(filePath);
        if (!SubscriberFile.exists()) {
            SubscriberFile.createNewFile();
        }
        PrintWriter output = new PrintWriter(new FileWriter(SubscriberFile));
        List<Subscriber> results = new ArrayList<Subscriber>();
        String formatspecifier = new String();
        for (int i = 0; i < 19; i++) {
            formatspecifier += "%s|";
        }
        formatspecifier += "%s";
        for (k = 0; k < 20; k++) {
            Subscriber s = new Subscriber();
            fillsubscriber1001(s, k);
            results.add(s);
            output.println(String.format(formatspecifier,
                    k, s.getMDN(),
                    s.getIMSI(), s.getMarketingCaterogy(), s.getProduct(),
                    s.getFirst_Name(), s.getLast_Name(), s.getEmail(),
                    s.getLanguage(), s.getCurrency(), s.getActive_Account_Type(),
                    s.getMDN_Status(), s.getIDType(), s.getIDNumber(), s.getGender(),
                    s.getAddress(), s.getCity(), s.getBirthPlace(),
                    s.getDateOfBirth(), s.getBalance()));
        }
        output.close();
        // this completes the insertion of the Subscribers

        // Update the subscribers.
//        String filePath1002 = "E:\\BulkUploadSubscribers" + 1002 + ".csv";
//        File SubscriberFile1002 = new File(filePath1002);
//        if (!SubscriberFile1002.exists()) {
//            SubscriberFile1002.createNewFile();
//        }
//        PrintWriter output1002 = new PrintWriter(new FileWriter(SubscriberFile1002));
//
//        for (k = 0; k < 1; k++) {
//            Subscriber s = results.get(k);
//            fillsubscriber1002(s, k);
//            output1002.println(String.format(
//                    formatspecifier,
//                    k, s.getMDN(), s.getIMSI(), s.getMarketingCaterogy(), s.getProduct(), s.getFirst_Name(), s.getLast_Name(), s.getEmail(), s.getLanguage(), s.getCurrency(), s.getActive_Account_Type(), s.getMDN_Status(), s.getIDType(), s.getGender(), s.getAddress(), s.getCity(), s.getBirthPlace(), s.getDateOfBirth(), s.getOriginalMDN(), s.getNewMDN(), s.getBalance()));
//        }
//        output1002.close();
//        String str = "0|628811214600|628811214600|Postpaid_Regular_Subscriber|SMART Postpaid|subscriber0|last0|raju@mfino.com|en_US|IDR|PREPAID|Initialized|||Male|||||";
//        String split[] = str.split("[|]");
//        for (int i = 0; i < split.length; i++) {
//            System.out.print(split[i]);
//        }
        System.out.println("TestRaju");

    }
}
