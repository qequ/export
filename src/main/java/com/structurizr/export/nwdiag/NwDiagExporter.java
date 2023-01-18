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


    private String chooseColor() {
        if (plantUMLColors.size() == 0) {
            return "White";
        }
        String color = plantUMLColors.get(0);
        plantUMLColors.remove(0);
        return color;
    }

    private String getSoftSysColor(String name) {
        softSysColors.computeIfAbsent(name, k -> chooseColor());
        return softSysColors.get(name);
    }

    private void saveTags(Set<String> containerInstancetags, String contInstanceName, HashMap<String, ArrayList<String>> tagsList) {
        for (String s : containerInstancetags) {
            tagsList.computeIfAbsent(s, k -> new ArrayList<String>());
            tagsList.get(s).add(contInstanceName);
        }
    }

    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNodeDecorator deploymentNode, IndentingWriter writer) {
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
        List<Component> components = new ArrayList<>(componentSet);
        components.sort(Comparator.comparing(Component::getName));
        if (components.size() == 0) {
            return "";
        }

        String compNames = "\n\n";
        for (Component comp : components) {
            compNames = compNames.concat(format("%s\n", comp.getName()));
        }
        return compNames;
    }


    protected String checkRepeatedContainers(String containerName, HashMap<String, Integer> counter) {

        counter.computeIfAbsent(containerName, k -> 0);
        Integer repetitions = counter.get(containerName) + 1;
        counter.put(containerName, repetitions);

        return containerName.concat(format("_%s", repetitions.toString()));
    }


    protected void writeContainerInstance(View view, ContainerInstanceDecorator decorator, IndentingWriter writer) {
        String containerVarName  = decorator.getVarName();
        String containerName = decorator.getName();
        String color =  getSoftSysColor(decorator.getSoftwareSystemFather());

        String compsNames = "";
        if (addContainerComponents){
            compsNames = getComponentNames(decorator.getComponents());
        }

        String formatString = format("%s[color = \"%s\", description=\"<b>%s</b>%s\"]", containerVarName, color, containerName, compsNames);
        writer.writeLine(formatString);


    }

    private void write(DeploymentView view, DeploymentNodeDecorator deploymentNodeDecorator, IndentingWriter writer) {
        startDeploymentNodeBoundary(view, deploymentNodeDecorator, writer);

        for (ContainerInstanceDecorator contInstanceDecorator : deploymentNodeDecorator.getContainerInstanceDecorators()) {
            if (view.isElementInView(contInstanceDecorator.getWrapee())) {
                writeContainerInstance(view, contInstanceDecorator, writer);
            }
        }

        endDeploymentNodeBoundary(view, writer);

    }


    protected void writeNetworks(List<DeploymentNodeDecorator> depNodesDecorators, IndentingWriter writer) {
        HashMap<String, ArrayList<String>> tagsList = new HashMap<String, ArrayList<String>>();


        for (DeploymentNodeDecorator depNodeDecorator : depNodesDecorators) {
            for (ContainerInstanceDecorator contInstDecorator:  depNodeDecorator.getContainerInstanceDecorators()) {
                saveTags(contInstDecorator.getTags(), contInstDecorator.getVarName(), tagsList );
            }
        }
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
        writer.writeLine("@startuml" );
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

    protected List<ContainerInstanceDecorator> createContainerInstanceWrappers(Set<ContainerInstance> containerInstances, HashMap<String, Integer> countContNames) {
        List<ContainerInstanceDecorator> decorators = new LinkedList<ContainerInstanceDecorator>();
        for (ContainerInstance contInstance : containerInstances) {
            String properName = contInstance.getContainer().getCanonicalName().split("//")[1].replaceAll("\\s+", "_");
            String[] parts =  properName.split("\\.");
            String varName = checkRepeatedContainers(parts[1], countContNames);
            String softSysFather = parts[0];

            ContainerInstanceDecorator decorator = new ContainerInstanceDecorator(contInstance, parts[1],varName, softSysFather);

            decorators.add(decorator);
        }

        return decorators;
    }
    protected  List<DeploymentNodeDecorator> createDeploymentNodesWrappers(List<DeploymentNode> deploymentNodes) {
        HashMap<String, Integer> countContNames = new HashMap<String, Integer>();
        List<DeploymentNodeDecorator> deploymentNodesDec = new LinkedList<DeploymentNodeDecorator>();

        for (DeploymentNode depNode : deploymentNodes) {
            List<ContainerInstanceDecorator> contInstancesDecorators = createContainerInstanceWrappers(depNode.getContainerInstances(), countContNames);
            DeploymentNodeDecorator decorator = new DeploymentNodeDecorator(depNode, contInstancesDecorators);
            deploymentNodesDec.add(decorator);
        }

        return  deploymentNodesDec;
    }

    public Diagram export(DeploymentView view, Integer animationStep) {
        this.frame = animationStep;
        IndentingWriter writer = new IndentingWriter();
        List<DeploymentNode> deploymentNodes = new LinkedList<DeploymentNode>();

        writeHeader(view, writer);

        for (ElementView elementView : view.getElements()) {
            if (elementView.getElement() instanceof DeploymentNode && elementView.getElement().getParent() == null) {
                deploymentNodes.add((DeploymentNode)elementView.getElement());
            }
        }

        List<DeploymentNodeDecorator> depNodesDecorators = createDeploymentNodesWrappers(deploymentNodes);

        writeNetworks(depNodesDecorators, writer);

        for (DeploymentNodeDecorator depNodeDecorator : depNodesDecorators){
            write(view, depNodeDecorator, writer);
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
