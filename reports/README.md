# Resultados de pruebas JUnit

Reportes generados el 6 de noviembre de 2026 ejecutando `mvn -B -Dstyle.color=never clean test` en una copia independiente de cada rama.

| Rama | Commit probado | Pruebas | Fallos | Errores | Omitidas |
| --- | --- | --- | --- | --- | --- |
| realRefactorized | b0e0a21650d798b526af05584378a56dff1fc19f | 60 | 0 | 0 | 0 |
| refactorized | 282b61735baa39780b4f35e6fa6c91624d37623a | 60 | 0 | 0 | 0 |

Cada carpeta contiene:

- `junit-output.txt`: salida completa de Maven, incluyendo stdout y stderr.
- `junit-results.xml`: las 14 suites XML de Maven Surefire agrupadas bajo un elemento `testsuites`, con sus casos y totales.

Entorno: Windows 11, Oracle JDK 21.0.10, Maven 3.9.16. Ambas ejecuciones finalizaron con `BUILD SUCCESS`.

El mensaje `Error al parsear cliente: ID_INVALIDO,...` es la salida esperada de la prueba que comprueba el manejo de un registro CSV corrupto; no representa una prueba fallida.
