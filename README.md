# Laboratorio 3 - Programación III (LG-016) - Sistema de Gestión de Biblioteca

**Equipo:** [NOMBRE DEL EQUIPO]
**Repositorio:** `lab3-[nombreequipo]-programacioniii`
**Tablero Kanban:** [ENLACE AL TABLERO]

## Integrantes, participación y concepto a defender

Coevaluación de participación en escala independiente de 0 % a 100 % por persona (no suma 100 %), acordada por consenso del equipo.

| Integrante | Carnet | Participación | Concepto que defiende |
|---|---|---|---|
| [Nombre completo 1] | [carnet] | [__ %] | Manejo de excepciones (try-catch-finally, checked/unchecked, orden de los catch) |
| [Nombre completo 2] | [carnet] | [__ %] | Excepciones personalizadas, throw/throws y try-with-resources |
| [Nombre completo 3] | [carnet] | [__ %] | Interfaz gráfica Swing: ventana, layouts, eventos y diálogos |
| [Nombre completo 4] | [carnet] | [__ %] | Hilos: Thread / Runnable aplicados a una tarea del proyecto |

## Descripción y evolución del proyecto

Sistema de biblioteca con **usuarios** (estudiantes y docentes) que piden prestados **recursos** (libros y revistas).

| Laboratorio | Qué se construyó |
|---|---|
| I | Clases y objetos del dominio de la biblioteca. |
| II | Herencia, clase abstracta, polimorfismo e interfaz, con un menú por consola (MVC). |
| **III** | **Excepciones propias, ventana Swing y tareas en hilos**, sobre las mismas clases del Laboratorio II. |

### Lo del Laboratorio II que sigue funcionando

| Concepto del Lab II | Dónde sigue en el Lab III |
|---|---|
| Herencia | `Usuario` -> `Estudiante`, `Docente`; `Recurso` -> `Libro`, `Revista` (paquete `modelo`) |
| Clase abstracta | `Usuario` con el método abstracto `getLimitePrestamos()`; la ventana muestra "Préstamos 2/3" (estudiante) o "0/5" (docente) |
| Sobrecarga | `Usuario.puedePrestar()` y `puedePrestar(int)`; se usa al procesar cada préstamo |
| Sobrescritura | `getLimitePrestamos()`, `Docente.presentarse()` ("Prof."), `toString()` de `Libro` y `Revista` (catálogo) |
| Interfaz | `Prestable`, implementada por `Libro` y `Revista`; el catálogo es un `ArrayList<Prestable>` |

### Cambios hechos en el Laboratorio III

- La vista de consola se reemplazó por una ventana Swing (`vista.VentanaBiblioteca`).
- Las listas pasaron del controlador a `modelo.BibliotecaRepositorio`, que contiene la lógica de negocio.
- `Prestable` ganó `multa(int)` (método `default`) y `Revista` lo sobrescribe para cobrar el doble.
- Los constructores y el repositorio validan reglas de negocio con `throw`.
- La ventana principal solo conserva el formulario, los botones y la bitácora. **Ver Catálogo** y **Ver Usuarios** abren cada lista en su propia ventana (`vista.VentanaLista`), que se actualiza sola con cada operación.
- **Agregar Usuario** abre un formulario (Estudiante o Docente, nombre, carnet y carrera/facultad); el repositorio rechaza carnets repetidos o datos vacíos.
- El usuario de un préstamo o devolución ya no se elige de una lista: se **escribe** (carnet o nombre) y el repositorio verifica que exista (`buscarUsuario`).

## Dónde está cada requisito de la guía

| Requisito | Dónde |
|---|---|
| try-catch en 3 o más puntos | `ControladorBiblioteca`: `registrarRecurso`, `agregarUsuario`, `prestarRecurso`, `devolverRecurso`, `calcularMulta` |
| Varios catch en orden (específico a general) | `NumberFormatException` -> `ReglaNegocioException` -> `Exception` (y `RecursoNoDisponibleException` antes de `Exception`) |
| `throw` de una regla de negocio | `Recurso`, `Libro`, `Revista`, `Usuario` (constructores), `Prestable.validarDiasRetraso`, `BibliotecaRepositorio` |
| `throws` con excepción checked | `BibliotecaRepositorio.procesarPrestamo(...) throws RecursoNoDisponibleException` |
| Dos excepciones propias (checked y unchecked) | Ver la tabla de abajo |
| `finally` | `ControladorBiblioteca`: `limpiarCampos()` y `refrescarVista()` |
| try-with-resources con `AutoCloseable` propio | `RegistradorOperaciones` (bitácora de cada operación) |
| JFrame, JLabel, JTextField, JButton | `VentanaBiblioteca` (principal) y `VentanaLista` (catálogo y usuarios en ventanas aparte) |
| Layouts | `BorderLayout`, `GridLayout` y `FlowLayout` |
| `ActionListener` con lambda | `ControladorBiblioteca.conectarEventos()` |
| `JOptionPane` con `this` como padre y tipo correcto | `VentanaBiblioteca.mostrarError / mostrarAdvertencia / mostrarInformacion` |
| Tarea en hilo con `Runnable` | `hilos.TareaReporteCatalogo` (reporte lento sin congelar la ventana) |
| Bonificación `synchronized` | `BibliotecaRepositorio.procesarPrestamo` y `hilos.SimuladorConcurrencia` |

## Excepciones propias

| Excepción | Tipo | Cuándo se lanza | Dato extra |
|---|---|---|---|
| `RecursoNoDisponibleException` | Checked (`extends Exception`) | Se intenta prestar un recurso que ya está prestado | `getTituloRecurso()` y `getUsuarioActual()` |
| `ReglaNegocioException` | Unchecked (`extends RuntimeException`) | Título o autor vacío, edición menor o igual a 0, días de retraso negativos, usuario o recurso inexistente, usuario con carnet repetido o datos vacíos, límite de préstamos alcanzado, devolver algo que no tiene, título duplicado | - |

## Capturas

> Tomar las capturas en la computadora del equipo, guardarlas en la carpeta `docs/` y descomentar las líneas.

<!-- ![Ventana principal](docs/captura-ventana.png) -->
<!-- ![Diálogo de excepción propia](docs/captura-excepcion.png) -->

## Cómo compilar y ejecutar (JDK 21)

Desde la carpeta raíz del repositorio.

Linux o macOS:

```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out main.Main
```

Windows (PowerShell):

```
javac -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
java -cp out main.Main
```

Desde un IDE (IntelliJ IDEA o NetBeans): abrir la carpeta del repositorio, marcar `src` como *Sources Root* y ejecutar la clase `main.Main`.

## Estructura

```
src/
  modelo/         Usuario, Estudiante, Docente, Recurso, Libro, Revista, Prestable,
                  BibliotecaRepositorio, RegistradorOperaciones
  excepciones/    RecursoNoDisponibleException, ReglaNegocioException
  vista/          VentanaBiblioteca, VentanaLista
  controlador/    ControladorBiblioteca
  hilos/          OyenteTarea, TareaReporteCatalogo, SimuladorConcurrencia
  main/           Main
```

## Guion de la demo (3 errores a propósito)

1. **Dato no numérico:** tipo *Revista*, título "Prueba", edición `abc`, **Registrar Recurso** (diálogo de error).
2. **Regla de negocio incumplida:** título `Clean Code`, días de retraso `-1`, **Calcular Multa** (advertencia).
3. **Excepción propia:** escribir `Clean Code` y el usuario `20231234` (Ana), **Prestar Recurso**; repetir con el usuario `Luis Perez` (advertencia "Recurso no disponible" con el nombre de quien lo tiene).
4. **Usuario inexistente:** escribir el usuario `Fantasma`, **Prestar Recurso** (advertencia que sugiere usar **Agregar Usuario**). Luego **Agregar Usuario** y volver a prestar escribiendo su nombre.
5. **Ventanas aparte:** **Ver Catálogo** y **Ver Usuarios** (se actualizan solos tras cada operación).
6. **Hilos:** **Generar Reporte en Hilo** (la ventana sigue respondiendo) y **Simular Préstamo Concurrente** (un éxito y un rechazo).

## Uso de herramientas de IA

- **Qué se usó:** Claude (Anthropic).
- **Para qué:** reorganizar el proyecto del Laboratorio III sobre las clases del Laboratorio II, escribir y ajustar las excepciones, la ventana Swing y los hilos, y redactar los comentarios de defensa y esta documentación.
- **Cómo lo validó la IA en su entorno:** compiló con `javac -Xlint:all` (JDK 21) sin errores ni advertencias, ejecutó pruebas automáticas sobre el modelo (reglas de negocio, excepciones, multas y 20 simulaciones concurrentes) y probó la ventana con una pantalla virtual simulando los clics de cada botón.
- **Cómo lo validó el equipo:** [COMPLETAR: qué probó cada integrante en su computadora].
- **Un caso en que la IA dio código incorrecto y cómo se corrigió:** [COMPLETAR con un caso real del equipo].
