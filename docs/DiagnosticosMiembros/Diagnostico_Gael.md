# Diagnóstico de Refactorización — Módulo de Modelos (`gestionmats.model`)

* **Autor:** Gael
* **Proyecto:** SDG_MDC (Sistema de Gestión de Materiales de Construcción)
* **Área asignada:** Paquete `gestionmats.model`
* **Etapa:** Etapa 1 — Inventario justificado de oportunidades (Semana 1)
* **Técnicas de referencia:** [Resumen_Tecnicas_Refactorizacion.md](../../Resumen_Tecnicas_Refactorizacion.md) según ADA 7

---

## Tabla Resumen de Oportunidades Identificadas

| ID | Ubicación | Señal encontrada | Técnica propuesta | Prioridad |
| :---: | :--- | :--- | :--- | :---: |
| **OPN-01** | `Usuario.java`<br>`Cliente.java`<br>`Producto.java`<br>`Proveedor.java`<br>`Venta.java`<br>(Métodos `setId...()`) | Exposición de métodos modificadores (setters) para los identificadores únicos de las entidades (`idUsuario`, `idCliente`, `idProducto`, `idProveedor`, `idVenta`), permitiendo mutar la identidad en runtime y afectando la integridad referencial y las operaciones con colecciones (`equals` / `hashCode`). | **Remove Setting Method** | Alta |
| **OPN-02** | `DetalleOrden.java`<br>`DetalleOrden.calcularSubtotal()` | Acceso directo a atributos privados (`producto`, `cantidadEsperada`) dentro de la propia clase en lugar de usar sus métodos accesores (getters). | **Self Encapsulate Field** | Media |
| **OPN-03** | `OrdenCompra.java`<br>`OrdenCompra.getDetalles()` | Retorno de referencia mutable a la lista interna `detalles`, permitiendo modificaciones externas sin pasar por métodos de control de la entidad. | **Encapsulate Collection** | Alta |
| **OPN-04** | `Producto.java`<br>`Producto.calcularStockMinimo()`, `getEstado()` | Presencia de números mágicos literales (`0.25` y `0.10`) para el cálculo de umbrales críticos de stock sin significado semántico explícito. | **Replace Magic Number with Symbolic Constant** | Media |
| **OPN-05** | `Cliente.java`<br>`Cliente.aplicarPuntosComoDescuento()` | Mezcla de consulta y modificación en un solo método: calcula el descuento equivalente de puntos y borra el saldo a cero en la misma llamada. | **Separate Query from Modifier** | Alta |

---

## Fichas Detalladas por Oportunidad

### Oportunidad 1: Inmutabilidad de los Identificadores de Entidad (`Usuario`, `Cliente`, `Producto`, `Proveedor`, `Venta`)

* **Identificador:** OPN-01
* **Autor:** Gael
* **Ubicación:** 
  * Proyecto: `SDG_MDC`
  * Archivos y clases implicadas:
    1. `src/main/java/gestionmats/model/Usuario.java` — Clase `Usuario`, método `public void setIdUsuario(int idUsuario)` (línea 27)
    2. `src/main/java/gestionmats/model/Cliente.java` — Clase `Cliente`, método `public void setIdCliente(int id)` (línea 62)
    3. `src/main/java/gestionmats/model/Producto.java` — Clase `Producto`, método `public void setIdProducto(String idProducto)` (línea 17)
    4. `src/main/java/gestionmats/model/Proveedor.java` — Clase `Proveedor`, método `public void setIdProveedor(int idProveedor)` (línea 37)
    5. `src/main/java/gestionmats/model/Venta.java` — Clase `Venta`, método `public void setIdVenta(int idVenta)` (línea 62)
* **Señal encontrada:**
  Las entidades centrales del dominio (`Usuario`, `Cliente`, `Producto`, `Proveedor` y `Venta`) exponen métodos setters públicos para sus identificadores primarios (`setIdUsuario`, `setIdCliente`, `setIdProducto`, `setIdProveedor`, `setIdVenta`). 
  
  El identificador único de una entidad representa su identidad inmutable a lo largo de su ciclo de vida. Permitir que cualquier componente externo modifique el ID tras la instanciación introduce graves riesgos:
  * Rompe la identidad de los objetos en memoria y arriesga la consistencia en colecciones basadas en hashing (`HashSet`, `HashMap`), donde `equals()` y `hashCode()` dependen del ID.
  * Facilita errores sutiles de concurrencia o de lógica donde una entidad puede ser reasignada erróneamente a otro registro existente.
  * Permite estados inválidos o temporales en objetos donde el ID no debería cambiar una vez persistido o recuperado de la base de datos CSV.
* **Técnica propuesta:**
  **Remove Setting Method** (Sección 2.5 — Simplificación de llamadas a métodos).
* **Justificación:**
  Eliminar los setters de ID en estas entidades y asegurar que el identificador solo se asigne en el constructor (o mediante un mecanismo de creación controlado) convierte los identificadores en atributos de solo lectura / inmutables tras su creación. Esto preserva la integridad referencial del sistema, garantiza que los objetos mantengan una identidad inequívoca y previene mutaciones accidentales en controladores y DAOs.
  
  *(Nota de implementación para DAOs: en clases como `ClienteDaoCsv`, en lugar de instanciar con constructor vacío `new Cliente()` y luego invocar `setIdCliente(...)`, se utilizará el constructor parametrizado que recibe el ID y demás atributos).*
* **Comportamiento que debe conservarse:**
  Todas las entidades (`Usuario` y sus subtipos, `Cliente`, `Producto`, `Proveedor`, `Venta`) deben seguir conservando su capacidad de ser instanciadas con su identificador correspondiente; los métodos de lectura `getIdUsuario()`, `getIdCliente()`, `getIdProducto()`, `getIdProveedor()` e `getIdVenta()` deben seguir retornando con exactitud el valor asignado sin alteraciones observables.
* **Prueba prevista:**
  Construir un conjunto de pruebas unitarias (`IdentidadEntidadesTest` o tests individuales por clase) que:
  1. Instancien cada entidad con identificadores válidos a través de sus constructores y verifiquen que los métodos `get...()` retornen el identificador esperado.
  2. Validen que dos entidades con el mismo ID se consideren equivalentes en colecciones hash y que su identidad no pueda ser alterada posteriormente por ausencia de métodos modificadores.
  3. Verifiquen que la carga y lectura desde los DAOs correspondientes continúe operando idénticamente.
* **Prioridad:**
  **Alta.** La inmutabilidad de la identidad de las entidades es un principio fundamental del diseño orientado a objetos y previene errores críticos de inconsistencia en todo el ciclo de persistencia y sesión.

---

### Oportunidad 2: Acceso Directo a Atributos en Cálculo de Subtotal

* **Identificador:** OPN-02
* **Autor:** Gael
* **Ubicación:** 
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/model/DetalleOrden.java`
  * Clase: `DetalleOrden`
  * Método implicado: `public double calcularSubtotal()` (líneas 28-35)
* **Señal encontrada:**
  En el método `calcularSubtotal()`, la clase accede de forma directa a sus variables de instancia privadas `producto` y `cantidadEsperada` (`producto != null`, `producto.getPrecioVenta() * cantidadEsperada`), a pesar de que la clase ya cuenta con los métodos de acceso públicos `getProducto()` y `getCantidadEsperada()`. Este acoplamiento interno directo dificulta la extensión futura de la clase si se deseara sobreescribir la obtención del producto, aplicar carga perezosa (lazy loading) o incorporar validaciones defensivas en los getters.
* **Técnica propuesta:**
  **Self Encapsulate Field** (Sección 2.3 — Organización de datos / Controlar el acceso).
* **Justificación:**
  Al forzar a que todos los accesos a los campos dentro de la propia clase se realicen a través de sus getters (`getProducto()` y `getCantidadEsperada()`), se centraliza cualquier lógica de acceso, facilitando que futuras subclases o extensiones puedan redefinir cómo se obtienen dichos datos sin tener que reescribir la lógica de cálculo.
* **Comportamiento que debe conservarse:**
  El método `calcularSubtotal()` debe retornar exactamente el producto de `precioVenta * cantidadEsperada` cuando el producto no es nulo, y `0.0` cuando el producto es nulo, preservando la compatibilidad con las pantallas de compras y pedidos (`GerenteNuevoPedidoController` y `GerentePedidosController`).
* **Prueba prevista:**
  Construir una prueba unitaria `DetalleOrdenTest` que:
  1. Compruebe `calcularSubtotal()` con un producto válido con precio definido y cantidad esperada positiva.
  2. Compruebe el caso límite donde `producto` es `null`, esperando `0.0`.
  3. Compruebe el cálculo cuando la cantidad es `0`.
* **Prioridad:**
  **Media.** Es un cambio de bajo riesgo que mejora el diseño interno y la extensibilidad sin romper contratos existentes.

---

### Oportunidad 3: Exposición de Colección Mutable de Detalles de Órdenes de Compra

* **Identificador:** OPN-03
* **Autor:** Gael
* **Ubicación:** 
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/model/OrdenCompra.java`
  * Clase: `OrdenCompra`
  * Método implicado: `public List<DetalleOrden> getDetalles()` (línea 31)
* **Señal encontrada:**
  El método `getDetalles()` retorna directamente la referencia interna a la lista mutable `this.detalles`. Esto permite que cualquier componente externo (controlador, DAO o servicio) pueda alterar la colección (`orden.getDetalles().clear()`, `orden.getDetalles().add(...)`) saltándose las invariantes del objeto y el método oficial `addDetalleOrden(DetalleOrden detalle)`. (Un problema idéntico ocurre en `Venta.java` con `getDetalles()` y `setDetalles()`).
* **Técnica propuesta:**
  **Encapsulate Collection** (Sección 2.3 — Organización de datos / Controlar el acceso).
* **Justificación:**
  Retornar una vista de solo lectura mediante `Collections.unmodifiableList(this.detalles)` y proveer métodos específicos de modificación controlada (`addDetalleOrden`, `removerDetalleOrden`) evita que clientes externos alteren la colección a espaldas de la entidad, garantizando que el modelo mantenga el control exclusivo sobre su estado y reglas de negocio.
* **Comportamiento que debe conservarse:**
  Los clientes que leen la lista (como `GerentePedidosController` para mostrar la tabla de detalles o `OrdenCompraDaoCsv` para guardar) deben poder iterar y consultar los elementos sin alteraciones. El agregado de nuevos detalles debe seguir realizándose exitosamente mediante `addDetalleOrden()`.
* **Prueba prevista:**
  Construir una prueba unitaria `OrdenCompraTest` que:
  1. Agregue detalles mediante `addDetalleOrden()` y compruebe que se reflejan en la lista leída con `getDetalles()`.
  2. Intente modificar directamente la lista retornada por `getDetalles()` (mediante `.add()` o `.clear()`) y compruebe que se lance la excepción `UnsupportedOperationException`.
* **Prioridad:**
  **Alta.** Exponer listas mutables en entidades transaccionales (órdenes de compra y ventas) abre la puerta a inconsistencias de stock, cálculos erróneos y pérdidas de datos.

---

### Oportunidad 4: Números Mágicos en Umbrales de Stock de Productos

* **Identificador:** OPN-04
* **Autor:** Gael
* **Ubicación:** 
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/model/Producto.java`
  * Clase: `Producto`
  * Métodos implicados: `calcularStockMinimo()` (línea 93) y `getEstado()` (líneas 101-114)
* **Señal encontrada:**
  En los métodos `calcularStockMinimo()` y `getEstado()` se utilizan números literales directos: `0.25` (25% del stock máximo como stock mínimo) y `0.10` (10% del stock máximo como nivel crítico). Estos números mágicos carecen de nombre explicativo en el código, oscurecen la justificación del cálculo y requerirían cambios dispersos en múltiples métodos si las políticas de abastecimiento o almacén del negocio cambian.
* **Técnica propuesta:**
  **Replace Magic Number with Symbolic Constant** (Sección 2.3 — Organización de datos / Dar significado a datos simples).
* **Justificación:**
  Reemplazar estos literales por constantes simbólicas autoexplicativas (`PORCENTAJE_STOCK_MINIMO = 0.25;` y `PORCENTAJE_STOCK_CRITICO = 0.10;`) comunica la intención de negocio, facilita la comprensión del algoritmo de clasificación de alertas en inventario y centraliza cualquier modificación futura de las políticas de almacenamiento.
* **Comportamiento que debe conservarse:**
  Para cualquier valor de `stockMaximo` y `stockActual`, los métodos `calcularStockMinimo()` y `getEstado()` deben seguir retornando exactamente los mismos valores numéricos y los mismos estados del enum `EstadoProducto` (`LLENO`, `OK`, `BAJO`, `CRITICO`).
* **Prueba prevista:**
  Construir una prueba unitaria `ProductoTest` que verifique:
  1. `calcularStockMinimo()` devuelve el 25% entero del `stockMaximo`.
  2. `getEstado()` evalúa correctamente los cuatro estados:
     * `LLENO` cuando `stockActual == stockMaximo`.
     * `OK` cuando `stockActual > stockMinimo`.
     * `BAJO` cuando `stockActual > nivelCritico` y `<= stockMinimo`.
     * `CRITICO` cuando `stockActual <= nivelCritico`.
* **Prioridad:**
  **Media.** Mejora la legibilidad del dominio y la mantenibilidad de las reglas de negocio de almacén.

---

### Oportunidad 5: Consulta con Efecto Secundario en Puntos de Lealtad del Cliente

* **Identificador:** OPN-05
* **Autor:** Gael
* **Ubicación:** 
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/model/Cliente.java`
  * Clase: `Cliente`
  * Método implicado: `public double aplicarPuntosComoDescuento()` (líneas 54-58)
* **Señal encontrada:**
  El método `aplicarPuntosComoDescuento()` realiza dos acciones distintas y contradictorias: consulta el monto monetario equivalente a los puntos acumulados (`puntosLealtad * VALOR_PUNTO`) y al mismo tiempo modifica el estado reseteando el saldo a cero (`this.puntosLealtad = 0.0`). Esto viola el principio de separación entre comando y consulta (*Command-Query Separation*). Si una vista del punto de venta o un controlador necesita previsualizar el descuento aplicable sin consumirlo de inmediato (por ejemplo, antes de que el cliente confirme el pago), llamar a este método borra los puntos del cliente de forma destructiva e irreversible.
* **Técnica propuesta:**
  **Separate Query from Modifier** (Sección 2.5 — Simplificación de llamadas a métodos).
* **Justificación:**
  Separar la operación en dos métodos independientes: una consulta pura `calcularDescuentoDePuntos()` (que calcula y retorna el monto en pesos sin alterar el estado) y un método modificador `canjearPuntos()` o `consumirPuntos()` (que restablece el saldo tras la confirmación de la venta). Esto permite previsualizar descuentos de manera segura y mantiene la responsabilidad única de cada método.
* **Comportamiento que debe conservarse:**
  La tasa de conversión de puntos a pesos (`puntos * 0.10`) debe seguir siendo la misma, y cuando se concrete el canje, el saldo de puntos debe quedar en cero tal como ocurre actualmente.
* **Prueba prevista:**
  Construir una prueba unitaria `ClienteTest` que:
  1. Acumule puntos mediante `acumularPuntos()` y verifique que la consulta del descuento retorne el monto esperado sin alterar el saldo de puntos del cliente.
  2. Ejecute el método modificador y confirme que el saldo se resetee a `0.0`.
* **Prioridad:**
  **Alta.** Previene pérdida involuntaria de datos del cliente durante cotizaciones o revisiones en la interfaz del punto de venta.

---

## Oportunidades Adicionales Identificadas (Respaldo para Consolidación de Equipo)

1. **`Venta.java` — `Replace Nested Conditional with Guard Clauses`:** En `getDescuentoPorPuntos()`, simplificar la anidación de condiciones nulas y booleanas aplicando cláusulas de guarda al inicio del método para retornar `0.0` temprano.
2. **`Vendedor.java` — `Move Method`:** El método `registrarVenta(...)` interactúa directamente con múltiples DAOs y persistencia de archivos, violando la separación de responsabilidades de un modelo de dominio y correspondiendo más a una capa de servicio.
