Автоматизировать проверки методов сервиса https://petstore.swagger.io/#/

POST /pet
GET /pet/{petId}
PUT /pet
DELETE /pet/{petId}


Необходимо продумать минимум 4 теста (минимум по одному тесту на метод)

Дополнительные тесты приветствуются, так же можно писать тесты на негативные сценарии.



Требования:

Все тесты должны проходить
Использовать RestAssured или Feign в качестве клиента (они уже подключены в BuilderInference.gradle)