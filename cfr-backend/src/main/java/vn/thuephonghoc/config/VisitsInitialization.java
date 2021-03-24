package vn.thuephonghoc.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import vn.thuephonghoc.service.VisitsService;

@Component
public class VisitsInitialization implements ApplicationRunner {

    private final VisitsService visitsService;

    public VisitsInitialization(VisitsService visitsService) {
        this.visitsService = visitsService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("--------------- Khởi tạo thống kê trang web ---------------");
        visitsService.save();
        System.out.println("--------------- Đã hoàn thành thống kê trang khởi tạo ---------------");
    }
}
