package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
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
        sb.append("select ");

        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
            sb.append("t1." + introspectedColumn.getActualColumnName() + ", ");
        }
        sb.delete(sb.length()-2,sb.length()-1);
        sb.append("\n");

        sb.append("     from " + introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime()+" t1\n"); //$NON-NLS-1$

        sb.append("     where 1=1 \n");
        sb.append("     order by id desc \n");
        sb.append("     LIMIT #{pageQuery.limit} OFFSET #{pageQuery.pageOffset}");
        answer.addElement(new TextElement(sb.toString()));
        parentElement.addElement(answer);
    }
}
