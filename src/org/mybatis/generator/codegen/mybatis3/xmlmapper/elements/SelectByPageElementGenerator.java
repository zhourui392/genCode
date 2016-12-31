package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 
 * @author Zhou Rui
 * 
 */
public class SelectByPageElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectByPageElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", "getListByConditions")); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", //$NON-NLS-1$
                introspectedTable.getBaseRecordType()));

        StringBuilder sb = new StringBuilder();
        sb.append("select * from " + introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime()+"\n"); //$NON-NLS-1$

        sb.append("     where 1=1 \n");
        sb.append("     order by id desc \n");
        sb.append("     LIMIT #{pageQuery.limit} OFFSET #{pageQuery.pageOffset}");
        answer.addElement(new TextElement(sb.toString()));
        parentElement.addElement(answer);
    }
}
