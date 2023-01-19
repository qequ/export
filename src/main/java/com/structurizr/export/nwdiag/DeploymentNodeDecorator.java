package com.structurizr.export.nwdiag;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DeploymentNodeDecorator {

    private DeploymentNode wrapee;
    private List<ContainerInstanceDecorator> containerInstanceDecoratorsList;
    private  List<DeploymentNodeDecorator> deploymentNodeDecoratorChildren;

    DeploymentNodeDecorator(DeploymentNode depNode, List<ContainerInstanceDecorator> contInstancesDecorators, List<DeploymentNodeDecorator> children ) {
        this.wrapee = depNode;
        contInstancesDecorators.sort(Comparator.comparing(ContainerInstanceDecorator::getName));
        this.containerInstanceDecoratorsList = contInstancesDecorators;
        this.deploymentNodeDecoratorChildren = children;
    }

    public List<ContainerInstanceDecorator> getContainerInstanceDecorators() {
        return  this.containerInstanceDecoratorsList;
    }

    public List<DeploymentNodeDecorator> getDeploymentNodeDecoratorChildren() {
        return this.deploymentNodeDecoratorChildren;
    }

    public String getName(){
        return this.wrapee.getName();
    }

    public Set<String> getTags() {
        return this.wrapee.getTagsAsSet();
    }

    public String getInstances() {
        return this.wrapee.getInstances();
    }
}
