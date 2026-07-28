# Карта покрытия API

Endpoint → автоматизированные проверки для Restful Booker.

| Endpoint | Method | Покрытие | Класс / методы | Теги |
|---|---|---|---|---|
| `/ping` | GET | Health возвращает `201` | `AuthApiTest` health | `smoke`, `api` |
| `/auth` | POST | Валидные credentials → token | `AuthApiTest` valid auth | `smoke`, `contract` |
| `/auth` | POST | Невалидные credentials отклоняются | `AuthApiTest` parameterized negatives | `negative` |
| `/booking` | GET | Список id бронирований | `BookingApiTest.getBookingIds_returnsList` | `smoke`, `contract` |
| `/booking` | GET | Фильтр firstname + lastname | `...getBookingIds_withNameFilters...` | `regression` |
| `/booking` | GET | Фильтр только firstname | `...withFirstNameFilter...` | `regression` |
| `/booking` | GET | Фильтр только lastname | `...withLastNameFilter...` | `regression` |
| `/booking` | GET | Неизвестные фильтры → пустой список | `...withUnknownNameFilters...` | `negative` |
| `/booking` | POST | Создание booking | `...createBooking_returnsIdAndBody` | `smoke`, `contract` |
| `/booking` | POST | `additionalneeds = null` | `...withNullAdditionalNeeds...` | `regression` |
| `/booking` | POST | `totalprice = 0` | `...withZeroTotalPrice...` | `regression`, `contract` |
| `/booking` | POST | `depositpaid = false` + refetch | `...withDepositNotPaid...` | `regression` |
| `/booking` | POST | Пустое тело отклоняется | `...withEmptyBody...` | `negative` |
| `/booking` | POST | Битый JSON отклоняется | `...withMalformedJson...` | `negative` |
| `/booking` | POST | Неверный Content-Type отклоняется | `...withWrongContentType...` | `negative` |
| `/booking/{id}` | GET | Получение созданного booking | `...getBookingById_returnsCreatedBooking` | `smoke`, `contract` |
| `/booking/{id}` | GET | Несуществующий → `404` | `...withNonExistentId_returnsNotFound` | `negative` |
| `/booking/{id}` | PUT | Update с токеном + refetch | `...updateBooking_withToken_updatesFields` | `smoke` |
| `/booking/{id}` | PUT | Без токена → `403` | `...updateBooking_withoutToken...` | `negative` |
| `/booking/{id}` | PUT | Невалидный токен → `403` | `...updateBooking_withInvalidToken...` | `negative` |
| `/booking/{id}` | PATCH | Обновление firstname + refetch | `...partialUpdateBooking_updatesFirstname` | `smoke` |
| `/booking/{id}` | PATCH | Multi-field patch + refetch | `...partialUpdateBooking_updatesMultipleFields` | `regression` |
| `/booking/{id}` | PATCH | Без токена → `403` | `...partialUpdateBooking_withoutToken...` | `negative` |
| `/booking/{id}` | PATCH | Невалидный токен → `403` | `...partialUpdateBooking_withInvalidToken...` | `negative` |
| `/booking/{id}` | DELETE | Удаление booking | `...deleteBooking_removesBooking` | `smoke` |
| `/booking/{id}` | DELETE | Повторный delete → client error | `...deleteBooking_twice...` | `regression`, `negative` |
| `/booking/{id}` | DELETE | Несуществующий id → client error | `...deleteBooking_withNonExistentId...` | `negative` |
| `/booking/{id}` | DELETE | Без токена → `403` | `...deleteBooking_withoutToken...` | `negative` |
| `/booking/{id}` | DELETE | Невалидный токен → `403` | `...deleteBooking_withInvalidToken...` | `negative` |

## Известные особенности sandbox

- Фильтры по датам (`checkin` / `checkout`) намеренно не входят в жёсткое покрытие: публичный Restful Booker может быть eventually-consistent / flaky.
- Пустой / битый / неверный Content-Type при create проверяются как `status >= 400` (sandbox может вернуть `400` или `500`).
- Повторный delete / delete отсутствующего id: ожидаем `404` или `405` в зависимости от версии sandbox.
