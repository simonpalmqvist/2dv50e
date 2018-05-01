-- Information for a list of projects
SELECT
  id,
  name,
  (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) AS watchers,
  (SELECT COUNT(DISTINCT c.author_id) FROM commit c WHERE c.project_id = p.id) AS contributors,
  (SELECT MAX(created_at) FROM commit c WHERE c.project_id = p.id) - p.created_at AS active,
  ROUND((SELECT AVG(wmc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS wmc_avg,
  ROUND((SELECT AVG(dit) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS dit_avg,
  ROUND((SELECT AVG(cbo) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS cbo_avg,
  ROUND((SELECT AVG(rfc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS rfc_avg,
  ROUND((SELECT AVG(noc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS noc_avg,
  ROUND((SELECT AVG(loc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS loc_avg
FROM
  project p
WHERE
  id IN (34, 147, 190, 252, 313, 340, 613, 807, 934, 937)
ORDER BY 3 DESC;

-- Top 10 projects
SELECT
  id
FROM
  project p
WHERE
  p.id in (SELECT w.project_id FROM watcher w GROUP BY 1 ORDER BY COUNT(*) DESC LIMIT 10);

-- Number of java files
SELECT COUNT(*) FROM software_metrics WHERE path NOT LIKE '%Test.java';

-- Number of java files by type
SELECT type, COUNT(*) FROM software_metrics WHERE path NOT LIKE '%Test.java' GROUP BY 1;

-- Number of lines of code
SELECT SUM(loc) FROM software_metrics WHERE path NOT LIKE '%Test.java';

-- Average metrics
SELECT
  ROUND(AVG(cbo), 2) AS CBO,
  ROUND(AVG(dit), 2) AS DIT,
  ROUND(AVG(noc), 2) AS NOC,
  ROUND(AVG(nof), 2) AS NOF,
  ROUND(AVG(nopf), 2) AS NOPF,
  ROUND(AVG(nosf), 2) AS NOSF,
  ROUND(AVG(nom), 2) AS NOM,
  ROUND(AVG(nopm), 2) AS NOPM,
  ROUND(AVG(nosm), 2) AS NOSM,
  ROUND(AVG(nosi), 2) AS NOSI,
  ROUND(AVG(rfc), 2) AS RFC,
  ROUND(AVG(wmc), 2) AS WMC,
  ROUND(AVG(loc), 2) AS LOC
FROM
  software_metrics s
WHERE
  path NOT LIKE '%Test.java';

-- Average metrics per project
SELECT
  project_id,
  ROUND(AVG(wmc), 2) AS WMC,
  ROUND(AVG(dit), 2) AS DIT,
  ROUND(AVG(cbo), 2) AS CBO,
  ROUND(AVG(rfc), 2) AS RFC,
  ROUND(AVG(noc), 2) AS NOC,
  ROUND(AVG(loc), 2) AS LOC
FROM
  software_metrics s
WHERE
  path NOT LIKE '%Test.java'
GROUP BY 1;


ROUND((SELECT AVG(wmc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS wmc_avg,
ROUND((SELECT AVG(dit) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS dit_avg,
ROUND((SELECT AVG(cbo) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS cbo_avg,
ROUND((SELECT AVG(rfc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS rfc_avg,
ROUND((SELECT AVG(noc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS noc_avg,
ROUND((SELECT AVG(loc) FROM software_metrics s WHERE s.project_id = p.id AND path NOT LIKE '%Test.java'), 2) AS loc_avg


SELECT
  id,
  name,
  (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) AS watchers,
  (SELECT COUNT(DISTINCT c.author_id) FROM commit c WHERE c.project_id = p.id) AS contributors,
  (SELECT MAX(created_at) FROM commit c WHERE c.project_id = p.id) - p.created_at AS active
FROM
  project p
WHERE
  id = 147