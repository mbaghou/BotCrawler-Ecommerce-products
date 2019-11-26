package iwp.crawler.iwpcrawler.repository;

import iwp.crawler.iwpcrawler.model.ScrappedDepartement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScrappedRepositoy extends MongoRepository<ScrappedDepartement, String> {

}
