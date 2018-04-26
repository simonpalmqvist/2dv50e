-- Information for a list of projects
SELECT
  id,
  (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) AS watchers,
  (SELECT COUNT(DISTINCT c.author_id) FROM commit c WHERE c.project_id = p.id) AS contributors,
  (SELECT MAX(created_at) FROM commit c WHERE c.project_id = p.id) - p.created_at AS active
FROM
  project p
WHERE
  id IN (789,936,935,1126);

-- Top 10 projects
SELECT
  *
FROM
  project p
ORDER BY  (SELECT COUNT(w.project_id) FROM watcher w WHERE w.project_id = p.id) DESC
LIMIT 10;

-- Number of java files
SELECT COUNT(*) FROM software_metrics;

-- Average metrics
SELECT
  AVG(cbo),
  AVG(dit),
  AVG(noc),
  AVG(nof),
  AVG(nopf),
  AVG(nosf),
  AVG(nom),
  AVG(nopm),
  AVG(nosm),
  AVG(nosi),
  AVG(rfc),
  AVG(wmc),
  AVG(loc)
FROM
  software_metrics s;

-- Average metrics per project
SELECT
  project_id,
  AVG(cbo),
  AVG(dit),
  AVG(noc),
  AVG(nof),
  AVG(nopf),
  AVG(nosf),
  AVG(nom),
  AVG(nopm),
  AVG(nosm),
  AVG(nosi),
  AVG(rfc),
  AVG(wmc),
  AVG(loc)
FROM
  software_metrics s
GROUP BY 1;
