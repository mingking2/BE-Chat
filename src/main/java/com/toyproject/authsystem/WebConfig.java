//package com.toyproject.authsystem;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:8080","http://localhost:3000") // 허용할 원격 주소
//                .allowedMethods("*") // 허용할 HTTP 메서드 (GET, POST, PUT, DELETE 등)
//                .allowedHeaders("*") // 허용할 HTTP 헤더
//                .allowCredentials(true) // 쿠키 전송 허용
//
//                .maxAge(3600); // 사전 전달 요청(Preflight request)의 캐시 시간
//    }
//
//}
