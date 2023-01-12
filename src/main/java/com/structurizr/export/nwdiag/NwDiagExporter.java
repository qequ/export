package com.structurizr.export.nwdiag;

import com.structurizr.Workspace;
import com.structurizr.export.Diagram;
import com.structurizr.export.DiagramExporter;
import com.structurizr.export.IndentingWriter;
import com.structurizr.export.Legend;
import com.structurizr.export.plantuml.PlantUMLDiagram;
import com.structurizr.model.*;
import com.structurizr.util.StringUtils;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.ElementView;
import com.structurizr.view.View;

import java.util.*;
import java.util.Arrays;

import static java.lang.String.format;

public class NwDiagExporter implements DiagramExporter {
    private List<String> plantUMLColors = new LinkedList<String>(Arrays.asList("Red", "Peru", "White", "Crimson", "Gold", "Violet", "Yellow", "White", "Chocolate",
            "Blue", "Coral", "Tomato", "Lime", "Cyan", "Indigo", "Ivory", "LimeGreen", "Navy", "Olive"));

    private HashMap<String, String> softSysColors = new HashMap<String, String>();
    private Object frame = null;
    private boolean addContainerComponents = false;

    private HashMap<String, ArrayList<String>> tagsList = new HashMap<String, ArrayList<String>>();


    private String chooseColor() {
        String color = plantUMLColors.get(0);
        plantUMLColors.remove(0);
        return color;
    }

    private String getSoftSysColor(String name) {
        softSysColors.computeIfAbsent(name, k -> chooseColor());
        return softSysColors.get(name);
    }
    private void saveTags(Set<String> containerInstancetags, String contInstanceName) {
        for(String s: containerInstancetags) {
            tagsList.computeIfAbsent(s, k -> new ArrayList<String>());
            tagsList.get(s).add(contInstanceName);
        }
    }

    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        addContainerComponents = Boolean.parseBoolean(view.getProperties().getOrDefault("nwdiag.include_components", "false"));

        writer.writeLine(
                format("group %s {", deploymentNode.getName())
        );
        writer.writeLine(format("description = \"%s\"", deploymentNode.getName()));
        writer.indent();
    }

    protected void endDeploymentNodeBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
        addContainerComponents = false;
    }

    protected String getComponentNames(Set<Component> componentSet) {
        if (componentSet.size() == 0) {
            return  "";
        }

        String compNames = "\n\n";
        for (Component comp: componentSet) {
            compNames = compNames.concat(format("%s\n", comp.getName()));
        }
        return compNames;
    }


    protected void saveContainerInstanceTags(ContainerInstance contInstance) {
        String properName = contInstance.getContainer().getCanonicalName().split("//")[1].replaceAll("\\s+", "_");
        String[] parts = properName.split("\\.");
        String containerName = parts[1];

        Set<String> tags = contInstance.getTagsAsSet();
        saveTags(tags, containerName);
    }

    protected String createDescriptionString(String containerName, String compsNames) {
        if (compsNames == "") {
            return "";
        }
        return format(", description=\"<b>%s</b>%s\"", containerName, compsNames);

    }

    protected void writeElement(View view, Element element, IndentingWriter writer) {

        if (element instanceof ContainerInstance) {
            String properName = ((ContainerInstance) element).getContainer().getCanonicalName().split("//")[1].replaceAll("\\s+", "_");
            String[] parts =  properName.split("\\.");
            String color = getSoftSysColor(parts[0]);
            String containerName = parts[1];

            String formatString = "";
            if (addContainerComponents) {
                String compsNames = getComponentNames(((ContainerInstance) element).getContainer().getComponents());
                formatString = format("%s[color = \"%s\"%s]", containerName, color, createDescriptionString(containerName, compsNames));
            }
            else {
                formatString = format("%s[color = \"%s\"]", containerName, color);

            }

            writer.writeLine(formatString);

        }
    }


    private void write(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        List<ContainerInstance> containerInstances = new ArrayList<>(deploymentNode.getContainerInstances());
        containerInstances.sort(Comparator.comparing(ContainerInstance::getName));
        for (ContainerInstance containerInstance : containerInstances) {
            if (view.isElementInView(containerInstance)) {
                saveContainerInstanceTags(containerInstance);
            }
        }
        writeNetworks(view, writer);


        startDeploymentNodeBoundary(view, deploymentNode, writer);

        List<DeploymentNode> children = new ArrayList<>(deploymentNode.getChildren());
        children.sort(Comparator.comparing(DeploymentNode::getName));
        for (DeploymentNode child : children) {
            if (view.isElementInView(child)) {
                write(view, child, writer);

            }
        }

        List<InfrastructureNode> infrastructureNodes = new ArrayList<>(deploymentNode.getInfrastructureNodes());
        infrastructureNodes.sort(Comparator.comparing(InfrastructureNode::getName));
        for (InfrastructureNode infrastructureNode : infrastructureNodes) {
            if (view.isElementInView(infrastructureNode)) {
                writeElement(view, infrastructureNode, writer);
            }
        }

        List<SoftwareSystemInstance> softwareSystemInstances = new ArrayList<>(deploymentNode.getSoftwareSystemInstances());
        softwareSystemInstances.sort(Comparator.comparing(SoftwareSystemInstance::getName));
        for (SoftwareSystemInstance softwareSystemInstance : softwareSystemInstances) {
            if (view.isElementInView(softwareSystemInstance)) {
                writeElement(view, softwareSystemInstance, writer);
            }
        }

        for (ContainerInstance containerInstance : containerInstances) {
            if (view.isElementInView(containerInstance)) {
                writeElement(view, containerInstance, writer);
            }
        }

        endDeploymentNodeBoundary(view, writer);
    }

    protected void writeNetworks(View view, IndentingWriter writer) {
        tagsList.remove("Container Instance");
        for (Map.Entry<String, ArrayList<String>> tags: tagsList.entrySet()) {
            writer.writeLine(format("network %s {", tags.getKey()));

            for (String container: tags.getValue()) {
                writer.writeLine(format("%s;", container));
            }
            writer.writeLine("}");

        }
    }

    protected void writeHeader(View view, IndentingWriter writer) {
        String viewTitle = view.getTitle();
        if (StringUtils.isNullOrEmpty(viewTitle)) {
            viewTitle = view.getName();
        }
        writer.writeLine("@startuml " + viewTitle);
        writer.writeLine();

        writer.writeLine("<style>");
        writer.writeLine("nwdiagDiagram {");
        writer.writeLine("  group {");
        writer.writeLine("    BackGroundColor cadetblue" );
        writer.writeLine("    #LineColor black" );
        writer.writeLine("    #LineThickness 2.0" );
        writer.writeLine("    #FontSize 11" );
        writer.writeLine("    FontStyle bold" );
        writer.writeLine("    #Margin 5" );
        writer.writeLine("    #Padding 5" );
        writer.writeLine("  }" );
        writer.writeLine("}" );
        writer.writeLine("</style>");

        writer.writeLine("nwdiag {");
    }

    protected void writeFooter(View view, IndentingWriter writer) {
        writer.writeLine("}");
        writer.writeLine();
        writer.writeLine("@enduml");

    }

    protected Diagram createDiagram(View view, String definition) {
        return new PlantUMLDiagram(view, definition);
    }


    public Diagram export(DeploymentView view, Integer animationStep) {
        this.frame = animationStep;
        IndentingWriter writer = new IndentingWriter();
        writeHeader(view, writer);

        for (ElementView elementView : view.getElements()) {
            if (elementView.getElement() instanceof DeploymentNode && elementView.getElement().getParent() == null) {
                write(view, (DeploymentNode)elementView.getElement(), writer);
            }
        }

        writeFooter(view, writer);

        return createDiagram(view, writer.toString());
    }

    protected Legend createLegend(View view) {
        return null;
    }

    public Diagram export(DeploymentView view) {
        Diagram diagram = export(view, null);
        diagram.setLegend(createLegend(view));
        return diagram;
    }

    @Override
    public Collection<Diagram> export(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("A workspace must be provided.");
        }

        Collection<Diagram> diagrams = new ArrayList<>();


        for (DeploymentView view : workspace.getViews().getDeploymentViews()) {
            Diagram diagram = export(view);
            if (diagram != null) {
                diagrams.add(diagram);
            }
        }

        return diagrams;
    }
}
