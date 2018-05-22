-- Number of java files
SELECT COUNT(*) FROM software_metrics;

-- Number of java files by type
SELECT type, COUNT(*) FROM software_metrics GROUP BY 1;

-- Number of lines of code
SELECT SUM(loc) FROM software_metrics;

-- metrics per project on quality corpus
COPY (
SELECT
  project,
  ROUND(AVG(wmc), 2) AS WMC,
  ROUND(AVG(dit), 2) AS DIT,
  ROUND(AVG(cbo), 2) AS CBO,
  ROUND(AVG(rfc), 2) AS RFC,
  ROUND(AVG(noc), 2) AS NOC,
  ROUND(AVG(loc), 2) AS LOC
FROM
  corpus
GROUP BY 1
) TO '/tmp/quality_corpus_with_metrics.csv' WITH CSV DELIMITER ';';

--metrics per project on big data corpus

COPY (
SELECT
  id,
  name,
  url,
  (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) AS watchers,
  (SELECT COUNT(DISTINCT c.author_id) FROM project_commit pc LEFT JOIN commit c ON pc.commit_id = c.id WHERE pc.project_id = p.id) AS contributors,
  (SELECT MAX(created_at) FROM project_commit pc LEFT JOIN commit c ON pc.commit_id = c.id WHERE pc.project_id = p.id) - p.created_at AS active,
  ROUND((SELECT AVG(wmc) FROM software_metrics s WHERE s.project_id = p.id), 2) AS wmc_avg,
  ROUND((SELECT AVG(dit) FROM software_metrics s WHERE s.project_id = p.id), 2) AS dit_avg,
  ROUND((SELECT AVG(cbo) FROM software_metrics s WHERE s.project_id = p.id), 2) AS cbo_avg,
  ROUND((SELECT AVG(rfc) FROM software_metrics s WHERE s.project_id = p.id), 2) AS rfc_avg,
  ROUND((SELECT AVG(noc) FROM software_metrics s WHERE s.project_id = p.id), 2) AS noc_avg,
  ROUND((SELECT AVG(loc) FROM software_metrics s WHERE s.project_id = p.id), 2) AS loc_avg
FROM
  project p
WHERE
  p.id IN (SELECT DISTINCT s.project_id FROM software_metrics s)
) TO '/tmp/projects_with_metrics.csv' With CSV DELIMITER ';';

-- Average results on quality corpus
SELECT
  ROUND(AVG(wmc), 2) AS WMC,
  ROUND(stddev_pop(wmc), 2) AS WMC_STDDEV,
  ROUND(AVG(dit), 2) AS DIT,
  ROUND(stddev_pop(dit), 2) AS DIT_STDDEV,
  ROUND(AVG(cbo), 2) AS CBO,
  ROUND(stddev_pop(cbo), 2) AS CBO_STDDEV,
  ROUND(AVG(rfc), 2) AS RFC,
  ROUND(stddev_pop(rfc), 2) AS RFC_STDDEV,
  ROUND(AVG(noc), 2) AS NOC,
  ROUND(stddev_pop(noc), 2) AS NOC_STDDEV,
  ROUND(AVG(loc), 2) AS LOC,
  ROUND(stddev_pop(loc), 2) AS LOC_STDDEV
FROM corpus;

-- Average results on big data corpus
SELECT
  ROUND(AVG(wmc), 2) AS WMC,
  ROUND(stddev_pop(wmc), 2) AS WMC_STDDEV,
  ROUND(AVG(dit), 2) AS DIT,
  ROUND(stddev_pop(dit), 2) AS DIT_STDDEV,
  ROUND(AVG(cbo), 2) AS CBO,
  ROUND(stddev_pop(cbo), 2) AS CBO_STDDEV,
  ROUND(AVG(rfc), 2) AS RFC,
  ROUND(stddev_pop(rfc), 2) AS RFC_STDDEV,
  ROUND(AVG(noc), 2) AS NOC,
  ROUND(stddev_pop(noc), 2) AS NOC_STDDEV,
  ROUND(AVG(loc), 2) AS LOC,
  ROUND(stddev_pop(loc), 2) AS LOC_STDDEV
FROM
software_metrics s;
