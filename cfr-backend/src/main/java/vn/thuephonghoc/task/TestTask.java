package vn.thuephonghoc.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestTask {

    public void run(){
    	log.info("Successful execution");
    }

    public void run1(String str){
        log.info("The execution is successful, the parameters are: {}", str);
    }
}
