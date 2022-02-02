package edu.pitt.dbmi.dataset;


import java.util.ArrayList;
import java.util.Arrays;

public class Node {

    String nodeName;
    ArrayList<String> states;
    ArrayList<String> parents;
    ArrayList<String> probs;

    public Node(String name){
        nodeName = name;
        states = new ArrayList<String>();
        parents = new ArrayList<String>();
        probs = new ArrayList<String>();
    }
    public Node(String name, String[] sList, String[] pList, String[] probList){
        nodeName = name;
        states = new ArrayList<String>(Arrays.asList(sList));
        parents = new ArrayList<String>(Arrays.asList(pList));
        probs = new ArrayList<String>(Arrays.asList(probList));
    }
    public Node(String name, String[] sList, String[] probList){
        nodeName = name;
        states = new ArrayList<String>(Arrays.asList(sList));
        parents = new ArrayList<String>();
        probs = new ArrayList<String>(Arrays.asList(probList));
    }
}
