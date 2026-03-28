package com.example.api.graphql.generated;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import jakarta.validation.constraints.NotNull;

@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    date = "2026-03-28T13:06:07.025361-04:00[America/New_York]"
)
public class ApiErrorResponse {

    @NotNull
    private String code;
    @NotNull
    private String message;
    private ApiErrorResponseViolations violations;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(final String code, final String message,
                            final ApiErrorResponseViolations violations) {
        this.code = code;
        this.message = message;
        this.violations = violations;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApiErrorResponseViolations getViolations() {
        return violations;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiErrorResponse that = (ApiErrorResponse) obj;
        return Objects.equals(code, that.code)
            && Objects.equals(message, that.message)
            && Objects.equals(violations, that.violations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, violations);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        if (code != null) {
            joiner.add("code=\"" + code + "\"");
        }
        if (message != null) {
            joiner.add("message=\"" + message + "\"");
        }
        if (violations != null) {
            joiner.add("violations=" + violations);
        }
        return joiner.toString();
    }

    public static class ApiErrorResponseViolations {
        private List<String> field;
        private List<String> message;

        public ApiErrorResponseViolations() {
        }

        public ApiErrorResponseViolations(final List<String> field, final List<String> message) {
            this.field = field;
            this.message = message;
        }

        public List<String> getField() {
            return field;
        }

        public List<String> getMessage() {
            return message;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final ApiErrorResponseViolations that = (ApiErrorResponseViolations) obj;
            return Objects.equals(field, that.field)
                && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, message);
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
            if (field != null) {
                joiner.add("field=" + field);
            }
            if (message != null) {
                joiner.add("message=" + message);
            }
            return joiner.toString();
        }
    }
}
