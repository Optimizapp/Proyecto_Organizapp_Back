package co.javeriana.dw.organizapp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";
    private static final Path DOTENV_PATH = Path.of(".env");

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!Files.exists(DOTENV_PATH)) {
            return;
        }

        Map<String, Object> dotenvProperties = loadDotenvProperties(DOTENV_PATH);
        if (dotenvProperties.isEmpty()) {
            return;
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource dotenvPropertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, dotenvProperties);

        if (propertySources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
            propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, dotenvPropertySource);
            return;
        }

        propertySources.addLast(dotenvPropertySource);
    }

    private Map<String, Object> loadDotenvProperties(Path dotenvPath) {
        Map<String, Object> properties = new LinkedHashMap<>();

        try {
            List<String> lines = Files.readAllLines(dotenvPath);
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmedLine.indexOf('=');
                if (separatorIndex <= 0) {
                    continue;
                }

                String key = trimmedLine.substring(0, separatorIndex).trim();
                String value = trimmedLine.substring(separatorIndex + 1).trim();
                properties.put(key, stripQuotes(value));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar el archivo .env", exception);
        }

        return properties;
    }

    private String stripQuotes(String value) {
        if (value.length() >= 2) {
            boolean wrappedInDoubleQuotes = value.startsWith("\"") && value.endsWith("\"");
            boolean wrappedInSingleQuotes = value.startsWith("'") && value.endsWith("'");
            if (wrappedInDoubleQuotes || wrappedInSingleQuotes) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
