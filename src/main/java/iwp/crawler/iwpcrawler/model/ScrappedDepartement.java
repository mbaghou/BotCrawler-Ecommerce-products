package iwp.crawler.iwpcrawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scrappedDepartement")
public class ScrappedDepartement {

    @Id
    private String scrapped;
    private int count;

    public ScrappedDepartement() {
    }

    public ScrappedDepartement(String scrapped, int count) {
        this.scrapped = scrapped;
        this.count = count;
    }

    public String getScrapped() {
        return scrapped;
    }

    public void setScrapped(String scrapped) {
        this.scrapped = scrapped;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
