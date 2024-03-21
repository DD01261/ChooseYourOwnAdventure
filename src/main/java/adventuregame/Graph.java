package adventuregame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.net.http.common.Pair;

import javax.swing.*;
import adventuregame.MainMenu;

/**
 * Class for graph includes, constructor and functions for graph
 */
public class Graph {
    private ArrayList<Node> nodeList;
    private Map<Node, ArrayList<Pair<Choice, Node>>> adjacencyList;
    private Node root;
    private String jsonFilePath;

    /**m
     * Node class implementation
     * <<Subject to change>>
     */
    public class Node {
        private String description;
        private ArrayList<Choice> choiceLinks;

        public Node(String description, ArrayList<Choice> choiceLinks) {
            this.description = description;
            this.choiceLinks = choiceLinks;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Constructor for the Graph class
     */
    public Graph(String jsonFilePath) throws IOException {
        this.jsonFilePath = jsonFilePath;
        initialize();
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public Map<Node, ArrayList<Pair<Choice, Node>>> getAdjacencyList() {
        return adjacencyList;
    }

    public void setJsonFilePath(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public String getJsonFilePath() {
        return jsonFilePath;
    }

    public void initialize() throws IOException {
        adjacencyList = new HashMap<>();
        constructGraph();
    }

    private String parseFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContents.append(line).append('\n');
            }
        }
        return fileContents.toString();
    }

    private ArrayList<Node> parseGameJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<ArrayList<Node>>() {});
    }

    private void constructGraph() throws IOException {
        String gameJSON = parseFile(new File(jsonFilePath));
        nodeList = parseGameJson(gameJSON);

        for (Node n : nodeList) {
            ArrayList<Pair<Choice, Node>> graphConnections = new ArrayList<>();
            for (Choice choice : n.choiceLinks) {
                int index = choice.getChoiceLink();
                if (index == -1) {
                    // TODO: handle the terminating cases
                }
                Node nodeLink = nodeList.get(index);
                Pair<Choice, Node> choiceNodePair = new Pair<>(choice, nodeLink);
                graphConnections.add(choiceNodePair);
            }
            adjacencyList.put(n, graphConnections);
        }
    }

    /**
     * Gives the next node in the adjencencyList
     */
    public Node nextNode(Node currentNode, int choiceIndex) {
        ArrayList<Pair<Choice, Node>> choices = adjacencyList.get(currentNode);
        if (choiceIndex < 0 || choiceIndex >= choices.size()) {
            throw new IllegalArgumentException("Invalid choice index");
        }
        Pair<Choice, Node> selectedChoice = choices.get(choiceIndex);

        return selectedChoice.second;
    }

    public static void main(String[] args) {
        try {
            // display main menu
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    MainMenu mm = new MainMenu();
                    mm.createMM();
                }
            });
            
            Graph myGraph = new Graph("game.json");
            myGraph.initialize();  
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
