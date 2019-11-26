package iwp.crawler.iwpcrawler.utils;

public class WalmartSelector {

    public static final String WALMART_URL = "https://www.walmart.com";
    public static final String WALMAR_QUERY = "https://www.walmart.com/search/?cat_id=0&page=@PAGE&query=";
    public static final String WALMART_ITEM = "https://www.walmart.com/ip/";


    public static final String EXCLUDE_WALMART_PATTERN =
            ".*(Streaming|Coupon|coupon|Free|Business|Walmart|WALMART|" +
                    ".com|Download|download|Membership|membership|Shopping|shopping|Sales|Sell|Deals|Shop|Brand|Rental|Unlimited|Gift|Trade|Discover|Made in).*|App";














    private WalmartSelector(){}
}
