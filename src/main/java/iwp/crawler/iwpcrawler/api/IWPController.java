package iwp.crawler.iwpcrawler.api;


import iwp.crawler.iwpcrawler.bean.MasterResponse;
import iwp.crawler.iwpcrawler.engine.ScrapingEngine;
import iwp.crawler.iwpcrawler.scrappers.ProcessCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iwp")
public class IWPController {
    private static final Logger logger = LogManager.getLogger(IWPController.class);

    @Autowired
    private ScrapingEngine scrapingEngine;

    @GetMapping("/scrap/{department}")
    public ResponseEntity startScraping(@PathVariable String department){
        scrapingEngine.startScraping(department);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }


    @GetMapping("/json")
    public ResponseEntity<MasterResponse>  getJson(){
        logger.info("master ping for json ...");
        return new ResponseEntity<>(new MasterResponse(ProcessCache.getInstance().getAmazonItems(), ProcessCache.getInstance().getCurrentDp()), HttpStatus.OK);
    }

    @GetMapping("/wakeup")
    public ResponseEntity<HttpStatus> wakeup(){
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
