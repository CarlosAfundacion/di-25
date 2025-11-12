
# Práctica  — Registro de Usuarios+ con **CustomTkinter** y **MVC**

## Objetivo

Evolucionar la app de **Registro de Usuarios** (Ejercicio 14) a una versión mejor estructurada y persistente, **usando CustomTkinter** y **arquitectura MVC** en **módulos separados**.

La aplicación permitirá:

* **Alta, listado, selección, edición y eliminación** de usuarios.
* **Persistencia CSV** (guardar/cargar).
* **Previsualización con imagen (avatar)** por usuario.
* **Búsqueda/filtrado** y **barra de estado**.
*  **Auto-guardado no bloqueante** con hilos.

> **Restricción didáctica:** solo se puede usar lo visto en los apuntes adjuntos: `customtkinter`, widgets básicos, `Toplevel`, `csv`, excepciones, `bind/trace_add`, `variables de control`, `after`, `threading` (opcional).
> No usar librerías externas ni patrones no explicados.

---

## Requisitos técnicos **obligatorios**

### 1) Arquitectura y estructura de archivos (**MVC**)

El proyecto debe tener esta estructura (nombres **obligatorios**):

```
registro_usuarios_ctk/
├── app.py                      # Punto de entrada (bootstrap)
├── model/
│   ├── __init__.py
│   └── usuario_model.py        # Clases Usuario y GestorUsuarios (lógica y datos)
├── view/
│   ├── __init__.py
│   └── main_view.py            # Ventana principal y vistas auxiliares (CTk/CTkToplevel)
└── controller/
    ├── __init__.py
    └── app_controller.py       # Orquesta modelo y vista (callbacks, validaciones)
```

**Responsabilidades:**

* **Model**: clases de datos y lógica de negocio. *No importa CTk ni widgets*.
* **View**: widgets y layout con **CustomTkinter**. *No contiene reglas de negocio*.
* **Controller**: recibe eventos de la vista, invoca al modelo, y actualiza la vista.

### 2) CustomTkinter

* Toda la UI debe usar **CustomTkinter** (`CTk`, `CTkFrame`, `CTkLabel`, `CTkEntry`, `CTkButton`, `CTkTextbox` o `CTkScrollableFrame`, `CTkCheckBox`, `CTkRadioButton`, `CTkOptionMenu`/`CTkComboBox` si lo deseáis, `CTkToplevel`).
* Apariencia inicial configurada en `app.py`:

  ```python
  import customtkinter as ctk
  ctk.set_appearance_mode("System")      # "Light"/"Dark" permitido
  ctk.set_default_color_theme("blue")    # “green” o “dark-blue” opcional
  ```

### 3) Persistencia CSV

* Guardar y cargar en `usuarios.csv` con cabecera:
  `nombre,edad,genero,avatar`
* Codificación **UTF-8** y `newline=''` (apuntes).
* Manejo de **excepciones**: `FileNotFoundError`, filas corruptas, etc.

### 4) Imágenes (avatares)

* Carpeta `assets/` con al menos 3 imágenes (p. ej. `avatar1.png`, `avatar2.png`, `avatar3.png`, ~64–96 px).
* Previsualización del avatar en la vista (CTKLabel con CTkImage)).
* **Mantener referencia** a las `CTkImage` (atributos de clase o variables) para evitar que se recolecten.

### 5) Flujo principal y funciones mínimas

* **Alta** con **CTkToplevel modal** (`grab_set()`), validando: nombre no vacío, edad numérica 0–100, género (“masculino”, “femenino”, “otro”), avatar seleccionado.
* **Edición** con **CTkToplevel** al hacer doble clic sobre un usuario listado.
* **Eliminación** del usuario seleccionado.
* **Listado** con scroll y **previsualización** (datos + avatar).
* **Búsqueda/filtrado** (por nombre y/o género) con `trace_add`/`command`.
* **Barra de estado** para mensajes (errores, guardado OK, recuentos).
* Menú “Archivo” → **Guardar**, **Cargar**, **Salir**; “Ayuda” → **Acerca de**.

### 6) (Ampliación opcional) Auto-guardado no bloqueante

* Botón que activa un **thread** que cada 10 s ejecuta guardado.
* **Nunca** actualizar widgets desde el hilo; usar `after(0, ...)`.
* Al salir, detener el hilo de forma segura.

---

## Detalle por capas

### Modelo — `model/usuario_model.py`

```python
class Usuario:
    def __init__(self, nombre: str, edad: int, genero: str, avatar: str):
        self.nombre = nombre
        self.edad = edad
        self.genero = genero
        self.avatar = avatar  # ruta relativa en assets/

class GestorUsuarios:
    def __init__(self):
        self._usuarios = []  # lista de Usuario

    def listar(self):
        return list(self._usuarios)

    def añadir(self, usuario: Usuario):
        # validaciones mínimas (nombre no vacío, edad en rango, genero permitido)
        self._usuarios.append(usuario)

    def eliminar(self, indice: int):
        # controlar índices fuera de rango
        ...

    def actualizar(self, indice: int, usuario_actualizado: Usuario):
        ...

    def guardar_csv(self, ruta: str = "usuarios.csv"):
        # csv.writer, utf-8, newline='', try/except
        ...

    def cargar_csv(self, ruta: str = "usuarios.csv"):
        # limpia y repuebla _usuarios; maneja FileNotFoundError y filas corruptas
        ...
```

### Vista — `view/main_view.py`

* Clase `MainView` que recibe `master` (la `CTk` que crea `app.py`).
* Componentes mínimos:

  * **Zona izquierda**: lista de usuarios (p.ej. `CTkTextbox` o un contenedor con filas), **Scrollbar**.
  * **Zona derecha (panel de previsualización)**: `CTkLabel` para avatar y datos.
  * **Zona superior**: búsqueda por nombre (`CTkEntry`) + filtros por género (`CTkRadioButton` o `CTkOptionMenu`).
  * **Zona inferior**: **barra de estado** (`CTkLabel`).
  * **Botonera**: “Añadir”, “Eliminar”, “Editar” (o doble clic para editar), “Salir”.
  * **Menú**: Archivo (Guardar, Cargar, Salir), Ayuda (Acerca de).
* **No contiene lógica de negocio**: expone **callbacks configurables** (el `controller` las inyecta).

### Controlador — `controller/app_controller.py`

* Recibe `root`, crea `GestorUsuarios` y `MainView`.
* Conecta botones/menú/entradas con métodos del controlador.
* Implementa:

  * **alta_usuario_modal()** (Toplevel modal) → crea `Usuario` y llama a `model.añadir`.
  * **editar_usuario_modal(indice)** → abre modal con datos precargados.
  * **eliminar_usuario()**, **guardar()**, **cargar()**.
  * **aplicar_filtros()** (por nombre y género) y **refrescar_lista()**.
  * **seleccion_cambiada()** → actualiza previsualización.
  * **salir()** → cierre ordenado (+ detener hilo si ampliación).

---

## Fases y entregables 

### Fase 1 — Estructura MVC + Lista y previsualización

* Crear **estructura de carpetas** y archivos.
* Vista con layout base, CTkScrollableFrame ( o lista con scroll) y panel de previsualización (sin imágenes aún).
* Controlador conectando **listar/selección** y **salir**.
* **Entrega**: app arranca, muestra lista (vacía) y responde a selección.

### Fase 2 — Alta con **CTkToplevel** (modal) + Avatares

* Modal **Añadir** con `Entry nombre`, `Scale edad` o `Entry validada`, `RadioButton genero`, selección de **avatar** (3 opciones).
* Validación y alta en modelo; actualización de la vista.
* Mostrar **avatar** en previsualización (cargar `PhotoImage` y **conservar referencia**).
* **Entrega**: alta funcional con avatar y previsualización correcta.

### Fase 3 — **CSV** (guardar/cargar) + excepciones

* Menú Archivo con Guardar/Cargar; `utf-8`, `newline=''`, cabecera correcta.
* Control de `FileNotFoundError` y filas inválidas; no debe romper la app.
* **Entrega**: cierro app, la abro, cargo CSV, veo usuarios con avatares.

### Fase 4 — **Búsqueda/filtrado** + **barra de estado** + **editar**

* Búsqueda por nombre (con `trace_add`) y filtro por género.
* Barra de estado con recuentos y mensajes (guardado OK, errores, etc.).
* Doble clic en usuario para **editar** en modal y actualizar registro.
* **Entrega**: filtros dinámicos, mensajes en barra de estado y edición operativa.

### Fase 5 — **Auto-guardado con hilos**

* Botón que activa un hilo que cada 10 s guarda CSV; comunicar a la UI con `after()`. Parar hilo en `salir()`.
* **Entrega**: versión final estable; (si aplica) auto-guardado no bloqueante.

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

