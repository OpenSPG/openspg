package com.antgroup.openspgapp.arks.sofaboot;

import com.antgroup.openspgapp.api.http.server.filter.AclFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
