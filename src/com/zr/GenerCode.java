package com.zr;

import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.lf5.util.StreamUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;

public class GenerCode {
	public static List<EachModel> eachModels = new ArrayList<>();
	public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		String fileURL = GenerCode.class.getResource("/").getFile();
		File configFile = 
			new File(fileURL.replace("%20", " ")+"/generatorConfig.xml");
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
				callback, warnings);
		myBatisGenerator.generate(null);

		//获取到模型、字段，然后生成JS和Html页面
		for (EachModel eachModel :eachModels){
			System.out.println(eachModel.toString());

			//生成js、html,使用模板替换：
			//1、读取模板
			InputStream is = GenerCode.class.getClassLoader().getResourceAsStream("org/template/template.html");

			String html = new String(StreamUtils.getBytes(is));

			html = html.replaceAll("##modelHead",eachModel.getModelName()+"管理");
			//2、替换Header
			StringBuilder tableHeader = new StringBuilder();
			for (String field : eachModel.getFileds()){
				tableHeader.append("                    <th width=\"15%\">");
				tableHeader.append(field);
				tableHeader.append("</th>\n");
			}
			tableHeader.append("                    <th width=\"20%\">操作</th>");
			html = html.replaceAll("##TableHead",tableHeader.toString());


			//3、替换新增模态框
			String id = eachModel.getModelName()+"Id";
			StringBuilder modalBody = new StringBuilder();
			modalBody.append("                        <input type=\"hidden\" id=\""+id+"\">\n");
			for (String field : eachModel.getFileds()){
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
			File targetFile = new File(directory, eachModel.getModelName()+".html");

			writeFile(targetFile, html,"utf-8");


			//生成JS
			//生成js、html,使用模板替换：
			//1、读取模板
			InputStream JSIS = GenerCode.class.getClassLoader().getResourceAsStream("org/template/template.js");

			String jsTemplate = new String(StreamUtils.getBytes(JSIS));

			jsTemplate.replaceAll("##Model", StringUtility.upperFirstString(eachModel.getModelName()));
			jsTemplate = jsTemplate.replaceAll("##modelHead",eachModel.getModelName()+"管理");
			//5、替换JS链接
			StringBuilder initAddModelValueSB = new StringBuilder("        \\$(\"#"+id+"\").val(\"\");\n");
			for (String field : eachModel.getFileds()){
				initAddModelValueSB.append("        \\$(\"#"+field+"\").val(\"\");\n");
			}

			jsTemplate = jsTemplate.replaceAll("##modalValueInit",initAddModelValueSB.toString());




			//6、替换JS分页


			//7、替换新增部分

			//8、替换修改部分

			//9、替换删除部分

			//输出JS
			//输出Html
			File jsDirectory =  new File("src/com/teleus/js");
			if (!jsDirectory.exists()){
				jsDirectory.mkdirs();
			}
			File jsTargetFile = new File(jsDirectory, eachModel.getModelName()+".js");

			writeFile(jsTargetFile, jsTemplate,"utf-8");


		}

	}

	/**
	 * Writes, or overwrites, the contents of the specified file
	 *
	 * @param file
	 * @param content
	 */
	private static void writeFile(File file, String content, String fileEncoding) throws IOException {
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
