package com.jobhunter.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class EnvConfig {

    @PostConstruct
    public void loadDotEnv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // For each key we care about, if not already in System.getenv, push to System properties
        setIfMissing("ADZUNA_APP_ID", dotenv);
        setIfMissing("ADZUNA_APP_KEY", dotenv);
        setIfMissing("JOOBLE_API_KEY", dotenv);
    }

    private void setIfMissing(String key, Dotenv dotenv) {
        if (System.getenv(key) == null && dotenv.get(key) != null) {
            // Spring can resolve ${VAR_NAME} from system properties as well
            System.setProperty(key, dotenv.get(key));
        }
    }
}
