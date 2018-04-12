package com.sp222kh.investigitor.models;

import com.github.mauricioaniche.ck.CKNumber;

import javax.persistence.*;

@Entity
@Table(name = "software_metrics", indexes = {
        @Index(columnList = "project_id", name = "software_metrics_project_id_index")
})
public class SoftwareMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "project_id", nullable = false)
    private long projectId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1024)
    private String path;

    @Column(nullable = false)
    private String type;

    /**
     * CBO (Coupling between objects): Counts the number of dependencies a class has. The tools checks for any type used
     * in the entire class (field declaration, method return types, variable declarations, etc). It ignores dependencies
     * to Java itself (e.g. java.lang.String).
     */
    @Column(nullable = false)
    private long cbo;

    /**
     * DIT (Depth Inheritance Tree): It counts the number of "fathers" a class has. All classes have DIT at least 1
     * (everyone inherits java.lang.Object). In order to make it happen, classes must exist in the project (i.e. if a
     * class depends upon X which relies in a jar/dependency file, and X depends upon other classes, DIT is counted as 2).
     */
    @Column(nullable = false)
    private long dit;

    /**
     * NOC (Number of Children): Counts the number of children a class has.
     */
    @Column(nullable = false)
    private long noc;

    /**
     * NOF (Number of fields): Counts the number of fields in a class, no matter its modifiers.
     */
    @Column(nullable = false)
    private long nof;

    /**
     * NOPF (Number of public fields): Counts only the public fields.
     */
    @Column(nullable = false)
    private long nopf;

    /**
     * NOSF (Number of static fields): Counts only the static fields.
     */
    @Column(nullable = false)
    private long nosf;

    /**
     * NOM (Number of methods): Counts the number of methods, no matter its modifiers.
     */
    @Column(nullable = false)
    private long nom;

    /**
     * NOPM (Number of public methods): Counts only the public methods.
     */
    @Column(nullable = false)
    private long nopm;

    /**
     * NOSM (Number of static methods): Counts only the static methods.
     */
    @Column(nullable = false)
    private long nosm;

    /**
     * NOSI (Number of static invocations): Counts the number of invocations to static methods. It can only count the
     * ones that can be resolved by the JDT.
     */
    @Column(nullable = false)
    private long nosi;

    /**
     * RFC (Response for a Class): Counts the number of unique method invocations in a class. As invocations are
     * resolved via static analysis, this implementation fails when a method has overloads with same number of
     * parameters, but different types.
     */
    @Column(nullable = false)
    private long rfc;

    /**
     * WMC (Weighted Methods per Class): or McCabe's complexity.
     * It counts the number of branch instructions in a class.
     */
    @Column(nullable = false)
    private long wmc;

    /**
     * LOC (Lines of code): It counts the lines of count, ignoring empty lines.
     */
    @Column(nullable = false)
    private long loc;

    protected SoftwareMetrics() {}

    public SoftwareMetrics(long projectId, CKNumber metrics) {
        this.projectId = projectId;
        this.name = metrics.getClassName();
        this.path = metrics.getFile();
        this.type = metrics.getType();
        this.cbo = metrics.getCbo();
        this.dit = metrics.getDit();
        this.noc = metrics.getNoc();
        this.nof = metrics.getNof();
        this.nopf = metrics.getNopf();
        this.nosf = metrics.getNosf();
        this.nom = metrics.getNom();
        this.nopm = metrics.getNopm();
        this.nosm = metrics.getNosm();
        this.nosi = metrics.getNosi();
        this.rfc = metrics.getRfc();
        this.wmc = metrics.getWmc();
        this.loc = metrics.getLoc();
    }
}
