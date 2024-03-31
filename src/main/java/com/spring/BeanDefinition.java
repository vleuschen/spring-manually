package com.spring;

/**
 * Bean的定义
 */
public class BeanDefinition {

    //Bean的类型
    private Class type;

    //Bean的作用域
    private String scope;

    //Bean是否是懒加载
    private Boolean isLazy;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getLazy() {
        return isLazy;
    }

    public void setLazy(Boolean lazy) {
        isLazy = lazy;
    }
}
