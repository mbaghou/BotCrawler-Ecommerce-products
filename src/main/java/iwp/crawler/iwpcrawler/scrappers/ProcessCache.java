package iwp.crawler.iwpcrawler.scrappers;

import iwp.crawler.iwpcrawler.engine.ScrapingEngine;
import iwp.crawler.iwpcrawler.model.AmazonItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessCache {
    private static final Logger logger = LogManager.getLogger(ProcessCache.class);

    private String currentDp;
    private List<AmazonItem> amazonItems = Collections.synchronizedList(new ArrayList<>());

    private static ProcessCache INSTANCE = new ProcessCache();

    private ProcessCache(){

    }

    public void add(AmazonItem o){
        amazonItems.add(o);
    }

    public List<AmazonItem> getAmazonItems() {
        List<AmazonItem> res = new ArrayList<>();
        res.addAll(amazonItems);
        amazonItems.clear();
        return res;
    }

    public static synchronized ProcessCache getInstance(){
        if(INSTANCE == null) INSTANCE = new ProcessCache();
        return INSTANCE;
    }

    public void clearItems(){
        this.amazonItems.clear();
    }

    public String getCurrentDp() {
        return currentDp;
    }

    public void setCurrentDp(String currentDp) {
        this.currentDp = currentDp;
    }
}
