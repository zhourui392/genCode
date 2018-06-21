package org.mybatis.generator.doc;

import com.zr.AuthorInfo;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 公共Java的注释模板
 * @author zhourui
 * @date 2018-6-21
 */
public abstract class AbstractCommonJavaDoc {
    public void getAll(TopLevelClass topLevelClass){
        topLevelClass.addJavaDocLine(getFirst());
        if (getEach() != null && getEach().length > 0){
            for (String each : getEach()){
                topLevelClass.addJavaDocLine(" * " + each);
            }
        }
        topLevelClass.addJavaDocLine(" * @author "+ AuthorInfo.author);
        topLevelClass.addJavaDocLine(" * @date "+ AuthorInfo.getDate());
        topLevelClass.addJavaDocLine(getEnd());
    }

    public String getFirst(){
        return "/**";
    }
    public String getEnd(){
        return " */";
    }

    /**
     * 子类中实现每一行的注释
     * @return
     */
    public abstract String[] getEach();
}
