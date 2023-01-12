package com.structurizr.export.nwdiag;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractExporterTests;
import com.structurizr.export.Diagram;

import com.structurizr.util.WorkspaceUtils;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

public class NwDiagExporterTests extends AbstractExporterTests {
    @Test
    public void test_NwDiagramExporterCreation() throws Exception{

        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-nwdiag-workspace.json"));


        NwDiagExporter exporter = new NwDiagExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);

        for (Diagram d: diagrams) {
            assertEquals(
                    "@startuml Deployment - oneNode\n" +
                            "\n" +
                            "<style>\n" +
                            "nwdiagDiagram {\n" +
                            "  group {\n" +
                            "    BackGroundColor cadetblue\n" +
                            "    #LineColor black\n" +
                            "    #LineThickness 2.0\n" +
                            "    #FontSize 11\n" +
                            "    FontStyle bold\n" +
                            "    #Margin 5\n" +
                            "    #Padding 5\n" +
                            "  }\n" +
                            "}\n" +
                            "</style>\n" +
                            "nwdiag {\n" +
                            "network anet {\n" +
                            "Container_11;\n" +
                            "Container_21;\n" +
                            "}\n" +
                            "network bnet {\n" +
                            "Container_12;\n" +
                            "Container_22;\n" +
                            "}\n" +
                            "group UnNode {\n" +
                            "description = \"UnNode\"\n" +
                            "  Container_11[color = \"Red\"]\n" +
                            "  Container_12[color = \"Red\"]\n" +
                            "  Container_21[color = \"Peru\"]\n" +
                            "  Container_22[color = \"Peru\"]\n" +
                            "}\n" +
                            "\n" +
                            "}\n" +
                            "\n" +
                            "@enduml",
                    d.getDefinition()
            );


        }

    }

    @Test
    public void test_NwDiagramExporterCreationPropertiesComponents() throws Exception {

        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-nwdiag-components-workspace.json"));


        NwDiagExporter exporter = new NwDiagExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);


        for (Diagram d: diagrams) {
            assertEquals(
               "@startuml Deployment - oneNode\n" +
                       "\n" +
                       "<style>\n" +
                       "nwdiagDiagram {\n" +
                       "  group {\n" +
                       "    BackGroundColor cadetblue\n" +
                       "    #LineColor black\n" +
                       "    #LineThickness 2.0\n" +
                       "    #FontSize 11\n" +
                       "    FontStyle bold\n" +
                       "    #Margin 5\n" +
                       "    #Padding 5\n" +
                       "  }\n" +
                       "}\n" +
                       "</style>\n" +
                       "nwdiag {\n" +
                       "network anet {\n" +
                       "Container_11;\n" +
                       "Container_21;\n" +
                       "}\n" +
                       "network bnet {\n" +
                       "Container_12;\n" +
                       "Container_22;\n" +
                       "}\n" +
                       "group UnNode {\n" +
                       "description = \"UnNode\"\n" +
                       "  Container_11[color = \"Red\", description=\"<b>Container_11</b>\\n\\nComponent 112\\nComponent 111\\n\"]\n" +
                       "  Container_12[color = \"Red\"]\n" +
                       "  Container_21[color = \"Peru\"]\n" +
                       "  Container_22[color = \"Peru\"]\n" +
                       "}\n" +
                       "\n" +
                       "}\n" +
                       "\n" +
                       "@enduml",
                    d.getDefinition()
            );


        }
    }
}
