package com.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConnWecomOpenApp
{
    public static void main( String[] args )
    {

        SpringApplication.run(ConnWecomOpenApp.class, args);
    }
}
