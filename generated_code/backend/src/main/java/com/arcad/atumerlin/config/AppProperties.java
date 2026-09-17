package com.arcad.atumerlin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Strongly-typed, externalised application configuration.
 *
 * <p>Bound from the {@code app.*} section of {@code application.yml}. Keeping these
 * values here (instead of hard-coding them) is what makes the REST API
 * "configurable": base path, CORS, pagination limits and seeding can all be
 * changed via configuration or environment variables.</p>
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Api api,
        Cors cors,
        Pagination pagination,
        Seed seed
) {

    public record Api(String basePath) {
        public Api {
            if (basePath == null || basePath.isBlank()) {
                basePath = "/api/v1";
            }
        }
    }

    public record Cors(
            List<String> allowedOrigins,
            List<String> allowedMethods,
            List<String> allowedHeaders,
            boolean allowCredentials
    ) {
    }

    public record Pagination(int defaultPageSize, int maxPageSize) {
        public Pagination {
            if (defaultPageSize <= 0) {
                defaultPageSize = 20;
            }
            if (maxPageSize <= 0) {
                maxPageSize = 200;
            }
        }
    }

    public record Seed(boolean enabled) {
    }
}
