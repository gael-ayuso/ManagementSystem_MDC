# Diagnóstico de Oportunidades de Refactorización - Cristopher

* **Proyecto:** Sistema de Gestión de Materiales de Construcción (`ADA7_ManagementSystem_MDC`)
* **Autor:** Cristopher Israel Cen Santana
* **Zonas analizadas:** `src/main/java/gestionmats/dao` y `src/main/java/gestionmats/services` (con observaciones adicionales en `src/main/java/gestionmats/factory`)

---

## Resumen de las 5 Oportunidades Principales (`dao` y `services`)

Se identificaron **5 oportunidades principales** dentro de `dao` y `services`, cada una en una clase/método distinto y aplicando una **técnica diferente** perteneciente a distintas familias del catálogo (*U2T2 Refactorización - Prácticas*):

| ID | Archivo / Clase | Método(s) implicado(s) | Familia | Técnica propuesta | Prioridad |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **1** | `dao/ClienteDaoCsv.java` (`ClienteDaoCsv`) | `listarTodos()` (líneas 31–59) | 1. Métodos de composición | **Extract Method** | Alta |
| **2** | `dao/OrdenCompraDaoCsv.java` (`OrdenCompraDaoCsv`) y `dao/DetalleOrdenDaoCsv.java` | `cargarDetalles(OrdenCompra)` y `guardarDetalles(OrdenCompra)` (líneas 67–86) | 2. Movimiento de funciones entre objetos | **Move Method** | Alta |
| **3** | `dao/DetalleOrdenDaoCsv.java` (`DetalleOrdenDaoCsv`) | `actualizarRecepcion(int, String, int)` (líneas 72–106) | 4. Simplificación de expresiones condicionales | **Remove Control Flag** | Media |
| **4** | `dao/DetalleOrdenDaoCsv.java` (`DetalleOrdenDaoCsv`) | `buscarPorIdOrden(int)` (líneas 31–40) y `actualizarRecepcion(...)` (líneas 77–84) | 3. Organización de datos | **Replace Magic Number with Symbolic Constant** | Media |
| **5** | `dao/AbstractDaoCsv.java` y subclases (`ProveedorDaoCsv`, `UsuarioDaoCsv`, `OrdenCompraDaoCsv`, `VentaDaoCsv`, `DetalleVentaDaoCsv`) | `generarSiguienteId()` / `obtenerUltimoId()` | 6. Manejo de la generalización | **Pull Up Method** | Alta |

*(Al final del documento se incluyen **3 oportunidades adicionales** encontradas en `dao`, `services` y `factory` —incluyendo técnicas de la Familia 5: Simplificación de llamadas a métodos) como respaldo por si se intercambia alguna al final).*

---

## Fichas Detalladas del Inventario (Etapa 1)

### Oportunidad 1

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `1` (`CRIS-01`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivo:** `src/main/java/gestionmats/dao/ClienteDaoCsv.java`<br>**Clase:** `ClienteDaoCsv`<br>**Método implicado:** `public List<Cliente> listarTodos()` (líneas 31–59) |
| **Señal encontrada** | El método `listarTodos()` realiza múltiples tareas dentro de un mismo bloque: lee todas las líneas del archivo CSV, filtra la cabecera y las líneas vacías, divide cada línea con `split(",", -1)`, instancia `Cliente`, asigna manualmente sus 7 atributos convirtiendo tipos y aplicando valores por defecto con `getDato(...)`, y captura excepciones de parseo. Mientras que la operación inversa sí está separada en `mapearACsv(Cliente c)` (línea 74), el parseo de CSV a objeto está incrustado dentro del ciclo `for`, dificultando leer el método, probar el parseo de una línea de forma aislada o reutilizarlo en otras operaciones. |
| **Técnica propuesta** | **Extract Method** *(Familia 1: Métodos de composición)* |
| **Justificación** | Extraer el bloque de conversión de una línea de texto a un objeto `Cliente` (líneas 40–56) hacia un método con nombre descriptivo (`private Cliente mapearDeCsv(String linea)`) separa la responsabilidad de iterar el archivo de la responsabilidad de construir la entidad. Esto hace visible la intención de cada bloque y alinea a `ClienteDaoCsv` con el diseño del resto de DAOs del paquete. |
| **Comportamiento que debe conservarse** | `listarTodos()` debe devolver exactamente la misma lista de objetos `Cliente` leídos desde `dataBase/clientes.csv`: omitiendo la primera línea si comienza con `"id"` y las líneas vacías, conservando las columnas vacías finales (`split(",", -1)`), asignando los mismos valores por defecto (`0`, `"Desconocido"`, `""`, `0.0`) mediante `getDato(...)` y descartando líneas con formato numérico inválido sin detener la lectura del resto del archivo. |
| **Prueba prevista** | Construir prueba unitaria pre-refactorización con JUnit (`ClienteDaoCsvTest`) que invoque el método público `listarTodos()` (y `buscarPorId()`) antes y después del cambio, verificando:<br>1. Que devuelva correctamente los objetos `Cliente` con sus 7 atributos a partir de las líneas del CSV.<br>2. Que conserve los valores por defecto cuando una línea tiene campos vacíos.<br>3. Que omita el encabezado y descarte líneas malformadas devolviendo únicamente los clientes válidos. |
| **Prioridad** | **Alta** — `listarTodos()` es invocado tanto directamente por los controladores como internamente por `buscarPorId()` y `obtenerUltimoId()`; mejorar su estructura interna facilita la lectura y mantenibilidad de la clase sin alterar su contrato público. |

Nota: Tener en cuenta la oportunidad que provocará un Cambio de cliente (Remove Setting Method: ID como final, setters de este removidos, afectando )

---

### Oportunidad 2

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `2` (`CRIS-02`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivo:** `src/main/java/gestionmats/dao/OrdenCompraDaoCsv.java` (en relación con `src/main/java/gestionmats/dao/DetalleOrdenDaoCsv.java`)<br>**Clase:** `OrdenCompraDaoCsv`<br>**Métodos implicados:** `private void cargarDetalles(OrdenCompra orden)` (líneas 67–78) y `private void guardarDetalles(OrdenCompra orden)` (líneas 80–86), junto con los atributos `rutaDetalles` (línea 14) y `productoDao` (línea 12). |
| **Señal encontrada** | `OrdenCompraDaoCsv` declara su propia ruta `private String rutaDetalles = "dataBase/DetallesOrden.csv"` y un `ProductoDaoCsv`, e implementa los métodos `cargarDetalles(OrdenCompra)` y `guardarDetalles(OrdenCompra)` que leen, separan por comas y escriben directamente sobre `DetallesOrden.csv`. Sin embargo, ya existe la clase `DetalleOrdenDaoCsv` dentro del mismo paquete `dao` cuya única responsabilidad es administrar ese archivo mediante `buscarPorIdOrden(int idOrden)` y `guardar(int idOrden, DetalleOrden d)`. Esto duplica la lógica de acceso a datos de `DetallesOrden.csv` y acopla innecesariamente a `OrdenCompraDaoCsv` con `ProductoDaoCsv` y con la estructura interna de otro archivo CSV. |
| **Técnica propuesta** | **Move Method** *(Familia 2: Movimiento de funciones entre objetos)* |
| **Justificación** | La lógica de lectura y escritura de `DetallesOrden.csv` utiliza datos y operaciones que corresponden a la responsabilidad de `DetalleOrdenDaoCsv`. Al delegar estas operaciones en una instancia de `DetalleOrdenDaoCsv` (usando `buscarPorIdOrden` y `guardar`) y retirar el manejo directo del archivo de detalles en `OrdenCompraDaoCsv`, cada DAO queda como único responsable de su propio archivo CSV y `OrdenCompraDaoCsv` deja de depender de `ProductoDaoCsv` y de `rutaDetalles`. *(Nota: Ambos archivos pertenecen a la carpeta `dao`, por lo que no invade zonas asignadas a otros miembros del equipo).* |
| **Comportamiento que debe conservarse** | Al consultar órdenes de compra (`mapearDeCsv`, `listarTodos`, `buscarPorId`), cada `OrdenCompra` debe seguir cargando exactamente la misma lista de objetos `DetalleOrden` asociados con su `Producto`, `cantidadEsperada` y `cantidadRecibida`. Al ejecutar `guardar(OrdenCompra)`, debe guardarse primero la cabecera en `OrdenesCompra.csv` y, si tuvo éxito, registrarse cada uno de sus detalles en `DetallesOrden.csv` retornando `true`. |
| **Prueba prevista** | Construir prueba unitaria pre-refactorización con JUnit (`OrdenCompraDaoCsvTest`) que invoque los métodos públicos `guardar(OrdenCompra)` y `buscarPorId(idOrden)` / `listarTodos()` antes y después del cambio, comprobando:<br>1. Que al guardar una `OrdenCompra` con una lista de `DetalleOrden` se escriban tanto en `OrdenesCompra.csv` como en `DetallesOrden.csv`.<br>2. Que al recuperar la orden mediante `buscarPorId(idOrden)`, `orden.getDetalles()` contenga exactamente los mismos productos y cantidades esperadas/recibidas. |
| **Prioridad** | **Alta** — Elimina la duplicación de responsabilidades de persistencia entre dos clases DAO y reduce el acoplamiento de `OrdenCompraDaoCsv` conservando intacto su comportamiento observable. |



---

### Oportunidad 3

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `3` (`CRIS-03`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivo:** `src/main/java/gestionmats/dao/DetalleOrdenDaoCsv.java`<br>**Clase:** `DetalleOrdenDaoCsv`<br>**Método implicado:** `public boolean actualizarRecepcion(int idOrden, String idProducto, int nuevaCantidadRecibida)` (líneas 72–106) |
| **Señal encontrada** | El método utiliza una variable booleana `boolean modificado = false;` como bandera de control para recordar si dentro del ciclo `for` se encontró la fila correspondiente a `idOrden` e `idProducto`; cuando la encuentra, actualiza la lista, asigna `modificado = true;`, hace `break;` y después del ciclo evalúa `if (modificado)` para escribir el archivo en disco y retornar `true`, o retornar `false` al final. Además, reimplementa manualmente la escritura del archivo con `BufferedWriter` y `FileWriter` (líneas 94–103) en lugar de separar o reutilizar la escritura. |
| **Técnica propuesta** | **Remove Control Flag** *(Familia 4: Simplificación de expresiones condicionales)* |
| **Justificación** | La variable `modificado` actúa únicamente como bandera de control de flujo para salir del ciclo y decidir si se guarda el archivo. Sustituir la bandera por un `return` directo (escribiendo el archivo y retornando el resultado en cuanto se actualiza la fila buscada, y dejando `return false;` al terminar el ciclo si no hubo coincidencia) hace inmediatamente visible el flujo principal y los puntos de salida del método. |
| **Comportamiento que debe conservarse** | Cuando exista una línea en `DetallesOrden.csv` cuyo `idOrden` e `idProducto` coincidan con los parámetros recibidos, debe actualizarse su columna de cantidad recibida (`datos[3]`), sobrescribirse el archivo conservando todas las demás líneas intactas y devolverse `true`. Si no existe coincidencia, el archivo no debe modificarse y debe devolverse `false`. |
| **Prueba prevista** | Construir prueba unitaria con JUnit (`DetalleOrdenDaoCsvTest`) que verifique:<br>1. Actualizar la cantidad recibida de un producto existente en una orden con múltiples productos y comprobar que solo cambie ese renglón y retorne `true`.<br>2. Intentar actualizar un `idOrden` o `idProducto` inexistente y comprobar que retorne `false` sin alterar el archivo. |
| **Prioridad** | **Media** — Simplifica el flujo de control de un método clave para la recepción de mercancía en almacén y reduce el nivel de anidamiento. |

---

### Oportunidad 4

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `4` (`CRIS-04`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivo:** `src/main/java/gestionmats/dao/DetalleOrdenDaoCsv.java`<br>**Clase:** `DetalleOrdenDaoCsv`<br>**Métodos implicados:** `public List<DetalleOrden> buscarPorIdOrden(int idOrden)` (líneas 31–40) y `public boolean actualizarRecepcion(int idOrden, String idProducto, int nuevaCantidadRecibida)` (líneas 77–84) |
| **Señal encontrada** | En ambos métodos se accede a las posiciones del arreglo `datos` mediante números literales repetidos (`datos[0]`, `datos[1]`, `datos[2]`, `datos[3]`) y se comprueba el tamaño mínimo de columnas con el literal `if (datos.length >= 4)`. El lector debe deducir o recordar que `0` es el ID de la orden, `1` el ID del producto, `2` la cantidad esperada, `3` la cantidad recibida y `4` el total de columnas del CSV. Si el orden de columnas del archivo cambia, hay que buscar y modificar manualmente cada índice numérico en varios métodos. |
| **Técnica propuesta** | **Replace Magic Number with Symbolic Constant** *(Familia 3: Organización de datos)* |
| **Justificación** | Definir constantes simbólicas privadas en la clase (`COL_ID_ORDEN = 0`, `COL_ID_PRODUCTO = 1`, `COL_CANT_ESPERADA = 2`, `COL_CANT_RECIBIDA = 3`, `MIN_COLUMNAS = 4`) comunica explícitamente el significado de cada posición del registro CSV tanto en `buscarPorIdOrden` como en `actualizarRecepcion`, evitando errores al leer o dar mantenimiento a ambos métodos. |
| **Comportamiento que debe conservarse** | `buscarPorIdOrden` y `actualizarRecepcion` deben seguir leyendo y modificando exactamente las mismas columnas del archivo `dataBase/DetallesOrden.csv` y produciendo los mismos objetos `DetalleOrden` y actualizaciones en disco. |
| **Prueba prevista** | Reutilizar y ejecutar las pruebas unitarias de `DetalleOrdenDaoCsvTest` (`buscarPorIdOrden` y `actualizarRecepcion`) verificando que las lecturas y actualizaciones de `cantidadEsperada` y `cantidadRecibida` conserven el resultado esperado antes y después del cambio. |
| **Prioridad** | **Media** — Mejora la legibilidad y mantenibilidad en una clase donde los mismos índices numéricos se usan en múltiples métodos. |

---

### Oportunidad 5

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `5` (`CRIS-05`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivos:** `src/main/java/gestionmats/dao/AbstractDaoCsv.java` y sus subclases `ProveedorDaoCsv.java` (`generarSiguienteId`, líneas 37–45), `UsuarioDaoCsv.java` (`generarSiguienteId`, líneas 85–93), `OrdenCompraDaoCsv.java` (`generarSiguienteId`, líneas 88–94), `VentaDaoCsv.java` (`obtenerUltimoId`, líneas 53–61) y `DetalleVentaDaoCsv.java` (`obtenerUltimoId`, líneas 51–59).<br>**Clases:** `AbstractDaoCsv<T>` y subclases concretas. |
| **Señal encontrada** | Cinco subclases de `AbstractDaoCsv<T>` repiten prácticamente el mismo código para recorrer `this.listarTodos()`, comparar el identificador entero de cada entidad contra un acumulador `maxId = 0` y devolver el último ID (`maxId`) o el siguiente (`maxId + 1`). Esta duplicación existe a pesar de que la superclase `AbstractDaoCsv<T>` ya define los métodos `listarTodos()` y `protected abstract String obtenerId(T entidad)`. |
| **Técnica propuesta** | **Pull Up Method** *(Familia 6: Manejo de la generalización)* |
| **Justificación** | Dado que `AbstractDaoCsv<T>` ya conoce cómo listar todas las entidades (`listarTodos()`) y cómo extraer el identificador de cualquier entidad (`obtenerId(T)`), se puede subir a la superclase el método que obtiene el ID numérico máximo (`obtenerUltimoId()` y `generarSiguienteId()`), eliminando el código duplicado en las subclases que manejan IDs enteros secuenciales. *(Nota: Todo el cambio ocurre dentro de `dao`; las firmas públicas llamadas por controladores o modelos se conservan).* |
| **Comportamiento que debe conservarse** | Para cada DAO concreto, `obtenerUltimoId()` debe seguir devolviendo el mayor ID entero registrado en su archivo CSV (o `0` si no hay registros), y `generarSiguienteId()` debe seguir devolviendo `maxId + 1` (o `1` si el archivo está vacío), manteniendo intacto el comportamiento para `ProductoDaoCsv` que utiliza IDs con prefijo `"M"`. |
| **Prueba prevista** | Construir pruebas unitarias en JUnit (`AbstractDaoCsvIdTest`) probando con archivos CSV con registros, desordenados y vacíos que `obtenerUltimoId()` y `generarSiguienteId()` en `ProveedorDaoCsv`, `UsuarioDaoCsv`, `OrdenCompraDaoCsv`, `VentaDaoCsv` y `DetalleVentaDaoCsv` devuelvan exactamente los mismos valores antes y después de subir el método. |
| **Prioridad** | **Alta** — Elimina código duplicado en 5 clases distintas de la jerarquía `dao` y centraliza la lógica de generación de identificadores en `AbstractDaoCsv`. |

---

## Oportunidades Adicionales / de Respaldo (Por si se requiere cambiar alguna)

En caso de que durante la revisión con el equipo se desee incluir la **Familia 5 (Simplificación de llamadas a métodos)**, incluir un caso de `services` o `factory`, o sustituir alguna de las anteriores, se documentan también estas 3 oportunidades encontradas:

### Oportunidad Adicional A (Familia 5 — 100% dentro de `dao` y `services`)

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `6` (`CRIS-EXTRA-01`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivo:** `src/main/java/gestionmats/dao/PinGerenteDaoCsv.java`<br>**Clase:** `PinGerenteDaoCsv`<br>**Métodos implicados:** `public boolean actualizarPin(int idUsuario, String pinNuevo)` (líneas 88–90) y `validarCualquierPin(String)` / `validarPin(int, String)` (líneas 18–57). |
| **Señal encontrada** | 1) El método `actualizarPin(int idUsuario, String pinNuevo)` tiene como única línea `return guardarPin(idUsuario, pinNuevo);` e incluso su comentario dice *"alias de guardarPin"*, siendo un método intermediario innecesario que además no es invocado en ninguna parte del sistema (**Inline Method**).<br>2) Los métodos `validarCualquierPin(String pin)` (líneas 18–34) y `validarPin(int idUsuario, String pin)` (líneas 39–57) duplican toda la lectura de `pines_gerente.csv`, el ciclo `for`, el `split(",")`, la validación `datos.length >= 2` y el bloque `try-catch`, variando únicamente en si se filtra o no por `idUsuario` (**Parameterize Method** de la Familia 5). |
| **Técnica propuesta** | **Parameterize Method** *(Familia 5: Simplificación de las llamadas a métodos)* o **Inline Method** *(Familia 1: Métodos de composición)* |
| **Justificación** | Unificar la lógica común de validación en un método parametrizado (o hacer que `validarCualquierPin` y `validarPin` reutilicen un método común que reciba un predicado o un ID opcional) elimina la duplicación de lectura y parseo del archivo de PINs sin alterar las firmas que consume `ServiciosAutorizacion`. |
| **Comportamiento que debe conservarse** | `validarCualquierPin(pin)` debe retornar `true` si el PIN coincide con el de cualquier gerente registrado y `false` en caso contrario; `validarPin(idUsuario, pin)` debe retornar `true` solo si coincide tanto el `idUsuario` como el `pin`. |
| **Prueba prevista** | Construir prueba unitaria `PinGerenteDaoCsvTest` y `ServiciosAutorizacionTest` comprobando PIN válido, PIN incorrecto, PIN vacío y líneas malformadas en el CSV. |
| **Prioridad** | **Media** — Limpia duplicación directa en la validación de seguridad utilizada por `ServiciosAutorizacion`. |

---

### Oportunidad Adicional B (Familia 5 — Involucra 1 línea en `RecepcionController.java`)

> **Aviso importante de zona:** Esta oportunidad pertenece a `DetalleOrdenDaoCsv.java` (carpeta `dao`), pero al cambiar o sobrecargar la firma del método se relaciona con su llamada en `src/main/java/gestionmats/controllers/almacenista/RecepcionController.java` (línea 246).

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `7` (`CRIS-EXTRA-02`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivo:** `src/main/java/gestionmats/dao/DetalleOrdenDaoCsv.java` (y llamada en `controllers/almacenista/RecepcionController.java`, línea 246)<br>**Clase:** `DetalleOrdenDaoCsv`<br>**Método implicado:** `public boolean actualizarRecepcion(int idOrden, String idProducto, int nuevaCantidadRecibida)` (línea 72) |
| **Señal encontrada** | Mientras que `guardar(int idOrden, DetalleOrden d)` (línea 56 de la misma clase) recibe el objeto `DetalleOrden` completo, `actualizarRecepcion` exige que el cliente descomponga el objeto `DetalleOrden` llamando a varios getters (`item.getProducto().getIdProducto()`, `item.getCantidadRecibida()`) únicamente para pasar sus valores por separado. |
| **Técnica propuesta** | **Preserve Whole Object** *(Familia 5: Simplificación de las llamadas a métodos)* |
| **Justificación** | Pasar el objeto `DetalleOrden` completo (`actualizarRecepcion(int idOrden, DetalleOrden detalle)`) hace más clara la llamada, mantiene consistencia con `guardar(int idOrden, DetalleOrden d)` y evita que los clientes tengan que extraer datos internos del objeto para pasárselos al DAO. |
| **Comportamiento que debe conservarse** | La actualización de la cantidad recibida en `DetallesOrden.csv` para la orden y el producto del `DetalleOrden` debe seguir guardando exactamente el mismo valor en disco y devolviendo `true` si se encontró el registro. |
| **Prueba prevista** | Prueba unitaria en `DetalleOrdenDaoCsvTest` verificando que `actualizarRecepcion(idOrden, detalleOrden)` actualice la columna de cantidad recibida igual que antes. |
| **Prioridad** | **Media** — Homogeniza la interfaz de `DetalleOrdenDaoCsv` y simplifica sus llamadas. |

---

### Oportunidad Adicional C (Carpeta `factory` — Familia 6: Form Template Method)

| Campo | Detalle |
| :--- | :--- |
| **Identificador** | `8` (`CRIS-EXTRA-03`) |
| **Autor** | Cristopher Israel Cen Santana |
| **Ubicación** | **Proyecto:** `ADA7_ManagementSystem_MDC`<br>**Archivos:** `src/main/java/gestionmats/factory/GeneradorRecibo.java`, `GeneradorReciboInstantaneo.java` (`generarRecibo()`, líneas 15–64) y `GeneradorReciboPedido.java` (`generarRecibo()`, líneas 20–71).<br>**Clases:** `GeneradorRecibo`, `GeneradorReciboInstantaneo`, `GeneradorReciboPedido` |
| **Señal encontrada** | Las dos subclases de `GeneradorRecibo` implementan `generarRecibo()` siguiendo exactamente la misma secuencia de pasos en el mismo orden: 1) encabezado del ticket/comprobante, 2) listado de productos y precios, 3) subtotal, descuento por puntos, descuento por promoción, total y método de pago/cambio en efectivo, y 4) pie de página. Los pasos 2 y 3 (34 líneas de código: líneas 25–58 en `GeneradorReciboInstantaneo` y líneas 32–65 en `GeneradorReciboPedido`) son idénticos carácter por carácter en ambas subclases. |
| **Técnica propuesta** | **Form Template Method** *(Familia 6: Manejo de la generalización)* |
| **Justificación** | Ambas subclases ejecutan el mismo algoritmo en el mismo orden variando únicamente el encabezado y el mensaje final. Definir la estructura general en `GeneradorRecibo.generarRecibo()` (subiendo los pasos comunes de detalle de productos y totales, y dejando métodos abstractos para el encabezado y pie de recibo) elimina más de 30 líneas duplicadas entre las dos subclases. |
| **Comportamiento que debe conservarse** | El texto devuelto por `GeneradorReciboInstantaneo.generarRecibo()` y `GeneradorReciboPedido.generarRecibo()` debe ser idéntico línea por línea al actual (incluyendo formato de moneda, descuentos condicionales y cálculo de cambio cuando el pago es `PagoEfectivo`). |
| **Prueba prevista** | Construir prueba unitaria `GeneradorReciboTest` que compare el `String` generado por `GeneradorReciboInstantaneo` y `GeneradorReciboPedido` con ventas con/sin descuentos y con pago en efectivo/tarjeta. |
| **Prioridad** | **Alta** — Es un caso ejemplar de `Form Template Method` que elimina duplicación exacta en la jerarquía `GeneradorRecibo`. |
