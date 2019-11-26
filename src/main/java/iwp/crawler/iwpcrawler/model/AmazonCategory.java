package iwp.crawler.iwpcrawler.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="amazonCategory")
public class AmazonCategory {

    private String rootcategory;
    private List<String> subcategories;

    public AmazonCategory() {
    }

    public AmazonCategory(String rootcategory, List<String> subcategpries) {
        this.rootcategory = rootcategory;
        this.subcategories = subcategpries;
    }

    public String getRootcategory() {
        return rootcategory;
    }

    public void setRootcategory(String rootcategory) {
        this.rootcategory = rootcategory;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<String> subcategories) {
        this.subcategories = subcategories;
    }
}
