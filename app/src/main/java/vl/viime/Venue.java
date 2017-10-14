package vl.viime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mousakhan on 2017-09-27.
 */

public class Venue implements Serializable {
    String name;
    String id;
    String price;
    String code;
    String cuisine;
    String type;
    String address;
    String description;
    String distance;
    String logo;
    String website;
    String number;
    String city;
    ArrayList<String> deals;
    double latitude;
    double longitude;
    long numberOfDeals;
}
