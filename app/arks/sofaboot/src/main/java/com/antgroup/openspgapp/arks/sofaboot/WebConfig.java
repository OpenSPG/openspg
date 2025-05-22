package com.antgroup.openspgapp.arks.sofaboot;

import static com.antgroup.openspgapp.api.http.server.filter.AclFilter.API_PREFIX;

import com.antgroup.openspgapp.api.http.server.filter.AclFilter;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
/* loaded from: WebConfig.class */
public class WebConfig implements WebMvcConfigurer {

  @Autowired private AclFilter aclFilter;

  @Bean
  public FilterRegistrationBean<AclFilter> customFilterRegistrationBean() {
    FilterRegistrationBean<AclFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(this.aclFilter);
    registrationBean.addUrlPatterns(new String[] {"/*"});
    return registrationBean;
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix(
        API_PREFIX,
        new Predicate<Class<?>>() {
          @Override
          public boolean test(Class<?> c) {
            return c.isAnnotationPresent(Controller.class)
                || c.isAnnotationPresent(RestController.class);
          }
        });
  }

  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedHeaders(new String[] {"*"})
        .allowedOriginPatterns(new String[] {"*"})
        .allowedMethods(new String[] {"*"})
        .allowCredentials(true)
        .maxAge(3600L);
  }

  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(new String[] {"/**"})
        .addResourceLocations(new String[] {"classpath:/static/"})
        .resourceChain(true);
  }

  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("forward:/index.html");
  }
}
