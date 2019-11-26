package iwp.crawler.iwpcrawler.scrappers;

import iwp.crawler.iwpcrawler.model.AmazonItem;
import iwp.crawler.iwpcrawler.utils.AmazonSelectors;
import iwp.crawler.iwpcrawler.utils.UserAgents;
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
public class AmazonItemsScrapper {

    private static final Logger logger = LogManager.getLogger(AmazonItemsScrapper.class);


    public void getDepartementItemsDetails(String department) {
        logger.info("------------ START SCRAPING AMAZON " + department.toUpperCase() + "----------");
        int count = 0;
        final int MAX_ITEMS = 100;
        int retry = 1;
        final int MAX_RETRY = 5;
        String URL = AmazonSelectors.AMAZON_QUERY_SEARCH + department;

        while(count <= MAX_ITEMS && retry <= MAX_RETRY){
            try {
                Document document = UserAgents.initDocument(URL);
                Elements items = document.select("div[data-asin][data-index]");
                if(!items.isEmpty()){
                    count += scrapProducts(items);
                    logger.info("Actual count = "+count + " --> trying to go next page ...");
                    Elements pagination = document.select("ul[class=\"a-pagination\"]");
                    if(!pagination.isEmpty()){
                        String nextpageUrl =  pagination.select("li[class=\"a-last\"]").select("a[href]").attr("href");
                        if(!nextpageUrl.isEmpty()){
                            URL = AmazonSelectors.AMAZON_URL+nextpageUrl;
                            logger.info("Sleep 5s before next page ...");
                            sleep(5000);
                        }else{
                            logger.info("Next page not found on " + URL + " -->Going to stop scraping " + department);
                            retry = MAX_RETRY + 1;
                        }
                    }else{
                        logger.info("Next page not found on " + URL + " -->Going to stop scraping " + department);
                        retry = MAX_RETRY + 1;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
                
            }
        }
    }

    private int scrapProducts(Elements items) {
        AtomicInteger runCount = new AtomicInteger(0);

        logger.info("--> Scraping " + items.size() + " items in this page");
        items.forEach(item -> {
            String itemId = item.attr("data-asin");
            String title = item.select("span[class=\"a-size-medium a-color-base a-text-normal\"]").text();
            if(title.isEmpty()){
                title = item.select("span[class=\"a-size-base-plus a-color-base a-text-normal\"]").text();
            }
            String type = item.select("span[class=\"a-size-base a-link-normal a-text-bold\"]").text().trim();
            if (!itemId.isEmpty() && !title.matches(AmazonSelectors.EXCLUDE_AMAZON_PATTERN) && !type.equalsIgnoreCase("App")) {
                try {
                    if(scrapProductDetails(itemId)){
                        runCount.incrementAndGet();
                        logger.warn("Sleep 10s to next item id...");
                        sleep(10000);
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }else{
                logger.info("exclude item id = " +itemId +" : " + type + "? because " + title);
            }
        });
        return runCount.get();
    }

    private boolean scrapProductDetails(String itemId) {
        int retry = 2;
        boolean isScrapped = false;
        do{
            logger.info("scraping item ... id = " + itemId);
            Document productDocument = null;
            try {
                productDocument = UserAgents.initDocument("https://www.amazon.com/dp/"+itemId);
                Elements containerProduct = productDocument.select("div[id=\"centerCol\"]");
                if(!containerProduct.isEmpty()){
                    retry = 0;
                    /********** CATEGORIES*************/
                    List<String> categories = new ArrayList<>();
                    Elements divElements = productDocument.select("ul[class=\"a-unordered-list a-horizontal a-size-small\"]");
                    Elements categoriesElms = divElements.select("a");
                    String title = containerProduct.select("span[id=\"productTitle\"]").text();
                    if(title.isEmpty()){
                         title = containerProduct.select("span[id*=\"Title\"]").text().trim();
                    }

                    if(categoriesElms.isEmpty()){
                        logger.warn("Document Categories empty for Item id = " + itemId);
                    }else if(title.isEmpty()){
                        logger.warn("title is empty for id = " + itemId);
                    }
                    else{
                        categoriesElms.forEach(cat -> {
                            categories.add(cat.text());
                        });

                        /************** SCORE ****************/
                        double score = 0.0;
                        String strScore = containerProduct.select("i[class=\"a-icon a-icon-star a-star-4-5\"]").select("span[class=\"a-icon-alt\"]").text();
                        if(!strScore.isEmpty()){
                            score = Double.parseDouble(strScore.split("\\s+")[0]);
                        }else{
                            strScore = containerProduct.select("span[class=\"reviewCountTextLinkedHistogram noUnderline\"]").attr("title");
                            if(!strScore.isEmpty()){
                                score = Double.parseDouble(strScore.split("\\s+")[0]);
                            }else{
                                //logger.warn("score null for " + itemId);
                            }
                        }
                        /************** PRICE ****************/
                        String strPrice = containerProduct.select("span[id=\"priceblock_ourprice\"]").text().replace("$","");
                        if(strPrice.isEmpty()){
                            strPrice = containerProduct.select("span[id=\"priceblock_dealprice\"]").text().replace("$","");
                            if(strPrice.isEmpty()) {
                                strPrice = productDocument.select("div[id=\"rightCol\"]").select("span[class=\"a-size-base a-color-price offer-price a-text-normal\"]").text().replace("$","");
                                if(strPrice.isEmpty()) {
                                    //logger.warn("Price null for : " + itemId);
                                }
                            }
                        }
                        ProcessCache.getInstance().add(new AmazonItem(itemId,"amazon.com",title, score,strPrice, categories));
                        isScrapped = true;
                    }
                }else{
                    logger.warn("Pattern selector don't match with item id = " + itemId);
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
