Регистрация пользователя - POST /api/clients/register в request body (x-www-form-urlencoded) client_id(имя пользователя), client_secret(пароль), grant_type="client_credentials"

Вход (получаем jwt токен) - POST /oauth2/token в request body (x-www-form-urlencoded) client_id(имя пользователя), client_secret(пароль), grant_type="client_credentials"

Управления задачами (в Authorization передаем barear jwt token)

  POST /api/tasks в request body (content="описание задачи")
  
  GET /api/tasks 

  GET /api/tasks/{id}

  PUT /api/tasks/{id} в request body (content="обновленная задача")

  DELETE /api/tasks/{id}

Запуск
mvn spring-boot:run
  
