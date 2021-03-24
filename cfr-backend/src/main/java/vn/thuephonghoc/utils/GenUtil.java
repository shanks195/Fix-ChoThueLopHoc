package vn.thuephonghoc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.*;
import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.entity.ColumnInfo;
import vn.thuephonghoc.entity.GenConfig;

import org.springframework.util.ObjectUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Code generation
 */
@Slf4j
@SuppressWarnings("all")
public class GenUtil {

    private static final String TIMESTAMP = "Timestamp";

    private static final String BIGDECIMAL = "BigDecimal";

    public static final String PK = "PRI";

    public static final String EXTRA = "auto_increment";

    /**
     * Get back-end code template name
     * @return List
     */
    private static List<String> getAdminTemplateNames() {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("Entity");
        templateNames.add("Dto");
        templateNames.add("Mapper");
        templateNames.add("Controller");
        templateNames.add("QueryCriteria");
        templateNames.add("Service");
        templateNames.add("ServiceImpl");
        templateNames.add("Repository");
        return templateNames;
    }

    /**
     * Get the name of the front-end code template
     * @return List
     */
    private static List<String> getFrontTemplateNames() {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("index");
        templateNames.add("api");
        return templateNames;
    }
    public static List<Map<String, Object>> preview(List<ColumnInfo> columns, GenConfig genConfig) {
        Map<String,Object> genMap = getGenMap(columns, genConfig);
        List<Map<String,Object>> genList = new ArrayList<>();
        // Get backend template
        List<String> templates = getAdminTemplateNames();
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        for (String templateName : templates) {
            Map<String,Object> map = new HashMap<>(1);
            Template template = engine.getTemplate("generator/admin/"+templateName+".ftl");
            map.put("content", template.render(genMap));
            map.put("name", templateName);
            genList.add(map);
        }
        // Get front-end template
        templates = getFrontTemplateNames();
        for (String templateName : templates) {
            Map<String,Object> map = new HashMap<>(1);
            Template template = engine.getTemplate("generator/front/"+templateName+".ftl");
            map.put(templateName, template.render(genMap));
            map.put("content", template.render(genMap));
            map.put("name", templateName);
            genList.add(map);
        }
        return genList;
    }

    public static String download(List<ColumnInfo> columns, GenConfig genConfig) throws IOException {
    	// The spliced path: /tmpcfr-gen-temp/, this path needs root user permission to create under Linux, non-root users will fail with permission errors, change to: /tmp/cfr-gen-temp/
        // String tempPath =System.getProperty("java.io.tmpdir") + "cfr-gen-temp" + File.separator + genConfig.getTableName() + File.separator;
        String tempPath =System.getProperty("java.io.tmpdir") + File.separator + "cfr-gen-temp" + File.separator + genConfig.getTableName() + File.separator;   
        	Map<String,Object> genMap = getGenMap(columns, genConfig);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
     // Generate backend code
        List<String> templates = getAdminTemplateNames();
        for (String templateName: templates) {
            Template template = engine.getTemplate("generator/admin/"+templateName+".ftl");
            String filePath = getAdminFilePath(templateName,genConfig,genMap.get("className").toString(),tempPath + "dokit" + File.separator);
            assert filePath != null;
            File file = new File(filePath);
            // If non-covered generation
            if(!genConfig.getCover() && FileUtil.exist(file)){
                continue;
            }
            // Generate code
            genFile(file, template, genMap);
        }
        // Generate front-end code
        templates = getFrontTemplateNames();
        for (String templateName : templates) {
            Template template = engine.getTemplate("generator/front/"+templateName+".ftl");
            String path = tempPath + "cfr/cfr-frontend"  + File.separator;
            String apiPath = path + "src" + File.separator + "api" + File.separator;
            String srcPath = path + "src" + File.separator + "views" + File.separator + genMap.get("changeClassName").toString() + File.separator;
            String filePath = getFrontFilePath(templateName, apiPath, srcPath, genMap.get("changeClassName").toString());
            assert filePath != null;
            File file = new File(filePath);
         // If non-covered generation
            if(!genConfig.getCover() && FileUtil.exist(file)){
                continue;
            }
            // Generate code
            genFile(file, template, genMap);
        }
        return tempPath;
    }

    public static void generatorCode(List<ColumnInfo> columnInfos, GenConfig genConfig) throws IOException {
        Map<String,Object> genMap = getGenMap(columnInfos, genConfig);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        // Generate backend code
        List<String> templates = getAdminTemplateNames();
        for (String templateName: templates) {
            Template template = engine.getTemplate("generator/admin/"+templateName+".ftl");
            String filePath = getAdminFilePath(templateName,genConfig,genMap.get("className").toString(),System.getProperty("user.dir"));

            assert filePath != null;
            File file = new File(filePath);

            // If non-covered generation
            if(!genConfig.getCover() && FileUtil.exist(file)){
                continue;
            }
            // Generate code
            genFile(file, template, genMap);
        }

        // Generate front-end code
        templates = getFrontTemplateNames();
        for (String templateName: templates) {
            Template template = engine.getTemplate("generator/front/"+templateName+".ftl");
            String filePath = getFrontFilePath(templateName,genConfig.getApiPath(),genConfig.getPath(),genMap.get("changeClassName").toString());

            assert filePath != null;
            File file = new File(filePath);

            // If non-covered generation
            if(!genConfig.getCover() && FileUtil.exist(file)){
                continue;
            }
            // Generate code
            genFile(file, template, genMap);
        }
    }

 // Get template data
    private static Map<String,Object> getGenMap(List<ColumnInfo> columnInfos, GenConfig genConfig) {
        // Store template field data
        Map<String,Object> genMap = new HashMap<>(16);
        // interface alias
        genMap.put("apiAlias",genConfig.getApiAlias());
        // package name
        genMap.put("package",genConfig.getPack());
        // Author
        genMap.put("author",genConfig.getAuthor());
        // Creation date
        genMap.put("date", LocalDate.now().toString());
        // Table Name
        genMap.put("tableName",genConfig.getTableName());
        // Class name starting with uppercase
        String className = StringUtils.toCapitalizeCamelCase(genConfig.getTableName());
        // Class name starting with lowercase
        String changeClassName = StringUtils.toCamelCase(genConfig.getTableName());

        // Determine whether to remove the table prefix
        if (StringUtils.isNotEmpty(genConfig.getPrefix())) {
            className = StringUtils.toCapitalizeCamelCase(StrUtil.removePrefix(genConfig.getTableName(),genConfig.getPrefix()));
            changeClassName = StringUtils.toCamelCase(StrUtil.removePrefix(genConfig.getTableName(),genConfig.getPrefix()));
        }
        // Save the class name
        genMap.put("className", className);
        // Save the class name starting with lowercase
        genMap.put("changeClassName", changeClassName);
        // There is a Timestamp field
        genMap.put("hasTimestamp",false);
        // There is a Timestamp field in the query class
        genMap.put("queryHasTimestamp",false);
        // There is a BigDecimal field
        genMap.put("hasBigDecimal",false);
        // There is a BigDecimal field in the query class
        genMap.put("queryHasBigDecimal",false);
        // Do you need to create a query
        genMap.put("hasQuery",false);
        // Incremental primary key
        genMap.put("auto",false);
        // exists dictionary
        genMap.put("hasDict",false);
        // Date annotation exists
        genMap.put("hasDateAnnotation",false);
        // Save field information
        List<Map<String,Object>> columns = new ArrayList<>();
        // Save the information of the query field
        List<Map<String,Object>> queryColumns = new ArrayList<>();
        // Store dictionary information
        List<String> dicts = new ArrayList<>();
        // Store between information
        List<Map<String,Object>> betweens = new ArrayList<>();
        // Store field information that is not empty
        List<Map<String,Object>> isNotNullColumns = new ArrayList<>();
        for (ColumnInfo column : columnInfos) {
            Map<String,Object> listMap = new HashMap<>(16);
         // Field description
            listMap.put("remark",column.getRemark());
            // Field Type
            listMap.put("columnKey",column.getKeyType());

            // Primary key type
            String colType = ColUtil.cloToJava(column.getColumnType());
            // field names starting with lowercase
            String changeColumnName = StringUtils.toCamelCase(column.getColumnName().toString());
            // Field names starting with uppercase
            String capitalColumnName = StringUtils.toCapitalizeCamelCase(column.getColumnName().toString());
            if(PK.equals(column.getKeyType())){
                // Store the primary key type
                genMap.put("pkColumnType",colType);
                // Store field names starting with lowercase
                genMap.put("pkChangeColName",changeColumnName);
                // Store field names starting with uppercase
                genMap.put("pkCapitalColName",capitalColumnName);
            }
            if(TIMESTAMP.equals(colType)){
                genMap.put("hasTimestamp",true);
            }
            if(BIGDECIMAL.equals(colType)){
                genMap.put("hasBigDecimal",true);
            }
            if(EXTRA.equals(column.getExtra())){
                genMap.put("auto",true);
            }
            // The primary key exists in the dictionary
            if(StringUtils.isNotBlank(column.getDictName())){
                genMap.put("hasDict",true);
                dicts.add(column.getDictName());
            }
            // storage field type
            listMap.put("columnType",colType);
            // Store the name of the original segment
            listMap.put("columnName",column.getColumnName());
            // not null
            listMap.put("istNotNull",column.getNotNull());
            // Field list display
            listMap.put("columnShow",column.getListShow());
            // form display
            listMap.put("formShow",column.getFormShow());
            // Form component type
            listMap.put("formType", StringUtils.isNotBlank(column.getFormType())? column.getFormType(): "Input");
            // field names starting with lowercase
            listMap.put("changeColumnName",changeColumnName);
            //Field names starting with uppercase
            listMap.put("capitalColumnName",capitalColumnName);

            // Dictionary name
            listMap.put("dictName",column.getDictName());
            // Date annotation
            listMap.put("dateAnnotation",column.getDateAnnotation());
            if(StringUtils.isNotBlank(column.getDateAnnotation())){
                genMap.put("hasDateAnnotation",true);
            }
            // Add non-empty field information
            if(column.getNotNull()){
                isNotNullColumns.add(listMap);
            }
            // Determine whether there is a query, if so, set the query field into columnQuery
            if(!StringUtils.isBlank(column.getQueryType())){
                // query type
                listMap.put("queryType",column.getQueryType());
                // Does the query exist?
                genMap.put("hasQuery",true);
                if(TIMESTAMP.equals(colType)){
                    // Store the Timestamp type in the query
                    genMap.put("queryHasTimestamp",true);
                }
                if(BIGDECIMAL.equals(colType)){
                    // Store the BigDecimal type in the query
                    genMap.put("queryHasBigDecimal",true);
                }
                if("between".equalsIgnoreCase(column.getQueryType())){
                    betweens.add(listMap);
                } else {
                    // Add to the query list
                    queryColumns.add(listMap);
                }
            }
            // Add to the field list
            columns.add(listMap);
        }
        // save the field list
        genMap.put("columns",columns);
        // Save the query list
        genMap.put("queryColumns",queryColumns);
        // save the field list
        genMap.put("dicts",dicts);
        // Save the query list
        genMap.put("betweens",betweens);
        // Save non-empty field information
        genMap.put("isNotNullColumns",isNotNullColumns);
        return genMap;
    }

    /**
     *Define the back-end file path and name
     */
    private static String getAdminFilePath(String templateName, GenConfig genConfig, String className, String rootPath) {
        String projectPath = rootPath;
        String packagePath = projectPath + File.separator + "src" +File.separator+ "main" + File.separator + "java" + File.separator;
        if (!ObjectUtils.isEmpty(genConfig.getPack())) {
            packagePath += genConfig.getPack().replace(".", File.separator) + File.separator;
        }

        if ("Entity".equals(templateName)) {
            return packagePath + "entity" + File.separator + className + ".java";
        }

        if ("Controller".equals(templateName)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if ("Service".equals(templateName)) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if ("ServiceImpl".equals(templateName)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if ("Dto".equals(templateName)) {
            return packagePath + "dto" + File.separator + className + "Dto.java";
        }

        if ("QueryCriteria".equals(templateName)) {
            return packagePath + "query" + File.separator + className + "QueryCriteria.java";
        }

        if ("Mapper".equals(templateName)) {
            return packagePath  + "mapper" + File.separator + className + "Mapper.java";
        }

        if ("Repository".equals(templateName)) {
            return packagePath + "repository" + File.separator + className + "Repository.java";
        }

        return null;
    }

    /**
     * Define the front-end file path and name
     */
    private static String getFrontFilePath(String templateName, String apiPath, String path, String apiName) {
        if ("api".equals(templateName)) {
            return apiPath + File.separator + apiName + ".js";
        }
        if ("index".equals(templateName)) {
            return path  + File.separator + "index.vue";
        }
        return null;
    }

    private static void genFile(File file,Template template,Map<String,Object> map) throws IOException {
        // Generate object file
        Writer writer = null;
        try {
            FileUtil.touch(file);
            writer = new FileWriter(file);
            template.render(map, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert writer != null;
            writer.close();
        }
    }
}

