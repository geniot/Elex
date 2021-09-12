package io.github.geniot.elex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(WebConfig.class);

    public static final String TASK_THREAD_NAME_PREFIX = "elex_task_executor_thread";

    @Autowired
    DictionariesPool dictionariesPool;

    @Autowired
    MyFileSystemWatcher myFileSystemWatcher;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "index.html");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**/*.*").addResourceLocations("classpath:/static/");
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds(1);
        executor.setThreadNamePrefix(TASK_THREAD_NAME_PREFIX);
        executor.initialize();
        return executor;
    }

    @PostConstruct
    public void onConstruct() {
        dictionariesPool.update();
        myFileSystemWatcher.start();
    }

    @PreDestroy
    public void onDestroy() {
        myFileSystemWatcher.stop();
        dictionariesPool.stop();
    }
}
