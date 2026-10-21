# Diagnóstico de Refactorización — Controladores de Venta (`gestionmats.controllers`)

* **Autor:** Diego
* **Proyecto:** SDG_MDC (Sistema de Gestión de Materiales de Construcción)
* **Área asignada:** `LoginController`, `ReciboController` y `VendedorController` (paquete `gestionmats.controllers`)
* **Etapa:** Etapa 1 — Inventario justificado de oportunidades (Semana 1)
* **Técnicas de referencia:** [Resumen_Tecnicas_Refactorizacion.md](../../Resumen_Tecnicas_Refactorizacion.md) según ADA 7
* **Estado inicial de pruebas:** El proyecto no cuenta con pruebas automatizadas (no existe `src/test` y el `pom.xml` no incluye JUnit). Para la Etapa 2 se agregará la dependencia `junit-jupiter`.

> Los números de línea corresponden a la versión actual de `VendedorController.java`, que incluye los comentarios `// REFACTOR:` del diagnóstico.

---

## Tabla Resumen de Oportunidades Identificadas

| ID | Ubicación | Señal encontrada | Técnica propuesta | Prioridad |
| :---: | :--- | :--- | :--- | :---: |
| **DG-01** | `VendedorController.java`<br>`handleFinalizarVenta()` | Método de ~140 líneas que concentra todo el proceso de venta: pago, confirmación, construcción de la venta, recibo, persistencia, stock, puntos y limpieza de la pantalla. | **Extract Method** | Alta |
| **DG-02** | `VendedorController.java`<br>`setupTablaProductos()`, `setupTablaCarrito()`, `setupTablaHistorial()`, `handleVerDetalleVenta()` | El mismo `TableCell` anónimo para formatear dinero (`$ 0.00`) está copiado 5 veces y `String.format("$ %.2f", ...)` aparece 9 veces. | **Extract Method** | Media |
| **DG-03** | `LoginController.java`<br>`handleLogin()` | `switch` sobre `RolUsuario` que decide qué vista y qué título abrir; agregar un rol obliga a modificar el controlador. | **Replace Conditional with Polymorphism** | Media |
| **DG-04** | `VendedorController.java`<br>`GerenteInventarioController.java`<br>(Método `applyFilters()`) | Condición de filtrado idéntica en dos controladores que solo usa datos de `Producto` (*Feature Envy* + código duplicado). | **Move Method** | Media |
| **DG-05** | `VendedorController.java`<br>`handleAgregarAlCarrito()` | El controlador recalcula cantidad, subtotal y total de un `DetalleVenta`, duplicando la fórmula del constructor de esa clase. | **Move Method** | Alta |

---

## Fichas Detalladas por Oportunidad

### Oportunidad 1: Método Largo para Finalizar la Venta

* **Identificador:** DG-01
* **Autor:** Diego
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/controllers/VendedorController.java`
  * Clase: `VendedorController`
  * Método implicado: `private void handleFinalizarVenta()` (líneas 690-830)
* **Señal encontrada:**
  El método `handleFinalizarVenta()` tiene alrededor de 140 líneas y realiza, en secuencia, todas las etapas del cierre de una venta:
  * Pide los datos del pago con un `switch` de tres casos (efectivo, débito y crédito), cada uno con su propio diálogo, parseo y validación.
  * Solicita confirmación al usuario.
  * Construye el objeto `Venta` y le asigna detalles, estrategia de pago y estrategia de descuento.
  * Elige el generador de recibo (instantáneo o pedido) y muestra el recibo.
  * Guarda la venta y sus detalles en los CSV y descuenta el stock de cada producto.
  * Actualiza los puntos de lealtad del cliente.
  * Limpia el carrito, descuentos, cliente y etiquetas.

  Incluso contiene un comentario de sección (`// ==== ACTUALIZACIÓN DE PUNTOS CORREGIDA ====`) para separar bloques, lo cual es una señal típica de que ese bloque debería ser un método con nombre propio. El tamaño dificulta entender el flujo, modificar un paso sin afectar otro, y hace imposible probar cualquier parte por separado.
* **Técnica propuesta:**
  **Extract Method** (Sección 2.1 — Métodos de composición).
* **Justificación:**
  Cada bloque tiene un propósito identificable que puede expresarse con un nombre. Se propone extraer:
  1. `Optional<EstrategiaPago> pedirPago(String metodo, double total)` — el `switch` de métodos de pago.
  2. `boolean confirmarVenta(double total)` — mensaje y diálogo de confirmación.
  3. `Venta crearVenta(String metodo, double total, EstrategiaPago pago)` — construcción del objeto `Venta`.
  4. `GeneradorRecibo crearGenerador(Venta venta)` — selección del generador de recibo.
  5. `int guardarVenta(Venta venta)` — persistencia de venta, detalles y descuento de stock.
  6. `void actualizarPuntosCliente(double total)` — reemplaza al comentario de sección.
  7. `void reiniciarVenta()` — limpieza final del estado.

  Con esto, `handleFinalizarVenta()` se lee como una secuencia de pasos, y la lógica que no depende de la interfaz (`crearVenta`, `actualizarPuntosCliente`) queda aislada y puede probarse de forma unitaria.
* **Comportamiento que debe conservarse:**
  Con el mismo carrito, cliente y método de pago:
  * Se guarda la misma `Venta` (id consecutivo, subtotal, porcentaje de descuento y total) y los mismos `DetalleVenta`.
  * El stock de cada producto disminuye en la cantidad vendida.
  * Un cliente registrado queda con `puntos actuales − puntos usados + total × 0.05` (mínimo `0`).
  * El texto del recibo generado es idéntico.
  * Al terminar, el carrito, los descuentos, el cliente y los puntos usados quedan reiniciados.
  * Cancelar en cualquier diálogo sigue sin guardar nada.
* **Prueba prevista:**
  Construir pruebas unitarias `VendedorVentaTest` que:
  1. Verifiquen el cálculo de puntos de `actualizarPuntosCliente` con tres casos: cliente con puntos usados, sin puntos usados y resultado negativo (debe quedar en `0`).
  2. Verifiquen que `crearVenta` produce una `Venta` con los campos esperados (cliente, vendedor, tipo, método, subtotal, descuento y total).

  Además, registrar una prueba manual antes y después del cambio: una venta por cada método de pago (efectivo, débito y crédito), comparando el contenido de los CSV de ventas, detalles, productos y clientes.
* **Prioridad:**
  **Alta.** Es el flujo principal del punto de venta y el método más grande de la clase; además concentra errores detectados durante el diagnóstico (el recibo se muestra antes de guardar la venta y el manejo de puntos canjeados).

---

### Oportunidad 2: Formato de Moneda Duplicado en las Tablas

* **Identificador:** DG-02
* **Autor:** Diego
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/controllers/VendedorController.java`
  * Clase: `VendedorController`
  * Métodos implicados:
    1. `setupTablaProductos()` — columna `colPrecioProducto` (línea 258)
    2. `setupTablaCarrito()` — columnas `colPrecioCarrito`, `colSubtotalCarrito`, `colTotalCarrito` (línea 304)
    3. `setupTablaHistorial()` — columna `colHisTotal` (línea 854)
    4. `handleVerDetalleVenta()` — columnas `dPrecio` y `dTotal` (líneas 939 y 946)
* **Señal encontrada:**
  El mismo bloque de 7 líneas que crea un `TableCell` anónimo para mostrar un número con el formato `$ 0.00` está copiado 5 veces en la clase. Además, la expresión `String.format("$ %.2f", ...)` aparece 9 veces (incluyendo las etiquetas de subtotal y total en `actualizarTotales()` y el resumen en `handleVerDetalleVenta()`). Si se requiere cambiar el formato (por ejemplo, a `MXN 0.00` o mostrar separador de miles), hay que modificar cada copia, y es fácil que alguna quede con un formato distinto.
* **Técnica propuesta:**
  **Extract Method** (Sección 2.1 — Métodos de composición).
* **Justificación:**
  El bloque repetido es idéntico en todos los lugares; lo único que cambia es la columna a la que se aplica. Esa columna se convierte en el parámetro del nuevo método:

  ```java
  private static String moneda(double valor) {
      return String.format("$ %.2f", valor);
  }

  private static <S> void formatoMoneda(TableColumn<S, Double> col) {
      col.setCellFactory(c -> new TableCell<>() {
          @Override
          protected void updateItem(Double item, boolean empty) {
              super.updateItem(item, empty);
              setText(empty || item == null ? null : moneda(item));
          }
      });
  }
  ```

  Cada uso queda en una sola línea (`formatoMoneda(colHisTotal);`), el formato se define en un único lugar y el nombre del método expresa la intención.
* **Comportamiento que debe conservarse:**
  Todas las columnas de dinero siguen mostrando `$ ` seguido del valor con dos decimales (por ejemplo, `$ 120.00`); las celdas vacías o con valor `null` siguen sin texto; las etiquetas de subtotal y total muestran exactamente el mismo texto que antes del cambio.
* **Prueba prevista:**
  Construir una prueba unitaria `FormatoMonedaTest` (dejando `moneda()` con visibilidad de paquete) que verifique:
  1. `moneda(0)` retorna `$ 0.00`.
  2. `moneda(120)` retorna `$ 120.00`.
  3. `moneda(99.999)` retorna `$ 100.00` (redondeo).

  Además, verificación visual antes y después en las tablas de productos, carrito, historial y el diálogo de detalle de venta.
* **Prioridad:**
  **Media.** No provoca un fallo actualmente, pero es la duplicación más repetida de la clase; el cambio es pequeño y de bajo riesgo.

---

### Oportunidad 3: Selección de Vista por Rol con `switch` en el Login

* **Identificador:** DG-03
* **Autor:** Diego
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Archivo: `src/main/java/gestionmats/controllers/LoginController.java`
  * Clase: `LoginController`
  * Método implicado: `private void handleLogin()` (bloque `switch (rol)`, líneas 115-128)
* **Señal encontrada:**
  Después de validar las credenciales, `handleLogin()` usa un `switch` sobre el enum `RolUsuario` para decidir qué archivo FXML cargar y qué título poner a la ventana:

  ```java
  switch (rol) {
      case GERENTE:
          cargarVistaUsuario("/views/gerente/GerenteView.fxml", "SDG MDC - Panel de Gerente");
          break;
      case VENDEDOR:
          cargarVistaUsuario("/views/VendedorView.fxml", "SDG MDC - Punto de Venta");
          break;
      case ALMACENISTA:
          cargarVistaUsuario("/views/almacenista/AlmacenistaView.fxml", "SDG MDC - Control de Almacén");
          break;
      default:
          AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error de Acceso", "Rol no reconocido", "Consulte con soporte.");
  }
  ```

  La vista y el título dependen únicamente del rol, pero esa información vive en el controlador y no en el tipo que la determina. Si se agrega un rol nuevo (por ejemplo, `CAJERO`), hay que recordar modificar este `switch`; si se olvida, el usuario cae en el caso `default` ("Rol no reconocido") aunque su rol sea válido.
* **Técnica propuesta:**
  **Replace Conditional with Polymorphism** (Sección 2.4 — Simplificación de expresiones condicionales).
* **Justificación:**
  Cada caso del `switch` representa un comportamiento propio del rol. Se traslada esa información al enum, de modo que cada constante sepa cuál es su vista y su título:

  ```java
  public enum RolUsuario {
      GERENTE("/views/gerente/GerenteView.fxml", "SDG MDC - Panel de Gerente"),
      VENDEDOR("/views/VendedorView.fxml", "SDG MDC - Punto de Venta"),
      ALMACENISTA("/views/almacenista/AlmacenistaView.fxml", "SDG MDC - Control de Almacén");

      private final String vista;
      private final String titulo;

      RolUsuario(String vista, String titulo) {
          this.vista = vista;
          this.titulo = titulo;
      }

      public String getVista() { return vista; }
      public String getTitulo() { return titulo; }
  }
  ```

  El `switch` completo se reemplaza por una sola línea en `handleLogin()`:

  ```java
  cargarVistaUsuario(rol.getVista(), rol.getTitulo());
  ```

  Al agregar un rol nuevo, el compilador obliga a definir su vista y su título en el mismo lugar donde se declara el rol, y el controlador ya no necesita cambiar.
* **Comportamiento que debe conservarse:**
  * Un usuario `GERENTE` abre `GerenteView.fxml` con el título "SDG MDC - Panel de Gerente".
  * Un usuario `VENDEDOR` abre `VendedorView.fxml` con el título "SDG MDC - Punto de Venta".
  * Un usuario `ALMACENISTA` abre `AlmacenistaView.fxml` con el título "SDG MDC - Control de Almacén".
  * Si la vista no existe, se sigue mostrando la alerta "Módulo en desarrollo" de `cargarVistaUsuario()`.
  * Con credenciales incorrectas se mantiene la animación de error y el mensaje.
  * `RolUsuario.valueOf(...)` sigue funcionando igual al leer usuarios del CSV (`UsuarioDaoCsv`) y en `GerenteEmpleadoController`.
* **Prueba prevista:**
  Construir una prueba unitaria `RolUsuarioTest` que:
  1. Verifique que `getVista()` y `getTitulo()` de cada rol devuelven exactamente las rutas y títulos que hoy están en el `switch`.
  2. Verifique que para cada rol el recurso FXML existe (`RolUsuario.class.getResource(rol.getVista()) != null`).
  3. Verifique que `RolUsuario.valueOf("VENDEDOR")` sigue regresando `VENDEDOR`.

  Además, prueba manual antes y después: iniciar sesión con un usuario de cada rol y con credenciales incorrectas.
* **Prioridad:**
  **Media.** No provoca un fallo actualmente, pero es el punto de entrada de la aplicación y el cambio es pequeño y de bajo riesgo.
  *Nota: la solución modifica `RolUsuario.java` (paquete `model`); coordinar con el integrante que revisa ese paquete.*

---

### Oportunidad 4: Lógica de Filtrado de Productos Ubicada en los Controladores

* **Identificador:** DG-04
* **Autor:** Diego
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Archivos y clases implicadas:
    1. `src/main/java/gestionmats/controllers/VendedorController.java` — Clase `VendedorController`, método `private void applyFilters()` (línea 278)
    2. `src/main/java/gestionmats/controllers/gerente/GerenteInventarioController.java` — Clase `GerenteInventarioController`, método `private void applyFilters()` (línea 135)
* **Señal encontrada:**
  Los dos controladores contienen exactamente la misma condición para filtrar productos:

  ```java
  boolean matchSearch = search.isEmpty() || p.getNombre().toLowerCase().contains(search) || p.getIdProducto().toLowerCase().contains(search);
  boolean matchCat = cat == null || cat.equals("Todas") || p.getCategoria().equals(cat);
  ```

  La condición solo utiliza datos de `Producto` (`getNombre()`, `getIdProducto()`, `getCategoria()`) y ningún dato propio del controlador (*Feature Envy*). Si la regla de búsqueda cambia (por ejemplo, buscar también por proveedor), hay que modificarla en dos clases distintas y mantenerlas sincronizadas.
* **Técnica propuesta:**
  **Move Method** (Sección 2.2 — Movimiento de funciones entre objetos).
* **Justificación:**
  La lógica pertenece a la clase cuyos datos utiliza. Se mueve a `Producto`:

  ```java
  public boolean coincideCon(String busqueda, String categoria) {
      boolean matchSearch = busqueda.isEmpty() || nombre.toLowerCase().contains(busqueda) || idProducto.toLowerCase().contains(busqueda);
      boolean matchCat = categoria == null || categoria.equals("Todas") || this.categoria.equals(categoria);
      return matchSearch && matchCat;
  }
  ```

  Ambos controladores quedan con `productosFiltrados.setPredicate(p -> p.coincideCon(search, cat));`. La regla de negocio queda en un solo lugar y puede probarse sin interfaz gráfica.
* **Comportamiento que debe conservarse:**
  * Con búsqueda vacía y categoría `"Todas"` o `null` se muestran todos los productos.
  * La búsqueda no distingue mayúsculas y coincide con el nombre o el ID.
  * La categoría debe coincidir exactamente.
  * Ambos criterios se combinan con AND.
  * El resultado es el mismo en la pantalla del vendedor y en el inventario del gerente.
* **Prueba prevista:**
  Construir una prueba unitaria `ProductoTest` para `coincideCon` con los casos:
  1. Búsqueda vacía y categoría `"Todas"` → `true`.
  2. Parte del nombre en mayúsculas → `true`.
  3. Búsqueda por ID → `true`.
  4. Categoría distinta → `false`.
  5. Texto que no coincide → `false`.
  6. Categoría `null` → `true`.
* **Prioridad:**
  **Media.** La duplicación cruza dos controladores (vendedor y gerente); el cambio es pequeño y fácil de probar.
  *Nota: involucra `GerenteInventarioController`; coordinar con el integrante que revisa los controladores del gerente para no duplicar la oportunidad.*

---

### Oportunidad 5: Recálculo de un Renglón del Carrito desde el Controlador

* **Identificador:** DG-05
* **Autor:** Diego
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Archivos y clases implicadas:
    1. `src/main/java/gestionmats/controllers/VendedorController.java` — Clase `VendedorController`, método `handleAgregarAlCarrito()` (líneas 373-375)
    2. `src/main/java/gestionmats/model/DetalleVenta.java` — Clase `DetalleVenta`, constructor (líneas 37-38)
* **Señal encontrada:**
  Cuando el producto agregado ya existe en el carrito, el controlador modifica directamente el `DetalleVenta` con tres llamadas consecutivas:

  ```java
  item.setCantidad(item.getCantidad() + cantidad);
  item.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
  item.setTotal(item.getSubtotal() * (1 - item.getDescuentoAplicado() / 100));
  ```

  Las tres líneas usan exclusivamente datos del propio `DetalleVenta` (*Feature Envy*), y la fórmula `subtotal × (1 − descuento / 100)` ya existe en el constructor de `DetalleVenta`. Si la regla de cálculo cambia, hay que recordar modificarla en ambos lugares; si solo se modifica una, el carrito mostrará montos distintos según si el producto se agregó una o varias veces.
* **Técnica propuesta:**
  **Move Method** (Sección 2.2 — Movimiento de funciones entre objetos).
* **Justificación:**
  El cálculo depende únicamente del estado de `DetalleVenta`, por lo que debe residir en esa clase. Se agrega:

  ```java
  public void agregarCantidad(int extra) {
      this.cantidad += extra;
      this.subtotal = precioUnitario * cantidad;
      this.total = subtotal * (1 - descuentoAplicado / 100);
  }
  ```

  El controlador solo invoca `item.agregarCantidad(cantidad)`, y el objeto mantiene la consistencia entre cantidad, subtotal y total por sí mismo.
* **Comportamiento que debe conservarse:**
  Al agregar un producto que ya está en el carrito: la cantidad se suma; `subtotal = precioUnitario × cantidad`; `total = subtotal × (1 − descuentoAplicado / 100)`; la tabla se refresca, los totales se recalculan y se muestra la notificación "Cantidad actualizada". No se crea un renglón nuevo.
* **Prueba prevista:**
  Construir una prueba unitaria `DetalleVentaTest` que:
  1. Sin descuento: precio `100`, cantidad `2`, agregar `3` → cantidad `5`, subtotal `500`, total `500`.
  2. Con descuento de `10 %`: mismo caso → total `450`.
  3. Verifique que el resultado de `agregarCantidad` coincide con crear un `DetalleVenta` nuevo con la cantidad final.
* **Prioridad:**
  **Alta.** Afecta directamente los montos cobrados en la venta; mantener la fórmula duplicada puede provocar cobros incorrectos si solo una de las copias se actualiza.

---

## Oportunidades Adicionales Identificadas (Respaldo para Consolidación de Equipo)

1. **`VendedorController.java` — `Replace Magic Number with Symbolic Constant`:** En `handleAplicarDescuento()` el PIN del gerente `"1234"` está escrito en el código, y el valor `0.05` (valor de un punto y porcentaje de puntos ganados) aparece en `handleCanjearPuntos()` y `handleFinalizarVenta()` sin nombre que explique su significado.
2. **`VendedorController.java` — `Parameterize Method`:** `handleNuevoCliente()` y `handleEditarCliente()` construyen el mismo diálogo de cliente; solo cambian el título y si los campos vienen llenos. Además, editar no valida nombre ni teléfono.
3. **`VendedorController.java` — `Replace Temp with Query`:** En `handleFinalizarVenta()` el total se obtiene parseando el texto de la etiqueta `lblTotal`; debería calcularse con un método `calcularTotal()`.
## Errores Detectados Durante el Diagnóstico (No son refactorización)

1. **Stock:** `handleAgregarAlCarrito()` valida la cantidad contra el stock sin considerar lo que ya está en el carrito; con stock 10 se pueden agregar 8 y luego 8 más.
2. **Puntos canjeados:** si se canjean puntos dos veces en `handleCanjearPuntos()`, el descuento suma ambos canjes, pero `puntosUsadosEnEstaVenta` se sobrescribe, por lo que al cliente solo se le restan los puntos del último canje.
3. **Recibo antes de guardar:** `handleFinalizarVenta()` muestra el recibo antes de guardar la venta; si el guardado falla, el cliente ya recibió un recibo de una venta inexistente.
