package com.sp222kh.investigitor.tasks;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;

public class WekaImplementation {

    public static void main(String[] args) throws Exception {
        SimpleKMeans kmeans = new SimpleKMeans();

        kmeans.setSeed(10);
        kmeans.setPreserveInstancesOrder(true);
        kmeans.setNumClusters(3);

        Instances data = new Instances(new BufferedReader(new FileReader("result.arff")));

        kmeans.buildClusterer(data);

        System.out.print(kmeans.toString());

        int[] assignments = kmeans.getAssignments();

        int i=0;
        for(int clusterNum : assignments) {
            System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
            i++;
        }
    }
}
