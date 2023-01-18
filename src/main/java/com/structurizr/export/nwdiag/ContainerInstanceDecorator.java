package com.structurizr.export.nwdiag;

import com.structurizr.model.Component;
import com.structurizr.model.ContainerInstance;

import java.util.Set;

public class ContainerInstanceDecorator {
    private ContainerInstance wrapee;
    private String varName;
    private String name;
    private String softwareSystemFather;
    ContainerInstanceDecorator(ContainerInstance contInstance, String realName,  String varName, String softSysFather) {
        this.wrapee = contInstance;
        this.varName = varName;
        this.name = realName;
        this.softwareSystemFather = softSysFather;
    }

    public Set<String> getTags() {
        return this.wrapee.getTagsAsSet();
    }

    public ContainerInstance getWrapee() {
        return wrapee;
    }

    public String getName(){
        return name;
    }
    public String getVarName() {
        return varName;
    }

    public String getSoftwareSystemFather(){
        return softwareSystemFather;
    }

    public Set<Component> getComponents(){
        return wrapee.getContainer().getComponents();
    }
}
