package iwp.crawler.iwpcrawler.api;

import iwp.crawler.iwpcrawler.bean.SlaveUrl;
import iwp.crawler.iwpcrawler.engine.MonitorEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class MasterSlavesController {

    @Autowired
    MonitorEngine monitorEngine;

    @PostMapping("/ping")
    public ResponseEntity<HttpStatus> masterSlaves(@RequestBody SlaveUrl slaveUrl) throws InterruptedException {
        monitorEngine.masterSlaves(slaveUrl.getUrls());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
