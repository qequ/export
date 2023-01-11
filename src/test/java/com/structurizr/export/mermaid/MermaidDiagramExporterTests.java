package com.structurizr.export.mermaid;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractExporterTests;
import com.structurizr.export.Diagram;
import com.structurizr.model.*;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MermaidDiagramExporterTests extends AbstractExporterTests {

    @Test
    public void test_BigBankPlcExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-36141-workspace.json"));
        MermaidDiagramExporter exporter = new MermaidDiagramExporter();

        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(7, diagrams.size());

        Diagram diagram = diagrams.stream().filter(d -> d.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-SystemLandscape.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("SystemContext")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-SystemContext.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("Containers")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-Containers.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("Components")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-Components.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("SignIn")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-SignIn.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("DevelopmentDeployment")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-DevelopmentDeployment.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("LiveDeployment")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-LiveDeployment.mmd"));
        assertEquals(expected, diagram.getDefinition());

        // and the sequence diagram version
        workspace.getViews().getConfiguration().addProperty(exporter.MERMAID_SEQUENCE_DIAGRAM_PROPERTY, "true");
        diagrams = exporter.export(workspace);
        diagram = diagrams.stream().filter(d -> d.getKey().equals("SignIn")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/36141-SignIn-sequence.mmd"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_AmazonWebServicesExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-54915-workspace.json"));
        ThemeUtils.loadThemes(workspace);
        workspace.getViews().getDeploymentViews().iterator().next().enableAutomaticLayout(AutomaticLayout.RankDirection.LeftRight, 300, 300);

        MermaidDiagramExporter exporter = new MermaidDiagramExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(1, diagrams.size());

        Diagram diagram = diagrams.stream().findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/54915-AmazonWebServicesDeployment.mmd"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_GroupsExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/groups.json"));
        ThemeUtils.loadThemes(workspace);

        MermaidDiagramExporter exporter = new MermaidDiagramExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(3, diagrams.size());

        Diagram diagram = diagrams.stream().filter(md -> md.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/groups-SystemLandscape.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("Containers")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/groups-Containers.mmd"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("Components")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/mermaid/groups-Components.mmd"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_renderContainerDiagramWithExternalContainers() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");

        container1.uses(container2, "Uses");

        ContainerView containerView = workspace.getViews().createContainerView(softwareSystem1, "Containers", "");
        containerView.add(container1);
        containerView.add(container2);

        containerView.setExternalSoftwareSystemBoundariesVisible(true);
        Diagram diagram = new MermaidDiagramExporter().export(containerView);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [Software System 1 - Containers]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    subgraph 1 [Software System 1]\n" +
                "      style 1 fill:#ffffff,stroke:#444444,color:#444444\n" +
                "\n" +
                "      2[\"<div style='font-weight: bold'>Container 1</div><div style='font-size: 70%; margin-top: 0px'>[Container]</div>\"]\n" +
                "      style 2 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    subgraph 3 [Software System 2]\n" +
                "      style 3 fill:#ffffff,stroke:#cccccc,color:#cccccc\n" +
                "\n" +
                "      4[\"<div style='font-weight: bold'>Container 2</div><div style='font-size: 70%; margin-top: 0px'>[Container]</div>\"]\n" +
                "      style 4 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    2-. \"<div>Uses</div><div style='font-size: 70%'></div>\" .->4\n" +
                "  end", diagram.getDefinition());

        containerView.setExternalSoftwareSystemBoundariesVisible(false);
        diagram = new MermaidDiagramExporter().export(containerView);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [Software System 1 - Containers]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    subgraph 1 [Software System 1]\n" +
                "      style 1 fill:#ffffff,stroke:#444444,color:#444444\n" +
                "\n" +
                "      2[\"<div style='font-weight: bold'>Container 1</div><div style='font-size: 70%; margin-top: 0px'>[Container]</div>\"]\n" +
                "      style 2 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    4[\"<div style='font-weight: bold'>Container 2</div><div style='font-size: 70%; margin-top: 0px'>[Container]</div>\"]\n" +
                "    style 4 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "\n" +
                "    2-. \"<div>Uses</div><div style='font-size: 70%'></div>\" .->4\n" +
                "  end", diagram.getDefinition());
    }

    @Test
    public void test_renderComponentDiagramWithExternalComponents() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        Component component1 = container1.addComponent("Component 1");
        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");
        Component component2 = container2.addComponent("Component 2");

        component1.uses(component2, "Uses");

        ComponentView componentView = workspace.getViews().createComponentView(container1, "Components", "");
        componentView.add(component1);
        componentView.add(component2);

        componentView.setExternalSoftwareSystemBoundariesVisible(true);
        Diagram diagram = new MermaidDiagramExporter().export(componentView);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [Software System 1 - Container 1 - Components]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    subgraph 2 [Container 1]\n" +
                "      style 2 fill:#ffffff,stroke:#444444,color:#444444\n" +
                "\n" +
                "      3[\"<div style='font-weight: bold'>Component 1</div><div style='font-size: 70%; margin-top: 0px'>[Component]</div>\"]\n" +
                "      style 3 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    subgraph 5 [Container 2]\n" +
                "      style 5 fill:#ffffff,stroke:#cccccc,color:#cccccc\n" +
                "\n" +
                "      6[\"<div style='font-weight: bold'>Component 2</div><div style='font-size: 70%; margin-top: 0px'>[Component]</div>\"]\n" +
                "      style 6 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    3-. \"<div>Uses</div><div style='font-size: 70%'></div>\" .->6\n" +
                "  end", diagram.getDefinition());

        componentView.setExternalSoftwareSystemBoundariesVisible(false);
        diagram = new MermaidDiagramExporter().export(componentView);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [Software System 1 - Container 1 - Components]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    subgraph 2 [Container 1]\n" +
                "      style 2 fill:#ffffff,stroke:#444444,color:#444444\n" +
                "\n" +
                "      3[\"<div style='font-weight: bold'>Component 1</div><div style='font-size: 70%; margin-top: 0px'>[Component]</div>\"]\n" +
                "      style 3 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    6[\"<div style='font-weight: bold'>Component 2</div><div style='font-size: 70%; margin-top: 0px'>[Component]</div>\"]\n" +
                "    style 6 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "\n" +
                "    3-. \"<div>Uses</div><div style='font-size: 70%'></div>\" .->6\n" +
                "  end", diagram.getDefinition());
    }

    @Test
    public void test_renderGroupStyles() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addPerson("User 1").setGroup("Group 1");
        workspace.getModel().addPerson("User 2").setGroup("Group 2");
        workspace.getModel().addPerson("User 3").setGroup("Group 3");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "");
        view.addDefaultElements();

        workspace.getViews().getConfiguration().getStyles().addElementStyle("Group:Group 1").color("#111111");
        workspace.getViews().getConfiguration().getStyles().addElementStyle("Group:Group 2").color("#222222");

        MermaidDiagramExporter exporter = new MermaidDiagramExporter();
        Diagram diagram = exporter.export(view);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [System Landscape]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    subgraph group1 [Group 1]\n" +
                "      style group1 fill:#ffffff,stroke:#111111,color:#111111\n" +
                "\n" +
                "      1[\"<div style='font-weight: bold'>User 1</div><div style='font-size: 70%; margin-top: 0px'>[Person]</div>\"]\n" +
                "      style 1 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    subgraph group2 [Group 2]\n" +
                "      style group2 fill:#ffffff,stroke:#222222,color:#222222\n" +
                "\n" +
                "      2[\"<div style='font-weight: bold'>User 2</div><div style='font-size: 70%; margin-top: 0px'>[Person]</div>\"]\n" +
                "      style 2 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    subgraph group3 [Group 3]\n" +
                "      style group3 fill:#ffffff,stroke:#cccccc,color:#cccccc\n" +
                "\n" +
                "      3[\"<div style='font-weight: bold'>User 3</div><div style='font-size: 70%; margin-top: 0px'>[Person]</div>\"]\n" +
                "      style 3 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "\n" +
                "  end", diagram.getDefinition());

        workspace.getViews().getConfiguration().getStyles().addElementStyle("Group").color("#aabbcc");

        diagram = exporter.export(view);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [System Landscape]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    subgraph group4 [Group 1]\n" +
                "      style group4 fill:#ffffff,stroke:#111111,color:#111111\n" +
                "\n" +
                "      1[\"<div style='font-weight: bold'>User 1</div><div style='font-size: 70%; margin-top: 0px'>[Person]</div>\"]\n" +
                "      style 1 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    subgraph group5 [Group 2]\n" +
                "      style group5 fill:#ffffff,stroke:#222222,color:#222222\n" +
                "\n" +
                "      2[\"<div style='font-weight: bold'>User 2</div><div style='font-size: 70%; margin-top: 0px'>[Person]</div>\"]\n" +
                "      style 2 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "    subgraph group6 [Group 3]\n" +
                "      style group6 fill:#ffffff,stroke:#aabbcc,color:#aabbcc\n" +
                "\n" +
                "      3[\"<div style='font-weight: bold'>User 3</div><div style='font-size: 70%; margin-top: 0px'>[Person]</div>\"]\n" +
                "      style 3 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    end\n" +
                "\n" +
                "\n" +
                "  end", diagram.getDefinition());
    }

    @Test
    public void test_renderCustomView() {
        Workspace workspace = new Workspace("Name", "Description");
        Model model = workspace.getModel();

        CustomElement a = model.addCustomElement("A");
        CustomElement b = model.addCustomElement("B", "Custom", "Description");
        a.uses(b, "Uses");

        CustomView view = workspace.getViews().createCustomView("key", "Title", "Description");
        view.addDefaultElements();

        Diagram diagram = new MermaidDiagramExporter().export(view);
        assertEquals("graph TB\n" +
                "  linkStyle default fill:#ffffff\n" +
                "\n" +
                "  subgraph diagram [Title]\n" +
                "    style diagram fill:#ffffff,stroke:#ffffff\n" +
                "\n" +
                "    1[\"<div style='font-weight: bold'>A</div><div style='font-size: 70%; margin-top: 0px'></div>\"]\n" +
                "    style 1 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "    2[\"<div style='font-weight: bold'>B</div><div style='font-size: 70%; margin-top: 0px'>[Custom]</div><div style='font-size: 80%; margin-top:10px'>Description</div>\"]\n" +
                "    style 2 fill:#dddddd,stroke:#9a9a9a,color:#000000\n" +
                "\n" +
                "    1-. \"<div>Uses</div><div style='font-size: 70%'></div>\" .->2\n" +
                "  end", diagram.getDefinition());
    }

}