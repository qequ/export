package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.model.*;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.RelationshipView;
import com.structurizr.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class NwDiagramExporter extends AbstractPlantUMLExporter{

    private HashMap<String, ArrayList<String>> tagsList = new HashMap<String, ArrayList<String>>();

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);
        writer.writeLine("nwdiag {");
    }

    @Override
    protected void writeFooter(View view, IndentingWriter writer) {
        for (Map.Entry<String, ArrayList<String>> tags: tagsList.entrySet()) {
            writer.writeLine(format("network %s {", tags.getKey()));

            for (String container: tags.getValue()) {
                writer.writeLine(format("%s;", container));
            }
        }

        writer.writeLine("}");
        writer.writeLine("}");
        writer.writeLine();
        super.writeFooter(view, writer);

    }

    @Override
    protected void startEnterpriseBoundary(View view, String enterpriseName, IndentingWriter writer) {

    }

    @Override
    protected void endEnterpriseBoundary(View view, IndentingWriter writer) {

    }

    @Override
    protected void startGroupBoundary(View view, String group, IndentingWriter writer) {

    }

    @Override
    protected void endGroupBoundary(View view, IndentingWriter writer) {

    }

    @Override
    protected void startSoftwareSystemBoundary(View view, SoftwareSystem softwareSystem, IndentingWriter writer) {

    }

    @Override
    protected void endSoftwareSystemBoundary(View view, IndentingWriter writer) {

    }

    @Override
    protected void startContainerBoundary(View view, Container container, IndentingWriter writer) {

    }

    @Override
    protected void endContainerBoundary(View view, IndentingWriter writer) {

    }

    @Override
    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        writer.writeLine(
                format("group %s {", deploymentNode.getName())
        );
        writer.indent();
    }

    @Override
    protected void endDeploymentNodeBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    private void saveTags(Set<String> containerInstancetags, String contInstanceName) {
        for(String s: containerInstancetags) {
            tagsList.computeIfAbsent(s, k -> new ArrayList<String>());
            tagsList.get(s).add(contInstanceName);
        }
    }

    @Override
    protected void writeElement(View view, Element element, IndentingWriter writer) {

        if (element instanceof ContainerInstance) {
            //System.out.println(element.getName());
            String idContInstance = idOf(element);

            writer.writeLine(format("%s", idContInstance));

            Set<String> tags = element.getTagsAsSet();
            saveTags(tags, idContInstance);

        }
    }

    @Override
    protected void writeRelationship(View view, RelationshipView relationshipView, IndentingWriter writer) {

    }

    @Override
    public Diagram export(DeploymentView view) {
        return super.export(view);
    }
}
