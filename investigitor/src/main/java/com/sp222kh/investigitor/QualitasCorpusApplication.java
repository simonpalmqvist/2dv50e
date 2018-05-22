package com.sp222kh.investigitor;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKReport;
import com.sp222kh.investigitor.models.SoftwareMetrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QualitasCorpusApplication {

    private static int id = 0;
    private static BufferedWriter out;


    static {
        try {
            out = new BufferedWriter(new FileWriter("qualitasClass.csv"), 32768);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        File projects = new File(args[0]);

        for(File project : projects.listFiles()) {
            System.out.println("Collecting metrics for " + project.getName());
            CKReport report = new CK().calculate(project.getAbsolutePath());

            List<SoftwareMetrics> metrics = report.all()
                    .stream()
                    .filter(result -> !result.isError())
                    .map(result -> new SoftwareMetrics(id++, result))
                    .collect(Collectors.toList());

            for(SoftwareMetrics m : metrics) {
                writeMetrics(out, m.getProjectId(), project.getName(),
                        m.getWmc(), m.getDit(), m.getCbo(), m.getRfc(), m.getLoc());
            }

        }
    }

    private static void writeMetrics(BufferedWriter writer, long id, String project,
                                     long wmc, long dit, long cbo, long rfc, long loc) throws IOException {
        writer.write(id + "," + project + "," + wmc + "," + dit + "," + cbo + "," + rfc + "," + loc + "\n");
    }
}
