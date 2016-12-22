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
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

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

        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaServiceType();
                
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        //生成接口
        Interface interfaze = interfaceout(commentGenerator,
                myBatis3JavaMapperType);
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }

        //生成实现类
        TopLevelClass topLevelClass = geneTopLevelClass(commentGenerator,
                myBatis3JavaMapperType);

        if (context.getPlugins().modelExampleClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }


        return answer;
    }

    private TopLevelClass geneTopLevelClass(CommentGenerator commentGenerator,
                                            String myBatis3JavaMapperType){
        String cc = myBatis3JavaMapperType.substring(0, myBatis3JavaMapperType.lastIndexOf("."));
        String bb = myBatis3JavaMapperType.substring(myBatis3JavaMapperType.lastIndexOf(".")+1,myBatis3JavaMapperType.length());

        String myBatis3JavaMapperTypeDone = cc+".impl."+bb+"Impl";
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(myBatis3JavaMapperTypeDone);

        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        // add default constructor
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(type.getShortName());
        method.addBodyLine("oredCriteria = new ArrayList<Criteria>();"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        return topLevelClass;
    }

	private Interface interfaceout(CommentGenerator commentGenerator,
			String myBatis3JavaMapperType) {
        String cc = myBatis3JavaMapperType.substring(0, myBatis3JavaMapperType.lastIndexOf("."));
        String bb = myBatis3JavaMapperType.substring(myBatis3JavaMapperType.lastIndexOf(".")+1,myBatis3JavaMapperType.length());

        String myBatis3JavaMapperTypeDone = cc+"."+bb;
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

        rootInterface =cc+".BaseService<"+type.getShortName().replace("Service", "").replace("Base", "")+">";
        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

        addCountByExampleMethod(interfaze);

        return interfaze;
	}


    protected void addCountByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
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

}
