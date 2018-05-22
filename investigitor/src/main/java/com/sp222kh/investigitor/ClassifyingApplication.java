package com.sp222kh.investigitor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassifyingApplication {

    public static void main(String[] args) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("classified_projects.csv"), 32768);
        Iterable<CSVRecord> records = getCorpusProjects(); //getQualityCorpusProjects();

        List<Project> projects = new ArrayList<>();
        addProjectsToList(records, projects);

        List<Cluster> clusters = createQualityClusters();
        clusters.addAll(createNonQualityClusters());

        for (Project p : projects) {
            String label = "not classified";
            boolean matches = false;
            for (Cluster c : clusters) {
                if(!matches) {
                    matches = c.doesProjectMatch(p);
                    if(matches) label = c.label;
                }
            }
            out.write(p.id + ";" + label + "\n");
        }
        out.close();
    }

    private static Iterable<CSVRecord> getQualityCorpusProjects() throws IOException {
        File file = new File("quality_corpus_with_metrics.csv");
        FileReader in = new FileReader(file);
        return CSVFormat.MYSQL.withHeader(
                "id",
                "wmc",
                "dit",
                "cbo",
                "rfc",
                "noc",
                "loc")
                .withDelimiter(';')
                .withQuote('"')
                .withEscape('\\')
                .parse(in);
    }

    private static Iterable<CSVRecord> getCorpusProjects() throws IOException {
        File file = new File("projects_with_metrics.csv");
        FileReader in = new FileReader(file);
        return CSVFormat.MYSQL.withHeader(
                "id",
                "name",
                "url",
                "watchers",
                "collabs",
                "days",
                "wmc",
                "dit",
                "cbo",
                "rfc",
                "noc",
                "loc")
                .withDelimiter(';')
                .withQuote('"')
                .withEscape('\\')
                .parse(in);
    }

    private static void addProjectsToList(Iterable<CSVRecord> records, List<Project> list) {
        for (CSVRecord r : records) {
            list.add(new Project(
                    r.get("id"),
                    Double.parseDouble(r.get("wmc")),
                    Double.parseDouble(r.get("dit")),
                    Double.parseDouble(r.get("cbo")),
                    Double.parseDouble(r.get("rfc")),
                    Double.parseDouble(r.get("loc"))));
        }
    }

    private static List<Cluster> createNonQualityClusters() {
        List<Cluster> clusters = new ArrayList<>();

        clusters.add(new Cluster("non-quality", 0,
                74.3887, 36.1174, 2.2703, 0.518, 16.0486, 2.2022, 47.4461, 8.1925, 333.7265, 130.4973));
        clusters.add(new Cluster("examples", 1,
                8.6275, 5.6831, 1.3127, 0.3458, 4.8516, 2.6411, 7.6346, 4.4017, 54.0641, 25.1761));
        clusters.add(new Cluster("non-quality", 2,
                25.8903, 10.5682, 2.866, 2.1188, 13.4156, 2.6102, 24.9987, 8.0144, 149.8406, 46.0946));

        return clusters;
    }

    private static List<Cluster> createQualityClusters() {
        List<Cluster> clusters = new ArrayList<>();

        clusters.add(new Cluster("quality", 0,
                29.0031, 4.9424, 2.525, 0.7366, 5.6035, 1.6925, 17.644, 4.2628, 177.1324, 27.107));
        clusters.add(new Cluster("quality", 1,
                69.016, 33.9403, 2.7925, 0.7258, 7.5731, 2.5316, 30.5071, 5.6215, 304.2483, 77.4643));
        clusters.add(new Cluster("quality", 2,
                42.6458, 8.5074, 2.0868, 0.3604, 6.5445, 2.1162, 20.6905, 4.8322, 250.5466, 47.4217));
        clusters.add(new Cluster("quality", 3,
                17.3446, 4.4001, 2.0506, 0.3695, 5.318, 1.1292, 12.0444, 2.719, 115.6731, 32.3984));

        return clusters;
    }

    private static class Project {

        private final String id;
        private final double wmc;
        private final double dit;
        private final double cbo;
        private final double rfc;
        private final double loc;

        Project(String id, double wmc, double dit, double cbo, double rfc, double loc) {
            this.id = id;
            this.wmc = wmc;
            this.dit = dit;
            this.cbo = cbo;
            this.rfc = rfc;
            this.loc = loc;
        }
    }

    private static class Cluster {
        private final String label;
        private final int id;
        private final double wmc;
        private final double wmcstddev;
        private final double dit;
        private final double ditstddev;
        private final double cbo;
        private final double cbostddev;
        private final double rfc;
        private final double rfcstddev;
        private final double loc;
        private final double locstddev;

        Cluster(String label, int id, double wmc, double wmcstddev, double dit, double ditstddev, double cbo,
                double cbostddev, double rfc, double rfcstddev, double loc, double locstddev) {
            this.label = label;
            double x = 2.2;
            this.id = id;
            this.wmc = wmc;
            this.wmcstddev = wmcstddev * x;
            this.dit = dit;
            this.ditstddev = ditstddev * x;
            this.cbo = cbo;
            this.cbostddev = cbostddev * x;
            this.rfc = rfc;
            this.rfcstddev = rfcstddev * x;
            this.loc = loc;
            this.locstddev = locstddev * x;
        }

        public boolean doesProjectMatch(Project p) {
            return inRange(p.wmc, wmc, wmcstddev) &&
                    inRange(p.dit, dit, ditstddev) &&
                    inRange(p.cbo, cbo, cbostddev) &&
                    inRange(p.rfc, rfc, rfcstddev) &&
                    inRange(p.loc, loc, locstddev);
        }

        private boolean inRange(double a, double b, double stddev) {
            return a >= b - stddev && a <= b + stddev;
        }
    }
}
