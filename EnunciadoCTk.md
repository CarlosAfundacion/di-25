
# Práctica Guiada — Registro de Usuarios con CustomTkinter y MVC

## 1. Objetivo del Proyecto

En esta práctica, vamos a construir una aplicación de escritorio completa y bien estructurada para el registro de usuarios. Partiremos de los conceptos básicos de CustomTkinter y aplicaremos la **arquitectura Modelo-Vista-Controlador (MVC)**, un estándar profesional que nos permitirá mantener nuestro código organizado, escalable y fácil de mantener.

La aplicación final permitirá:
*   Alta, listado, selección, edición y eliminación de usuarios.
*   Persistencia de datos en un archivo CSV (guardar/cargar).
*   Previsualización de un avatar (imagen) por cada usuario.
*   *Funcionalidades avanzadas (Fases 4-5):* Búsqueda, filtrado y auto-guardado.

> **Restricción Didáctica:** Para esta práctica, nos limitaremos a usar las herramientas vistas en los apuntes: `customtkinter`, `tkinter.Menu`, `csv`, manejo de excepciones, `pathlib` y callbacks (`command` y `lambda`). No se deben usar librerías externas ni patrones no explicados.

---

## 2. Requisitos Técnicos y Estructura

### 2.1. Arquitectura MVC
El proyecto **debe** seguir esta estructura de archivos y carpetas:

```
registro_usuarios_ctk/
├──__init__.py
├── app.py                      # Punto de entrada de la aplicación
├── assets/                     # Carpeta para imágenes (avatares)
│   ├── avatar1.png
│   └── ...
├── model/
│   ├── __init__.py
│   └── usuario_model.py        # Clases de datos y lógica de negocio
├── view/
│   ├── __init__.py
│   └── main_view.py            # Clases de la interfaz gráfica
└── controller/
    ├── __init__.py
    └── app_controller.py       # Orquesta la comunicación entre Modelo y Vista
```

**Responsabilidades Claras:**
*   **Modelo:** La "cocina". Contiene los datos y las reglas (`Usuario`, `GestorUsuarios`). **No debe importar `customtkinter`**.
*   **Vista:** El "comedor". Construye la interfaz con widgets de CustomTkinter. **No contiene lógica de negocio**.
*   **Controlador:** El "camarero". Recibe eventos de la Vista, pide al Modelo que actúe y actualiza la Vista con los resultados.

### 2.2. CustomTkinter y Arranque
*   Toda la interfaz usará widgets de **CustomTkinter**.
*   El archivo `app.py` será el punto de entrada y configurará la apariencia inicial:

```python
# app.py (Arranque mínimo)
import customtkinter as ctk
from controller.app_controller import AppController

if __name__ == "__main__":
    ctk.set_appearance_mode("System")
    ctk.set_default_color_theme("blue")

    app = ctk.CTk()
    app.title("Registro de Usuarios (CTk + MVC)")
    app.geometry("800x500")

    controller = AppController(app) # El controlador inicia todo lo demás
    app.mainloop()
```

---

## 3. Guía de Implementación Paso a Paso

### **Fase 1: El Esqueleto MVC y la Visualización de Datos**

**Objetivo:** Crear la estructura base funcional. Al final de esta fase, la aplicación arrancará, mostrará una lista de usuarios de prueba y permitirá seleccionarlos para ver sus detalles.

**Concepto Clave: ¿Qué es MVC?**
Imagina que construyes un restaurante:
*   **El Modelo:** Es la **cocina**. Contiene los ingredientes (los datos) y las recetas (la lógica para manipularlos). La cocina no sabe nada de los clientes.
*   **La Vista:** Es el **comedor**. Es todo lo que el cliente ve: las mesas, la carta, los platos. Solo muestra la comida y toma los pedidos.
*   **El Controlador:** Es el **camarero**. Conecta todo: toma un pedido de la Vista, lo lleva al Modelo, recoge el plato y lo lleva de vuelta a la Vista.

#### **Paso 1.1: El Modelo (`model/usuario_model.py`)**

1.  **Crea la clase `Usuario`**: Una clase simple con `__init__` para almacenar `nombre`, `edad`, `genero` y `avatar`.
2.  **Crea la clase `GestorUsuarios`**:
    *   En su `__init__`, crea una lista vacía para los usuarios (`self._usuarios = []`).
    *   **Para probar**, crea un método privado (ej. `_cargar_datos_de_ejemplo`) que añada 2 o 3 objetos `Usuario` a la lista. Llama a este método desde el `__init__`.
    *   Crea un método `listar(self)` que devuelva la lista de usuarios.

#### **Paso 1.2: La Vista (`view/main_view.py`)**

1.  **Crea la clase `MainView`**.
2.  En `__init__`, diseña el layout principal usando un `grid` de dos columnas.
3.  **Crea los widgets**: Un `CTkScrollableFrame` a la izquierda y varios `CTkLabel` a la derecha.
4.  **Crea el método `actualizar_lista_usuarios(self, usuarios, on_seleccionar_callback)`**: Este método recibirá datos y una **función** del Controlador para conectar los eventos.
5.  **Crea el método `mostrar_detalles_usuario(self, usuario)`**: Su única tarea es actualizar el texto de los `CTkLabel`.

#### **¡Foco en los Callbacks! - El Corazón de la Interacción**

**Concepto Clave: ¿Qué es un `Callback`?**
Un `callback` (o "función de retorno de llamada") es como dejar tu número de teléfono. Le dices a un widget: "Cuando alguien haga clic en ti, no tomes tú la decisión, simplemente **llámame a este número** (a esta función)".

El botón de la Vista no sabe qué hacer, solo sabe a qué función del Controlador tiene que llamar.

**Pista de Implementación para la lista de usuarios:**
No podemos hacer `command=self.seleccionar_usuario` directamente, porque ¿cómo sabría el controlador *qué* usuario se ha pulsado? Necesitamos pasarle el índice. Aquí es donde `lambda` brilla.

```python
# Snippet para view/main_view.py en el método actualizar_lista_usuarios
# Dentro del bucle que crea los botones...
for i, usuario in enumerate(usuarios):
    btn = ctk.CTkButton(
        self.lista_usuarios_scrollable,
        text=usuario.nombre,
        # Esto crea una mini-función que "recuerda" el valor de 'i'
        # y lo pasa al callback que nos dio el controlador.
        command=lambda idx=i: on_seleccionar_callback(idx)
    )
    btn.pack(fill="x", padx=5, pady=2)
```

#### **Paso 1.3: El Controlador (`controller/app_controller.py`)**

1.  **Crea la clase `AppController`**. En `__init__`, instancia el Modelo y la Vista.
2.  **Crea el método `refrescar_lista_usuarios(self)`**: Pide los datos al modelo y los pasa a la vista, junto con el callback de selección. Su cnotenido sería el siguiente:
   <img width="899" height="69" alt="imagen" src="https://github.com/user-attachments/assets/851e6e9e-e60e-4ac4-881f-65d21043dcc6" />

4.  **Crea el método `seleccionar_usuario(self, indice)`**: Este es el callback. Recibe el índice, pide el usuario completo al modelo y se lo pasa a la vista para que muestre los detalles. Este método contiene entre otras cosas la llamada a la vista para que muestre los detalles.
 <img width="697" height="32" alt="imagen" src="https://github.com/user-attachments/assets/030b1506-4515-4ce9-a74b-996a987890f9" />

6.  **Llamada Inicial**: Llama a `self.refrescar_lista_usuarios()` al final del `__init__` para poblar la lista al arrancar.

**Checkpoint Fase 1:** Ejecuta `app.py`. Deberías ver una ventana con tu lista de usuarios de ejemplo. Al hacer clic en ellos, los detalles se actualizan.
<img width="1175" height="772" alt="imagen" src="https://github.com/user-attachments/assets/ff7d1964-bf40-4fe7-9622-b16883c0bd91" />


---

### **Fase 2: Añadir Usuarios y Mostrar Avatares**

**Objetivo:** Implementar la funcionalidad para añadir nuevos usuarios a través de una ventana modal y mostrar su imagen de avatar.

#### **Paso 2.1: La Vista Modal (`view/main_view.py`)**

1.  **Crea la clase `AddUserView`** sin herencia. Esta clase gestionará la ventana emergente.
    ```python
    # Estructura para AddUserView en view/main_view.py
    import customtkinter as ctk

    class AddUserView:
        def __init__(self, master):
            self.window = ctk.CTkToplevel(master)
            self.window.title("Añadir Nuevo Usuario")
            self.window.geometry("300x350")
            self.window.grab_set() # ¡Esto la hace modal!
            
            # --- Aquí dentro, crea todos tus widgets y añádelos a self.window ---
            self.nombre_entry = ctk.CTkEntry(self.window)
            self.nombre_entry.pack(...)
            self.guardar_button = ctk.CTkButton(self.window, text="Guardar")
            self.guardar_button.pack(...)
    ```
2.  **Crea el método `get_data(self)`** en `AddUserView`, que recoja y devuelva los valores del formulario en un diccionario.

#### **Paso 2.2: El Controlador (`controller/app_controller.py`)**

1.  **Abre la ventana**: Crea el método `abrir_ventana_añadir(self)`.
2.  **Conecta el callback de guardado**: El controlador es quien le da vida al botón "Guardar" de la ventana modal.
    ```python
    # Snippet para controller/app_controller.py en abrir_ventana_añadir
    add_view = AddUserView(self.master)
    # Le decimos al botón "Guardar": "Cuando te pulsen, llama a 'añadir_usuario'
    # del controlador y pásale una referencia a ti misma (add_view)".
    add_view.guardar_button.configure(command=lambda: self.añadir_usuario(add_view))
    ```
3.  **Procesa los datos**: Crea `añadir_usuario(self, add_view)`.
    *   Obtén los datos con `add_view.get_data()`.
    *   **¡Valida!** Usa `try-except` para la edad y muestra un `messagebox.showerror` si hay un error.
    *   Si todo está OK, crea el `Usuario`, añádelo al modelo, refresca la lista y cierra la modal con `add_view.window.destroy()`.

#### **Pista Clave: ¡Ojo con las Rutas de Archivo y las Imágenes!**

**Problema 1: Rutas que se rompen.** `"assets/avatar1.png"` puede fallar si ejecutas el script desde otra carpeta.
**Solución:** Usa `pathlib` para crear rutas seguras.
```python
# Snippet para el __init__ del Controlador
from pathlib import Path
self.BASE_DIR = Path(__file__).resolve().parent.parent
self.ASSETS_PATH = self.BASE_DIR / "assets"
```

**Problema 2: Las imágenes desaparecen.** Python es muy eficiente y elimina objetos que no se usan (recolección de basura).
**Solución:** Crea un diccionario en tu controlador para guardar las imágenes cargadas.
```python
# Snippet para el __init__ del Controlador
self.avatar_images = {} # Esta "caché" mantiene vivas las imágenes
```
Recuerda también que si un usuario no tiene imagen, la vista debe borrar la del anterior (`self.avatar_label.configure(image="")`).

**Checkpoint Fase 2:** Ahora deberías poder añadir nuevos usuarios con su avatar. Al seleccionarlos, su imagen debe aparecer.
<img width="1161" height="772" alt="imagen" src="https://github.com/user-attachments/assets/4bb1b3aa-b0c5-4a3b-9284-373ada3556cf" />
<img width="1114" height="731" alt="imagen" src="https://github.com/user-attachments/assets/51779820-ab6d-4f59-abcc-09105a466a4d" />

---

### **Fase 3: Persistencia de Datos con CSV**

**Objetivo:** Guardar la lista de usuarios en un archivo para que los datos no se pierdan.

#### **Paso 3.1: El Modelo (`model/usuario_model.py`)**

1.  **Importa el módulo `csv`**.
2.  **Crea el método `guardar_csv`**: Usa `csv.writer` para escribir la cabecera y los datos.
3.  **Crea el método `cargar_csv`**:
    *   Usa `try-except FileNotFoundError`.
    *   Usa `csv.reader` y sáltate la cabecera con `next(lector)`.
    *   **¡Importante!** Limpia la lista actual (`self._usuarios.clear()`) antes de cargar.
    *   **Pista de robustez:** Envuelve la lectura de cada fila en su propio `try-except` para que una línea corrupta en el CSV no rompa toda la aplicación.

#### **Paso 3.2: La Vista (`view/main_view.py`)**

1.  **Importa `tkinter`**.
2.  En el `__init__`, crea la barra de menú. La Vista solo crea los "contenedores" del menú.
    ```python
    # Snippet para el menú en MainView.__init__
    self.menubar = tkinter.Menu(master)
    master.config(menu=self.menubar)
    self.menu_archivo = tkinter.Menu(self.menubar, tearoff=0)
    self.menubar.add_cascade(label="Archivo", menu=self.menu_archivo)
    ```

#### **Paso 3.3: El Controlador (`controller/app_controller.py`)**

1.  **Conecta el menú**: En el `__init__`, añade los comandos a los menús que expone la vista.
    ```python
    # Pista para conectar el menú en AppController.__init__
    self.view.menu_archivo.add_command(label="Guardar", command=self.guardar_usuarios)
    # ... y los demás ...
    ```
2.  **Crea los métodos manejadores** (`guardar_usuarios`, `cargar_usuarios`). Estos llaman al modelo y luego usan `messagebox` para dar feedback al usuario.
3.  **Carga Inicial Automática**: Al final del `__init__`, llama a `self.cargar_usuarios()` para que la aplicación intente cargar el CSV al arrancar.

**Checkpoint Fase 3:** Tu aplicación ahora debería poder guardar y cargar su estado. ¡Los datos persisten entre sesiones!
<img width="1172" height="776" alt="imagen" src="https://github.com/user-attachments/assets/818a632c-3980-47c2-8079-c7ded1485b4a" />

---

### Fase 4 — **Búsqueda/filtrado** + **barra de estado** + **editar**

* Búsqueda por nombre (con `trace_add`) y filtro por género.
* Barra de estado con recuentos y mensajes (guardado OK, errores, etc.).
* Doble clic en usuario para **editar** en modal y actualizar registro.
* **Entrega**: filtros dinámicos, mensajes en barra de estado y edición operativa.
<img width="660" height="462" alt="imagen" src="https://github.com/user-attachments/assets/2a27094c-53c9-4059-8a82-e2e691921132" />

<img width="816" height="295" alt="imagen" src="https://github.com/user-attachments/assets/44a21740-09cc-4701-9fc5-56a8dc41a27c" />


### Fase 5 — **Auto-guardado con hilos**

* Botón que activa un hilo que cada 10 s guarda CSV; comunicar a la UI con `after()`. Parar hilo en `salir()`.
* **Entrega**: versión final estable; (si aplica) auto-guardado no bloqueante.
  
<img width="666" height="469" alt="imagen" src="https://github.com/user-attachments/assets/c6759ce3-7614-486f-8d55-eeb0bc6063e1" />



  

---

## Entrega

1. Enlace a GitHub del proyecto (`registro_usuarios_ctk`) con:

   * Estructura de carpetas obligatoria.
   * Carpeta `assets/` con avatares.
   * `usuarios.csv` de ejemplo.
2. **vídeo corto** mostrando:

   * Alta con avatar, edición, eliminación.
   * Búsqueda/filtro y barra de estado.
   * Guardar/cargar CSV.
   * (Bonus) Auto-guardado en acción.
3. Commits por fase (mínimo 5) con mensajes claros.

---

## Recomendaciones prácticas (para el alumnado)

* **No toquéis widgets desde hilos** . Usad `after()`.
* Conservad referencias a `CTkImage` (atributos de instancia).
* En el modelo, validad antes de mutar (`añadir/actualizar`).
* En la vista, exponed **métodos** tipo `set_lista(usuarios)`, `mostrar_usuario(usuario)`, `set_estado(texto)`.
* En el controlador, centralizad los **bindings** y la lógica de filtrado.

---

## Arranque mínimo (sugerencia para `app.py`)

```python
import customtkinter as ctk
from controller.app_controller import AppController

if __name__ == "__main__":
    ctk.set_appearance_mode("System")
    ctk.set_default_color_theme("blue")

    app = ctk.CTk()
    app.title("Registro de Usuarios (CTk + MVC)")
    app.geometry("900x600")

    controller = AppController(app)  # crea modelo y vista dentro
    app.mainloop()
```

