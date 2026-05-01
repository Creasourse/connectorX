package com.cs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: RestTemplateConfig
 * @Author: wwd
 * @TODO:
 * @Date: 2025/4/11 11:25
 */
@Configuration
public class RestTemplateConfig {

    @Value("${http.header.token}")
    private String headerToken;

    @Bean
    public RestTemplateForHttp restTemplate() throws KeyManagementException, NoSuchAlgorithmException {

        // 测试环境开启 生产禁用
        // 创建一个信任所有证书的TrustManager
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };

        // 安装信任所有证书的SSLContext
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        RestTemplateForHttp restTemplate = new RestTemplateForHttp();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            // 将用户名和密码编码为 Base64
            request.getHeaders().add("Authorization", headerToken);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
