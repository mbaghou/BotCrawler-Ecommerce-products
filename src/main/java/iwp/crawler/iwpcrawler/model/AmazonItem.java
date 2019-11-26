package iwp.crawler.iwpcrawler.model;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

public class AmazonItem {

    @Id
    @Indexed(unique = true)
    private String datasin;
    private String source;
    private String title;
    private Double score;
    private String price;
    private String category;
    private String subcategory1;
    private String subcategory2;
    private String subcategory3;
    private String subcategory4;

    public AmazonItem() {
    }


    public AmazonItem(String datasin, String source, String title, Double score, String price, List<String> categories) {
        if(!categories.isEmpty()){
            try {
                this.category = categories.get(0);
                this.subcategory1 = categories.get(1);
                this.subcategory2 = categories.get(2);
                this.subcategory3 = categories.get(3);
                this.subcategory4 = categories.get(4);
            }catch (IndexOutOfBoundsException e){

            }finally {
                this.datasin = datasin;
                this.source = source;
                this.title = title;
                this.score = score;
                this.price = price;
            }

        }
    }

    public String getDatasin() {
        return datasin;
    }

    public void setDatasin(String datasin) {
        this.datasin = datasin;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory1() {
        return subcategory1;
    }

    public void setSubcategory1(String subcategory1) {
        this.subcategory1 = subcategory1;
    }

    public String getSubcategory2() {
        return subcategory2;
    }

    public void setSubcategory2(String subcategory2) {
        this.subcategory2 = subcategory2;
    }

    public String getSubcategory3() {
        return subcategory3;
    }

    public void setSubcategory3(String subcategory3) {
        this.subcategory3 = subcategory3;
    }

    public String getSubcategory4() {
        return subcategory4;
    }

    public void setSubcategory4(String subcategory4) {
        this.subcategory4 = subcategory4;
    }

}
