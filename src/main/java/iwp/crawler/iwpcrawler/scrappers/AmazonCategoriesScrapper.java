package iwp.crawler.iwpcrawler.scrappers;

import iwp.crawler.iwpcrawler.model.AmazonCategory;
import iwp.crawler.iwpcrawler.model.AmazonDepartement;
import iwp.crawler.iwpcrawler.repository.AmazonDepartementRepository;
import iwp.crawler.iwpcrawler.repository.AmazonRepository;
import iwp.crawler.iwpcrawler.utils.AmazonSelectors;
import iwp.crawler.iwpcrawler.utils.UserAgents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AmazonCategoriesScrapper {

    private static final Logger logger = LogManager.getLogger(AmazonCategoriesScrapper.class);

    @Autowired
    private AmazonRepository amazonRepository;
    @Autowired
    private AmazonDepartementRepository amazonDepartementRepository;


    public void scrapAndSaveAmazonCategories() {
        try {
            logger.info("-------- Start Scraping Amazon Categories -------");
            Document document = UserAgents.initDocument(AmazonSelectors.AMAZON_CATEGORY_URL);
            Elements categoriesSets = document.select(AmazonSelectors.CATEGORIES_FSD_COLUMN_SELECTOR);
            categoriesSets.forEach(categoriesSet -> {
                Elements categories = categoriesSet.select(AmazonSelectors.CATEGORY_FSD_BOX_SELECTOR);
                categories.forEach(category -> {
                    String root_category = category.select(AmazonSelectors.CATEGORY_ROOT_TITLE_SELECTOR).text();
                    if (!AmazonSelectors.AMAZON_EXCLUDED_CATEGORIES.contains(root_category)) {
                        List<String> subCats = new ArrayList<>();
                        Elements subCategories = category.select(AmazonSelectors.SUB_CATEGORY_SELECTOR);
                        subCategories.forEach(subCategory -> {
                            String subCategoryName = subCategory.text();
                            if (!subCategoryName.matches(AmazonSelectors.EXCLUDE_AMAZON_PATTERN)) {
                                subCats.add(subCategoryName);
                            }
                        });
                        amazonRepository.insert(new AmazonCategory(root_category, subCats));
                    }
                });
            });
            amazonRepository.insert(new AmazonCategory("Clothing, Shoes, Jewelry & Watches", Arrays.asList(
                    "Clothing Women", "Clothing Men", "Clothing Girls", "Clothing Boys", "Clothing Baby",
                    "Shoes Women", "Shoes Men", "Shoes Girls", "Shoes Boys", "Shoes Baby",
                    "Jewelry Women", "Jewelry Men", "Jewelry Girls", "Jewelry Boys", "Jewelry Baby",
                    "Watches Women", "Watches Men", "Watches Girls", "Watches Boys", "Watches Baby"
            )));

            logger.info("-------- END Scraping Amazon Categories -------");

        } catch (IOException e) {
            logger.error("Error while scraping categories name " + AmazonSelectors.AMAZON_CATEGORY_URL);
            logger.error(e.getMessage());
        }
    }


    public void scrapAmazonDepartement() {
        logger.info("-------- Start Scraping Amazon Departements -------");
        List<AmazonCategory> amazonCategories = amazonRepository.findAll();
        amazonCategories.forEach(amazonCategory -> {
            amazonCategory.getSubcategories().forEach(subcategory -> {
                final String category_query_url = AmazonSelectors.AMAZON_QUERY_SEARCH + subcategory;
                try {
                    Document document = UserAgents.initDocument(category_query_url);
                    Elements departements = document.select(AmazonSelectors.DEPARTEMENT_SELECTOR);
                    departements.forEach(departement -> {
                        String departementCategory = departement.select(AmazonSelectors.DEPARTEMENT_CATEGORY_SELECTOR).text();
                        String url = departement.select(AmazonSelectors.DEPARTEMENT_HREF_SELECTOR).attr("href");
                        amazonDepartementRepository.save(new AmazonDepartement(subcategory,departementCategory,url));
                    });
                } catch (IOException e) {
                    logger.error("Error while scraping subcategory " + category_query_url);
                    logger.error(e.getMessage());
                }
            });
        });
        logger.info("-------- END Scraping Amazon Departements -------");
    }
}
