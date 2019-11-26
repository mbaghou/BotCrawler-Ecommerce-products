package iwp.crawler.iwpcrawler.engine;

import com.google.common.base.Strings;
import com.mongodb.Block;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import iwp.crawler.iwpcrawler.bean.MasterResponse;
import iwp.crawler.iwpcrawler.bean.SlaveDepartement;
import iwp.crawler.iwpcrawler.model.AmazonItem;
import iwp.crawler.iwpcrawler.model.ScrappedDepartement;
import iwp.crawler.iwpcrawler.repository.AmazonItemRepository;
import iwp.crawler.iwpcrawler.repository.ScrappedRepositoy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@EnableAsync
public class MonitorEngine {
    private static final Logger logger = LogManager.getLogger(MonitorEngine.class);
    private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ScrappedRepositoy scrappedRepositoy;
    @Autowired
    private AmazonItemRepository amazonItemRepository;
    @Autowired
    @Qualifier("amazonDpCollection")
    private MongoCollection amazonDepCollection;
    @Autowired CleanRepo cleanRepo;


    public void masterSlaves(List<String> slaves) throws InterruptedException {
        scrap(slaves);
    }

    public void scrap(List<String> slaves) throws InterruptedException {
        cleanRepo.exportEmptyDep();
        List<String> activeSlaves = wakeUpSlaves(slaves);
        List<SlaveDepartement> slaveDepartements = startSlavesScrap(activeSlaves);
        logger.info("sleep 60s time before scrap ...");
        Thread.sleep(65000);
        subscribeToSlaves(slaveDepartements);
    }


    private List<String> wakeUpSlaves(List<String> slaves) {
        List<String> activeSlaves;
        activeSlaves = slaves.parallelStream().filter(slave -> !Strings.isNullOrEmpty(slave)).filter(slave -> {
            logger.info("wakeup " + slave);
            String URL = "https://"+slave+".herokuapp.com/iwp/wakeup";
            try{
                ResponseEntity responseEntity = restTemplate.getForEntity(URL,Object.class);
                if(responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)){
                    return true;
                }
                return false;
            }catch (RestClientException e){
                logger.error(" ----> escape slave " + slave);
                logger.error(e.getLocalizedMessage());
                return false;
            }
        }).collect(Collectors.toList());
        logger.info("Actives slaves = " + activeSlaves.toString());
        return activeSlaves;
    }

    private List<SlaveDepartement> startSlavesScrap(List<String> activeSlaves) {
        List<SlaveDepartement> slaveDepartements = new ArrayList<>();
        activeSlaves.forEach(slave -> {
            String dep = findDepartment();
            if(dep == null){
                logger.error("findrandomdep return null - escape slave " + slave);
            }else{
                String URL = "https://"+slave+".herokuapp.com/iwp/scrap/"+dep;
                try {
                    ResponseEntity responseEntity = restTemplate.getForEntity(URL,Object.class);
                    slaveDepartements.add(new SlaveDepartement(dep, slave));
                    amazonDepCollection.deleteMany(new Document("departement",dep));
                }catch (RestClientException e){
                    logger.error(slave);
                    logger.error(e.getMessage());
                }catch (MongoException e){
                    logger.error(e.getMessage());
                }
            }

        });
        logger.info("start scraping below slaves : ");
        slaveDepartements.forEach(slave -> {
            hashMap.put(slave.getSlave(), 0);
            logger.info(slave.getSlave() + " --> " + slave.getDepartement());
        });
        return slaveDepartements;
    }

    private void subscribeToSlaves(List<SlaveDepartement> slaveDepartements) {
        int l = slaveDepartements.size();
        int max = 50*l;

        if(slaveDepartements == null || slaveDepartements.isEmpty()){
            logger.error("slaves list null");
            System.exit(0);
        }

        while(hashMap.values().stream().mapToInt(i->i).sum() <= max){
            logger.info("Condition = " + hashMap.values().stream().mapToInt(i->i).sum());
            slaveDepartements.stream().filter(slave -> hashMap.get(slave.getSlave()) <= 50).forEach(slave -> {
                try {
                    String URL = "https://"+slave.getSlave()+".herokuapp.com";
                    ResponseEntity<MasterResponse> responseEntity = this.restTemplate.getForEntity(URL+"/iwp/json", MasterResponse.class);
                    if(responseEntity.getBody() != null){
                        List<AmazonItem> items = responseEntity.getBody().getAmazonItems();
                        String currentdp = responseEntity.getBody().getDepartement();

                        if(currentdp.equals("FINISH") || currentdp ==null){
                            logger.info("------- " + slave.getDepartement() + " FINISH ------------");
                            hashMap.put(slave.getSlave(), 50);
                        }

                        if(items != null && items.size() > 0){
                            try{
                                amazonItemRepository.saveAll(items);
                                logger.info("scrapped N = " + items.size());
                                Optional<ScrappedDepartement> scrappedDepartement = scrappedRepositoy.findById(slave.getDepartement());
                                ScrappedDepartement savedScrappedDep = null;
                                if(scrappedDepartement.isPresent()){
                                    savedScrappedDep = scrappedDepartement.get();
                                    savedScrappedDep.setCount(savedScrappedDep.getCount()+items.size());
                                }else{
                                    savedScrappedDep = new ScrappedDepartement(currentdp,items.size());
                                }
                                scrappedRepositoy.save(savedScrappedDep);
                            }catch (DuplicateKeyException e){
                                logger.error("duplicate elements catched");
                            }catch (MongoException e){
                                logger.error(e.getMessage());
                            }
                        }else{
                            logger.warn("empty for " + slave.getSlave() + " --> " + slave.getDepartement());
                            Integer k = hashMap.get(slave.getSlave());
                            if(k==null) k = 0;
                            k++;
                            hashMap.put(slave.getSlave(),k);
                        }
                    }else{
                        logger.warn("empty for " + slave);
                        Integer k = hashMap.get(slave.getSlave());
                        if(k==null) k = 0;
                        k++;
                        hashMap.put(slave.getSlave(),k);
                    }
                }catch (ResourceAccessException e){
                    logger.error(e.getLocalizedMessage());
                    logger.error(slave);
                    Integer k = hashMap.get(slave.getSlave());
                    if(k==null) k = 0;
                    k++;
                    hashMap.put(slave.getSlave(),k);
                }catch (HttpServerErrorException e){
                    logger.error(slave + " - " + e.getMessage());
                    Integer k = hashMap.get(slave.getSlave());
                    if(k==null) k = 0;
                    k++;
                    hashMap.put(slave.getSlave(),k);
                }
            });
            try{
                logger.info("sleep 70s ...");
                Thread.sleep(75000);
            } catch (InterruptedException e){
                logger.error(e.getMessage());
            }
        }
    }


    public String findDepartment(){
        List<String> deps = new ArrayList<>();
        Block<Document> processBlock = document -> deps.add(document.getString("departement"));

        List<? extends Bson> pipeline = Arrays.asList(
                new Document()
                        .append("$sample", new Document()
                                .append("size", 1.0)
                        )
        );

        amazonDepCollection.aggregate(pipeline)
                .allowDiskUse(false)
                .forEach(processBlock);
        return deps.isEmpty() ? null : deps.get(0);
    }
}
