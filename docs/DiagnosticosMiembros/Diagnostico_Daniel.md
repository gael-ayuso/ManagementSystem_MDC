# Diagnóstico de Refactorización — Controllers de Gerente

* **Autor:** Daniel
* **Proyecto:** SDG_MDC (Sistema de Gestión de Materiales de Construcción)
* **Área asignada:** Controllers de la carpeta de Gerente
* **Etapa:** Etapa 1 — Inventario justificado de oportunidades (Semana 1)
* **Técnicas de referencia:** Técnicas de refactorización correspondientes a las Familias 2, 3, 4 y 5.

---

## Tabla Resumen de Oportunidades Identificadas

| ID | Ubicación | Señal encontrada | Técnica propuesta | Prioridad |
| :---: | :--- | :--- | :--- | :---: |
| **DA-01** | `GerenteClienteController`<br>`abrirDialogoCliente()` | El método concentra la creación del diálogo, construcción de campos, carga de información, validaciones y creación o modificación del cliente. | **Extract Class** | Alta |
| **DA-02** | `GerenteProductoFormController`<br>`handleGuardar()` | El método contiene condiciones extensas para validar campos obligatorios y reglas antes de guardar un producto. | **Decompose Conditional** | Media |
| **DA-03** | `GerenteClienteController`<br>`setupFilters()` | Se utiliza directamente el valor `0` para identificar al cliente que debe ocultarse de la tabla, sin indicar su significado. | **Replace Magic Number with Symbolic Constant** | Media |
| **DA-04** | `GerenteReporteController`<br>`procesarDatos()` | El método realiza lógica de filtrado, consultas, cálculos de reportes y actualización de componentes de la interfaz. | **Move Method** | Alta |
| **DA-05** | `GerenteInventarioController`<br>`handleAgregar()` y `handleEditar()` | Ambos métodos repiten el proceso para cargar y mostrar el formulario de productos, cambiando principalmente el producto que se envía. | **Parameterize Method** | Media |

---

## Fichas Detalladas por Oportunidad

### Oportunidad 1: Separación del Formulario de Clientes

* **Identificador:** DA-01
* **Autor:** Daniel
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Clase: `GerenteClienteController`
  * Método implicado: `abrirDialogoCliente()`
* **Señal encontrada:**
  El método `abrirDialogoCliente()` controla prácticamente todo el proceso relacionado con el formulario de clientes. Dentro del mismo método se crea el diálogo, se construyen los campos del formulario, se cargan los datos cuando se está editando un cliente, se valida la información capturada, se crea o modifica el objeto `Cliente` y finalmente se realiza la operación de guardado o actualización.

  Esto provoca que `GerenteClienteController` tenga más de una responsabilidad, ya que además de administrar la tabla de clientes y las operaciones CRUD, también se encarga directamente de toda la lógica relacionada con la construcción y validación del formulario.

* **Técnica propuesta:**
  **Extract Class** — Familia 2.

* **Justificación:**
  Se propone extraer la lógica relacionada con el formulario a una nueva clase, por ejemplo `ClienteFormDialog` o `ClienteFormController`. Esta nueva clase tendría la responsabilidad de construir el formulario, cargar los datos de un cliente cuando sea necesario y validar la información ingresada.

  De esta manera, `GerenteClienteController` podría mantenerse enfocado en administrar la tabla, seleccionar clientes y ejecutar las operaciones CRUD, mientras que la nueva clase se encargaría exclusivamente del formulario. Esto permitiría reducir el tamaño y las responsabilidades del controlador principal, además de facilitar futuras modificaciones en la interfaz del formulario.

* **Comportamiento que debe conservarse:**
  El usuario debe poder continuar agregando y editando clientes de la misma manera. Los campos mostrados, las validaciones existentes y las operaciones de guardado o actualización deben conservar exactamente el mismo comportamiento observable.

* **Prueba prevista:**
  Realizar pruebas que:
  1. Comprueben que el formulario para agregar clientes continúa mostrando todos los campos necesarios.
  2. Verifiquen que al editar un cliente se carguen correctamente sus datos existentes.
  3. Comprueben que las validaciones impidan guardar información inválida.
  4. Verifiquen que los clientes puedan seguir siendo creados y modificados correctamente después de separar la clase.

* **Prioridad:**
  **Alta.** El método concentra una cantidad considerable de responsabilidades, por lo que separar el formulario ayudaría a mejorar de manera importante la organización y mantenibilidad del controlador.

---

### Oportunidad 2: Separación de las Condiciones de Validación del Producto

* **Identificador:** DA-02
* **Autor:** Daniel
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Clase: `GerenteProductoFormController`
  * Método implicado: `handleGuardar()`
* **Señal encontrada:**
  En el método `handleGuardar()` existen condiciones extensas encargadas de verificar los campos obligatorios y diferentes reglas antes de permitir que un producto sea guardado.

  Actualmente, para comprender qué validaciones se están realizando es necesario analizar directamente varias expresiones dentro de los bloques `if`. Esto hace que el método sea menos claro y dificulta identificar rápidamente qué condición está provocando que un producto no pueda guardarse.

* **Técnica propuesta:**
  **Decompose Conditional** — Familia 4.

* **Justificación:**
  Se propone separar las diferentes condiciones de validación en métodos pequeños con nombres que indiquen claramente qué se está comprobando. Por ejemplo:

  `hayCamposObligatoriosVacios()`

  `stockEsInvalido()`

  `nombreProductoDuplicado()`

  De esta manera, `handleGuardar()` podría expresar directamente las reglas que deben cumplirse antes de guardar el producto, sin incluir toda la lógica de las condiciones dentro del mismo método.

  Esto facilitaría la lectura, mantenimiento y modificación de las validaciones sin alterar el funcionamiento actual del programa.

* **Comportamiento que debe conservarse:**
  Las mismas validaciones deben continuar ejecutándose antes de guardar un producto. Ningún producto que actualmente sea considerado inválido debe poder guardarse después de aplicar la refactorización.

* **Prueba prevista:**
  Realizar pruebas que:
  1. Intenten guardar un producto con campos obligatorios vacíos.
  2. Comprueben el comportamiento cuando los valores de stock sean inválidos.
  3. Verifiquen que no puedan guardarse productos duplicados cuando la validación correspondiente aplique.
  4. Confirmen que un producto con todos sus datos correctos se guarde normalmente.

* **Prioridad:**
  **Media.** La refactorización mejora principalmente la legibilidad y mantenimiento del método sin modificar directamente la funcionalidad del sistema.

---

### Oportunidad 3: Número Mágico para Identificar al Cliente Oculto

* **Identificador:** DA-03
* **Autor:** Daniel
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Clase: `GerenteClienteController`
  * Método implicado: `setupFilters()`
  * Condición identificada: `if (c.getIdCliente() == 0) return false;`

* **Señal encontrada:**
  Dentro de `setupFilters()` se utiliza directamente el número `0` para determinar que un cliente no debe mostrarse dentro de la tabla.

  Aunque el programa conoce el propósito de este valor, una persona que lea el código no puede saber inmediatamente qué representa el número `0` ni por qué ese cliente debe ser excluido.

* **Técnica propuesta:**
  **Replace Magic Number with Symbolic Constant** — Familia 3.

* **Justificación:**
  Se propone reemplazar el valor literal `0` por una constante con un nombre que explique su propósito. Por ejemplo:

  `private static final int ID_CLIENTE_OCULTO = 0;`

  Posteriormente, la condición podría escribirse como:

  `if (c.getIdCliente() == ID_CLIENTE_OCULTO) return false;`

  Utilizar una constante permite que el significado del valor sea más claro y evita depender directamente de un número cuyo propósito no es evidente al leer el código.

* **Comportamiento que debe conservarse:**
  El cliente cuyo identificador es `0` debe continuar sin mostrarse en la tabla. Los demás clientes deben seguir apareciendo normalmente de acuerdo con los filtros existentes.

* **Prueba prevista:**
  Realizar pruebas que:
  1. Comprueben que el cliente con identificador `0` continúe oculto.
  2. Verifiquen que los clientes con identificadores diferentes continúen mostrándose.
  3. Comprueben que los demás filtros de búsqueda sigan funcionando de la misma manera.

* **Prioridad:**
  **Media.** Es una modificación pequeña y de bajo riesgo que mejora la comprensión del código y permite identificar claramente el propósito del valor utilizado.

---

### Oportunidad 4: Separación de la Lógica de Procesamiento de Reportes

* **Identificador:** DA-04
* **Autor:** Daniel
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Clase: `GerenteReporteController`
  * Método implicado: `procesarDatos()`
* **Señal encontrada:**
  El método `procesarDatos()` realiza una cantidad considerable de lógica que no corresponde únicamente al manejo de la interfaz gráfica.

  El método obtiene las ventas, las filtra por fechas, consulta sus detalles, busca información de productos, aplica filtros de categoría, calcula ingresos, determina el producto más vendido y obtiene la categoría con mayores ventas. Después de realizar estos cálculos, también se encarga de actualizar etiquetas y gráficas dentro de la interfaz.

  Por lo tanto, el controlador combina lógica de presentación con lógica de procesamiento de datos y generación de reportes.

* **Técnica propuesta:**
  **Move Method** — Familia 2.

* **Justificación:**
  Se propone mover la lógica relacionada con el procesamiento y cálculo de los reportes a una clase especializada, por ejemplo `ReporteService`.

  Esta clase podría encargarse de recibir los criterios seleccionados por el usuario, filtrar las ventas y calcular los resultados necesarios para el reporte. Posteriormente, `GerenteReporteController` solamente tendría que obtener los filtros de la interfaz, llamar al servicio y utilizar los resultados obtenidos para actualizar las etiquetas y gráficas.

  De esta forma se separaría la lógica de negocio de la lógica de presentación y sería más sencillo modificar los cálculos de los reportes sin afectar directamente al controlador de JavaFX.

* **Comportamiento que debe conservarse:**
  Los filtros por fecha y categoría deben continuar funcionando de la misma manera. Los ingresos calculados, el producto más vendido, la categoría con mayores ventas y los datos mostrados en las gráficas deben mantenerse iguales a los resultados actuales.

* **Prueba prevista:**
  Realizar pruebas que:
  1. Comprueben el filtrado de ventas utilizando diferentes rangos de fechas.
  2. Verifiquen el filtrado por categoría.
  3. Comparen los ingresos calculados antes y después de la refactorización.
  4. Comprueben que el producto más vendido y la categoría con mayores ventas continúen obteniéndose correctamente.
  5. Verifiquen que el controlador siga mostrando correctamente los resultados en etiquetas y gráficas.

* **Prioridad:**
  **Alta.** El método combina una gran cantidad de procesamiento de datos con tareas de interfaz, por lo que mover esta lógica permitiría mejorar considerablemente la separación de responsabilidades.

---

### Oportunidad 5: Unificación de la Apertura del Formulario de Productos

* **Identificador:** DA-05
* **Autor:** Daniel
* **Ubicación:**
  * Proyecto: `SDG_MDC`
  * Clase: `GerenteInventarioController`
  * Métodos implicados: `handleAgregar()` y `handleEditar()`
* **Señal encontrada:**
  Los métodos `handleAgregar()` y `handleEditar()` realizan prácticamente el mismo proceso para abrir el formulario de productos.

  Ambos métodos cargan el archivo FXML, obtienen una instancia de `GerenteProductoFormController`, crean un `Stage`, configuran la ventana y esperan hasta que el formulario sea cerrado.

  La diferencia principal es que `handleEditar()` envía un producto existente para que sus datos sean cargados, mientras que `handleAgregar()` abre el formulario sin proporcionar un producto.

* **Técnica propuesta:**
  **Parameterize Method** — Familia 5.

* **Justificación:**
  Se propone crear un único método que reciba como parámetro el producto que se desea editar:

  `private void abrirFormularioProducto(Producto producto)`

  De esta manera, `handleAgregar()` podría llamar:

  `abrirFormularioProducto(null);`

  Mientras que `handleEditar()` podría enviar el producto seleccionado:

  `abrirFormularioProducto(productoSeleccionado);`

  Así se evita repetir la misma configuración del formulario en dos métodos diferentes y la diferencia entre agregar o editar se controla mediante un parámetro.

* **Comportamiento que debe conservarse:**
  Al seleccionar la opción de agregar, el formulario debe continuar abriéndose vacío para registrar un nuevo producto. Al seleccionar la opción de editar, debe continuar cargando los datos del producto seleccionado. La ventana y su comportamiento deben permanecer iguales.

* **Prueba prevista:**
  Realizar pruebas que:
  1. Comprueben que `handleAgregar()` abra correctamente un formulario vacío.
  2. Verifiquen que `handleEditar()` cargue los datos del producto seleccionado.
  3. Comprueben que la ventana continúe configurándose y cerrándose de la misma manera.
  4. Verifiquen que después de agregar o editar un producto la información del inventario continúe actualizándose correctamente.

* **Prioridad:**
  **Media.** La refactorización elimina código duplicado y facilita el mantenimiento del proceso de apertura del formulario sin modificar el comportamiento existente.