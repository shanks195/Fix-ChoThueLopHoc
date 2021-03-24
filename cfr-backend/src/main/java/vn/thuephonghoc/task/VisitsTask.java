package vn.thuephonghoc.task;

import org.springframework.stereotype.Component;

import vn.thuephonghoc.service.VisitsService;

@Component
public class VisitsTask {

    private final VisitsService visitsService;

    public VisitsTask(VisitsService visitsService) {
        this.visitsService = visitsService;
    }

    public void run(){
        visitsService.save();
    }
}
