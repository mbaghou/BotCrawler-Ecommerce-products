package iwp.crawler.iwpcrawler.configuration;

import com.mongodb.client.MongoCollection;
import iwp.crawler.iwpcrawler.engine.MonitorEngine;
import iwp.crawler.iwpcrawler.engine.ScrapingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@Configuration
public class AppConfig {

    @Autowired private MongoTemplate mongoTemplate;

    @Bean
    ScrapingEngine scrapingEngine(){
        return new ScrapingEngine();
    }

    @Bean
    MonitorEngine monitorEngine(){
        return new MonitorEngine();
    }

    @Bean
    RestTemplate restTemplate(){
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(120000);
        httpRequestFactory.setConnectTimeout(120000);
        httpRequestFactory.setReadTimeout(120000);
        return new RestTemplate(httpRequestFactory);
    }

    @Bean(name = "scrapExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncThread-");
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setQueueCapacity(1000);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;

    }

    @Bean("amazonDpCollection")
    public MongoCollection amazonDepCollection(){
        return mongoTemplate.getCollection("amazondepartement");
    }


    @Bean("scrappedDepartementCollection")
    public MongoCollection amazonScrCollection(){
        return mongoTemplate.getCollection("scrappedDepartement");
    }

}
