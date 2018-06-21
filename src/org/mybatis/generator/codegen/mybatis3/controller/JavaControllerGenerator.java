package org.mybatis.generator.codegen.mybatis3.controller;

import com.zr.Commons;
import com.zr.EachModel;
import com.zr.GenerCode;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.internal.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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

        List<IntrospectedColumn> introspectedColumns = introspectedTable.getBaseColumns();
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        Properties tableTDProperties = introspectedTable.getTableConfiguration().getProperties();
        Enumeration<Object> keys = tableTDProperties.keys();
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(baseRecordType);
        String lowModelShortName = StringUtility.lowFirstString(modelType.getShortName());


        EachModel eachModel = new EachModel();
        eachModel.setModelName(lowModelShortName);
        for (IntrospectedColumn introspectedColumn : introspectedColumns){
            if (tableTDProperties.size() != 0){
                if (tableTDProperties.get(introspectedColumn.getActualColumnName()) != null){
                    String actualColumn = introspectedColumn.getActualColumnName();
                    String value = tableTDProperties.getProperty(actualColumn);
                    eachModel.addFiled(actualColumn);
                    eachModel.addFiledMemo(value);
                }
            }else {
                String actualColumn = introspectedColumn.getActualColumnName();
                String value = tableTDProperties.getProperty(actualColumn);
                eachModel.addFiled(actualColumn);
                eachModel.addFiledMemo(value);
            }
        }
        GenerCode.eachModels.add(eachModel);
        GenerCode.basePackage = modelType.getPackageName().replace(".entity","");

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        //生成Controller
        TopLevelClass topLevelClass = geneTopLevelClass(commentGenerator,
                myBatis3JavaControllerType,baseRecordType,serviceType,introspectedColumns,primaryKeyColumns,tableTDProperties);

        if (context.getPlugins().modelExampleClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private TopLevelClass geneTopLevelClass(CommentGenerator commentGenerator,
                                            String myBatis3JavaControllerType,
                                            String baseRecordType, String myBatis3JavaServiceType,
                                            List<IntrospectedColumn> introspectedColumns,
                                            List<IntrospectedColumn> primaryKeyColumns, Properties tableTDProperties){
        FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(myBatis3JavaControllerType);

        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(baseRecordType);
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(myBatis3JavaServiceType);
        String baseController = Commons.NAME_BASE_CONTROLLER;
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
        String loggerString = Logger.class.getName();
        String loggerFactoryString = LoggerFactory.class.getName();
        FullyQualifiedJavaType loggerType = new FullyQualifiedJavaType(loggerString);
        FullyQualifiedJavaType loggerFactoryType = new FullyQualifiedJavaType(loggerFactoryString);
        loggerField.setType(loggerType);
        loggerField.setName("logger");
        loggerField.setInitializationString("LoggerFactory.getLogger("+controllerType.getShortName()+".class)");
        topLevelClass.addField(loggerField);
        //service
        Field serviceFiled = new Field();
        serviceFiled.setVisibility(JavaVisibility.PRIVATE);
        serviceFiled.setType(serviceType);
        serviceFiled.setName(StringUtility.lowFirstString(serviceType.getShortName()));
        serviceFiled.addAnnotation("@Resource");
        topLevelClass.addField(serviceFiled);

        new ControllerJavaDoc().getAll(topLevelClass);

        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        //generate Fields


        addGetByIdMethod(commentGenerator,topLevelClass,modelType,serviceType);
        addDeleteByIdMethod(commentGenerator,topLevelClass,modelType,serviceType);
        addAddMethod(commentGenerator,topLevelClass,modelType,serviceType,introspectedColumns,tableTDProperties);
        addUpdateMethod(commentGenerator,topLevelClass,modelType,serviceType,introspectedColumns,primaryKeyColumns,tableTDProperties);
        addGetByPageMethod(commentGenerator,topLevelClass,modelType,serviceType);

        //add imports
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = new HashSet<>();
        fullyQualifiedJavaTypes.add(baseControllerType);
        fullyQualifiedJavaTypes.add(modelType);
        fullyQualifiedJavaTypes.add(serviceType);
        FullyQualifiedJavaType controllerAnnotationType = new FullyQualifiedJavaType(Controller.class.getName());

        fullyQualifiedJavaTypes.add(controllerAnnotationType);
        FullyQualifiedJavaType requestMappingType = new FullyQualifiedJavaType(RequestMapping.class.getName());
        fullyQualifiedJavaTypes.add(requestMappingType);
        FullyQualifiedJavaType pathVariableType = new FullyQualifiedJavaType(PathVariable.class.getName());
        fullyQualifiedJavaTypes.add(pathVariableType);
        FullyQualifiedJavaType requestMethodType = new FullyQualifiedJavaType(RequestMethod.class.getName());
        fullyQualifiedJavaTypes.add(requestMethodType);
        FullyQualifiedJavaType requestParamType = new FullyQualifiedJavaType(RequestParam.class.getName());
        fullyQualifiedJavaTypes.add(requestParamType);
        FullyQualifiedJavaType responseBodyType = new FullyQualifiedJavaType(ResponseBody.class.getName());
        fullyQualifiedJavaTypes.add(responseBodyType);
        FullyQualifiedJavaType resourceType = new FullyQualifiedJavaType(Resource.class.getName());
        fullyQualifiedJavaTypes.add(resourceType);
        FullyQualifiedJavaType rootType = new FullyQualifiedJavaType(Commons.NAME_ROOT);
        fullyQualifiedJavaTypes.add(rootType);
        fullyQualifiedJavaTypes.add(loggerType);
        fullyQualifiedJavaTypes.add(loggerFactoryType);
        fullyQualifiedJavaTypes.add(FullyQualifiedJavaType.getDateInstance());
        FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(Commons.NAME_PAGE_QUERY);
        fullyQualifiedJavaTypes.add(pageQueryType);
        FullyQualifiedJavaType pageResultType = new FullyQualifiedJavaType(Commons.NAME_PAGE_RESULT);
        fullyQualifiedJavaTypes.add(pageResultType);

        topLevelClass.addImportedTypes(fullyQualifiedJavaTypes);

        return topLevelClass;
    }

    protected void addGetByIdMethod(CommentGenerator commentGenerator,TopLevelClass topLevelClass,
                              FullyQualifiedJavaType modelType, FullyQualifiedJavaType serviceType) {
        String lowModelShortName = StringUtility.lowFirstString(modelType.getShortName());
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("get"+modelType.getShortName()+"ById");
        Parameter parameter = new Parameter(FullyQualifiedJavaType.getIntInstance(),"id");
        parameter.addAnnotation("@PathVariable(\"id\")");
        method.addParameter(parameter);
        method.addAnnotation("@ResponseBody");
        method.addAnnotation("@RequestMapping(value=\"/"+lowModelShortName+"/{id}\", method= RequestMethod.GET)");
        method.addBodyLine(modelType.getShortName()+" "+lowModelShortName + " = "
                + StringUtility.lowFirstString(serviceType.getShortName()) +".getById(id);"); //$NON-NLS-1$
        method.addBodyLine("return Root.getRootOKAndSimpleMsg().setData("+lowModelShortName+").toJsonString();"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    protected void addDeleteByIdMethod(CommentGenerator commentGenerator,TopLevelClass topLevelClass,
                              FullyQualifiedJavaType modelType, FullyQualifiedJavaType serviceType) {
        String lowModelShortName = StringUtility.lowFirstString(modelType.getShortName());
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("delete"+modelType.getShortName()+"ById");
        Parameter parameter = new Parameter(FullyQualifiedJavaType.getIntInstance(),"id");
        parameter.addAnnotation("@PathVariable(\"id\")");
        method.addParameter(parameter);
        method.addAnnotation("@ResponseBody");
        method.addAnnotation("@RequestMapping(value=\"/"+lowModelShortName+"/{id}\", method= RequestMethod.DELETE)");
        method.addBodyLine("boolean resultBoolean = "
                + StringUtility.lowFirstString(serviceType.getShortName()) +".deleteById(id);"); //$NON-NLS-1$
        method.addBodyLine("if (resultBoolean) {return Root.getRootOKAndSimpleMsg().toJsonString();}"); //$NON-NLS-1$
        method.addBodyLine("return Root.getRootFailAndSimpleMsg().toJsonString();"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    protected void addAddMethod(CommentGenerator commentGenerator, TopLevelClass topLevelClass,
                                FullyQualifiedJavaType modelType, FullyQualifiedJavaType serviceType,
                                List<IntrospectedColumn> introspectedColumns,Properties tableTDProperties) {
        String lowModelShortName = StringUtility.lowFirstString(modelType.getShortName());
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("add"+modelType.getShortName());
        method.addAnnotation("@ResponseBody");
        method.addAnnotation("@RequestMapping(value=\"/"+lowModelShortName+"\", method= RequestMethod.POST)");

        method.addBodyLine(modelType.getShortName() + " "+lowModelShortName+" = new "+modelType.getShortName() +"();");
        for (IntrospectedColumn introspectedColumn : introspectedColumns){
            if (!introspectedColumn.getActualColumnName().equals("id")){
                if (!tableTDProperties.isEmpty()){
                    if (tableTDProperties.getProperty(introspectedColumn.getActualColumnName()) != null){
                        addAddParamsOnController(lowModelShortName, method, introspectedColumn);
                    }
                }else {
                    addAddParamsOnController(lowModelShortName, method, introspectedColumn);
                }

            }
        }


        method.addBodyLine("boolean resultBoolean = "
                + StringUtility.lowFirstString(serviceType.getShortName()) +".add("+lowModelShortName+");"); //$NON-NLS-1$
        method.addBodyLine("if (resultBoolean) {return Root.getRootOKAndSimpleMsg().toJsonString();}"); //$NON-NLS-1$
        method.addBodyLine("return Root.getRootFailAndSimpleMsg().toJsonString();"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    private void addAddParamsOnController(String lowModelShortName, Method method, IntrospectedColumn introspectedColumn) {
        switch (isDatabaseFileds(introspectedColumn.getActualColumnName())){
            case 1:
                //same as 2
            case 2:
                method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"(new Date());");
                break;
            case 4:
                method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"(\"ToBeChanged\");");
                break;
            case 3:
                method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"(new Date());");
                break;
            case 5:
                method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"(\"ToBeChanged\");");
                break;
            default:
                break;
        }

        if (isDatabaseFileds(introspectedColumn.getActualColumnName()) != 0){
            return;
        }

        Parameter parameter = new Parameter(introspectedColumn.getFullyQualifiedJavaType(),introspectedColumn.getActualColumnName());
        parameter.addAnnotation("@RequestParam(value = \""+introspectedColumn.getActualColumnName()+"\")");
        method.addParameter(parameter);

        method.addBodyLine(lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"("+introspectedColumn.getActualColumnName()+");");
    }

    protected void addUpdateMethod(CommentGenerator commentGenerator, TopLevelClass topLevelClass,
                                FullyQualifiedJavaType modelType, FullyQualifiedJavaType serviceType,
                                List<IntrospectedColumn> introspectedColumns,List<IntrospectedColumn> primaryKeyColumns,
                                   Properties tableTDProperties) {
        String lowModelShortName = StringUtility.lowFirstString(modelType.getShortName());
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("update"+modelType.getShortName());
        method.addAnnotation("@ResponseBody");
        method.addAnnotation("@RequestMapping(value=\"/"+lowModelShortName+"/{id}\", method= RequestMethod.POST)");

        method.addBodyLine(modelType.getShortName() + " "+lowModelShortName+" = new "+modelType.getShortName() +"();");


        //primary key
        for (IntrospectedColumn introspectedKey : primaryKeyColumns){
            Parameter parameter = new Parameter(FullyQualifiedJavaType.getIntInstance(),introspectedKey.getActualColumnName());
            parameter.addAnnotation("@PathVariable(\""+introspectedKey.getActualColumnName()+"\")");
            method.addParameter(parameter);
            method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedKey.getActualColumnName())+"("+introspectedKey.getActualColumnName()+");");
        }

        for (IntrospectedColumn introspectedColumn : introspectedColumns){
            if (!tableTDProperties.isEmpty()){
                if (tableTDProperties.getProperty(introspectedColumn.getActualColumnName()) != null){
                    addUpdateParamsOnController(lowModelShortName, method, introspectedColumn);
                }
            }else{
                addUpdateParamsOnController(lowModelShortName, method, introspectedColumn);
            }
        }

        method.addBodyLine("boolean resultBoolean = "
                + StringUtility.lowFirstString(serviceType.getShortName()) +".update("+lowModelShortName+");"); //$NON-NLS-1$
        method.addBodyLine("if (resultBoolean) {return Root.getRootOKAndSimpleMsg().toJsonString();}"); //$NON-NLS-1$
        method.addBodyLine("return Root.getRootFailAndSimpleMsg().toJsonString();"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    private void addUpdateParamsOnController(String lowModelShortName, Method method, IntrospectedColumn introspectedColumn) {
        switch (isDatabaseFileds(introspectedColumn.getActualColumnName())){
            case 3:
                method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"(new Date());");
                break;
            case 5:
                method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"(\"ToBeChanged\");");
                break;
            default:
                break;
        }

        if (isDatabaseFileds(introspectedColumn.getActualColumnName()) != 0){
            return;
        }

        //not update
        Parameter parameter = new Parameter(introspectedColumn.getFullyQualifiedJavaType(),introspectedColumn.getActualColumnName());
        parameter.addAnnotation("@RequestParam(value = \""+introspectedColumn.getActualColumnName()+"\")");
        method.addParameter(parameter);

        method.addBodyLine("if ("+introspectedColumn.getActualColumnName()+" != null){");
        method.addBodyLine(""+lowModelShortName+".set"+ StringUtility.upperFirstString(introspectedColumn.getActualColumnName())+"("+introspectedColumn.getActualColumnName()+");");
        method.addBodyLine("}");
    }

    protected void addGetByPageMethod(CommentGenerator commentGenerator, TopLevelClass topLevelClass,
                                   FullyQualifiedJavaType modelType, FullyQualifiedJavaType serviceType) {
        String lowModelShortName = StringUtility.lowFirstString(modelType.getShortName());
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("get"+modelType.getShortName()+"sByPage");
        method.addAnnotation("@ResponseBody");
        method.addAnnotation("@RequestMapping(value=\"/"+lowModelShortName+"s\", method= RequestMethod.GET)");

        FullyQualifiedJavaType integerType = new FullyQualifiedJavaType(Integer.class.getName());
        Parameter parameter = new Parameter(integerType,"pageIndex");
        parameter.addAnnotation("@RequestParam(value = \"pageIndex\", required=false)");
        method.addParameter(parameter);

        method.addBodyLine("PageQuery pageQuery = new PageQuery();");
        method.addBodyLine("if (pageIndex != null){");
        method.addBodyLine("pageQuery.setPageIndex(pageIndex);");
        method.addBodyLine("}");
        method.addBodyLine("PageResult<"+modelType.getShortName()+"> pageResult = "+ StringUtility.lowFirstString(serviceType.getShortName())+".getPageList(pageQuery);" );
        method.addBodyLine("return Root.getRootOKAndSimpleMsg().setData(pageResult).toJsonString();"); //$NON-NLS-1$
        topLevelClass.addMethod(method);
    }
    private int isDatabaseFileds(String field){
        if (field.toLowerCase().equals("creationtime")){
            return 1;
        }
        if (field.toLowerCase().equals("createdtime")){
            return 2;
        }
        if (field.toLowerCase().equals("updatedtime")){
            return 3;
        }
        if (field.toLowerCase().equals("createdby")){
            return 4;
        }
        if (field.toLowerCase().equals("updatedby")){
            return 5;
        }
        return 0;
    }
}
