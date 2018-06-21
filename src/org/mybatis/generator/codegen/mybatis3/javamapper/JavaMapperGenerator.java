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

package org.mybatis.generator.codegen.mybatis3.javamapper;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zr.Commons;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.CountByExampleMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.DeleteByPrimaryKeyMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertSelectiveMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByPrimaryKeyMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeySelectiveMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeyWithBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeyWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * @author Jeff Butler
 * 
 */
public class JavaMapperGenerator extends AbstractJavaClientGenerator {

    /**
     * 
     */
    public JavaMapperGenerator() {
        super(true);
    }

    public JavaMapperGenerator(boolean requiresMatchedXMLGenerator) {
        super(requiresMatchedXMLGenerator);
    }
    
    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();
        String baseRecordType = introspectedTable.getBaseRecordType();

        List<CompilationUnit> answer = new ArrayList<>();

//		Interface interfaze2 = interfacebase(commentGenerator, myBatis3JavaMapperType);
//
//        if (context.getPlugins().clientGenerated(interfaze2, null,
//                introspectedTable)) {
//            answer.add(interfaze2);
//        }
//
//        List<CompilationUnit> extraCompilationUnits2 = getExtraCompilationUnits();
//        if (extraCompilationUnits2 != null) {
//            answer.addAll(extraCompilationUnits2);
//        }

        Interface interfaze = interfaceout(commentGenerator,
                myBatis3JavaMapperType, baseRecordType);

        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }


        return answer;
    }

	private Interface interfaceout(CommentGenerator commentGenerator,
			String myBatis3JavaMapperType,String baseRecord) {
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
        		myBatis3JavaMapperType);
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(
                baseRecord);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface =  Commons.NAME_BASE_MAPPER+"<"+type.getShortName().replace("Mapper", "").replace("Base", "")+">";
        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = new HashSet<>();
            fullyQualifiedJavaTypes.add(fqjt);
//            fullyQualifiedJavaTypes.add(FullyQualifiedJavaType.getNewListInstance());
            fullyQualifiedJavaTypes.add(baseRecordType);
            interfaze.addImportedTypes(fullyQualifiedJavaTypes);
        }

        new MapperJavaDoc().getAll(interfaze);

		return interfaze;
	}

	private Interface interfacebase(CommentGenerator commentGenerator,
			String myBatis3JavaMapperType) {
		String cc = myBatis3JavaMapperType.substring(0, myBatis3JavaMapperType.lastIndexOf("."));
		String bb = myBatis3JavaMapperType.substring(myBatis3JavaMapperType.lastIndexOf(".")+1,myBatis3JavaMapperType.length());
		
		String myBatis3JavaMapperTypeDone = cc+".base.Base"+bb;
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
        		myBatis3JavaMapperTypeDone);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
            .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        rootInterface = Commons.NAME_BASE_MAPPER+"<"+type.getShortName().replace("Mapper", "").replace("Base", "")+">";
        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
        
        addCountByExampleMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        addInsertMethod(interfaze);
        addInsertSelectiveMethod(interfaze);
        addSelectByPrimaryKeyMethod(interfaze);
        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

		return interfaze;
	}

    private void addGetByPageMethod(Interface interfaze,FullyQualifiedJavaType mapperType,
                                    FullyQualifiedJavaType modelType) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(Commons.NAME_PAGE_QUERY);
        FullyQualifiedJavaType returnFqj = new FullyQualifiedJavaType("List<"+modelType.getShortName()+">");
        method.setReturnType(returnFqj);
        method.setName("getListByConditions");
        Parameter  parameter = new Parameter(pageQueryType, "pageQuery");
        parameter.addAnnotation("@Param(\"pageQuery\")");
        method.addParameter(parameter);
        interfaze.addMethod(method);
    }
    private void addGetByPageCountMethod(Interface interfaze) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("getCountByConditions");
        interfaze.addMethod(method);
    }

    protected void addCountByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
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

    public List<CompilationUnit> getExtraCompilationUnits() {
        return null;
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLMapperGenerator();
    }
}
