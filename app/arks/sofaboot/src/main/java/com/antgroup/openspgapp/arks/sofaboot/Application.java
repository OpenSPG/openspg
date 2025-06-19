package com.antgroup.openspgapp.arks.sofaboot;

import com.antgroup.openspg.common.util.NetworkAddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@ImportResource({"classpath*:spring/*.xml"})
@EnableScheduling
@Configuration
@SpringBootApplication
@ComponentScan(basePackages = {"com.antgroup.openspgapp"})
/* loaded from: Application.class */
public class Application {
  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    LOGGER.info("OpenSPG Application Started!!!");
    LOGGER.info("  ___                   ____  ____   ____  ");
    LOGGER.info(" / _ \\ _ __   ___ _ __ / ___||  _ \\ / ___| ");
    LOGGER.info("| | | | '_ \\ / _ \\ '_ \\\\___ \\| |_) | |  _  ");
    LOGGER.info("| |_| | |_) |  __/ | | |___) |  __/| |_| | ");
    LOGGER.info(" \\___/| .__/ \\___|_| |_|____/|_|    \\____| ");
    LOGGER.info("      |_|                                  ");
    String serverPort = context.getEnvironment().getProperty("server.port");
    LOGGER.info("* Running on http://127.0.0.1:" + serverPort);
    LOGGER.info("* Running on http://" + NetworkAddressUtils.LOCAL_IP + ":" + serverPort);
  }
}
