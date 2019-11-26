package iwp.crawler.iwpcrawler.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "amazondepartement")
public class AmazonDepartement {

    private String subcategory;
    private String departement;
    private String url;

    public AmazonDepartement() {
    }

    public AmazonDepartement(String subcategory, String departement, String url) {
        this.subcategory = subcategory;
        this.departement = departement;
        this.url = url;
    }

    public AmazonDepartement(String departement) {
        this.departement = departement;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
