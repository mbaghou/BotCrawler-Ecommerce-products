package iwp.crawler.iwpcrawler.engine;

import com.mongodb.client.MongoCollection;
import iwp.crawler.iwpcrawler.model.AmazonDepartement;
import iwp.crawler.iwpcrawler.model.ScrappedDepartement;
import iwp.crawler.iwpcrawler.repository.AmazonDepartementRepository;
import iwp.crawler.iwpcrawler.repository.ScrappedRepositoy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Service
public class CleanRepo {
    private static final Logger logger = LogManager.getLogger(CleanRepo.class);

    @Autowired private ScrappedRepositoy scrappedRepositoy;
    @Autowired
    @Qualifier("amazonDpCollection")
    private MongoCollection amazonDepCollection;
    @Autowired
    @Qualifier("scrappedDepartementCollection")
    private MongoCollection scrappedDepCollection;
    @Autowired private AmazonDepartementRepository amazonDepartementRepository;

    public void cleanAmazonDepartments(){
        logger.info("start cleaning ...");
        List<ScrappedDepartement> scrappedDepartements = scrappedRepositoy.findAll();


        scrappedDepartements.forEach(dep ->
                amazonDepCollection.deleteMany(new Document("departement",dep.getScrapped()))
        );
        logger.info("end cleaning");
    }

    public void exportEmptyDep(){
        List<String> scrappedDepartements = new ArrayList<>();
        Document projection = new Document();
        projection.append("_id", 1.0);
        Document query = new Document();
        query.append("count", 0L);

        Block<Document> processBlock = document -> {
            System.out.println(document);
            scrappedDepartements.add(document.getString("_id"));
        };

        scrappedDepCollection.find(query).projection(projection).forEach(processBlock);
        scrappedDepartements.removeAll(scrappedDepartements);
        scrappedDepartements.forEach(dep -> amazonDepartementRepository.insert(new AmazonDepartement(dep)));
    }
}