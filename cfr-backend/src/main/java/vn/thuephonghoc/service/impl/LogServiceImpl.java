package vn.thuephonghoc.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import vn.thuephonghoc.entity.Log;
import vn.thuephonghoc.mapper.LogErrorMapper;
import vn.thuephonghoc.mapper.LogSmallMapper;
import vn.thuephonghoc.query.LogQueryCriteria;
import vn.thuephonghoc.repository.LogRepository;
import vn.thuephonghoc.service.LogService;
import vn.thuephonghoc.utils.*;
import vn.thuephonghoc.utils.ip.AddressUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    private final LogErrorMapper logErrorMapper;

    private final LogSmallMapper logSmallMapper;

    public LogServiceImpl(LogRepository logRepository, LogErrorMapper logErrorMapper, LogSmallMapper logSmallMapper) {
        this.logRepository = logRepository;
        this.logErrorMapper = logErrorMapper;
        this.logSmallMapper = logSmallMapper;
    }

    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)),pageable);
        String status = "ERROR";
        if (status.equals(criteria.getLogType())) {
            return PageUtil.toPage(page.map(logErrorMapper::toDto));
        }
        return page;
    }

    @Override
    public List<Log> queryAll(LogQueryCriteria criteria) {
        return logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)));
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)),pageable);
        return PageUtil.toPage(page.map(logSmallMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log){

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        vn.thuephonghoc.aop.log.Log aopLog = method.getAnnotation(vn.thuephonghoc.aop.log.Log.class);

        //Method path
        String methodName = joinPoint.getTarget().getClass().getName()+"."+signature.getName()+"()";

        StringBuilder params = new StringBuilder("{");
        Object[] argValues = joinPoint.getArgs();
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        if(argValues != null){
            for (int i = 0; i < argValues.length; i++) {
                params.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
            }
        }

        if (log != null) {
            log.setDescription(aopLog.value());
        }
        assert log != null;
        log.setRequestIp(ip);

        String loginPath = "login";
        if(loginPath.equals(signature.getName())){
            try {
                assert argValues != null;
                username = new JSONObject(argValues[0]).get("username").toString();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        log.setAddress(AddressUtils.getRealAddressByIP(log.getRequestIp()));
        log.setMethod(methodName);
        log.setUsername(username);
        log.setParams(params.toString() + " }");
        log.setBrowser(browser);
        logRepository.save(log);
    }

    @Override
    public Object findByErrDetail(Long id) {
        Log log = logRepository.findById(id).orElseGet(Log::new);
        ValidationUtil.isNull( log.getId(),"Log","id", id);
        byte[] details = log.getExceptionDetail();
        return Dict.create().set("exception",new String(ObjectUtil.isNotNull(details) ? details : "".getBytes()));
    }

    @Override
    public void download(List<Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Log log : logs) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Username", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP source", log.getAddress());
            map.put("Description", log.getDescription());
            map.put("request method", log.getMethod());
            map.put("request parameters", log.getParams());
            map.put("Browser", log.getBrowser());
            map.put("Request time/ms", log.getTime());
            map.put("Creation Date", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadError(List<Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Log log : logs) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Username", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP source", log.getAddress());
            map.put("Description", log.getDescription());
            map.put("Request method", log.getMethod());
            map.put("Request parameters", log.getParams());
            map.put("Browser", log.getBrowser());
            map.put("Request time/ms", log.getTime());
            map.put("Exception Details", new String(ObjectUtil.isNotNull(log.getExceptionDetail())? log.getExceptionDetail(): "".getBytes()));
            map.put("Creation Date", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByError() {
        logRepository.deleteByLogType("ERROR");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByInfo() {
        logRepository.deleteByLogType("INFO");
    }
}
