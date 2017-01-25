package com.zr;

import org.apache.log4j.lf5.util.StreamUtils;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhour on 2017/1/21.
 */
public class EachModel {
    private String modelName;
    private List<String> fileds = new ArrayList<>();

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    private boolean isFourFileds(String field){
        if (field.toLowerCase().equals("creationtime")){
            return true;
        }
        if (field.toLowerCase().equals("createdtime")){
            return true;
        }
        if (field.toLowerCase().equals("updatedtime")){
            return true;
        }
        if (field.toLowerCase().equals("createdby")){
            return true;
        }
        if (field.toLowerCase().equals("updatedby")){
            return true;
        }
        return false;
    }

    public void generateHtmlAndJs() throws IOException {
        //生成js、html,使用模板替换：
        //1、读取模板
        InputStream is = GenerCode.class.getClassLoader().getResourceAsStream("org/template/template.html");

        String html = new String(StreamUtils.getBytes(is));

        html = html.replaceAll("##modelHead",getModelName()+"管理");
        //2、替换Header
        StringBuilder tableHeader = new StringBuilder();
        for (String field : getFileds()){
            tableHeader.append("                    <th width=\"15%\">");
            tableHeader.append(field);
            tableHeader.append("</th>\n");
        }
        tableHeader.append("                    <th width=\"20%\">操作</th>");
        html = html.replaceAll("##TableHead",tableHeader.toString());


        //3、替换新增模态框
        String id = getModelName()+"Id";
        StringBuilder modalBody = new StringBuilder();
        modalBody.append("                        <input type=\"hidden\" id=\""+id+"\">\n");
        for (String field : getFileds()){
            modalBody.append("                        <div class=\"form-group\" style=\"height:50px;\">\n");
            modalBody.append("                            <label for=\""+field+"\">"+field+"</label>\n");
            modalBody.append("                            <input class=\"form-control\" id=\""+field+"\">\n");
            modalBody.append("                        </div>\n");
        }
        html = html.replaceAll("##modalBody",modalBody.toString());

        //4、替换修改模态框  ?

        //输出Html
        File directory =  new File("src/com/teleus/html");
        if (!directory.exists()){
            directory.mkdirs();
        }
        File targetFile = new File(directory, getModelName()+".html");

        writeFile(targetFile, html,"utf-8");


        //生成JS
        //生成js、html,使用模板替换：
        //1、读取模板
        InputStream JSIS = GenerCode.class.getClassLoader().getResourceAsStream("org/template/templateJS");

        String jsTemplate = new String(StreamUtils.getBytes(JSIS));

        String firstUpperModel = StringUtility.upperFirstString(getModelName());

        jsTemplate.replaceAll("##Model", firstUpperModel);
        jsTemplate = jsTemplate.replaceAll("##modelHead",getModelName()+"管理");
        //5、替换JS链接
        StringBuilder initAddModelValueSB = new StringBuilder("        \\$(\"#"+id+"\").val(\"\");\n");
        for (String field : getFileds()){
            initAddModelValueSB.append("        \\$(\"#"+field+"\").val(\"\");\n");
        }

        jsTemplate = jsTemplate.replaceAll("##modalValueInit",initAddModelValueSB.toString());




        //6、替换JS分页
        StringBuilder pageToSetValueSB = new StringBuilder("");
        pageToSetValueSB.append("                        "+getModelName()+"Html += \"<tr>\";\n");
        for (String field : getFileds()){
            pageToSetValueSB
                    .append("                        "+getModelName()+"Html += \"<td>\"+"+getModelName()+"."+field+"+\"</td>\";\n");
        }
        pageToSetValueSB.append("                        "+getModelName()+"Html += \"<td><div class='oper'>\";\n");
        pageToSetValueSB.append("                        "+getModelName()+"Html += \"<a href='javascript:void(0)' " +
                "class='btn btn-success btn-xs' value=\"+"+getModelName()+".id+\"><span class='glyphicon glyphicon-pencil'></span></a>\";\n");
        pageToSetValueSB.append("                        "+getModelName()+"Html " +
                "+= \"<a href='javascript:void(0)' class='btn btn-danger btn-xs' value=\"+"+getModelName()+".id+\">" +
                "<span class='glyphicon glyphicon-remove'></span></a>\";\n");
        pageToSetValueSB.append("                        "+getModelName()+"Html += \"</div>\"\n");
        pageToSetValueSB.append("                        "+getModelName()+"Html += \"</td>\";");

        jsTemplate = jsTemplate.replaceAll("##pageToSetValue",pageToSetValueSB.toString());

        //7、替换新增部分
        StringBuilder getAddValueFromModalSB = new StringBuilder("");
        for (String field : getFileds()){
            if (!"id".equals(field)){
                getAddValueFromModalSB.append("    var "+field+" = \\$(\"#"+field+"\").val();\n");
            }
        }
        getAddValueFromModalSB.append("    var json = {\n");
        for (String field : getFileds()){
            if (!"id".equals(field)){
                getAddValueFromModalSB.append("        "+field+" : "+field+",\n");
            }
        }
        getAddValueFromModalSB.append("    };\n");
        jsTemplate = jsTemplate.replaceAll("##getAddValueFromModal",getAddValueFromModalSB.toString());



        //8、替换修改查询部分
        StringBuilder queryBeforeUpdateSB = new StringBuilder("");
        for (String field : getFileds()){
            if ("id".equals(field)){
                queryBeforeUpdateSB.append("                    \\$(\"#"+getModelName()+"Id\").val("+getModelName()+".id);\n");
            }else {
                queryBeforeUpdateSB.append("                    \\$(\"#"+field+"\").val("+getModelName()+"."+field+");\n");
            }
        }
        jsTemplate = jsTemplate.replaceAll("##QueryBeforeUpdate",queryBeforeUpdateSB.toString());

        //批量替换
        jsTemplate = jsTemplate.replaceAll("##ModelName",firstUpperModel);
        jsTemplate = jsTemplate.replaceAll("##LowModelName",getModelName());
        jsTemplate = jsTemplate.replaceAll("##eachRomColoms",Integer.toString(getFileds().size()));

        //输出JS
        //输出Html
        File jsDirectory =  new File("src/com/teleus/js");
        if (!jsDirectory.exists()){
            jsDirectory.mkdirs();
        }
        File jsTargetFile = new File(jsDirectory, getModelName()+".js");

        writeFile(jsTargetFile, jsTemplate,"utf-8");
    }

    public void addFiled(String filed) {
        if (!isFourFileds(filed)){
            fileds.add(filed);
        }
    }

    @Override
    public String toString() {
        StringBuilder filedsSb = new StringBuilder();
        fileds.forEach(s -> filedsSb.append(s+","));
        return "modelName:"+modelName+",fields:"+filedsSb;
    }

    public String getModelName() {
        return modelName;
    }

    public List<String> getFileds() {
        return fileds;
    }

    public void setFileds(List<String> fileds) {
        this.fileds = fileds;
    }


    /**
     * Writes, or overwrites, the contents of the specified file
     *
     * @param file
     * @param content
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }
}
