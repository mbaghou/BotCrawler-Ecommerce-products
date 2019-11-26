package iwp.crawler.iwpcrawler.repository;

import iwp.crawler.iwpcrawler.model.AmazonItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmazonItemRepository extends MongoRepository<AmazonItem, String> {

}
