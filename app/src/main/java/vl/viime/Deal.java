package vl.viime;

import java.io.Serializable;

/**
 * Created by mousakhan on 2017-10-09.
 */

public class Deal implements Serializable {
    String id;
    String shortDescription;
    String title;
    String numRedeptionsRequired;
    String validFrom;
    String validTo;
    String recurringFrom;
    String recurringTo;
    String numberOfPeople;
    String venueId;
}
