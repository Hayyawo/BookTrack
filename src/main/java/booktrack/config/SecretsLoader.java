package booktrack.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SecretsLoader implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> secrets = new HashMap<>();

        loadSecret("SPRING_DATASOURCE_PASSWORD_FILE", "SPRING_DATASOURCE_PASSWORD", secrets, environment);

        loadSecret("JWT_SECRET_KEY_FILE", "JWT_SECRET_KEY", secrets, environment);

        if (!secrets.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("secrets", secrets));
            log.info("Loaded {} secrets from files", secrets.size());
        }
    }

    private void loadSecret(String fileEnvVar, String targetEnvVar, Map<String, Object> secrets, ConfigurableEnvironment environment) {
        String filePath = environment.getProperty(fileEnvVar);
        if (filePath != null) {
            try {
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    String content = Files.readString(path).trim();
                    secrets.put(targetEnvVar, content);
                    log.info("Loaded secret from: {}", filePath);
                }
            } catch (IOException e) {
                log.warn("Failed to load secret from: {}", filePath, e);
            }
        }
    }
}
