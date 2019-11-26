package iwp.crawler.iwpcrawler.engine;

import iwp.crawler.iwpcrawler.scrappers.AmazonItemsScrapper;
import iwp.crawler.iwpcrawler.scrappers.ProcessCache;
import iwp.crawler.iwpcrawler.scrappers.WalmartItemsScrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Future;

@EnableAsync
public class ScrapingEngine {
    private static final Logger logger = LogManager.getLogger(ScrapingEngine.class);

    @Autowired private AmazonItemsScrapper amazonItemsScrapper;
    @Autowired private WalmartItemsScrapper walmartItemsScrapper;

    @Async("scrapExecutor")
    public Future<HttpStatus> startScraping(String department) {
        ProcessCache.getInstance().setCurrentDp(department);
        amazonItemsScrapper.getDepartementItemsDetails(department);
        walmartItemsScrapper.getDepartementItemsDetails(department);
        ProcessCache.getInstance().setCurrentDp("FINISH");
        return new AsyncResult<>(HttpStatus.OK);
    }



}
