# Explore With Me (диплом)

---

## **Архитектура**

---

### Инфраструктура(`infra`)

* **discovery-server**: Реестр сервисов. Запускается на порту 8761.
* **config-server**: Централизованный сервер конфигураций.
* **gateway-server**: Точка входа для всех внешних клиентских запросов. Запускается на порту 8080.

### Бизнес-логика(`core`)

* **category-service**: Управление категориями событий.
* **comment-service**: Управление комментариями (дополнительная функциональность).
* **compilation-service**: Управление подборками событий.
* **event-service**: Управление событиями.
* **request-service**: Управление заявками на участие в событиях.
* **user-service**: Управление пользователями.

### Статистика(`stats`)

* **stats-server**: Сервис сбора и отдачи статистики просмотров событий. Взаимодействие через **stats-client**.

---

## **Конфигурация**

---

### Корневая директория конфигураций: `infra/config-repo/`

### Структура директории:

* `core/` — конфигурации для сервисов бизнес-логики.
* `infra/` — конфигурации для инфраструктурных сервисов.
* `stats/` — конфигурация для сервиса статистики.
* `application.yaml` — базовые настройки сервисов.

---

## **Внутренний API**

---

Взаимодействие между модулями осуществляется при помощи OpenFeign (маршруты */internal*).

### category-service:

* *GET /internal/categories/{catId}* - получение категории по id.

### event-service:

* *GET /internal/events/{eventId}* - получение события по id.

* *GET /internal/events/exists/categories/{catId}* - проверка существования события категории с указанным id.

* *GET /internal/events?ids=?* - получение списка событий по списку id.

* *PUT /internal/events/{eventId}/requests/increment* - увеличение количества подтвержденных запросов события по id.

* *PUT /internal/events/{eventId}/requests/decrement* - уменьшение количества подтвержденных запросов события по id.

### request-service:

* *GET /internal/requests/{eventId}* - получение списка запросов на событие по id.

* *PUT /internal/requests?ids=?&status=?* - изменение статуса запросов по списку их id.

### user-service:

* *GET /internal/users/exists/{userId}* - проверка существования пользователя по id.

* *GET /internal/users/{userId}* - получение пользователя по id.

---

## **Внешний API**

---

* [Спецификация основного сервиса](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)
* [Спецификация сервиса статистики](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json)

---