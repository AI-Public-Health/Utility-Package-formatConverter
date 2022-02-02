package edu.pitt.dbmi.dataset;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;

public class ConvertGENIEtoBIF {

    public static String inputFileString = "";
    public static Hashtable<String, String> codesTable;
    public static String hash = "";
    public static String inputName = "";
    public static String out = "";
    public static boolean useCodeDescriptions = false;
    public static double ita;

    public static void main(String[] args) throws Exception{
        ita = 2;
        ArrayList<String> sharedNodeList = new ArrayList<String>();
        sharedNodeList.add("MINVOL"); sharedNodeList.add("VENTALV");
        sharedNodeList.add("VENTLUNG"); sharedNodeList.add("KINKEDTUBE");


        String inputFileLoc = "C:/Users/yey5/Dropbox/00.thesis/3.experiment/simulation/model_data/";
        String inputFileName = "INTUBATION_SOURCE_NOISY1.xdsl";
        String inputFile = inputFileLoc + inputFileName;
        String format = "bif";

        // GET INPUT DATA
        System.out.println("\n------------\n Reading file... ");
        System.out.println(inputFile);
        inputFileString = FileManager.read(inputFile);


        // TRANSFORM TO BIF
        System.out.println("\n------------\n Transforming... ");
        if(format.equals("bif")){
            transformToBIF(sharedNodeList);
        }


        // CREATE OUTPUT FILE
        System.out.println("\n------------\n Creating file... ");
        String outputName = inputFileLoc+ inputFileName.replace(".xdsl","") + "ita2a."+format;
        System.out.println(outputName);
        FileManager.write(outputName, out);
        System.out.println(out);
        System.out.println("\n------------\nNew File created");
    }


    public static void transformToBIF(){

        //ADD header
        out = "<?xml version=\"1.0\"?>\n" +
                "<!-- DTD for the XMLBIF 0.3 format -->\n"+
                "<!DOCTYPE BIF [\n"+
                "<!ELEMENT BIF ( NETWORK )*>\n"+
                "<!ATTLIST BIF VERSION CDATA #REQUIRED>\n"+
                "<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n"+
                "<!ELEMENT NAME (#PCDATA)>\n"+
                "<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n"+
                "<!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n"+
                "<!ELEMENT OUTCOME (#PCDATA)>\n"+
                "<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n"+
                "<!ELEMENT FOR (#PCDATA)>\n"+
                "<!ELEMENT GIVEN (#PCDATA)>\n"+
                "<!ELEMENT TABLE (#PCDATA)>\n"+
                "<!ELEMENT PROPERTY (#PCDATA)>\n"+
                "]>\n" +
                "\n" +
                "\n" +
                "<BIF VERSION=\"0.3\">\n"+
                "<NETWORK>\n";

        String nameOfNetwork = Tools.parseXML(inputFileString, "<smile version=\"1.0\" id=\"","\" numsamples=\"1000\" discsamples=\"10000\">")[0];
        System.out.println(nameOfNetwork);
        updateOut("<NAME>" + nameOfNetwork + "</NAME>\n");
        //Create Nodes
        String[] nodeNameList = Tools.parseXML(inputFileString, "<name>", "</name>");
        ArrayList<Node> nodeList = new ArrayList<Node>();
        for (int i=0; i<nodeNameList.length; i++){
            String nodeName = nodeNameList[i];
            String start = "<cpt id=\"" + nodeName + "\">";
            String thisNodeInfo = Tools.parseXML(inputFileString, start, "</cpt>")[0];
            thisNodeInfo= thisNodeInfo.replaceAll("<state id=\"","<stateid>");
            thisNodeInfo= thisNodeInfo.replaceAll("\" />","</stateid>");
            //	System.out.println(thisNodeInfo);
            String[] nodeStates = Tools.parseXML(thisNodeInfo,"<stateid>","</stateid>");
            String parentString = "";
            if (thisNodeInfo.contains("parents")){
                parentString = Tools.parseXML(thisNodeInfo,"<parents>","</parents>")[0];
            }
            String probString = Tools.parseXML(thisNodeInfo,"<probabilities>","</probabilities>")[0];
            String[] parents = null;
            if (parentString.length()>0){
                if (parentString.contains(" ")){
                    parents = parentString.split("\\s+");
                }
                else { parents = new String[1]; parents[0] = parentString; }
            }
            String[] probs = null;
            if (probString.contains(" ")){
                probs = probString.split("\\s+");
            }
            Node oneNode = null;
            if (parentString.length()>0) {oneNode = new Node(nodeName,nodeStates,parents,probs);}
            else {oneNode = new Node(nodeName,nodeStates,probs);}
            nodeList.add(oneNode);
        }
        String classDescription="";
        for (int i=0; i<nodeList.size(); i++){
            if (nodeList.get(i).nodeName.equals("INTUBATION")) {
                classDescription = getNodeDescription(nodeList.get(i));
                System.out.println("get intubation");
            }
            else {updateOut(getNodeDescription(nodeList.get(i)));}
        }
        updateOut(classDescription);
        String classDefinition="";
        for (int i=0; i<nodeList.size(); i++){
            if (nodeList.get(i).nodeName.equals("INTUBATION")) {
                classDefinition = getNodeDefinition(nodeList.get(i));
                System.out.println("get intubation");
            }
            else {updateOut(getNodeDefinition(nodeList.get(i)));}
        }
        updateOut(classDefinition);
        updateOut("</NETWORK>\n</BIF>\n");
    }

    public static void transformToBIF(ArrayList<String> shareNodeNameList) throws Exception{

        //ADD header
        out = "<?xml version=\"1.0\"?>\n" +
                "<!-- DTD for the XMLBIF 0.3 format -->\n"+
                "<!DOCTYPE BIF [\n"+
                "<!ELEMENT BIF ( NETWORK )*>\n"+
                "<!ATTLIST BIF VERSION CDATA #REQUIRED>\n"+
                "<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n"+
                "<!ELEMENT NAME (#PCDATA)>\n"+
                "<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n"+
                "<!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n"+
                "<!ELEMENT OUTCOME (#PCDATA)>\n"+
                "<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n"+
                "<!ELEMENT FOR (#PCDATA)>\n"+
                "<!ELEMENT GIVEN (#PCDATA)>\n"+
                "<!ELEMENT TABLE (#PCDATA)>\n"+
                "<!ELEMENT PROPERTY (#PCDATA)>\n"+
                "]>\n" +
                "\n" +
                "\n" +
                "<BIF VERSION=\"0.3\">\n"+
                "<NETWORK>\n";

        String nameOfNetwork = Tools.parseXML(inputFileString, "<smile version=\"1.0\" id=\"","\" numsamples=\"1000\" discsamples=\"10000\">")[0];
        System.out.println(nameOfNetwork);
        updateOut("<NAME>" + nameOfNetwork + "</NAME>\n");
        //Create Nodes
        String[] nodeNameList = Tools.parseXML(inputFileString, "<name>", "</name>");
        ArrayList<Node> nodeList = new ArrayList<Node>();
        for (int i=0; i<nodeNameList.length; i++){
            String nodeName = nodeNameList[i];
            String start = "<cpt id=\"" + nodeName + "\">";
            String thisNodeInfo = Tools.parseXML(inputFileString, start, "</cpt>")[0];
            thisNodeInfo= thisNodeInfo.replaceAll("<state id=\"","<stateid>");
            thisNodeInfo= thisNodeInfo.replaceAll("\" />","</stateid>");
            //	System.out.println(thisNodeInfo);
            String[] nodeStates = Tools.parseXML(thisNodeInfo,"<stateid>","</stateid>");
            String parentString = "";
            if (thisNodeInfo.contains("parents")){
                parentString = Tools.parseXML(thisNodeInfo,"<parents>","</parents>")[0];
            }
            String probString = Tools.parseXML(thisNodeInfo,"<probabilities>","</probabilities>")[0];
            String[] parents = null;
            if (parentString.length()>0){
                if (parentString.contains(" ")){
                    parents = parentString.split("\\s+");
                }
                else { parents = new String[1]; parents[0] = parentString; }
            }
            String[] probs = null;
            if (probString.contains(" ")){
                probs = probString.split("\\s+");
            }
            Node oneNode = null;
            if (parentString.length()>0) {oneNode = new Node(nodeName,nodeStates,parents,probs);}
            else {oneNode = new Node(nodeName,nodeStates,probs);}
            nodeList.add(oneNode);
        }
        String classDescription="";
        for (int i=0; i<nodeList.size(); i++){
            if (nodeList.get(i).nodeName.equals("INTUBATION")) {
                classDescription = getNodeDescription(nodeList.get(i));
                System.out.println("get intubation");
            }
            else {
                updateOut(getNodeDescription(nodeList.get(i)));
            }
        }
        updateOut(classDescription);
        String classDefinition="";
        for (int i=0; i<nodeList.size(); i++){
            if (nodeList.get(i).nodeName.equals("INTUBATION")) {
                classDefinition = getNodeDefinition(nodeList.get(i));
                System.out.println("get intubation");
            }
            else {
                if (shareNodeNameList.contains(nodeList.get(i).nodeName)){
                    updateOut(getOneNoisyNodeDefinition(nodeList.get(i)));
                }
                else {
                    updateOut(getNodeDefinition(nodeList.get(i)));
                }
            }
        }
        updateOut(classDefinition);
        updateOut("</NETWORK>\n</BIF>\n");
    }


    public static String getOneNoisyNodeDefinition(Node n) throws Exception{
        System.out.println(n.nodeName);
        String s = "<DEFINITION>\n";
        s = s + "<FOR>" + n.nodeName + "</FOR>\n";
        ArrayList<String> parentList = n.parents;
        if (parentList.size()>0){
            for (int i=0; i<parentList.size(); i++){
                if (parentList.get(i).length()>0){
                    s = s + "<GIVEN>" + parentList.get(i) + "</GIVEN>\n";
                }
            }
        }
        s = s + "<TABLE>\n";
        ArrayList<String> probList = n.probs;
        ArrayList<Double> noisyProbList = new ArrayList<Double>();
        int numberValue = n.states.size();
        ArrayList<Double> oneRow = new ArrayList<Double>();
        for (int j=0; j<probList.size(); j++){
            if (oneRow.size()==numberValue) {
                ArrayList<Double> tempNoisy = addNoisy2CPT(oneRow, ita);
                noisyProbList.addAll(tempNoisy);
                oneRow = new ArrayList<Double>();
            }
            oneRow.add(Double.parseDouble(probList.get(j)));
        }
        ArrayList<Double> tempNoisy = addNoisy2CPT(oneRow, ita);
        noisyProbList.addAll(tempNoisy);
        int count = 0;
        for (int j=0; j<noisyProbList.size(); j++){
            count++;
            s = s + noisyProbList.get(j) + " ";
            if (count % numberValue ==0 && j!=noisyProbList.size()-1) {
                s = s + "\n";
            }
        }
        s = s + "\n</TABLE>\n</DEFINITION>\n";
        return s;
    }

    public static ArrayList<Double> addNoisy2CPT(ArrayList<Double> oldProbs, double ita) throws Exception{
        ArrayList<Double> newProbs = new ArrayList<Double>();
        double[] oldLogOdds = new double[oldProbs.size()-1];
        double[] newLogOdds = new double[oldLogOdds.length];
        double fraction = 1.0;
        for (int i=0; i<oldProbs.size(); i++){
            System.out.print(oldProbs.get(i)+",");
        }
        for (int i=0; i<oldLogOdds.length; i++){
            double prob = oldProbs.get(i);
            double logOdd = Math.log(prob / (fraction - prob));
            oldLogOdds[i] = logOdd;
            Random generator = new Random();
            double randomDouble = generator.nextGaussian()*ita;
            double newLogOdd = logOdd + randomDouble;
            System.out.println(logOdd + ";" +  randomDouble + ";" +newLogOdd);
            newLogOdds[i] = newLogOdd;
            fraction = fraction - prob;
        }
        fraction= Double.parseDouble(String.format("%.8g%n", fraction));
        System.out.println(oldProbs.get(oldProbs.size()-1));
        if (fraction!=oldProbs.get(oldProbs.size()-1)){
            throw new Exception("old probs do not add up to 1!");
        }
        double newFraction = 1.0;
        for (int j=0; j<oldProbs.size()-1; j++){
            double newOdd = Math.exp(newLogOdds[j]);
            double newProb = Double.parseDouble(String.format("%.8g%n", newFraction * newOdd /(1+newOdd))) ;
            newProbs.add(newProb);
            newFraction = newFraction - newProb;
        }
        newFraction= Double.parseDouble(String.format("%.8g%n", newFraction));
        newProbs.add(newFraction);
        return newProbs;
    }


    public static String getNodeDescription(Node n){
        String s = "<VARIABLE TYPE=\"nature\">\n";
        s = s + "<NAME>" + n.nodeName + "</NAME>\n";
        ArrayList<String> stateList = n.states;
        for (int i=0; i<stateList.size(); i++){
            s = s + "<OUTCOME>" + stateList.get(i) + "</OUTCOME>\n";
        }
        s = s + "</VARIABLE>\n";
        return s;
    }

    public static String getNodeDefinition(Node n){
        String s = "<DEFINITION>\n";
        s = s + "<FOR>" + n.nodeName + "</FOR>\n";
        ArrayList<String> parentList = n.parents;
        if (parentList.size()>0){
            for (int i=0; i<parentList.size(); i++){
                if (parentList.get(i).length()>0){
                    s = s + "<GIVEN>" + parentList.get(i) + "</GIVEN>\n";
                }
            }
        }
        s = s + "<TABLE>\n";
        ArrayList<String> probList = n.probs;
        int numberValue = n.states.size();
        int count = 0;
        for (int j=0; j<probList.size(); j++){
            count++;
            s = s + probList.get(j) + " ";
            if (count % numberValue ==0 && j!=probList.size()-1) {
                s = s + "\n";
            }
        }
        s = s + "\n</TABLE>\n</DEFINITION>\n";
        return s;
    }



    public static void updateOut(String s){
        out = out + s;
    }




}
