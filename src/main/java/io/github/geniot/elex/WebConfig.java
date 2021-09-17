package io.github.geniot.elex;

import io.github.geniot.elex.tasks.AsynchronousService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

@Configuration
@EnableWebMvc
@Getter
public class WebConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(WebConfig.class);

    public static final String TASK_THREAD_NAME_PREFIX = "elex_task_executor_thread";

    @Value("${path.data}")
    private String pathToData;
    private String pathToDataAbsolute;

    @Autowired
    AsynchronousService asynchronousService;
    @Autowired
    MyFileSystemWatcher myFileSystemWatcher;
    @Autowired
    DictionariesPool dictionariesPool;


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
        this.pathToDataAbsolute = new File(pathToData).getAbsolutePath() + File.separator;
        asynchronousService.updatePool();
        myFileSystemWatcher.start();
    }

    @PreDestroy
    public void onDestroy() {
        myFileSystemWatcher.stop();
        dictionariesPool.close();
    }
}
