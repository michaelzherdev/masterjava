# Многомодульный maven. Многопоточность. XML. Веб сервисы. Удаленное взаимодействие
## <a href="http://javawebinar.ru/masterjava">javaops.ru проект masterjava</a>

### _Разработка полнофункционального многомодульного Maven проекта_
- веб приложение (Tomcat, Thymleaf, jQuery)
- модуль экспорта из XML (JAXB, StAX)
- многопоточный почтовый сервис (JavaMail, java.util.concurrent.*)
- связь модулей через веб-сервисы (SOAP, JAX-WS) и по REST (JAX-RS)
- сохранение данных в RMDBS (postgresql)
- библиотеки Guava, StreamEx, Lombook, Typesafe config, jDBI

## Занятие 1 
- многопоточность.

## Занятие 2
- Разбор ДЗ (многопоточная реализация умножения матриц)
- <a href="http://openjdk.java.net/projects/code-tools/jmh/">Java Microbenchmark JMH</a> (от Алексея Шипилева)
- Обзор <a href="https://github.com/google/guava">Guava</a>
- Формат XML. Создание схемы XSD.
- Работа с XML в Java
  - JAXB, JAXP
  - StAX
  - XPath
  - XSLT

## Занятие 3
- Разбор ДЗ (работа с XML)
- Обзор <a href="https://github.com/amaembo/streamex">StreamEx</a> (от Тагира Валеева)
- Монады. flatMap
- SOA и Микросервисы
- Многомодульный Maven проект

## Занятие 4
- Разбор ДЗ (реализация структуры проекта, загрузка и разбор xml)
- Thymleaf
- Maven. Поиск и разрешение конфликтов зависимостей
- Логирование
- Выбор lightweight JDBC helper library. <a href="http://jdbi.org/">JDBI</a>
- Tomcat Class Loader. Memory Leaks

## Занятие 5
- Разбор ДЗ (реализуем модули persist, export и web)
- Конфигурирование приложения (<a href="https://github.com/typesafehub/config">Typesafe config</a>)
- Lombook

## Занятие 6
- Разбор ДЗ (доработка модели и модуля export)
- Миграция DB
- Веб-сервисы (REST/SOAP)
  - Java реализации SOAP
  - Имплементируем Mail Service
  
## Занятие 7
- Разбор ДЗ (реализация MailSender, сохранение результатов отправки)
- Стили WSDL. Кастомизация WSDL
- Публикация кастомизированного WSDL. Автогенерация.
- Деплой в Tomcat
- Создание клиента почтового сервиса

## Занятие 8
- Разбор ДЗ (отправка почты через Executor из модуля web)
- Доступ к переменным maven в приложении
- SOAP Exception. Выделение общей части схемы
- Передача двоичных данных в веб-сервисах. MTOM

## Занятие 9
- Разбор ДЗ (реализация загрузки и отправки вложений по почте)
- JAX-WS Message Context
- JAX-WS Handlers (логирование SOAP)

## Занятие 10
- Разбор ДЗ (реализация авторизации и статистики)
- JavaEE
  - CDI
  - JAX-RS. Интеграция с Jersey
  - EJB
  - JMS
 
## Занятие 11 (предварительно)
- Асинхронные сервлеты 3.x в Tomcat
- Maven Groovy cкрптинг (groovy-maven-plugin)
- AKKA
- Redis
