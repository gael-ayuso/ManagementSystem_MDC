# Actividad: Diagnóstico y refactorización de un proyecto legado

* **Duración:** Dos semanas.
* **Organización:** [Equipos](https://extranet.matematicas.uady.mx/enlinea/mod/resource/view.php?id=48855) de cuatro; **8 horas de trabajo por estudiante cada semana** (64 horas acumuladas por equipo).
* **Proyectos:** El proyecto legado del equipo y, cuando haga falta, proyectos escolares desarrollados previamente.

La actividad tendrá dos etapas:
1. **Primera etapa:** Cada estudiante diagnosticará cinco oportunidades de refactorización aplicando técnicas diferentes y revisando código diferente.
2. **Segunda etapa:** Seleccionará, aplicará y justificará entre tres y cinco, con las pruebas correspondientes.

Esto produce un inventario de **20 oportunidades distintas por equipo** y una entrega de **12 a 20 refactorizaciones aplicadas**, según la complejidad de los cambios.

> **NOTA:** Todas las refactorizaciones entregadas del bloque 1 de técnicas no se pueden utilizar, deben ser nuevos problemas identificados, pero sí pueden utilizar las técnicas del bloque 1.

---

## Objetivos de aprendizaje

Al concluir, cada estudiante podrá:

1. Reconocer en código existente una señal concreta que justifique una refactorización.
2. Elegir una técnica adecuada y explicar por qué corresponde a ese problema.
3. Aplicar cambios pequeños y comprobar que se conserva el comportamiento esperado.
4. Construir o adaptar pruebas unitarias y ejecutarlas antes y después de los cambios.
5. Documentar y defender sus decisiones usando el código, los cambios realizados y los resultados de las pruebas.

---

## Etapa 1. Inventario justificado de oportunidades — Semana 1

**Meta individual:** Cinco oportunidades diagnosticadas. Deben referirse a ubicaciones y problemas concretos; cuatro estudiantes no deben presentar como oportunidades distintas el mismo fragmento y problema. Sí pueden identificar una misma técnica para problemas diferentes.

### Distribución de tiempo sugerido (Semana 1)

| Trabajo por estudiante | Tiempo sugerido |
| :--- | :---: |
| Preparar el proyecto, ejecutarlo y registrar el estado inicial de sus pruebas | 1 h |
| Revisar el código e identificar cinco oportunidades | 3 h |
| Investigar el comportamiento de las zonas elegidas y formular la justificación | 2 h |
| Revisar el inventario con el equipo, eliminar duplicados y priorizar | 1 h |
| Elegir de tres a cinco oportunidades para la segunda semana y planear cómo probarlas | 1 h |
| **Total** | **8 h** |

### Campos obligatorios por entrada del inventario

| Campo | Qué debe registrar el estudiante |
| :--- | :--- |
| **Ubicación** | Proyecto, archivo, clase o función y método implicado. |
| **Señal encontrada** | Qué dificulta comprender, modificar o probar ese código. |
| **Técnica propuesta** | Nombre de la técnica vista en clase. |
| **Justificación** | Por qué esa técnica resuelve la señal identificada. |
| **Comportamiento que debe conservarse** | Qué resultado debe seguir siendo igual después del cambio. |
| **Prueba prevista** | Qué prueba existente se usará o qué prueba habrá que construir. |
| **Prioridad** | Alta, media o baja, con una razón breve. |
| **Autor** | Nombre del que realiza el diagnóstico. |
| **Identificador** | Número secuencial para el equipo del problema identificado. |

**Entrega al final de la semana 1:**  
Inventario consolidado de 20 oportunidades, resultado inicial de las pruebas disponibles y selección preliminar de tres a cinco cambios por estudiante. El docente puede revisar la selección antes de que comiencen a modificar el código.

---

## Etapa 2. Refactorización y comprobación — Semana 2

**Meta individual:** Aplicar y defender **de tres a cinco** oportunidades del inventario. Tres cambios bien justificados y probados cumplen la meta; el intervalo permite que un estudiante asuma cambios más complejos sin verse obligado a completar cinco.

### Distribución de tiempo sugerido (Semana 2)

| Trabajo por estudiante | Tiempo sugerido |
| :--- | :---: |
| Construir o ajustar pruebas para el comportamiento que va a proteger; ejecutarlas antes del cambio | 2 h |
| Aplicar sus refactorizaciones en ciclos pequeños y ejecutar las pruebas después de cada cambio | 4 h |
| Revisar el trabajo de otro integrante y atender las observaciones recibidas | 1 h |
| Completar evidencias y preparar la explicación individual | 1 h |
| **Total** | **8 h** |

### Adiciones requeridas en el inventario para cada cambio aplicado

1. **Identificador de refactorización:** Número secuencial de refactorización del equipo.
2. **Valoración:** Qué mejoró y qué limitación permanece, si la hay.
3. **Prueba:** Nombre o identificador de la prueba aplicada.

> **Aclaración:** Una prueba puede proteger más de un cambio cuando realmente comprueba el comportamiento afectado. Si una oportunidad resulta improcedente al estudiarla con mayor detalle, el estudiante explicará por qué la descartó y elegirá otra del inventario del equipo.

**Entrega final:**  
Proyectos originales (código), proyectos modificados (código), inventario actualizado, resultados de las pruebas y una demostración breve. Durante la demostración, el docente podrá elegir al azar uno de los cambios de cada integrante para que lo explique.

---

## Rúbrica de evaluación

| Criterio | Puntos | Evidencia para obtenerlos |
| :--- | :---: | :--- |
| **Diagnóstico individual** | 4 | Cinco oportunidades concretas, localizables y justificadas; elección pertinente de técnicas. |
| **Inventario y priorización del equipo** | 2 | Veinte oportunidades sin duplicar el mismo problema; prioridades razonadas y selección viable para la segunda etapa. |
| **Aplicación individual** | 8 | Entre tres y cinco refactorizaciones pertinentes, correctamente implementadas y explicadas. Se valora la calidad del cambio, no obtener cinco por obligación. |
| **Pruebas y conservación del comportamiento** | 4 | Pruebas significativas para las zonas modificadas, ejecuciones registradas antes y después, e investigación de cualquier fallo. |
| **Trazabilidad, revisión y defensa** | 2 | Cambios localizables en el historial, fichas completas, revisión entre compañeros y capacidad individual para explicar decisiones. |
| **Total** | **20** | |

---

### Criterio para aceptar un cambio
Debe existir una señal previa real, un cambio pertinente y evidencia de comprobación. Un cambio que altera deliberadamente la funcionalidad, o que se revierte solo para contar otra técnica, **no se acredita** como refactorización.