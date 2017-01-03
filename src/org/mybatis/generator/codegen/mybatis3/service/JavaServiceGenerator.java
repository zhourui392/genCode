/*
 *  Copyright 2009 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mybatis.generator.codegen.mybatis3.service;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class JavaServiceGenerator extends AbstractJavaGenerator {

    public JavaServiceGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String myBatis3JavaServiceType = introspectedTable.getMyBatis3JavaServiceType();
        String baseRecordType = introspectedTable.getBaseRecordType();
        String mapperType = introspectedTable.getMyBatis3JavaMapperType();

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        //生成接口
        Interface interfaze = interfaceout(commentGenerator,
                myBatis3JavaServiceType,baseRecordType);
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }

        //生成实现类
        TopLevelClass topLevelClass = geneTopLevelClass(commentGenerator,
                myBatis3JavaServiceType,baseRecordType,mapperType);

        if (context.getPlugins().modelExampleClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }


        return answer;
    }

    private TopLevelClass geneTopLevelClass(CommentGenerator commentGenerator,
                                            String myBatis3JavaServiceType,
                                            String baseRecordType,String myBatis3JavaMapperType){
        FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(myBatis3JavaServiceType);

        String myBatis3JavaServiceImpl = interfaceType.getPackageName()+".impl."+interfaceType.getShortName()+"Impl";
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(baseRecordType);
        FullyQualifiedJavaType implType = new FullyQualifiedJavaType(myBatis3JavaServiceImpl);
        String baseServiceImpl = interfaceType.getPackageName()+".base.impl.BaseServiceImpl<"+modelType.getShortName()+">";
        FullyQualifiedJavaType baseServiceImplType = new FullyQualifiedJavaType(baseServiceImpl);
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(myBatis3JavaMapperType);

        TopLevelClass topLevelClass = new TopLevelClass(implType);

        //add interface
        topLevelClass.addSuperInterface(interfaceType);

        //add extends
        topLevelClass.setSuperClass(baseServiceImplType);

        //add annotation
        topLevelClass.addAnnotation("@Service");

        //add field
        //logger
        Field loggerField = new Field();
        loggerField.setVisibility(JavaVisibility.PRIVATE);
        loggerField.setStatic(true);
        loggerField.setFinal(true);
        String loggerString = "org.slf4j.Logger";
        String loggerFactoryString = "org.slf4j.LoggerFactory";
        FullyQualifiedJavaType loggerType = new FullyQualifiedJavaType(loggerString);
        FullyQualifiedJavaType loggerFactoryType = new FullyQualifiedJavaType(loggerFactoryString);
        loggerField.setType(loggerType);
        loggerField.setName("logger");
        loggerField.setInitializationString("LoggerFactory.getLogger("+implType.getShortName()+".class)");
        loggerField.addAnnotation("@Resource");
        topLevelClass.addField(loggerField);
        //mapper
        Field mapperFiled = new Field();
        mapperFiled.setVisibility(JavaVisibility.PRIVATE);
        mapperFiled.setType(mapperType);
        mapperFiled.setName(StringUtility.lowFirstString(mapperType.getShortName()));
        mapperFiled.addAnnotation("@Resource");
        topLevelClass.addField(mapperFiled);


        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        // add override method
        String baseMapper = mapperType.getPackageName()+".base.BaseMapper<"+modelType.getShortName()+">";
        FullyQualifiedJavaType baseMapperType = new FullyQualifiedJavaType(baseMapper);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(baseMapperType);
        method.setName("getBaseMapper");
        method.addAnnotation("@Override");
        method.addBodyLine("return "+ StringUtility.lowFirstString(modelType.getShortName())+"Mapper;"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        addGetByPageMethodImpl(topLevelClass,implType,modelType,mapperType);

        //add imports
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = new HashSet<>();
        fullyQualifiedJavaTypes.add(interfaceType);
        fullyQualifiedJavaTypes.add(modelType);
        fullyQualifiedJavaTypes.add(baseServiceImplType);
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        fullyQualifiedJavaTypes.add(serviceType);
        FullyQualifiedJavaType resourceType = new FullyQualifiedJavaType("javax.annotation.Resource");
        fullyQualifiedJavaTypes.add(resourceType);
        fullyQualifiedJavaTypes.add(mapperType);
        fullyQualifiedJavaTypes.add(baseMapperType);
        fullyQualifiedJavaTypes.add(loggerType);
        fullyQualifiedJavaTypes.add(loggerFactoryType);
        FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(implType.getPackageName().replace("service.impl","")+"common.util.page.PageQuery");
        fullyQualifiedJavaTypes.add(pageQueryType);
        FullyQualifiedJavaType pageResultType = new FullyQualifiedJavaType(implType.getPackageName().replace("service.impl","")+"common.util.page.PageResult");
        fullyQualifiedJavaTypes.add(pageResultType);
        fullyQualifiedJavaTypes.add(FullyQualifiedJavaType.getNewListInstance());
        topLevelClass.addImportedTypes(fullyQualifiedJavaTypes);

        return topLevelClass;
    }

	private Interface interfaceout(CommentGenerator commentGenerator,
			String myBatis3JavaMapperType,String baseRecordType) {
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(
                baseRecordType);
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                myBatis3JavaMapperType);
        Interface interfaze = new Interface(serviceType);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = serviceType.getPackageName()+".base.BaseService<"+modelType.getShortName()+">";
        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);

            Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = new HashSet<>();
            fullyQualifiedJavaTypes.add(fqjt);
            fullyQualifiedJavaTypes.add(modelType);
            FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(serviceType.getPackageName().replace("service","")+"common.util.page.PageQuery");
            fullyQualifiedJavaTypes.add(pageQueryType);
            FullyQualifiedJavaType pageResultType = new FullyQualifiedJavaType(serviceType.getPackageName().replace("service","")+"common.util.page.PageResult");
            fullyQualifiedJavaTypes.add(pageResultType);
            interfaze.addImportedTypes(fullyQualifiedJavaTypes);
        }

        addGetByPageMethod(interfaze,serviceType,modelType);

        return interfaze;
	}


    protected void addGetByPageMethod(Interface interfaze,FullyQualifiedJavaType serviceType,
                                      FullyQualifiedJavaType modelType) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType pageResultType = new FullyQualifiedJavaType(serviceType.getPackageName().replace("service","")+"common.util.page.PageResult<"+modelType.getShortName()+">");
        FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(serviceType.getPackageName().replace("service","")+"common.util.page.PageQuery");
        method.setReturnType(pageResultType);
        method.setName("get"+modelType.getShortName()+"sByPage");
        method.addParameter(new Parameter(pageQueryType, "pageQuery")); //$NON-NLS-1$
        interfaze.addMethod(method);
    }

    protected void addGetByPageMethodImpl(TopLevelClass topLevelClass,FullyQualifiedJavaType serviceType,
                                      FullyQualifiedJavaType modelType,FullyQualifiedJavaType mapperType) {
        String mapperVar = StringUtility.lowFirstString(mapperType.getShortName());

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType pageResultType = new FullyQualifiedJavaType(serviceType.getPackageName().replace("service","")+"common.util.page.PageResult<"+modelType.getShortName()+">");
        FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(serviceType.getPackageName().replace("service","")+"common.util.page.PageQuery");
        method.setReturnType(pageResultType);
        method.setName("get"+modelType.getShortName()+"sByPage");
        method.addAnnotation("@Override");
        method.addParameter(new Parameter(pageQueryType, "pageQuery")); //$NON-NLS-1$
        method.addBodyLine("PageResult<"+modelType.getShortName()+"> result = new PageResult();");
        method.addBodyLine("List<"+modelType.getShortName()+"> list = " + mapperVar + ".getListByConditions(pageQuery);");
        method.addBodyLine("int totalCount = " + mapperVar + ".getCountByConditions();");
        method.addBodyLine("result.setPageQuery(pageQuery);");
        method.addBodyLine("result.setItems(list);");
        method.addBodyLine("result.setCount(totalCount);");
        method.addBodyLine("return result;");
        topLevelClass.addMethod(method);
    }

}
