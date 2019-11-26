package iwp.crawler.iwpcrawler;

import iwp.crawler.iwpcrawler.engine.CleanRepo;
import iwp.crawler.iwpcrawler.engine.MonitorEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class IwpCrawlerApplication implements CommandLineRunner {
	@Autowired
	Environment environment;
	@Autowired private MonitorEngine monitorEngine;
	@Autowired
	CleanRepo cleanRepo;
	static ConfigurableApplicationContext ctx;
	private static final Logger logger = LogManager.getLogger(IwpCrawlerApplication.class);

	public static void main(String[] args) {
		ctx = SpringApplication.run(IwpCrawlerApplication.class, args);
	}


	@Override
	public void run(String... args) {
		cleanRepo.exportEmptyDep();
		if(Arrays.asList(environment.getActiveProfiles()).contains("server")){
			System.out.println("Active slaves = " + args.length);
			List<String> slaves = Arrays.asList(args);
			if(!slaves.isEmpty()){
				try {
					monitorEngine.masterSlaves(slaves);
					exitApplication();
				} catch (InterruptedException e) {
					logger.warn(e.getMessage());
					logger.info("Exit ");
					System.exit(0);
				}catch (IllegalArgumentException e){
					logger.warn(e.getMessage());
					logger.info("Exit ");
					System.exit(0);
				}
			}
		}
	}


	public static void exitApplication() {
		int exitCode = SpringApplication.exit(ctx, new ExitCodeGenerator() {
			@Override
			public int getExitCode() {
				// no errors
				return 0;
			}
		});
		System.exit(exitCode);
	}
}
