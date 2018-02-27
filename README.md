# 2dv50e

## Start
```
cd investigitor && mvn package
cd.. && docker-compose up --build
```
## Access db
```
docker exec -it 2dv50e_db_1 psql -U ninja -d investigitor
```

