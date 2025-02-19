# java-shareit
  
Сервис для шеринга вещей.  
Приложение обеспечивает возможность рассказывать, какими вещами пользователи готовы поделиться, находить нужную вещь и брать её в аренду на какое-то время.  
Сервис не только позволяет бронировать вещь на определённые даты, но и закрывать к ней доступ на время бронирования от других желающих.  
На случай, если нужной вещи в сервисе нет, у пользователей есть возможность оставлять запросы на аренду вещи.  
  
  
REST-сервис из двух модулей с использованием Spring MVC, SpringData, Hibernate.
 

## Инструкция по развёртыванию и системные требования

Вы можете склонировать репозиторий и проверить работоспособность приложения.  
Ниже указаны характеристики проекта и зависимости, требующиеся для корректного запуска и работы приложения.  

  
Версия языка и SDK:  
* Java 21  
* Amazon Corretto 21  
  
Зависимости:  
* Spring Boot starter (web, validation, actuator, data jpa, test)
* Postgresql
* H2 (test)
* Hibernate
* Lombok
* Jakarta
* Hamcrest
* Jackson
* Jacoco
