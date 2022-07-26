package com.ggjcbgg.guguji;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j  //日志和实体类方法
@SpringBootApplication  //SpringBoot启动类
@ServletComponentScan  //扫描各种注解如@WebFilter
@EnableTransactionManagement    //开启事务注解
public class GugujiApplication {
    public static void main(String[] args) {
        SpringApplication.run(GugujiApplication.class,args);//启动SpringBoot
        log.info("项目启动成功");//控制台输出日志
    }
}
