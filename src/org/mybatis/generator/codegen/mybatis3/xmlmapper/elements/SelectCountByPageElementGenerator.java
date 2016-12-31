package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 
 * @author Zhou Rui
 * 
 */
public class SelectCountByPageElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectCountByPageElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", "getCountByConditions")); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", //$NON-NLS-1$
                "int"));

        StringBuilder sb = new StringBuilder();
        sb.append("select count(id) from " + introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime()+"\n"); //$NON-NLS-1$
        sb.append("     where 1=1 ");
        answer.addElement(new TextElement(sb.toString()));
        parentElement.addElement(answer);
    }
}
