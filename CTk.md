
### **Ampliación de Python y Tkinter**

### **1. Manipulación de Archivos en Python**

La persistencia de datos es clave en cualquier aplicación. Python lo hace sencillo y seguro.

#### **1.1. Apertura y Cierre de Archivos**

La forma recomendada es usar un gestor de contexto (`with`), que cierra el archivo automáticamente, incluso si ocurren errores.

```python
# Recomendado: El bloque 'with' asegura que el archivo se cierra solo.
try:
    with open('datos.txt', 'r', encoding='utf-8') as f:
        contenido = f.read()
        print(contenido)
except FileNotFoundError:
    print("El archivo no existe.")

# Escribir en un archivo (crea el archivo si no existe)
with open('salida.txt', 'w', encoding='utf-8') as f:
    f.write("Esta es la primera línea.\n")
    f.write("Esta es la segunda.")
```

**Modos de Apertura Comunes:**
*   `'r'`: **Lectura** (Read). Falla si el archivo no existe.
*   `'w'`: **Escritura** (Write). Sobrescribe el archivo si existe, o lo crea si no.
*   `'a'`: **Añadir** (Append). Agrega contenido al final del archivo sin borrar lo anterior.
*   `'b'`: **Binario** (Binary). Se combina con otros modos (ej. `'rb'`) para leer archivos que no son de texto, como imágenes.

#### **1.2. Ficheros CSV**

El formato CSV (Comma-Separated Values) es ideal para datos tabulares. El módulo `csv` de Python facilita su manejo.

```python
import csv

# Escribir en un archivo CSV
datos = [
    ['Nombre', 'Edad', 'Ciudad'],
    ['Ana', '28', 'Madrid'],
    ['Luis', '34', 'Barcelona']
]

with open('personas.csv', 'w', newline='', encoding='utf-8') as f:
    escritor = csv.writer(f)
    escritor.writerows(datos)

# Leer desde un archivo CSV
with open('personas.csv', 'r', encoding='utf-8') as f:
    lector = csv.reader(f)
    for fila in lector:
        print(fila)
```

**Buenas Prácticas:**
1.  **Usa `encoding='utf-8'`** para compatibilidad con caracteres especiales (acentos, etc.).
2.  **Maneja excepciones** como `FileNotFoundError`.
3.  **Usa `pathlib`** para manejar rutas de archivos de forma más robusta.

---

### **2. Gestión de Excepciones**

Las excepciones son errores que ocurren durante la ejecución. Gestionarlas evita que la aplicación se cierre inesperadamente.

El flujo es `try` -> `except` -> `else` -> `finally`.

*   `try`: Contiene el código que podría fallar.
*   `except`: Se ejecuta **solo si** ocurre una excepción en el `try`. Puedes capturar tipos de error específicos.
*   `else`: Se ejecuta **solo si no** hubo ninguna excepción.
*   `finally`: Se ejecuta **siempre**, haya o no haya excepción. Ideal para tareas de limpieza.

```python
try:
    # Intenta convertir un texto a número y dividir
    numero = int(input("Introduce un número: "))
    resultado = 10 / numero
except ValueError:
    # Se ejecuta si el input no es un número válido
    print("Error: Debes introducir un número entero.")
except ZeroDivisionError:
    # Se ejecuta si se intenta dividir por cero
    print("Error: No se puede dividir por cero.")
else:
    # Se ejecuta si todo fue bien
    print(f"El resultado es {resultado}")
finally:
    # Se ejecuta siempre
    print("Fin del bloque de gestión de errores.")
```

---

### **3. De Tkinter a CustomTkinter**

**CustomTkinter** es una biblioteca moderna que se construye sobre Tkinter. Ofrece widgets con un aspecto visual actualizado y más opciones de personalización.

Para poder trabajar con CustomTkinter tendremos que instalarlo tecleando los siguientes comandos en la consola:

```bash
pip install customtkinter
```

#### **3.1. Diferencias Clave**

*   **Apariencia:** Permite modos "Light", "Dark" y "System" fácilmente.
*   **Widgets:** Los widgets tienen el prefijo `CTk` (ej. `CTkButton`, `CTkFrame`).
*   **Propiedades Extra:** Añade atributos como `corner_radius`, `fg_color`, `hover_color`, `border_color`, etc., para un diseño más fino.

```python
import customtkinter as ctk

# Configuración básica de una app CustomTkinter
ctk.set_appearance_mode("System")  # "Light", "Dark"
ctk.set_default_color_theme("blue")  # "green", "dark-blue"

app = ctk.CTk()
app.title("Mi App con CTk")
app.geometry("400x300")

# Un botón con estilo moderno
boton = ctk.CTkButton(app, text="Haz clic", corner_radius=10)
boton.pack(pady=20)

app.mainloop()
```

<img width="291" height="191" alt="imagen" src="https://github.com/user-attachments/assets/3cf0bde9-1e83-41b6-a5b6-f4c68234229b" />

Puedes ver documentación de los distintos widgets en la [documentación oficial](https://customtkinter.tomschimansky.com/documentation/widgets):

#### **3.2. Eventos y Binding**

Además del `command` de los botones, puedes "vincular" (`bind`) funciones a casi cualquier evento (clics, teclas, movimiento del ratón) en cualquier widget.

El método `bind` recibe una función que siempre acepta un argumento: el objeto `event`, que contiene información como la posición del ratón (`event.x`, `event.y`) o la tecla pulsada (`event.keysym`).

```python
import customtkinter as ctk

app = ctk.CTk()
app.title("Eventos")

def al_hacer_clic(event):
    print(f"Clic en coordenadas: x={event.x}, y={event.y}")

def al_pulsar_tecla(event):
    print(f"Tecla pulsada: '{event.keysym}'")

# Vincular el clic izquierdo a la ventana principal
app.bind("<Button-1>", al_hacer_clic)

# Vincular cualquier pulsación de tecla a la ventana
app.bind("<Key>", al_pulsar_tecla)

# Para que el binding de teclas funcione, la ventana debe tener el "foco"
app.focus_set()

app.mainloop()
```
<img width="291" height="331" alt="imagen" src="https://github.com/user-attachments/assets/272f172d-1f20-4c60-a4c1-655027693257" />

#### **3.3. Ventanas Emergentes (Toplevel)**

`CTkToplevel` crea una ventana hija que aparece por encima de la principal.

*   **`grab_set()`**: Hace que la ventana emergente sea **modal**, es decir, bloquea la interacción con la ventana principal hasta que se cierre.
*   **`destroy()` vs `quit()`**:
    *   `widget.destroy()`: Cierra y elimina un widget específico (la ventana emergente, por ejemplo). Es lo más común.
    *   `app.quit()`: Termina el `mainloop`, cerrando la aplicación entera.

```python
import customtkinter as ctk

def abrir_ventana_emergente():
    ventana_hija = ctk.CTkToplevel(app)
    ventana_hija.title("Ventana Emergente")
    ventana_hija.geometry("200x100")
    ventana_hija.grab_set() # La hace modal

    label = ctk.CTkLabel(ventana_hija, text="Esta es una ventana modal.")
    label.pack(padx=20, pady=10)
    
    # Botón para cerrar solo esta ventana
    cerrar_btn = ctk.CTkButton(ventana_hija, text="Cerrar", command=ventana_hija.destroy)
    cerrar_btn.pack(pady=5)

app = ctk.CTk()
app.title("Ventana Principal")
app.geometry("400x300")

boton_abrir = ctk.CTkButton(app, text="Abrir emergente", command=abrir_ventana_emergente)
boton_abrir.pack(pady=40)

app.mainloop()
```
<img width="295" height="244" alt="imagen" src="https://github.com/user-attachments/assets/99368c03-7dfe-4286-a3e5-d75a705549c9" />


#### **3.4 Uso de Imágenes en CustomTkinter**

CustomTkinter facilita mucho el trabajo con imágenes gracias a su clase `CTkImage`, que utiliza internamente la biblioteca **Pillow**.
Esto permite cargar imágenes en formato **.png, .jpg, .webp**, etc., y adaptarlas automáticamente al modo claro/oscuro.

##### **3.4.1. Instalación de Pillow**

Para que `CTkImage` funcione, es necesario instalar en consola la biblioteca Pillow (PIL mejorada):

```bash
pip install Pillow
```

##### **3.4.2. Cargar una imagen en CustomTkinter**

El siguiente ejemplo muestra cómo cargar y visualizar una imagen con `CTkImage` en un `CTkLabel`:

```python
from PIL import Image
import customtkinter as ctk

app = ctk.CTk()
app.title("Ejemplo de imagen en CustomTkinter")
app.geometry("400x300")

# Crear un objeto CTkImage a partir de un archivo de imagen
imagen = ctk.CTkImage(
    light_image=Image.open("imagen_clara.png"),  # Imagen para modo claro
    dark_image=Image.open("imagen_oscura.png"),  # Imagen para modo oscuro
    size=(150, 150)  # Tamaño de la imagen dentro del widget
)

# Mostrar la imagen en un Label sin texto
label = ctk.CTkLabel(app, image=imagen, text="")
label.pack(pady=20)

app.mainloop()
```

 **Ventajas de `CTkImage`:**

* Cambia automáticamente entre la versión clara y oscura de la imagen según el modo del sistema o el configurado con `ctk.set_appearance_mode()`.
* Permite definir el tamaño directamente al crearla.
* No necesitas usar `ImageTk.PhotoImage` como en Tkinter.


##### **3.4.3. Uso de imágenes en botones**

También puedes usar imágenes dentro de botones `CTkButton`, ya sea solas o acompañadas de texto.

```python
from PIL import Image
import customtkinter as ctk

app = ctk.CTk()
app.title("Botón con imagen")
app.geometry("300x200")

# Cargar una imagen (la misma para ambos modos)
icono = ctk.CTkImage(
    light_image=Image.open("icono.png"),
    dark_image=Image.open("icono.png"),
    size=(24, 24)
)

# Crear botón con imagen y texto
boton = ctk.CTkButton(app, text="Descargar", image=icono, compound="left")
boton.pack(pady=20)

app.mainloop()
```

 El parámetro `compound` controla la posición relativa de la imagen:

* `"left"` → imagen a la izquierda del texto
* `"right"` → imagen a la derecha
* `"top"` → imagen arriba del texto
* `"bottom"` → imagen abajo del texto
* `"center"` → solo imagen (sin texto)


##### **3.4.4. Carga de imágenes desde carpetas del proyecto**

Si organizas tus imágenes en una carpeta (por ejemplo, `assets/`), puedes usar rutas relativas:

```python
from PIL import Image
import customtkinter as ctk
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
ruta_logo = BASE_DIR / "assets" / "logo.png"

app = ctk.CTk()
app.title("Imagen desde carpeta assets")

logo = ctk.CTkImage(
    light_image=Image.open(ruta_logo),
    dark_image=Image.open(ruta_logo),
    size=(120, 120)
)

ctk.CTkLabel(app, image=logo, text="").pack(pady=20)
app.mainloop()
```|

---

### **4. Funciones Lambda y Callbacks**

#### **4.1. Funciones Lambda**

Una función `lambda` es una pequeña función anónima definida en una sola línea. Es muy útil para crear *callbacks* (funciones que se pasan como argumento) de forma rápida.

Sintaxis: `lambda argumentos: expresion`

```python
# Un command simple con lambda
boton = ctk.CTkButton(app, text="Saludar", command=lambda: print("Hola Mundo"))

# Un bind que pasa argumentos adicionales a la función
def saludar(nombre):
    print(f"Hola, {nombre}")

boton_nombre = ctk.CTkButton(app, text="Saludar a Ana")
# Usamos lambda para poder llamar a la función con el argumento que queramos
boton_nombre.configure(command=lambda: saludar("Ana"))
```

#### **4.2. Callback en Tk/CTk**

Una callback (función de retorno o función de llamada) es una función que se ejecuta automáticamente cuando ocurre un evento, por ejemplo cuando el usuario pulsa un botón.

En Tkinter (y CTk), se asigna normalmente a los widgets mediante el parámetro command o mediante un binding de eventos (bind).

1.  **`command`**: Usado en botones, sliders, etc. Es simple y no recibe el objeto `event`.
2.  **`bind`**: Más potente, se asocia a eventos de teclado/ratón. La función *debe* aceptar el objeto `event`.
3.  **`trace_add`**: Se usa con variables de Tkinter (`StringVar`, `IntVar`). Ejecuta una función cada vez que el valor de la variable cambia.

```python
# Ejemplo de trace_add
def variable_cambiada(*args):
    print(f"El nuevo valor es: {texto_variable.get()}")

texto_variable = ctk.StringVar()
texto_variable.trace_add("write", variable_cambiada) # "write" es el modo de traza

entry = ctk.CTkEntry(app, textvariable=texto_variable)
```

---

### **5. Tareas en Segundo Plano (Hilos)**

**El problema:** Si una tarea tarda mucho (ej. descargar un archivo, una consulta a una base de datos), la interfaz gráfica se **congela**.

**La solución:** Ejecutar esas tareas en un **hilo secundario** (`threading`) para no bloquear el hilo principal de la UI.

**Regla de Oro:** **NUNCA** actualices un widget de Tkinter desde un hilo secundario. Hcaerlo puede causar errores impredecibles o cerrar la aplicación.

Para actualizar la UI de forma segura desde un hilo, usa `root.after(milisegundos, funcion)`. Esto "agenda" la ejecución de `funcion` en el hilo principal.

```python
import customtkinter as ctk
import threading
import time

def tarea_larga():
    """Esta función simula un trabajo pesado que se ejecuta en un hilo."""
    print("Iniciando tarea larga en segundo plano...")
    time.sleep(5)  # Simula 5 segundos de trabajo
    resultado = "Tarea completada!"
    print("Tarea finalizada.")
    
    # Agenda la actualización de la UI en el hilo principal
    app.after(0, lambda: etiqueta.configure(text=resultado))

def iniciar_tarea():
    etiqueta.configure(text="Procesando durante 5 segundos...")
    # Crea y arranca el hilo para que la UI no se congele
    hilo = threading.Thread(target=tarea_larga)
    hilo.daemon = True # Permite que la app cierre aunque el hilo no haya acabado
    hilo.start()

app = ctk.CTk()
app.title("Hilos y UI no bloqueante")
app.geometry("400x200")

etiqueta = ctk.CTkLabel(app, text="Pulsa el botón para iniciar.")
etiqueta.pack(pady=20)

boton = ctk.CTkButton(app, text="Iniciar Tarea Larga", command=iniciar_tarea)
boton.pack(pady=10)

app.mainloop()
```

---

### **6. Modularización: Módulos y Paquetes**

Para que un proyecto sea mantenible, hay que organizarlo.

*   **Módulo:** Un archivo `.py`. Es una unidad de código reutilizable.
*   **Paquete:** Una carpeta que contiene módulos y un archivo especial `__init__.py` (puede estar vacío). Agrupa módulos relacionados.
*   Un ejemplo de `__init__.py` sería:

  ```python
from .tarea import Tarea
   ```
Con esto, puedes importar directamente:
 ```python
from model import Tarea
   ```
Frente a la implementación sin paquetes en la cual tendríamos que hacer:

 ```python
from model.tarea import Tarea
   ```

Además nos permite definir funciones o variables del paquete, ejecutar código de inicialización del paquete  y controlar qué se exporta con `from model import *`

Una estructura típica para una aplicación MVC sería:

```
mi_app/
├── app.py              # Punto de entrada de la aplicación
|
├── model/
│   ├── __init__.py
│   └── tarea.py        # Define la lógica y los datos (ej. clase Tarea)
|
├── view/
│   ├── __init__.py
│   └── main_view.py    # Define la interfaz gráfica (la ventana principal)
|
└── controller/
    ├── __init__.py
    └── app_controller.py # Conecta la vista y el modelo
```

---

### **7. Patrón Modelo-Vista-Controlador (MVC)**

El patrón MVC es un estándar de arquitectura que separa la aplicación en tres partes interconectadas, haciendo el código más organizado, reutilizable y fácil de testear.

*   **Modelo (Model):**
    *   **Qué es:** El cerebro de la aplicación. Contiene los datos (`clase Tarea`) y la lógica de negocio (reglas para añadir, eliminar, marcar tareas).
    *   **Características:** Es completamente independiente de la interfaz gráfica. No sabe nada de botones ni ventanas.

*   **Vista (View):**
    *   **Qué es:** La interfaz de usuario (la ventana, los botones, las etiquetas). Lo que el usuario ve y con lo que interactúa.
    *   **Características:** Es "tonta". Solo muestra los datos que le da el Controlador y le avisa cuando el usuario hace algo (ej. "¡Han pulsado el botón de añadir!").

*   **Controlador (Controller):**
    *   **Qué es:** El intermediario. Orquesta todo.
    *   **Flujo:**
        1.  Recibe notificaciones de la **Vista** (ej. "clic en botón 'Añadir'").
        2.  Procesa la entrada y llama al **Modelo** para actualizar los datos (ej. "modelo, añade esta nueva tarea").
        3.  Pide los datos actualizados al **Modelo**.
        4.  Le pasa los nuevos datos a la **Vista** para que se redibuje.

#### **Ejemplo Práctico de MVC **

```python
import customtkinter as ctk

# ----------------------------------------------------------------------
# MODELO (Model)
# - Contiene los datos y la lógica de negocio.
# - No sabe NADA sobre la interfaz gráfica (no importa ctk).
# - Es completamente independiente y reutilizable.
# ----------------------------------------------------------------------
class CounterModel:
    def __init__(self):
        """Inicializa el dato principal: el contador."""
        self._count = 0

    def get_count(self):
        """Devuelve el valor actual del contador."""
        return self._count

    def increment(self):
        """La lógica de negocio: incrementar el contador en uno."""
        self._count += 1

# ----------------------------------------------------------------------
# VISTA (View)
# - Representa la interfaz de usuario (lo que se ve).
# - Contiene los widgets (botones, etiquetas, etc.).
# - No tiene lógica de negocio. Su única tarea es mostrar datos
#   y notificar al controlador cuando el usuario interactúa.
# ----------------------------------------------------------------------
class CounterView:
    def __init__(self, master):
        """Crea y posiciona los widgets en la ventana principal (master)."""
        # Configura el frame principal
        self.frame = ctk.CTkFrame(master)
        self.frame.pack(padx=20, pady=20, expand=True)

        # Etiqueta para mostrar el valor del contador
        self.count_label = ctk.CTkLabel(
            self.frame,
            text="Contador: 0",
            font=ctk.CTkFont(size=20, weight="bold")
        )
        self.count_label.pack(pady=10)

        # Botón para que el usuario pueda incrementar el contador
        # El 'command' (la acción a realizar) será establecido por el Controlador.
        self.increment_button = ctk.CTkButton(
            self.frame,
            text="Incrementar"
        )
        self.increment_button.pack(pady=10)

    def update_display(self, new_count):
        """Actualiza la etiqueta para mostrar el nuevo valor."""
        self.count_label.configure(text=f"Contador: {new_count}")

# ----------------------------------------------------------------------
# CONTROLADOR (Controller)
# - Actúa como intermediario entre el Modelo и la Vista.
# - Orquesta el flujo de la aplicación.
# ----------------------------------------------------------------------
class CounterController:
    def __init__(self, root):
        """
        1. Crea las instancias del Modelo y la Vista.
        2. Conecta los eventos de la Vista con sus métodos manejadores.
        """
        # Crea una instancia del modelo para manejar los datos
        self.model = CounterModel()
        
        # Crea una instancia de la vista para manejar la UI
        # Le pasa 'root' (la ventana principal) para que la vista se dibuje en ella
        self.view = CounterView(root)
        
        # Conecta el botón de la vista con el método de este controlador.
        # Cuando se pulse el botón, se llamará a 'self.increment_and_update'.
        self.view.increment_button.configure(command=self.increment_and_update)

    def increment_and_update(self):
        """
        Flujo de trabajo principal del controlador:
        1. Le dice al Modelo que cambie su estado (datos).
        2. Pide al Modelo los datos actualizados.
        3. Le dice a la Vista que se actualice con los nuevos datos.
        """
        # 1. Llama al modelo para que ejecute su lógica
        self.model.increment()
        
        # 2. Obtiene el nuevo estado del modelo
        current_count = self.model.get_count()
        
        # 3. Llama a la vista para que actualice la pantalla
        self.view.update_display(current_count)

# ----------------------------------------------------------------------
# PUNTO DE ENTRADA DE LA APLICACIÓN
# ----------------------------------------------------------------------
if __name__ == "__main__":
    # 1. Configurar la ventana principal de la aplicación
    app = ctk.CTk()
    app.title("Ejemplo Simple de MVC")
    app.geometry("300x200")

    # 2. Crear una instancia del Controlador
    # Esto inicializará el Modelo y la Vista, y los conectará.
    controller = CounterController(app)

    # 3. Iniciar el bucle principal de la aplicación
    app.mainloop()

```

### Cómo funciona este código:

1.  **Arranque**: Se crea la ventana principal (`app`) y se le pasa al `CounterController`.
2.  **Inicialización del Controlador**:
    *   El controlador crea el `CounterModel` (que inicializa el contador a `0`).
    *   También crea la `CounterView`, que dibuja una etiqueta mostrando "0" y un botón.
    *   **Lo más importante**: El controlador conecta el `command` del botón de la Vista a su propio método `increment_and_update`. La Vista no sabe qué hace ese método, solo sabe que tiene que llamarlo.
3.  **Interacción del Usuario**:
    *   El usuario hace clic en el botón "Incrementar".
    *   La Vista, como fue configurada por el controlador, ejecuta el método `increment_and_update` del controlador.
4.  **Flujo del Controlador**:
    *   El método `increment_and_update` le dice al **Modelo**: "increméntate". El modelo cambia su variable interna `_count` a `1`.
    *   El controlador le pide al **Modelo** el nuevo valor (`get_count()`), que ahora es `1`.
    *   El controlador le dice a la **Vista**: "actualiza tu pantalla con este nuevo valor: `1`".
5.  **Actualización de la Vista**: La vista recibe el `1` y actualiza el texto de su etiqueta a "Contador: 1".

Y así, el ciclo se repite, manteniendo cada componente con su responsabilidad bien definida.



