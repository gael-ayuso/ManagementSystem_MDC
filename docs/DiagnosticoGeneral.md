# Diagnóstico General e Inventario Consolidado de Refactorización (ADA 7 — Etapa 1)

* **Proyecto:** Sistema de Gestión de Materiales de Construcción (`SDG_MDC` / `ADA7_ManagementSystem_MDC`)
* **Equipo:**
  * **Cristopher Israel Cen Santana** — Paquetes `gestionmats.dao` y `gestionmats.services`
  * **Diego** — Paquete `gestionmats.controllers` (`VendedorController`, `LoginController`, `ReciboController`)
  * **Gael** — Paquete `gestionmats.model`
  * **Daniel** — Paquete `gestionmats.controllers.gerente`
* **Meta Etapa 1:** Inventario consolidado de **20 oportunidades** diagnosticadas, registro del estado inicial de pruebas y **selección preliminar justificada de 14 refactorizaciones** para la Etapa 2.

---

## 1. Estado Inicial de las Pruebas del Proyecto (Pre-Refactorización)

De acuerdo con la guía de trabajo de la Semana 1, se verificó el estado base del proyecto antes de realizar cualquier modificación de código:

* **Compilación y ejecución del sistema legado:** El proyecto compila correctamente con Java 17 y Maven (`pom.xml`), ejecutando los flujos de Login, Punto de Venta, Control de Almacén y Panel de Gerente sobre persistencia en archivos CSV (`dataBase/*.csv`).
* **Pruebas unitarias existentes:** El proyecto legado **no cuenta inicialmente con pruebas unitarias automatizadas** (no existe el directorio `src/test/java` ni la dependencia `junit-jupiter` en `pom.xml`).
* **Plan de arnés de pruebas (Etapa 2):** Antes de aplicar cada refactorización seleccionada, se añadirá `junit-jupiter` al `pom.xml` y cada integrante construirá y ejecutará las pruebas unitarias **pre-refactorización** sobre el código actual para registrar evidencia en verde del comportamiento original, ejecutando exactamente las mismas pruebas **post-refactorización** para comprobar que el comportamiento observable se conserva intacto.

---

## 2. Inventario Consolidado del Equipo (20 Oportunidades — Etapa 1)

### 2.1 Tabla Resumen de las 20 Oportunidades Identificadas

| ID Equipo | ID Autor | Autor | Ubicación (Archivo / Clase / Método) | Familia | Técnica Propuesta | Prioridad |
| :---: | :---: | :--- | :--- | :--- | :--- | :---: |
| **01** | `CRIS-01` | Cristopher | `dao/ClienteDaoCsv.java`<br>`ClienteDaoCsv.listarTodos()` | 1. Métodos de composición | **Extract Method** | Alta |
| **02** | `CRIS-02` | Cristopher | `dao/OrdenCompraDaoCsv.java`<br>`cargarDetalles()` y `guardarDetalles()` → `DetalleOrdenDaoCsv` | 2. Movimiento de funciones | **Move Method** | Alta |
| **03** | `CRIS-03` | Cristopher | `dao/DetalleOrdenDaoCsv.java`<br>`DetalleOrdenDaoCsv.actualizarRecepcion()` | 4. Expresiones condicionales | **Remove Control Flag** | Media |
| **04** | `CRIS-04` | Cristopher | `dao/DetalleOrdenDaoCsv.java`<br>`buscarPorIdOrden()` y `actualizarRecepcion()` | 3. Organización de datos | **Replace Magic Number with Symbolic Constant** | Media |
| **05** | `CRIS-05` | Cristopher | `dao/AbstractDaoCsv.java` y 5 subclases DAO<br>`obtenerUltimoId()` / `generarSiguienteId()` | 6. Manejo de la generalización | **Pull Up Method** | Alta |
| **06** | `OPN-01` | Gael | `model/Usuario.java`, `Cliente.java`, `Producto.java`, `Proveedor.java`, `Venta.java`<br>Métodos `setId...()` | 5. Llamadas a métodos | **Remove Setting Method** | Alta |
| **07** | `OPN-02` | Gael | `model/DetalleOrden.java`<br>`DetalleOrden.calcularSubtotal()` | 3. Organización de datos | **Self Encapsulate Field** | Media |
| **08** | `OPN-03` | Gael | `model/OrdenCompra.java`<br>`OrdenCompra.getDetalles()` | 3. Organización de datos | **Encapsulate Collection** | Alta |
| **09** | `OPN-04` | Gael | `model/Producto.java`<br>`calcularStockMinimo()` y `getEstado()` | 3. Organización de datos | **Replace Magic Number with Symbolic Constant** | Media |
| **10** | `OPN-05` | Gael | `model/Cliente.java`<br>`Cliente.aplicarPuntosComoDescuento()` | 5. Llamadas a métodos | **Separate Query from Modifier** | Alta |
| **11** | `DA-01` | Daniel | `controllers/gerente/GerenteClienteController.java`<br>`abrirDialogoCliente()` | 2. Movimiento de funciones | **Extract Class** | Alta |
| **12** | `DA-02` | Daniel | `controllers/gerente/GerenteProductoFormController.java`<br>`handleGuardar()` | 4. Expresiones condicionales | **Decompose Conditional** | Media |
| **13** | `DA-03` | Daniel | `controllers/gerente/GerenteClienteController.java`<br>`setupFilters()` y `loadData()` | 3. Organización de datos | **Replace Magic Number with Symbolic Constant** | Media |
| **14** | `DA-04` | Daniel | `controllers/gerente/GerenteReporteController.java`<br>`procesarDatos()` | 2. Movimiento de funciones | **Move Method** | Alta |
| **15** | `DA-05` | Daniel | `controllers/gerente/GerenteInventarioController.java`<br>`handleAgregar()` y `handleEditar()` | 5. Llamadas a métodos | **Parameterize Method** | Media |
| **16** | `DG-01` | Diego | `controllers/VendedorController.java`<br>`handleFinalizarVenta()` (~140 líneas) | 1. Métodos de composición | **Extract Method** | Alta |
| **17** | `DG-02` | Diego | `controllers/VendedorController.java`<br>`setupTablaProductos()`, `setupTablaCarrito()`, `setupTablaHistorial()`, `handleVerDetalleVenta()` | 1. Métodos de composición / 5. Llamadas a métodos | **Extract Method** / **Parameterize Method** | Media |
| **18** | `DG-03` | Diego | `controllers/LoginController.java` y `model/RolUsuario.java`<br>`handleLogin()` | 4. Expresiones condicionales | **Replace Conditional with Polymorphism** | Media |
| **19** | `DG-04` | Diego | `controllers/VendedorController.java` y `controllers/gerente/GerenteInventarioController.java`<br>`applyFilters()` → `Producto.coincideCon()` | 2. Movimiento de funciones | **Move Method** | Media |
| **20** | `DG-05` | Diego | `controllers/VendedorController.java` y `model/DetalleVenta.java`<br>`handleAgregarAlCarrito()` → `DetalleVenta.agregarCantidad()` | 2. Movimiento de funciones | **Move Method** | Alta |

---

### 2.2 Fichas Completas de las 20 Oportunidades del Inventario

#### Bloque A — Zona `dao` y `services` (Autor: Cristopher Israel Cen Santana)

##### Oportunidad 01 (`CRIS-01`)
* **Identificador:** `01` (`CRIS-01`)
* **Autor:** Cristopher Israel Cen Santana
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/dao/ClienteDaoCsv.java`, clase `ClienteDaoCsv`, método `public List<Cliente> listarTodos()` (líneas 31–59).
* **Señal encontrada:** El método `listarTodos()` realiza múltiples tareas dentro de un mismo bloque: lee las líneas del CSV, filtra la cabecera y líneas vacías, divide cada línea con `split(",", -1)`, instancia `Cliente`, asigna sus 7 atributos aplicando valores por defecto con `getDato(...)` y captura excepciones. Mientras que `mapearACsv(Cliente c)` sí está separado (línea 74), el parseo de CSV a objeto está incrustado dentro del ciclo `for`.
* **Técnica propuesta:** **Extract Method** *(Familia 1: Métodos de composición)*.
* **Justificación:** Extraer el bloque de conversión de una línea de texto a un objeto `Cliente` (líneas 40–56) hacia `private Cliente mapearDeCsv(String linea)` separa la iteración del archivo de la construcción de la entidad y alinea a `ClienteDaoCsv` con el resto de DAOs.
* **Comportamiento que debe conservarse:** `listarTodos()` debe devolver exactamente la misma lista de objetos `Cliente` desde `dataBase/clientes.csv`, omitiendo encabezado y líneas vacías, conservando columnas vacías finales (`split(",", -1)`), asignando los mismos valores por defecto (`0`, `"Desconocido"`, `""`, `0.0`) y descartando líneas con formato numérico inválido.
* **Prueba prevista:** Prueba unitaria pre y post-refactorización con JUnit (`ClienteDaoCsvTest`) invocando `listarTodos()` y `buscarPorId()` para verificar el mapeo de los 7 atributos, valores por defecto en campos vacíos e ignorado de líneas malformadas.
* **Prioridad:** **Alta** — `listarTodos()` es la base de lectura de clientes para `buscarPorId()`, `obtenerUltimoId()` y los controladores.

##### Oportunidad 02 (`CRIS-02`)
* **Identificador:** `02` (`CRIS-02`)
* **Autor:** Cristopher Israel Cen Santana
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/dao/OrdenCompraDaoCsv.java` (en relación con `DetalleOrdenDaoCsv.java`), clase `OrdenCompraDaoCsv`, métodos `cargarDetalles(OrdenCompra)` (líneas 67–78) y `guardarDetalles(OrdenCompra)` (líneas 80–86).
* **Señal encontrada:** `OrdenCompraDaoCsv` declara `private String rutaDetalles = "dataBase/DetallesOrden.csv"` y un `ProductoDaoCsv`, e implementa internamente la lectura y escritura directa de `DetallesOrden.csv`, duplicando la responsabilidad que ya pertenece a `DetalleOrdenDaoCsv` (`buscarPorIdOrden` y `guardar`).
* **Técnica propuesta:** **Move Method** *(Familia 2: Movimiento de funciones entre objetos)*.
* **Justificación:** Delegar la carga y guardado de los detalles en `DetalleOrdenDaoCsv` ubica la responsabilidad en la clase experta de ese archivo CSV y elimina de `OrdenCompraDaoCsv` los atributos `rutaDetalles` y `productoDao`.
* **Comportamiento que debe conservarse:** Al consultar órdenes (`listarTodos`, `buscarPorId`), cada `OrdenCompra` debe cargar exactamente la misma lista de `DetalleOrden`. Al ejecutar `guardar(OrdenCompra)`, debe guardarse la cabecera en `OrdenesCompra.csv` y todos sus detalles en `DetallesOrden.csv`.
* **Prueba prevista:** Prueba unitaria pre y post-refactorización con JUnit (`OrdenCompraDaoCsvTest`) que invoque `guardar(OrdenCompra)` y `buscarPorId(idOrden)` comprobando que la orden y sus detalles persistan y se recuperen igual.
* **Prioridad:** **Alta** — Elimina duplicación de persistencia y reduce el acoplamiento entre DAOs.

##### Oportunidad 03 (`CRIS-03`)
* **Identificador:** `03` (`CRIS-03`)
* **Autor:** Cristopher Israel Cen Santana
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/dao/DetalleOrdenDaoCsv.java`, clase `DetalleOrdenDaoCsv`, método `public boolean actualizarRecepcion(int idOrden, String idProducto, int nuevaCantidadRecibida)` (líneas 72–106).
* **Señal encontrada:** Utiliza la bandera booleana `boolean modificado = false;` únicamente para recordar si en el ciclo `for` se encontró la fila, hacer `break;` y luego evaluar `if (modificado)` para guardar el archivo y retornar `true`.
* **Técnica propuesta:** **Remove Control Flag** *(Familia 4: Simplificación de expresiones condicionales)*.
* **Justificación:** Sustituir la bandera `modificado` por la escritura y `return` directo en cuanto se actualiza la fila buscada (y `return false;` al terminar el ciclo) simplifica el flujo de control y elimina anidamiento innecesario.
* **Comportamiento que debe conservarse:** Si existe la fila con `idOrden` e `idProducto`, actualiza `datos[3]`, sobrescribe el archivo conservando las demás filas y devuelve `true`; si no existe, no modifica el archivo y devuelve `false`.
* **Prueba prevista:** Prueba unitaria en JUnit (`DetalleOrdenDaoCsvTest`) verificando actualización exitosa (`true`) y búsqueda de producto/orden inexistente (`false`).
* **Prioridad:** **Media** — Clarifica el flujo de actualización en la recepción de almacén.

##### Oportunidad 04 (`CRIS-04`)
* **Identificador:** `04` (`CRIS-04`)
* **Autor:** Cristopher Israel Cen Santana
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/dao/DetalleOrdenDaoCsv.java`, clase `DetalleOrdenDaoCsv`, métodos `buscarPorIdOrden(int)` (líneas 31–40) y `actualizarRecepcion(...)` (líneas 77–84).
* **Señal encontrada:** Ambos métodos acceden repetidamente al arreglo `datos` con índices literales mágicos (`datos[0]`, `datos[1]`, `datos[2]`, `datos[3]`) y validan `datos.length >= 4` sin constantes que expliquen qué columna representa cada posición.
* **Técnica propuesta:** **Replace Magic Number with Symbolic Constant** *(Familia 3: Organización de datos)*.
* **Justificación:** Definir constantes simbólicas (`COL_ID_ORDEN = 0`, `COL_ID_PRODUCTO = 1`, `COL_CANT_ESPERADA = 2`, `COL_CANT_RECIBIDA = 3`, `MIN_COLUMNAS = 4`) hace explícito el esquema del CSV en toda la clase.
* **Comportamiento que debe conservarse:** `buscarPorIdOrden` y `actualizarRecepcion` deben seguir leyendo y modificando exactamente las mismas columnas de `dataBase/DetallesOrden.csv`.
* **Prueba prevista:** Prueba unitaria independiente `TestDetalleOrdenConstantesCsv` en JUnit verificando el mapeo de las 4 columnas en `buscarPorIdOrden`, `guardar` y `actualizarRecepcion` antes y después del cambio.
* **Prioridad:** **Media** — Mejora legibilidad y evita errores de índices en mantenimiento.

##### Oportunidad 05 (`CRIS-05`)
* **Identificador:** `05` (`CRIS-05`)
* **Autor:** Cristopher Israel Cen Santana
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/dao/AbstractDaoCsv.java` y subclases `ProveedorDaoCsv`, `UsuarioDaoCsv`, `OrdenCompraDaoCsv`, `VentaDaoCsv`, `DetalleVentaDaoCsv` (métodos `generarSiguienteId()` y `obtenerUltimoId()`).
* **Señal encontrada:** Cinco subclases de `AbstractDaoCsv<T>` repiten el mismo ciclo sobre `this.listarTodos()` comparando el ID entero contra `maxId = 0`, pese a que `AbstractDaoCsv<T>` ya define `listarTodos()` y `obtenerId(T entidad)`.
* **Técnica propuesta:** **Pull Up Method** *(Familia 6: Manejo de la generalización)*.
* **Justificación:** Subir `obtenerUltimoId()` y `generarSiguienteId()` a `AbstractDaoCsv<T>` centraliza el cálculo del ID máximo en la superclase y elimina la duplicación en las 5 subclases.
* **Comportamiento que debe conservarse:** Cada DAO debe seguir devolviendo el mayor ID entero registrado (o `0` si está vacío) en `obtenerUltimoId()`, y `maxId + 1` en `generarSiguienteId()`.
* **Prueba prevista:** Pruebas unitarias en JUnit (`AbstractDaoCsvIdTest`) verificando el cálculo de IDs en DAOs con registros y vacíos.
* **Prioridad:** **Alta** — Elimina duplicación en 5 clases de la jerarquía DAO.

---

#### Bloque B — Zona `model` (Autor: Gael)

##### Oportunidad 06 (`OPN-01`)
* **Identificador:** `06` (`OPN-01`)
* **Autor:** Gael
* **Ubicación:** Proyecto `SDG_MDC`, archivos `Usuario.java` (`setIdUsuario`), `Cliente.java` (`setIdCliente`), `Producto.java` (`setIdProducto`), `Proveedor.java` (`setIdProveedor`) y `Venta.java` (`setIdVenta`) en `src/main/java/gestionmats/model/`.
* **Señal encontrada:** Las entidades del dominio exponen métodos setters públicos para sus identificadores únicos, permitiendo mutar su identidad tras la creación y comprometiendo su integridad en colecciones hash (`equals`/`hashCode`).
* **Técnica propuesta:** **Remove Setting Method** *(Familia 5: Simplificación de llamadas a métodos)*.
* **Justificación:** Eliminar los setters de ID y asignar el identificador únicamente desde el constructor garantiza la inmutabilidad de la identidad de cada entidad.
* **Comportamiento que debe conservarse:** Todas las entidades deben poder instanciarse con su ID mediante sus constructores y devolverlo sin cambios en `getId...()`.
* **Prueba prevista:** Prueba unitaria `IdentidadEntidadesTest` verificando la asignación en constructor y lectura en getters.
* **Prioridad:** **Alta** — Protege la integridad referencial del modelo.

##### Oportunidad 07 (`OPN-02`)
* **Identificador:** `07` (`OPN-02`)
* **Autor:** Gael
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/model/DetalleOrden.java`, clase `DetalleOrden`, método `public double calcularSubtotal()` (líneas 28–35).
* **Señal encontrada:** En `calcularSubtotal()` se accede directamente a los campos privados `producto` y `cantidadEsperada` en lugar de usar los métodos accesores `getProducto()` y `getCantidadEsperada()` ya existentes en la clase.
* **Técnica propuesta:** **Self Encapsulate Field** *(Familia 3: Organización de datos)*.
* **Justificación:** Usar los getters dentro de la propia clase centraliza el acceso a los atributos y facilita futuras validaciones o extensiones sin modificar la fórmula de cálculo.
* **Comportamiento que debe conservarse:** `calcularSubtotal()` debe retornar `precioVenta * cantidadEsperada` cuando el producto no es nulo, y `0.0` cuando es nulo.
* **Prueba prevista:** Prueba unitaria `DetalleOrdenTest` verificando el cálculo con producto válido, cantidad `0` y producto `null`.
* **Prioridad:** **Media** — Cambio limpio, aislado y de bajo riesgo.

##### Oportunidad 08 (`OPN-03`)
* **Identificador:** `08` (`OPN-03`)
* **Autor:** Gael
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/model/OrdenCompra.java`, clase `OrdenCompra`, método `public List<DetalleOrden> getDetalles()` (línea 31).
* **Señal encontrada:** `getDetalles()` devuelve directamente la referencia a la lista interna mutable `this.detalles`, permitiendo que código externo agregue o borre elementos sin pasar por `addDetalleOrden()`.
* **Técnica propuesta:** **Encapsulate Collection** *(Familia 3: Organización de datos)*.
* **Justificación:** Retornar `Collections.unmodifiableList(this.detalles)` protege la colección interna contra modificaciones externas no autorizadas manteniendo la lectura intacta.
* **Comportamiento que debe conservarse:** Los clientes que consultan e iteran `getDetalles()` deben seguir leyendo todos los elementos agregados mediante `addDetalleOrden()`.
* **Prueba prevista:** Prueba unitaria `OrdenCompraTest` comprobando que `addDetalleOrden()` agrega elementos correctamente y que la lista leída por `getDetalles()` contiene dichos elementos sin permitir mutación directa.
* **Prioridad:** **Alta** — Protege la integridad de las órdenes de compra sin afectar a ningún llamador existente.

##### Oportunidad 09 (`OPN-04`)
* **Identificador:** `09` (`OPN-04`)
* **Autor:** Gael
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/model/Producto.java`, clase `Producto`, métodos `calcularStockMinimo()` (línea 93) y `getEstado()` (líneas 101–114).
* **Señal encontrada:** Uso de los literales numéricos `0.25` y `0.10` para calcular los umbrales de stock mínimo (25%) y crítico (10%) sin nombres simbólicos que expliquen la regla de negocio.
* **Técnica propuesta:** **Replace Magic Number with Symbolic Constant** *(Familia 3: Organización de datos)*.
* **Justificación:** Reemplazar `0.25` y `0.10` por `PORCENTAJE_STOCK_MINIMO = 0.25` y `PORCENTAJE_STOCK_CRITICO = 0.10` documenta la regla de inventario en el propio código.
* **Comportamiento que debe conservarse:** `calcularStockMinimo()` y `getEstado()` deben retornar exactamente los mismos valores enteros y estados (`LLENO`, `OK`, `BAJO`, `CRITICO`) para cualquier combinación de `stockMaximo` y `stockActual`.
* **Prueba prevista:** Prueba unitaria `ProductoTest` evaluando `calcularStockMinimo()` y los cuatro estados de `getEstado()`.
* **Prioridad:** **Media** — Mejora la legibilidad de las reglas de negocio del inventario.

##### Oportunidad 10 (`OPN-05`)
* **Identificador:** `10` (`OPN-05`)
* **Autor:** Gael
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/model/Cliente.java`, clase `Cliente`, método `public double aplicarPuntosComoDescuento()` (líneas 54–58).
* **Señal encontrada:** El método calcula y devuelve el descuento monetario equivalente a los puntos del cliente (`puntosLealtad * 0.10`) y en la misma llamada modifica el estado poniendo `this.puntosLealtad = 0.0`.
* **Técnica propuesta:** **Separate Query from Modifier** *(Familia 5: Simplificación de llamadas a métodos)*.
* **Justificación:** Separar la consulta pura (`calcularDescuentoDePuntos()`) del modificador (`canjearPuntos()`) permite consultar el monto de descuento sin destruir el saldo de puntos del cliente antes de confirmar una operación.
* **Comportamiento que debe conservarse:** La conversión de puntos (`puntosLealtad * 0.10`) y la puesta en cero del saldo al consumirse deben producir exactamente los mismos valores.
* **Prueba prevista:** Prueba unitaria `ClienteTest` verificando la consulta del descuento sin alterar los puntos y el posterior reinicio a `0.0` al canjearlos.
* **Prioridad:** **Alta** — Evita efectos secundarios destructivos al consultar puntos de lealtad.

---

#### Bloque C — Zona `controllers/gerente` (Autor: Daniel)

##### Oportunidad 11 (`DA-01`)
* **Identificador:** `11` (`DA-01`)
* **Autor:** Daniel
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/gerente/GerenteClienteController.java`, clase `GerenteClienteController`, método `private void abrirDialogoCliente(Cliente clienteExistente)` (líneas 117–185).
* **Señal encontrada:** El método concentra la creación del `Dialog`, armado del `GridPane` e inputs de texto, validación de campos y persistencia en `ClienteDaoCsv`.
* **Técnica propuesta:** **Extract Class** *(Familia 2: Movimiento de funciones entre objetos)*.
* **Justificación:** Extraer la construcción y validación del diálogo a una clase dedicada deja a `GerenteClienteController` enfocado únicamente en la tabla y coordinación CRUD.
* **Comportamiento que debe conservarse:** El diálogo de creación y edición de clientes debe mostrar los mismos campos, validaciones y guardado en CSV.
* **Prueba prevista:** Pruebas de creación, carga de datos en edición y validación de campos obligatorios del formulario de cliente.
* **Prioridad:** **Alta** — Reduce el tamaño y responsabilidades de `GerenteClienteController`.

##### Oportunidad 12 (`DA-02`)
* **Identificador:** `12` (`DA-02`)
* **Autor:** Daniel
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/gerente/GerenteProductoFormController.java`, clase `GerenteProductoFormController`, método `private void handleGuardar()` (líneas 67–117).
* **Señal encontrada:** `handleGuardar()` encadena condiciones complejas de validación (6 campos obligatorios vacíos, verificación de si el nombre fue modificado en modo creación vs. edición y si está duplicado en el DAO, y validación de `stockActual > stockMax`).
* **Técnica propuesta:** **Decompose Conditional** *(Familia 4: Simplificación de expresiones condicionales)*.
* **Justificación:** Extraer las condiciones de validación a métodos con nombres claros (`hayCamposObligatoriosVacios(...)`, `esNombreDuplicado(...)`, `esStockInvalido(stockActual, stockMax)`) hace legible cada regla de negocio y permite probar la lógica de validación en JUnit.
* **Comportamiento que debe conservarse:** Se deben rechazar exactamente los mismos casos inválidos (campos vacíos, nombre duplicado, stock actual mayor al máximo o números inválidos) y guardar/actualizar los productos válidos.
* **Prueba prevista:** Prueba unitaria `GerenteProductoFormValidationTest` comprobando las condiciones de campos vacíos, nombre duplicado y `stockActual > stockMax` antes y después del cambio.
* **Prioridad:** **Media** — Clarifica las reglas de validación del formulario de productos y facilita su prueba unitaria.

##### Oportunidad 13 (`DA-03`)
* **Identificador:** `13` (`DA-03`)
* **Autor:** Daniel
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/gerente/GerenteClienteController.java`, clase `GerenteClienteController`, métodos `loadData()` (línea 72) y `setupFilters()` (línea 88).
* **Señal encontrada:** Se utiliza directamente el número literal `0` (`c.getIdCliente() != 0` y `if (c.getIdCliente() == 0) return false;`) para ocultar al cliente genérico de mostrador en la tabla sin una constante simbólica que explique su significado.
* **Técnica propuesta:** **Replace Magic Number with Symbolic Constant** *(Familia 3: Organización de datos)*.
* **Justificación:** Definir `private static final int ID_CLIENTE_OCULTO = 0;` (y un predicado/método de filtrado verificable) explica el propósito del valor `0` en ambos métodos.
* **Comportamiento que debe conservarse:** El cliente con ID `0` nunca debe mostrarse en la tabla de clientes del gerente, mientras que los clientes con ID distinto de `0` deben filtrarse normalmente por nombre o teléfono.
* **Prueba prevista:** Prueba unitaria `GerenteClienteFiltroTest` verificando que el cliente con ID `0` sea excluido siempre y que los demás clientes coincidan por nombre o teléfono.
* **Prioridad:** **Media** — Modificación rápida, aislada y de bajo riesgo.

##### Oportunidad 14 (`DA-04`)
* **Identificador:** `14` (`DA-04`)
* **Autor:** Daniel
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/gerente/GerenteReporteController.java`, clase `GerenteReporteController`, método `private void procesarDatos()` (líneas 115–183).
* **Señal encontrada:** `procesarDatos()` mezcla en el controlador de interfaz el filtrado de ventas no canceladas por rango de fechas, el cruce con detalles y productos por categoría, y el cálculo de ingresos totales, producto más vendido y mejor categoría junto con la actualización de `Label` y gráficas JavaFX.
* **Técnica propuesta:** **Move Method** *(Familia 2: Movimiento de funciones entre objetos)*.
* **Justificación:** Mover la lógica de procesamiento y cálculo de métricas del reporte a una clase/método de servicio desacoplado de los controles JavaFX separa el dominio de la presentación y permite probar todos los cálculos financieros con JUnit puro.
* **Comportamiento que debe conservarse:** Para cualquier rango de fechas y filtro de categoría, las ventas filtradas, la utilidad total, el producto más vendido (`"--"` si no hay datos) y la mejor categoría deben producir exactamente los mismos resultados.
* **Prueba prevista:** Prueba unitaria `ReporteCalculoTest` en JUnit verificando el filtrado por fechas, filtrado por categoría, suma de ingresos totales, producto más vendido y categoría dominante antes y después del cambio.
* **Prioridad:** **Alta** — Separa la lógica de negocio de reportes de la interfaz JavaFX y habilita pruebas unitarias completas.

##### Oportunidad 15 (`DA-05`)
* **Identificador:** `15` (`DA-05`)
* **Autor:** Daniel
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/gerente/GerenteInventarioController.java`, clase `GerenteInventarioController`, métodos `handleAgregar()` (líneas 153–182) y `handleEditar()` (líneas 184–217).
* **Señal encontrada:** Ambos métodos duplican la carga de `GerenteProductoFormView.fxml`, configuración del `Stage` modal, llamada a `showAndWait()` y recarga de la tabla si `isGuardadoExitoso()` es verdadero, variando solo en si se envía un `Producto` existente o no.
* **Técnica propuesta:** **Parameterize Method** *(Familia 5: Simplificación de llamadas a métodos)*.
* **Justificación:** Unificar ambos métodos en `private void abrirFormularioProducto(Producto producto)` elimina la duplicación de código de apertura de ventana modal.
* **Comportamiento que debe conservarse:** `handleAgregar()` debe abrir el formulario en modo creación y `handleEditar()` debe validar que haya un producto seleccionado y abrir el formulario cargando sus datos.
* **Prueba prevista:** Pruebas funcionales de apertura de formulario vacío al agregar y con datos precargados al editar.
* **Prioridad:** **Media** — Reduce duplicación en el controlador de inventario.

---

#### Bloque D — Zona `controllers` de Venta, Login y Recibo (Autor: Diego)

##### Oportunidad 16 (`DG-01`)
* **Identificador:** `16` (`DG-01`)
* **Autor:** Diego
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/VendedorController.java`, clase `VendedorController`, método `private void handleFinalizarVenta()` (líneas 690–830).
* **Señal encontrada:** Método monolítico de ~140 líneas que concentra todas las etapas del cierre de una venta: diálogos y validación de pago (`switch` de efectivo, débito y crédito), confirmación, construcción de `Venta`, generación de recibo, guardado en CSV de venta y detalles, descuento de stock, cálculo de puntos de lealtad del cliente y limpieza de pantalla.
* **Técnica propuesta:** **Extract Method** *(Familia 1: Métodos de composición)*.
* **Justificación:** Extraer cada etapa en métodos con propósito único (`pedirPago`, `confirmarVenta`, `crearVenta`, `crearGenerador`, `guardarVenta`, `actualizarPuntosCliente`, `reiniciarVenta`) reduce drásticamente la complejidad de `handleFinalizarVenta()` y permite probar unitariamente los cálculos de venta y puntos de lealtad sin depender de diálogos JavaFX.
* **Comportamiento que debe conservarse:** Con el mismo carrito, cliente y método de pago se guarda la misma `Venta` y `DetalleVenta`, disminuye el stock en la cantidad vendida, el cliente queda con `puntos actuales − puntos usados + total × 0.05` (mínimo `0`), se genera el mismo recibo y se reinicia el estado de la venta.
* **Prueba prevista:** Prueba unitaria `VendedorVentaTest` en JUnit verificando el cálculo de puntos de lealtad (con canje, sin canje y límite inferior `0`) y la construcción del objeto `Venta`.
* **Prioridad:** **Alta** — Es el flujo central del punto de venta y el método más extenso del sistema.

##### Oportunidad 17 (`DG-02`)
* **Identificador:** `17` (`DG-02`)
* **Autor:** Diego
* **Ubicación:** Proyecto `SDG_MDC`, archivo `src/main/java/gestionmats/controllers/VendedorController.java`, clase `VendedorController`, métodos `setupTablaProductos()`, `setupTablaCarrito()`, `setupTablaHistorial()` y `handleVerDetalleVenta()`.
* **Señal encontrada:** El mismo bloque de 7 líneas que instancia un `TableCell` anónimo para dar formato monetario (`$ 0.00`) está copiado 5 veces y `String.format("$ %.2f", ...)` se repite 9 veces en la clase.
* **Técnica propuesta:** **Extract Method** / **Parameterize Method** *(Familia 1 / Familia 5)*.
* **Justificación:** Extraer `moneda(double valor)` y `formatoMoneda(TableColumn<S, Double> col)` centraliza el formato monetario en un solo punto.
* **Comportamiento que debe conservarse:** Todas las celdas y etiquetas de montos deben seguir mostrando `$ ` seguido de dos decimales (ej. `$ 120.00`) y `null` cuando la celda esté vacía.
* **Prueba prevista:** Prueba unitaria `FormatoMonedaTest` verificando `moneda(0)`, `moneda(120)` y redondeo a 2 decimales.
* **Prioridad:** **Media** — Elimina la duplicación más frecuente dentro de `VendedorController`.

##### Oportunidad 18 (`DG-03`)
* **Identificador:** `18` (`DG-03`)
* **Autor:** Diego
* **Ubicación:** Proyecto `SDG_MDC`, archivos `src/main/java/gestionmats/controllers/LoginController.java` (`handleLogin()`, líneas 115–128) y `src/main/java/gestionmats/model/RolUsuario.java`.
* **Señal encontrada:** `handleLogin()` utiliza un `switch (rol)` sobre `RolUsuario` para elegir la ruta del archivo FXML y el título de la ventana correspondiente a cada rol (`GERENTE`, `VENDEDOR`, `ALMACENISTA`).
* **Técnica propuesta:** **Replace Conditional with Polymorphism** *(Familia 4: Simplificación de expresiones condicionales)*.
* **Justificación:** Trasladar la ruta de la vista y el título a cada constante de `RolUsuario` (`getVista()` y `getTitulo()`) permite reemplazar el `switch` por `cargarVistaUsuario(rol.getVista(), rol.getTitulo())`, asegurando que cualquier rol defina sus propios datos de vista.
* **Comportamiento que debe conservarse:** `GERENTE`, `VENDEDOR` y `ALMACENISTA` deben seguir abriendo exactamente las mismas rutas FXML con los mismos títulos de ventana, y `RolUsuario.valueOf(...)` debe seguir funcionando igual.
* **Prueba prevista:** Prueba unitaria `RolUsuarioTest` en JUnit verificando que `getVista()` y `getTitulo()` de los tres roles devuelvan los valores esperados, que los recursos FXML existan en el classpath y que `valueOf(...)` opere igual antes y después del cambio.
* **Prioridad:** **Media** — Simplifica el controlador de acceso y es 100% verificable con JUnit.

##### Oportunidad 19 (`DG-04`)
* **Identificador:** `19` (`DG-04`)
* **Autor:** Diego
* **Ubicación:** Proyecto `SDG_MDC`, archivos `src/main/java/gestionmats/controllers/VendedorController.java` (`applyFilters()`, línea 278) y `src/main/java/gestionmats/controllers/gerente/GerenteInventarioController.java` (`applyFilters()`, línea 135).
* **Señal encontrada:** Ambos controladores repiten exactamente la misma expresión booleana para filtrar objetos `Producto` por texto (nombre o ID) y categoría, consultando únicamente atributos de `Producto` (*Feature Envy*).
* **Técnica propuesta:** **Move Method** *(Familia 2: Movimiento de funciones entre objetos)*.
* **Justificación:** Mover la condición a `Producto.coincideCon(String busqueda, String categoria)` elimina la duplicación entre ambos controladores y ubica la regla en la clase propietaria de los datos.
* **Comportamiento que debe conservarse:** El filtrado por nombre/ID (insensible a mayúsculas) y categoría (`"Todas"` o coincidencia exacta) debe producir los mismos resultados en ambas pantallas.
* **Prueba prevista:** Prueba unitaria `ProductoFiltroTest` probando búsqueda vacía, coincidencia por nombre/ID y categoría.
* **Prioridad:** **Media** — Elimina duplicación entre dos controladores.

##### Oportunidad 20 (`DG-05`)
* **Identificador:** `20` (`DG-05`)
* **Autor:** Diego
* **Ubicación:** Proyecto `SDG_MDC`, archivos `src/main/java/gestionmats/controllers/VendedorController.java` (`handleAgregarAlCarrito()`, líneas 373–375) y `src/main/java/gestionmats/model/DetalleVenta.java`.
* **Señal encontrada:** Cuando se agrega al carrito un producto que ya estaba en él, `VendedorController` recalcula desde fuera la cantidad, el subtotal y el total llamando a `item.setCantidad(...)`, `item.setSubtotal(...)` e `item.setTotal(...)`, duplicando la fórmula `subtotal * (1 - descuentoAplicado / 100)` que ya está en el constructor de `DetalleVenta`.
* **Técnica propuesta:** **Move Method** *(Familia 2: Movimiento de funciones entre objetos)*.
* **Justificación:** Trasladar esa responsabilidad a un método en `DetalleVenta` (`agregarCantidad(int extra)`) evita que el controlador manipule manualmente los tres atributos y centraliza la regla de cálculo de importes dentro de `DetalleVenta`.
* **Comportamiento que debe conservarse:** Al incrementar la cantidad de un renglón del carrito, `cantidad`, `subtotal` y `total` (con o sin descuento porcentual) deben actualizarse con los mismos valores numéricos que producía el cálculo en el controlador.
* **Prueba prevista:** Prueba unitaria `DetalleVentaTest` en JUnit verificando el cálculo de cantidad, subtotal y total (sin descuento y con descuento porcentual) antes y después de la refactorización.
* **Prioridad:** **Alta** — Centraliza el cálculo de montos cobrados en el carrito y es 100% verificable mediante JUnit puro.

---

## 3. Selección Preliminar para la Etapa 2 (14 Elegidas vs. 6 Descartadas)

### 3.1 Evaluación de Aislamiento y Decisión por Oportunidad

| ID Equipo | ID Autor | Autor | Técnica Propuesta | Evaluación de Aislamiento e Impacto entre Ramas | Dificultad | Decisión Etapa 2 |
| :---: | :---: | :--- | :--- | :--- | :---: | :---: |
| **01** | `CRIS-01` | Cristopher | **Extract Method** | 🟢 **100% Aislada:** solo modifica `ClienteDaoCsv.java` | Fácil | ✅ **Seleccionada** |
| **02** | `CRIS-02` | Cristopher | **Move Method** | 🟢 **Aislada en `dao`:** solo modifica `OrdenCompraDaoCsv.java` | Media | ✅ **Seleccionada** |
| **03** | `CRIS-03` | Cristopher | **Remove Control Flag** | 🟢 **100% Aislada:** solo modifica `DetalleOrdenDaoCsv.java` | Fácil | ✅ **Seleccionada** |
| **04** | `CRIS-04` | Cristopher | **Replace Magic Number with Symbolic Constant** | 🟢 **100% Aislada:** mismo archivo `DetalleOrdenDaoCsv.java` y comparte test con `ID 03` | Muy Fácil | ✅ **Seleccionada** |
| **05** | `CRIS-05` | Cristopher | **Pull Up Method** | 🟡 Modifica 6 clases del paquete `dao` simultáneamente | Media | ❌ **Descartada** |
| **06** | `OPN-01` | Gael | **Remove Setting Method** | 🔴 **Cruza paquetes:** rompe `ClienteDaoCsv.java` (Cristopher) y `VendedorController.java` (Diego) | Difícil | ❌ **Descartada** |
| **07** | `OPN-02` | Gael | **Self Encapsulate Field** | 🟢 **100% Aislada:** solo modifica `DetalleOrden.java` | Muy Fácil | ✅ **Seleccionada** |
| **08** | `OPN-03` | Gael | **Encapsulate Collection** | 🟢 **100% Aislada:** solo modifica `OrdenCompra.java` | Fácil | ✅ **Seleccionada** |
| **09** | `OPN-04` | Gael | **Replace Magic Number with Symbolic Constant** | 🟢 **100% Aislada:** solo modifica `Producto.java` | Muy Fácil | ✅ **Seleccionada** |
| **10** | `OPN-05` | Gael | **Separate Query from Modifier** | 🟢 **100% Aislada:** solo modifica `Cliente.java` (0 llamadas externas afectadas) | Fácil | ✅ **Seleccionada** |
| **11** | `DA-01` | Daniel | **Extract Class** | 🟡 Alta dependencia de controles modales JavaFX (`Dialog`, `GridPane`) para pruebas JUnit | Difícil | ❌ **Descartada** |
| **12** | `DA-02` | Daniel | **Decompose Conditional** | 🟢 **100% Aislada:** solo modifica `GerenteProductoFormController.java` | Media | ✅ **Seleccionada** |
| **13** | `DA-03` | Daniel | **Replace Magic Number with Symbolic Constant** | 🟢 **100% Aislada:** solo modifica `GerenteClienteController.java` | Fácil | ✅ **Seleccionada** |
| **14** | `DA-04` | Daniel | **Move Method** | 🟢 **Aislada en reportes:** separa cálculos de `GerenteReporteController.java` para JUnit | Difícil / Media-Alta | ✅ **Seleccionada** |
| **15** | `DA-05` | Daniel | **Parameterize Method** | 🟡 Depende de `FXMLLoader.load()` y `Stage.showAndWait()` (ventanas modales) | Media | ❌ **Descartada** |
| **16** | `DG-01` | Diego | **Extract Method** | 🟢 **100% Aislada:** solo modifica `VendedorController.java` (~140 líneas) | Difícil / Extensa | ✅ **Seleccionada** |
| **17** | `DG-02` | Diego | **Extract Method** / **Parameterize Method** | 🟢 **100% Aislada:** solo modifica `VendedorController.java` | Fácil | ❌ **Descartada** *(Respaldo)* |
| **18** | `DG-03` | Diego | **Replace Conditional with Polymorphism** | 🟢 **Semi-aislada sin colisión:** toca `LoginController.java` y `RolUsuario.java` (no tocado por Gael) | Fácil | ✅ **Seleccionada** |
| **19** | `DG-04` | Diego | **Move Method** | 🔴 **Cruza 3 zonas:** toca `VendedorController` (Diego), `GerenteInventarioController` (Daniel) y `Producto` (Gael) | Media | ❌ **Descartada** |
| **20** | `DG-05` | Diego | **Move Method** | 🟢 **Semi-aislada sin colisión:** toca `VendedorController.java` y `DetalleVenta.java` (no tocado por Gael) | Fácil | ✅ **Seleccionada** |

---

### 3.2 Balance de Carga de Trabajo por Integrante (14 Refactorizaciones)

1. **Diego (3 cambios = 1 Difícil/Extenso + 2 Fáciles):**
   * Al tocarle el controlador monolítico `VendedorController.java` (~966 líneas) y el método `handleFinalizarVenta()` (~140 líneas), realiza el mínimo de **3 cambios**: el monolito (`ID 16`) y dos cambios muy rápidos y fáciles de probar con JUnit puro (`ID 18` e `ID 20`). *(Nota: Si el docente autoriza que Diego realice únicamente 2 cambios por la extensión de `handleFinalizarVenta`, aplicará `ID 16` e `ID 20`, quedando el equipo en **13 refactorizaciones**, cumpliendo igualmente el mínimo de 12).*
2. **Daniel (3 cambios = 1 Difícil/Media-Alta + 1 Media + 1 Fácil):**
   * Realiza el desacoplamiento de los cálculos estadísticos de reportes en `GerenteReporteController` (`ID 14`), la descomposición de validaciones en `GerenteProductoFormController` (`ID 12`) y la constante simbólica del cliente oculto en `GerenteClienteController` (`ID 13`).
3. **Gael (4 cambios = 4 Fáciles y 100% aislados en `model`):**
   * Como los cambios en las clases del modelo son cortos y directos de probar en JUnit sin interfaz gráfica, realiza **4 refactorizaciones rápidas** (`ID 07`, `ID 08`, `ID 09`, `ID 10`), cada una en un archivo distinto.
4. **Cristopher (4 cambios = 1 Media + 3 Fáciles y 100% aislados en `dao`):**
   * Realiza **4 refactorizaciones** concentradas únicamente en 3 archivos del paquete `dao` (`ID 01`, `ID 02`, `ID 03`, `ID 04`), donde además `ID 03` e `ID 04` están en la misma clase (`DetalleOrdenDaoCsv`) y se protegen con la misma suite de pruebas JUnit.

| Integrante | Cantidad Seleccionada | Desglose de Dificultad | Archivos que modificará (0 colisiones entre miembros) |
| :--- | :---: | :--- | :--- |
| **Diego** | **3** *(o 2)* | 1 Difícil (`ID 16`) + 2 Fáciles (`ID 18`, `ID 20`) | `VendedorController.java`, `LoginController.java`, `RolUsuario.java`, `DetalleVenta.java` |
| **Daniel** | **3** | 1 Difícil/Media-Alta (`ID 14`) + 1 Media (`ID 12`) + 1 Fácil (`ID 13`) | `GerenteReporteController.java`, `GerenteProductoFormController.java`, `GerenteClienteController.java` |
| **Gael** | **4** | 4 Fáciles (`ID 07`, `ID 08`, `ID 09`, `ID 10`) | `DetalleOrden.java`, `OrdenCompra.java`, `Producto.java`, `Cliente.java` |
| **Cristopher** | **4** | 1 Media (`ID 02`) + 3 Fáciles (`ID 01`, `ID 03`, `ID 04`) | `ClienteDaoCsv.java`, `OrdenCompraDaoCsv.java`, `DetalleOrdenDaoCsv.java` |
| **TOTAL EQUIPO** | **14** *(13 mín.)* | **Carga laboral equivalente (~8h por integrante)** | **0 archivos en conflicto entre ramas/miembros** |

---

### 3.3 Justificación Detallada de las 6 Oportunidades Descartadas

1. **`ID 05` (`CRIS-05` — `Pull Up Method` en `AbstractDaoCsv` y 5 subclases):** Implica modificar 6 archivos del paquete `dao` simultáneamente y agregar manejo de excepciones para DAOs con IDs alfanuméricos (`ProductoDaoCsv`). Cristopher ya cubre 4 oportunidades más limpias y acotadas en solo 3 archivos.
2. **`ID 06` (`OPN-01` — `Remove Setting Method` en `Usuario`, `Cliente`, `Producto`, `Proveedor`, `Venta`):** Eliminar `setIdCliente()` y `setIdVenta()` rompe directamente `ClienteDaoCsv.java` (línea 45, zona de Cristopher) y `VendedorController.java` (línea 671, zona de Diego), generando acoplamiento y conflictos de mezcla entre 3 integrantes.
3. **`ID 11` (`DA-01` — `Extract Class` en `GerenteClienteController.abrirDialogoCliente()`):** El método construye controles visuales de JavaFX (`Dialog`, `GridPane`, `TextField`, `showAndWait()`), lo que impide ejecutar pruebas unitarias estándar en JUnit sin inicializar el toolkit gráfico de JavaFX.
4. **`ID 15` (`DA-05` — `Parameterize Method` en `GerenteInventarioController.handleAgregar()` y `handleEditar()`):** Ambos métodos dependen de `FXMLLoader.load()` y `Stage.showAndWait()` (ventanas modales bloqueantes), siendo mucho más complejo de probar automáticamente en JUnit que `DA-02`, `DA-03` y `DA-04`.
5. **`ID 17` (`DG-02` — `Extract Method` de formato de moneda en `VendedorController`):** Se deja como respaldo de Diego porque ya aplica `Extract Method` en `ID 16` (`handleFinalizarVenta`), priorizando variedad de técnicas en su selección final (`Extract Method`, `Replace Conditional with Polymorphism` y `Move Method`).
6. **`ID 19` (`DG-04` — `Move Method` de `applyFilters()` a `Producto.coincideCon()`):** Requiere editar simultáneamente archivos de tres integrantes distintos: `VendedorController.java` (Diego), `GerenteInventarioController.java` (Daniel) y `Producto.java` (Gael). Además, Diego ya aplica `Move Method` de forma limpia en `ID 20` (`DG-05`).
