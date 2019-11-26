package iwp.crawler.iwpcrawler.scrappers;

import iwp.crawler.iwpcrawler.model.AmazonItem;
import iwp.crawler.iwpcrawler.utils.AmazonSelectors;
import iwp.crawler.iwpcrawler.utils.UserAgents;
import iwp.crawler.iwpcrawler.utils.WalmartSelector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@Service
public class WalmartItemsScrapper {
    private static final Logger logger = LogManager.getLogger(WalmartItemsScrapper.class);

    public void getDepartementItemsDetails(String department) {
        logger.info("------------ START SCRAPING WALMART " + department.toUpperCase() + "----------");
        int count = 0;
        final int MAX_ITEMS = 100;
        int retry = 1;
        final int MAX_RETRY = 5;
        int nextpage = 1;
        String URL = WalmartSelector.WALMAR_QUERY.replace("@PAGE",String.valueOf(nextpage)) + department;

        while(count <= MAX_ITEMS && retry <= MAX_RETRY){
            try {
                logger.info("Scraping page = " + URL);
                Document document = UserAgents.initDocument(URL);
                Elements items = document.select("div[class=\"search-result-listview-item Grid\"]");
                if(!items.isEmpty()){
                    count += scrapProducts(items);
                    logger.info("Actual count = "+count + " --> trying to go next page ...");
                    nextpage++;
                    URL = WalmartSelector.WALMAR_QUERY.replace("@PAGE",String.valueOf(nextpage)) + department;
                    try {
                        logger.info("sleep 5s and move to next page");
                        Thread.sleep(5000);
                    }catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    logger.error("Nothing found on page " + URL);
                    retry++;
                }
            }catch (SocketTimeoutException e){
                logger.error("URL " + URL);
                logger.error(e.getMessage());
                retry++;
            }
            catch (IOException e) {
                logger.error("error on : " +URL);
                logger.error(e.getMessage());
                retry++;
            }
        }

    }


    private int scrapProducts(Elements items) {
        AtomicInteger runCount = new AtomicInteger(0);

        logger.info("--> Scraping " + items.size() + " items in this page");
        items.forEach(item -> {
            String title = item.select("a[class=\"product-title-link line-clamp line-clamp-2\"]").attr("title");
            String itemUrl = item.select("a[class=\"product-title-link line-clamp line-clamp-2\"]").attr("href");

            if(title.isEmpty()){
                title = item.select("span[class=\"product-title-link*\"]").attr("title");
            }
            if (!title.matches(WalmartSelector.EXCLUDE_WALMART_PATTERN)) {
                try {
                    if(scrapProductDetails(itemUrl)){
                        runCount.incrementAndGet();
                        logger.warn("Sleep 10s to next item id...");
                        sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }else{
                logger.info("exclude item because " + title);
            }
        });
        return runCount.get();
    }

    private boolean scrapProductDetails(String itemUrl) {
        int retry = 2;
        boolean isScrapped = false;
        do{
            logger.info("scraping item ... url = " + itemUrl);
            Document productDocument = null;
            try {
                productDocument = UserAgents.initDocument(WalmartSelector.WALMART_URL+itemUrl);
                Elements containerProduct = productDocument.select("div[class=\"hf-Bot hf-PositionedRelative\"]");
                if(!containerProduct.isEmpty()){
                    retry = 0;
                    /********** CATEGORIES*************/
                    List<String> categories = new ArrayList<>();
                    Elements divElements = productDocument.select("ol[class=\"breadcrumb-list\"]");
                    Elements categoriesElms = divElements.select("a");
                    String title = containerProduct.select("div[class=\"productTitle\"]").select("div").text();


                    if(categoriesElms.isEmpty()){
                        logger.warn("Document Categories empty for Item url = " + itemUrl);
                    }else if(title.isEmpty()){
                        logger.warn("title is empty for url = " + itemUrl);
                    }
                    else{
                        categoriesElms.forEach(cat -> {
                            categories.add(cat.text());
                        });

                        /************** SCORE ****************/
                        double score = 0.0;
                        String strScore = containerProduct.select("span[class=\"seo-avg-rating\"]").text();
                        if(!strScore.isEmpty()){
                            score = Double.parseDouble(strScore);
                        }
                        /************** PRICE ****************/
                        String strPrice = containerProduct.select("span[class=\"price-group\"]").attr("aria-label").replace("$","");
                        String itemId = itemUrl.split("/")[3];
                        ProcessCache.getInstance().add(new AmazonItem(itemId,"walmart.com",title, score,strPrice, categories));
                        isScrapped = true;
                    }
                }else{
                    logger.warn("Pattern selector don't match with item url = " + itemUrl);
                    logger.warn("Maybe css default , Going to retry ...");
                    retry--;
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                retry--;
            }

        }while (retry != 0);
        return isScrapped;
    }
}
