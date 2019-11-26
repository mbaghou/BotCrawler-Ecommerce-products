package iwp.crawler.iwpcrawler.bean;

import iwp.crawler.iwpcrawler.model.AmazonItem;
import org.json.simple.JSONArray;

import java.util.List;

public class MasterResponse {

    private List<AmazonItem> amazonItems;
    private String departement;


    public MasterResponse() {
    }

    public MasterResponse(List<AmazonItem> amazonItems, String departement) {
        this.amazonItems = amazonItems;
        this.departement = departement;
    }

    public List<AmazonItem> getAmazonItems() {
        return amazonItems;
    }

    public void setAmazonItems(List<AmazonItem> amazonItems) {
        this.amazonItems = amazonItems;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }
}
