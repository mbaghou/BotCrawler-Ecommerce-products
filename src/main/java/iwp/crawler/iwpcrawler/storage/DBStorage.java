package iwp.crawler.iwpcrawler.storage;

import iwp.crawler.iwpcrawler.model.AmazonItem;
import iwp.crawler.iwpcrawler.repository.AmazonItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

public class DBStorage implements Consumer<AmazonItem> {


    @Autowired
    private AmazonItemRepository amazonItemRepository;

    @Override
    public void accept(AmazonItem o) {
        amazonItemRepository.save(o);
    }

    @Override
    public Consumer andThen(Consumer after) {
        return null;
    }
}
