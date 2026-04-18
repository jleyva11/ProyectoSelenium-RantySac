# RantySacProyecto – Selenium + TestNG + DataProvider + Allure

Framework de automatización UI para OWASP Juice Shop con:
- Selenium WebDriver (Java)
- TestNG
- DataProviders (data-driven testing)
- TestNG Listeners (captura automática de evidencias)
- Allure Report (reporte HTML)

---

## 1) Requisitos

- Java 17 (recomendado)
- Maven 3.9+
- Chrome / Edge / Firefox instalados

---

## 2) Estructura del proyecto

- `src/test/java/com/nopcommerce/pages` → Page Objects (POM)
- `src/test/java/com/nopcommerce/tests` → Tests TestNG
- `src/test/java/com/nopcommerce/dataproviders` → DataProviders (data-driven)
- `src/test/java/com/nopcommerce/listeners` → Listeners (screenshots + Allure)
- `src/test/resources/testng.xml` → Suite TestNG + listeners
- `src/test/resources/allure.properties` → Config Allure

---

## 3) DataProvider (Data-driven)

### ¿Qué es?
TestNG `@DataProvider` permite ejecutar un mismo test múltiples veces usando datasets (`Object[][]`).
Cada fila representa una ejecución independiente.

### Archivos implementados
- `LoginDataProvider.java`
- `OrderDataProvider.java`
- `RegisterDataProvider.java`

### Ejecución
Al ejecutar los tests data-driven:
- `LoginDataDrivenTest`
- `OrderDataDrivenTest`
- `RegisterDataDrivenTest`

TestNG correrá el método tantas veces como filas haya.

---

## 4) TestNG Listeners

### ¿Qué hace el listener?
- Escucha el evento de test fallido
- Captura screenshot
- Adjunta screenshot a Allure

Archivo:
- `src/test/java/com/nopcommerce/listeners/TestListener.java`

### Registro del listener
Se registró en `testng.xml` dentro de `<listeners>`.

---

## 5) Allure Report

### Resultados
Los resultados se guardan en:
- `target/allure-results`

Config:
- `src/test/resources/allure.properties`

### Generar reporte
1) Ejecutar tests:
```bash
mvn clean test
```

## 6) OWASP Juice Shop – Cómo levantar la aplicación

### 6.1 Local (recomendado si quieres máxima estabilidad)
Asegúrate de tener Juice Shop levantado en:
- `http://localhost:3000/#/`

### 6.2 Docker (intento y nota importante)
La forma estándar es usar la imagen oficial:
```bash
docker pull bkimminich/juice-shop
docker run --rm -p 3000:3000 bkimminich/juice-shop
