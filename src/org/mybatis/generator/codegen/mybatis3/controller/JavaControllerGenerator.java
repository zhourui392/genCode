package org.mybatis.generator.codegen.mybatis3.controller;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.CountByExampleMethodGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by zhourui on 2016/12/31.
 */
public class JavaControllerGenerator extends AbstractJavaGenerator {
    public JavaControllerGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String myBatis3JavaControllerType = introspectedTable.getMyBatis3JavaControllerType();
        String baseRecordType = introspectedTable.getBaseRecordType();
        String serviceType = introspectedTable.getMyBatis3JavaServiceType();

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        //生成Controller
        TopLevelClass topLevelClass = geneTopLevelClass(commentGenerator,
                myBatis3JavaControllerType,baseRecordType,serviceType);

        if (context.getPlugins().modelExampleClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }


        return answer;
    }

    private TopLevelClass geneTopLevelClass(CommentGenerator commentGenerator,
                                            String myBatis3JavaControllerType,
                                            String baseRecordType,String myBatis3JavaServiceType){
        FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(myBatis3JavaControllerType);

        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(baseRecordType);
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(myBatis3JavaServiceType);
        String baseController = controllerType.getPackageName()+".BaseController";
        FullyQualifiedJavaType baseControllerType = new FullyQualifiedJavaType(baseController);

        TopLevelClass topLevelClass = new TopLevelClass(controllerType);

        //add extends
        topLevelClass.setSuperClass(baseControllerType);

        //add annotation
        topLevelClass.addAnnotation("@Controller");

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
        loggerField.setInitializationString("LoggerFactory.getLogger("+controllerType.getShortName()+".class)");
        loggerField.addAnnotation("@Resource");
        topLevelClass.addField(loggerField);
        //service
        Field serviceFiled = new Field();
        serviceFiled.setVisibility(JavaVisibility.PRIVATE);
        serviceFiled.setType(serviceType);
        serviceFiled.setName(StringUtility.lowFirstString(serviceType.getShortName()));
        serviceFiled.addAnnotation("@Resource");
        topLevelClass.addField(serviceFiled);


        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        //TODO add method

        //add imports
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = new HashSet<>();
        fullyQualifiedJavaTypes.add(modelType);
        fullyQualifiedJavaTypes.add(serviceType);
        FullyQualifiedJavaType controllerAnnotationType = new FullyQualifiedJavaType("org.springframework.stereotype.Controller");
        fullyQualifiedJavaTypes.add(controllerAnnotationType);
        FullyQualifiedJavaType requestMappingType = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping");
        fullyQualifiedJavaTypes.add(requestMappingType);
        FullyQualifiedJavaType resourceType = new FullyQualifiedJavaType("javax.annotation.Resource");
        fullyQualifiedJavaTypes.add(resourceType);
        fullyQualifiedJavaTypes.add(loggerType);
        fullyQualifiedJavaTypes.add(loggerFactoryType);
        topLevelClass.addImportedTypes(fullyQualifiedJavaTypes);

        return topLevelClass;
    }

    protected void addCountByExampleMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    protected void initializeAndExecuteGenerator(
            AbstractJavaMapperMethodGenerator methodGenerator,
            Interface interfaze) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }
}
