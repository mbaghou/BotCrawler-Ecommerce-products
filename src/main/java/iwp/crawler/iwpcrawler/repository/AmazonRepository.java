package iwp.crawler.iwpcrawler.repository;

import iwp.crawler.iwpcrawler.model.AmazonCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmazonRepository extends MongoRepository<AmazonCategory, String> {


}
