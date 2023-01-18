package com.structurizr.export.nwdiag;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DeploymentNodeDecorator {

    private DeploymentNode wrapee;
    private List<ContainerInstanceDecorator> containerInstanceDecoratorsList;

    DeploymentNodeDecorator(DeploymentNode depNode, List<ContainerInstanceDecorator> contInstancesDecorators ) {
        this.wrapee = depNode;

        contInstancesDecorators.sort(Comparator.comparing(ContainerInstanceDecorator::getName));
        this.containerInstanceDecoratorsList = contInstancesDecorators;
    }

    public List<ContainerInstanceDecorator> getContainerInstanceDecorators() {
        return  this.containerInstanceDecoratorsList;
    }

    public String getName(){
        return this.wrapee.getName();
    }
}
