package com.example.ailearning.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI aiLearningOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI 岗课赛证学习平台 API")
                        .description("""
                                Spring Boot 后端接口文档。统一前缀为 /api/v1，统一响应结构为 {code, message, data, traceId}。
                                除登录接口外，业务接口均使用 JWT Bearer Token 鉴权。
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("AI 岗课赛证学习平台"))
                        .license(new License().name("Internal")))
                .servers(List.of(new Server().url("http://localhost:8080").description("本地开发环境")))
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("登录后复制 token，点击 Authorize，填入：Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    public GroupedOpenApi authAndPermissionApi() {
        return group("01-认证权限", "/api/v1/auth/**", "/api/v1/users/**", "/api/v1/roles/**", "/api/v1/permissions/**");
    }

    @Bean
    public GroupedOpenApi baseDataApi() {
        return group("02-基础数据", "/api/v1/colleges/**", "/api/v1/majors/**", "/api/v1/classes/**", "/api/v1/students/**", "/api/v1/teachers/**", "/api/v1/teacher-student-groups/**");
    }

    @Bean
    public GroupedOpenApi profileAndAiApi() {
        return group("03-画像与AI学习", "/api/v1/profile-sessions/**", "/api/v1/learning-profiles/**", "/api/v1/ai-generation-tasks/**", "/api/v1/resource-packages/**", "/api/v1/resources/**", "/api/v1/learning-paths/**", "/api/v1/learning-path-steps/**", "/api/v1/resource-recommendations/**", "/api/v1/ai-tutor/**", "/api/v1/learning-evaluations/**");
    }

    @Bean
    public GroupedOpenApi courseLearningApi() {
        return group("04-课程学习", "/api/v1/courses/**", "/api/v1/course-resources/**", "/api/v1/knowledge-points/**", "/api/v1/knowledge-point-relations/**", "/api/v1/learning-records/**", "/api/v1/quiz-attempts/**", "/api/v1/wrong-questions/**");
    }

    @Bean
    public GroupedOpenApi fusionApi() {
        return group("05-岗课赛证融合", "/api/v1/job-roles/**", "/api/v1/job-capabilities/**", "/api/v1/fusion-relations/**", "/api/v1/fusion-graph/**");
    }

    @Bean
    public GroupedOpenApi achievementApi() {
        return group("06-竞赛证书成果", "/api/v1/competitions/**", "/api/v1/competition-results/**", "/api/v1/certificates/**", "/api/v1/certificate-results/**");
    }

    @Bean
    public GroupedOpenApi employmentApi() {
        return group("07-就业扩展", "/api/v1/job-posts/**", "/api/v1/resumes/**", "/api/v1/job-applications/**");
    }

    @Bean
    public GroupedOpenApi statisticsApi() {
        return group("08-统计导出", "/api/v1/statistics/**");
    }

    @Bean
    public GroupedOpenApi dashboardApi() {
        return group("09-首页工作台", "/api/v1/dashboard/**", "/api/v1/teacher-dashboard/**");
    }

    @Bean
    public GroupedOpenApi allApi() {
        return group("00-全部接口", "/api/v1/**");
    }

    private GroupedOpenApi group(String groupName, String... paths) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(paths)
                .build();
    }
}
