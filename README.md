# Многомодульный maven. Многопоточность. XML. Веб сервисы. Удаленное взаимодействие
## [Программа проекта](#Программа-проекта-1)
### [Изменения проекта (Release Notes)](ReleaseNotes.md)

### _Разработка полнофункционального многомодульного Maven проекта_
#### состоящего из 3-х веб приложений:

![image](https://cloud.githubusercontent.com/assets/13649199/23876457/ab01ff0a-084e-11e7-964f-49c90579fac9.png)

- **приложение импорта** из XML (JAXB, StAX, XPath, XSLT)
- **многопоточного почтового веб-сервиса** (JavaMail, java.util.concurrent, JAX-WS, MTOM, хендлеры авторизации, логирования и статистики) 
- **веб приложения отправки почты с вложениями**
  - по SOAP (JAX-WS, MTOM)
  - по JAX-RS (Jersey)
  - по JMS ([ActiveMQ](http://activemq.apache.org/))
  - через [AKKA](http://akka.io/)
  - используя асинхронные сервлеты 3.0
- сохранение данных в PostgreSQL используя [jDBI](http://jdbi.org/)
- миграция базы [LiquiBase](http://www.liquibase.org/)
- использование в проекте [Guava](https://github.com/google/guava/wiki), [Thymleaf](http://www.thymeleaf.org/), [Lombok](https://projectlombok.org/), [StreamEx](https://github.com/amaembo/streamex), 
[Typesafe Config](https://github.com/typesafehub/config), [Java Microbenchmark JMH](http://openjdk.java.net/projects/code-tools/jmh)