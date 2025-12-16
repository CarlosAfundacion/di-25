

#  Guía de Implementación: Gestor de Préstamos (MVVM)

**Objetivo:** Crear una aplicación Android siguiendo la arquitectura MVVM sin librerías de enlace de datos (DataBinding). Construiremos la App desde los datos (abajo) hasta la interfaz (arriba).

---
## Paquetes obligatorios

* `com.example.aulaloan.data`

* `com.example.aulaloan.viewmodel`

* `com.example.aulaloan.ui`

##  Estructura del Proyecto
Crearemos las clases en este orden:
1.  `Prestamo` (data)
2.  `PrestamosRepository` (data)
3.  `MainViewModel` (viewmodel)
4.  `MainViewModelFactory` (viewmodel)
5.  `Layout XML` (res)
6.  `MainActivity` (ui)

---

### PASO 1: El Modelo (`Prestamo.java`)
Clase simple (POJO) para representar un dato.
*   **Atributos:**
    *   `String material`
    *   `String persona`
    *   `boolean activo` (inicializar en `true` por defecto).
*   **Requisitos:** Constructor completo, Getters, Setters.

---

### PASO 2: El Repositorio (`PrestamosRepository.java`)
Simulará nuestra base de datos. Es la única clase que guarda la lista real de objetos.
*   **Atributo:** `private List<Prestamo> prestamos = new ArrayList<>();`
*   **Métodos a implementar:**
    1.  `void prestar(String mat, String per)`: Crea el objeto y añádelo a la lista.
    2.  `void devolver(String mat)`: Busca en la lista por nombre de material. Si existe y está activo, cambia su estado a `false`.
    3.  `List<Prestamo> getTodos()`: Retorna la lista.
    4.  `void reset()`: Limpia la lista (`clear()`).

---

### PASO 3: El ViewModel (`MainViewModel.java`)
Aquí reside la lógica. Conecta la UI con el Repositorio.

#### A. Variables de Estado (LiveData)
Declara `MutableLiveData` para lo que la pantalla necesita "ver" (observar):
1.  `_listado` (String): Texto largo para mostrar en pantalla.
2.  `_resumen` (String): Texto tipo "Activos: 5 | Devueltos: 2".
3.  `_error` (String): Para mensajes temporales (Toasts).

#### B. Variables de Filtros
Variables normales (no LiveData) para recordar la configuración actual:
*   `boolean verSoloActivos`
*   `String terminoBusqueda`

#### C. Lógica de Actualización (`recalcularEstado`)
Este método privado es el corazón del ViewModel. Se debe ejecutar cada vez que **algo** cambia (nuevo préstamo, cambio de filtro, búsqueda, etc.).

**Flujo del método:**
1.  Obtener todos los datos del `repository`.
2.  Filtrar la lista según `terminoBusqueda` y `verSoloActivos`.
3.  Generar el String de la lista filtrada (usa el snippet de apoyo abajo).
4.  Calcular contadores para el resumen (Total vs Activos).
5.  Actualizar los LiveData: `.setValue(...)`.

> **Snippet de Apoyo:** Copia este método en tu ViewModel para generar el texto del listado fácilmente:
> ```java
> private String formatearLista(List<Prestamo> lista) {
>     StringBuilder sb = new StringBuilder();
>     for (Prestamo p : lista) {
>         // Solo añadir si cumple los filtros (si no lo has filtrado antes)
>         String estado = p.isActivo() ? "[ACTIVO]" : "[DEVUELTO]";
>         sb.append(estado)
>           .append(" ")
>           .append(p.getMaterial())
>           .append(" : ")
>           .append(p.getPersona())
>           .append("\n");
>     }
>     return sb.toString();
> }
> ```

#### D. Métodos Públicos (Interacción)
Estos métodos serán llamados por los botones de la Activity.
*   `prestar(...)` y `devolver(...)`: Llaman al repositorio. Si hay error (ej. campos vacíos), actualizan `_error`. Si todo va bien, **llaman a `recalcularEstado()`**.
*   `setVerSoloActivos(boolean)`: Actualiza la variable local y llama a `recalcularEstado()`.
*   `setBusqueda(String)`: Actualiza la variable local y llama a `recalcularEstado()`.

---

### PASO 4: La Factory (`MainViewModelFactory.java`)
Clase necesaria para pasar el Repositorio al Constructor del ViewModel.
*Copia y pega este código:*

```java
public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final PrestamosRepository repository;

    public MainViewModelFactory(PrestamosRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(repository);
    }
}
```

---

### PASO 5: Diseño de Interfaz (`activity_main.xml`)
Diseña una pantalla que contenga los siguientes elementos. **Es vital que uses estos IDs** para seguir la guía:

1.  **Entrada de datos:**
    *   `EditText` (`etMaterial`), `EditText` (`etPersona`), `Button` (`btnPrestar`).
2.  **Devolución:**
    *   `EditText` (`etMaterialDevolver`), `Button` (`btnDevolver`).
3.  **Filtros:**
    *   `EditText` (`etBuscar`) - *Hint: "Buscar material..."*
    *   Botones: `btnVerActivos`, `btnVerTodos`, `btnReset`.
4.  **Salida de información:**
    *   `TextView` (`tvResumen`) - *Para los contadores.*
    *   `TextView` (`tvListado`) - *Dentro de un ScrollView para ver toda la lista.*

---

### PASO 6: La Activity (`MainActivity.java`)
Aquí uniremos todo. Esta clase debe ser "tonta": no toma decisiones, solo pinta lo que dice el ViewModel y le avisa cuando se pulsan botones.

**Instrucciones de codificación:**

1.  **Declarar Vistas y ViewModel:**
    *   Declara todos los `EditText`, `Button` y `TextView` definidos en el Paso 5.
    *   Declara `private MainViewModel viewModel;`.

2.  **Inicialización (onCreate):**
    *   Haz el `findViewById` de todas las vistas.
    *   **Inyección de dependencias:** Instancia el `Repository`, pásalo a la `Factory`, y usa `ViewModelProvider` para obtener tu `viewModel`.

3.  **Fase 1: OBSERVAR (Output):**
    *   Suscríbete a `viewModel.getListado()`: Cuando cambie, actualiza `tvListado.setText()`.
    *   Suscríbete a `viewModel.getResumen()`: Cuando cambie, actualiza `tvResumen.setText()`.
    *   Suscríbete a `viewModel.getError()`: Si llega un mensaje no vacío, muestra un `Toast`.

4.  **Fase 2: EVENTOS (Input):**
    *   **Botones Simples:** En el `onClick` de `btnVerActivos`, `btnReset`, etc., llama al método correspondiente del ViewModel (ej: `viewModel.setVerSoloActivos(true)`).
    *   **Botón Prestar:** Obtén el texto de los EditText, pásalos a `viewModel.prestar(...)` y luego limpia las cajas de texto (la limpieza es responsabilidad de la UI).
    *   **Buscador Reactivo:** Añade un `addTextChangedListener` al `etBuscar`. En el método `onTextChanged`, llama a `viewModel.setBusqueda(...)`. Esto hará que la lista se filtre en tiempo real mientras escribes.

---
**¡Al finalizar el paso 6, ejecuta la App!** Deberías poder prestar, devolver, filtrar y buscar, y la interfaz se actualizará "mágicamente" gracias a los Observadores.
