package vn.thuephonghoc.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.entity.Picture;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.PictureQueryCriteria;
import vn.thuephonghoc.repository.PictureRepository;
import vn.thuephonghoc.service.PictureService;
import vn.thuephonghoc.utils.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@CacheConfig(cacheNames = "picture")
@Service(value = "pictureService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PictureServiceImpl implements PictureService {

    @Value("${smms.token}")
    private String token;

    private final PictureRepository pictureRepository;

    private static final String SUCCESS = "success";

    private static final String CODE = "code";

    private static final String MSG = "message";

    public PictureServiceImpl(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Override
    public Object queryAll(PictureQueryCriteria criteria, Pageable pageable){
        return PageUtil.toPage(pictureRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable));
    }

    @Override
    public List<Picture> queryAll(PictureQueryCriteria criteria) {
        return pictureRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Picture upload(MultipartFile multipartFile, String username) {
        File file = FileUtil.toFile(multipartFile);
        // Verify  the upload is repeated
        Picture picture = pictureRepository.findByMd5Code(FileUtil.getMd5(file));
        if(picture != null){
            return picture;
        }
        HashMap<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("smfile", file);
        // upload files
        String result= HttpRequest.post(CfrConstant.Url.SM_MS_URL + "/v2/upload")
                .header("Authorization", token)
                .form(paramMap)
                .timeout(20000)
                .execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(!jsonObject.get(CODE).toString().equals(SUCCESS)){
            throw new BadRequestException(TranslatorUtil.translate(jsonObject.get(MSG).toString()));
        }
        picture = JSON.parseObject(jsonObject.get("data").toString(), Picture.class);
        picture.setSize(FileUtil.getSize(Integer.parseInt(picture.getSize())));
        picture.setUsername(username);
        picture.setMd5Code(FileUtil.getMd5(file));
        picture.setFilename(FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename())+"."+FileUtil.getExtensionName(multipartFile.getOriginalFilename()));
        pictureRepository.save(picture);
        //
        FileUtil.del(file);
        return picture;

    }

    @Override
    public Picture findById(Long id) {
        Picture picture = pictureRepository.findById(id).orElseGet(Picture::new);
        ValidationUtil.isNull(picture.getId(),"Picture","id",id);
        return picture;
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            Picture picture = findById(id);
            try {
                HttpUtil.get(picture.getDelete());
                pictureRepository.delete(picture);
            } catch(Exception e){
                pictureRepository.delete(picture);
            }
        }
    }

    @Override
    public void synchronize() {
        //Chain build request
        String result = HttpRequest.get(CfrConstant.Url.SM_MS_URL + "/v2/upload_history")
                //Header information, multiple header information calls this method multiple times
                .header("Authorization", token)
                .timeout(20000)
                .execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        List<Picture> pictures = JSON.parseArray(jsonObject.get("data").toString(), Picture.class);
        for (Picture picture : pictures) {
            if(!pictureRepository.existsByUrl(picture.getUrl())){
                picture.setSize(FileUtil.getSize(Integer.parseInt(picture.getSize())));
                picture.setUsername("System Sync");
                picture.setMd5Code("");
                pictureRepository.save(picture);
            }
        }
    }

    @Override
    public void download(List<Picture> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Picture picture : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("File name", picture.getFilename());
            map.put("Picture address", picture.getUrl());
            map.put("File Size", picture.getSize());
            map.put("Operator", picture.getUsername());
            map.put("Height", picture.getHeight());
            map.put("Width", picture.getWidth());
            map.put("Delete Address", picture.getDelete());
            map.put("Creation Date", picture.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}

