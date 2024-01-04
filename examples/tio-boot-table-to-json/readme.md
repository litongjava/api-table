## build and run
```
docker build -t litongjava/tio-boot-table-to-json:1.0 .
docker run -dit -p 10051:10051 --name tio-boot-table-to-json litongjava/tio-boot-table-to-json:1.0
```

指定数据库连接信息
```
docker run --rm -p 10051:10051 --name tio-boot-table-to-json litongjava/tio-boot-table-to-json:1.0 /usr/java/jdk1.8.0_211/bin/java -jar tio-boot-table-to-json-1.0.jar --jdbc.url=jdbc:mysql://192.168.3.9:3307/ruoyi_vue_pro --jdbc.user=root --jdbc.pswd=robot_123456# --jdbc.showSql=true
```