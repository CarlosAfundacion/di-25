# Guía paso a paso – “Atrapando al Topo” con MVC

## 0. Idea general 

En este patrón:

* La **vista** NO tiene un atributo `controlador`.
* La vista solo tiene **atributos-callback** del estilo:

  * `self.on_celda_pulsada`
  * `self.on_nueva_partida`
  * `self.on_salir`
* Al pulsar un botón, la vista:

  * Comprueba si el callback existe.
  * Lo llama (pasando parámetros si hace falta).
* El **controlador**, al inicializarse, hace:

  * `vista.on_celda_pulsada = self.celda_pulsada`
  * `vista.on_nueva_partida = self.nueva_partida`

Es decir:

> La vista no sabe qué es el controlador, solo sabe que tiene "funciones" que debe llamar cuando pasa algo.

---

<img width="729" height="809" alt="imagen" src="https://github.com/user-attachments/assets/47654cbd-9bb6-4a44-b30b-385916393591" />


## PASO 1 – Estructura básica del proyecto

Crea la siguiente estructura:

```text
atrapa_topo_ctk/
    main.py
    modelo/
        __init__.py
        juego.py
    vista/
        __init__.py
        vista_juego.py
    controlador/
        __init__.py
        controlador_juego.py
```

* `modelo/` → solo lógica del juego.
* `vista/` → solo interfaz gráfica.
* `controlador/` → conecta modelo y vista.

Comprueba que tu IDE “ve” bien los paquetes (no errores de import).

---

## PASO 2 – MODELO: lógica del juego (modelo/juego.py)

Aquí el patrón no cambia respecto a la versión anterior; el cambio gordo va en vista/controlador.

### 2.1. Clase `JuegoTopo`

Objetivo: guardar el estado del juego y aplicar sus reglas.

Atributos que debe tener:

* `total_intentos` → por ejemplo, 10.
* `num_celdas` → 9 (3×3).
* `intentos_restantes`.
* `puntos`.
* `celda_topo` (índice entre 0 y 8, o entre 1 y 9, como prefieras).

### 2.2. Métodos clave

1. **Constructor**

   * Inicializa atributos.
   * Llama a `nueva_partida()`.

2. **`nueva_partida()`**

   * Pone `puntos = 0`.
   * Pone `intentos_restantes = total_intentos`.
   * Llama al método que genera la posición inicial del topo.

3. **`_generar_nueva_celda()`**

   * Elige una celda al azar entre 0 y `num_celdas - 1`.
   * Guarda el resultado en `celda_topo`.

4. **`probar_celda(indice: int) -> dict`**

   * Resta 1 intento.
   * Comprueba si `indice == celda_topo`.

     * Si acierta → suma 1 punto.
   * Decide si la partida ha terminado (`intentos_restantes == 0`).
   * Si no ha terminado → vuelve a generar una nueva `celda_topo`.
   * Devuelve un diccionario, por ejemplo:

     * `{"acierto": True, "fin": False}`
     * `{"acierto": False, "fin": True}`, etc.

5. **Getters**

   * `get_puntos()`
   * `get_intentos_restantes()`

>  Importante: aquí **no hay Tkinter ni CustomTkinter**. Solo lógica.

---

## PASO 3 – VISTA: interfaz + callbacks (vista/vista_juego.py)

### 3.1. Clase `VistaJuegoTopo`

* Hereda de `ctk.CTkFrame`.
* Su constructor recibe:

  * `master` (ventana principal).
* **No** recibe el controlador.

En el constructor:

1. Llamas a `super().__init__(master)`.
2. Defines los atributos-callback inicializados a `None`:

   * `self.on_celda_pulsada = None`
   * `self.on_nueva_partida = None`
   * `self.on_salir = None`
3. Creas una lista de botones:
   `self.botones=[]`
5. Llamas a métodos internos para:

   * Crear widgets.
   * Colocarlos.

### 3.2. Crear los widgets

Widgets recomendados:

* Label de título: “Atrapando al Topo”.
* Label de puntos.
* Label de intentos.
* Label de mensajes.
* Frame para el tablero 3×3.
* 9 botones dentro del tablero.
* Frame con botones:

  * “Nueva partida”.
  * “Salir”.

### 3.3. Tablero 3×3 con `lambda`

En el método que crea los botones del tablero:

* Recorre de 0 a 8 (o algo equivalente).
* En cada iteración:

  * Crea un botón.
  * El texto puede ser el número de la celda (`i+1`).
  * El `command` del botón **no llama al controlador directamente**, sino a un método propio de la vista que a su vez llamará al callback (explicado en Esquema mental)
  * Los botones se colocan en filas y columnas según la siguiente función en cada iteración.
    ```python
    fila, col = divmod(i,3)
    ```
  
    > La función `divmod` nos devuelve una tupla con la división entera y el resto de los dós parámetros que recibe. Esto nos permite calcular la posición en filas
    > y columnas en una cuadrícula.
  * Cada botón se añade a `self.botones`
   
Esquema mental:

```python
command=lambda indice=i: self._notificar_celda_pulsada(indice)
```

Donde:

* `_notificar_celda_pulsada` es un método privado de la vista (lo creas tú).
* Dentro de `_notificar_celda_pulsada` harás algo del tipo:

  * “Si `self.on_celda_pulsada` no es None, la llamo con `indice`”.

 Con esto consigues:

* El **botón** usa `lambda` para capturar su índice (`i`).
* La vista no sabe qué hará el controlador. Solo llama al callback si está definido.

### 3.4. Botones de “Nueva partida” y “Salir”

* Botón “Nueva partida”:

  * `command` llama a un método de la vista, por ejemplo `_on_nueva_partida`.
  * En `_on_nueva_partida`, compruebas si `self.on_nueva_partida` existe, y si es así la llamas.

* Botón “Salir”:

  * Llamar directamente a `self.master.destroy()`.
    
### 3.5. Métodos de la vista para que el controlador los use

La vista debe ofrecer métodos que el controlador llamará para actualizar la UI:

* `mostrar_mensaje(texto: str)`

  * Actualiza la etiqueta de mensajes.

* `actualizar_puntos(puntos: int)`

  * Actualiza la etiqueta de puntos.

* `actualizar_intentos(intentos: int)`

  * Actualiza la etiqueta de intentos.

* `bloquear_celdas()`

  * Recorre la lista de botones y los desactiva `state="disabled"` .

* `activar_celdas()`

  * Recorre los botones y los activa `state="normal"`.

### 3.6. Métodos internos de notificación (donde se usan los callbacks)

Crea estos métodos internos en la vista:

1. `_notificar_celda_pulsada(indice: int)`

   * Comprueba:

     * Si `self.on_celda_pulsada` no es `None`.
   * Si está definido → lo llama, pasando `indice`.

2. `_on_nueva_partida()`

   * Si `self.on_nueva_partida` no es `None`, la llama.

3. (Opcional) `_on_salir()`

   * Si usas `self.on_salir`, la llamas.
   * Si no, llamas directamente a `self.master.destroy()`.

>  Fíjate que la vista **no importa el controlador** ni llama a sus métodos directamente.
> Solo conoce funciones “colgadas” en sus atributos-callback.

---

## PASO 4 – CONTROLADOR (controlador/controlador_juego.py)

El controlador ahora se construye con **modelo y vista**, y se encarga de conectar los callbacks.

### 4.1. Clase `ControladorJuegoTopo`

En el constructor:

* Recibe:

  * `modelo: JuegoTopo`
  * `vista: VistaJuegoTopo`
* Guarda ambos en atributos:

  * `self.modelo`
  * `self.vista`

### 4.2. Registrar callbacks en la vista

Dentro del constructor, después de guardar la vista:

* Asigna los callbacks:

  * `vista.on_celda_pulsada = self.celda_pulsada`
  * `vista.on_nueva_partida = self.nueva_partida`
  * (opcional) `vista.on_salir = self.salir` (si quieres gestionarlo desde aquí).

De esta forma, cuando la vista ejecute:

* `self.on_celda_pulsada(indice)`
  en realidad estará llamando a
  `self.celda_pulsada(indice)` del controlador.

### 4.3. Método `nueva_partida(self)`

Muy parecido al patrón anterior:

1. Llama a `self.modelo.nueva_partida()`.
2. Obtiene:

   * `puntos = self.modelo.get_puntos()`
   * `intentos = self.modelo.get_intentos_restantes()`
3. Llama a la vista:

   * `vista.actualizar_puntos(puntos)`
   * `vista.actualizar_intentos(intentos)`
   * `vista.mostrar_mensaje("¡Nueva partida! Intenta atrapar al topo.")`
   * `vista.activar_celdas()`

### 4.4. Método `celda_pulsada(self, indice: int)`

Este método será el que la vista llame a través del callback.

Pasos:

1. Llama a `resultado = self.modelo.probar_celda(indice)`.
2. Lee el diccionario devuelto:

   * `acierto = resultado["acierto"]`
   * `fin = resultado["fin"]`
3. Según `acierto`, construye un mensaje parcial:

   * Acierto → “¡Has atrapado al topo!”
   * Fallo → “Has fallado…”
4. Obtiene:

   * `puntos = self.modelo.get_puntos()`
   * `intentos = self.modelo.get_intentos_restantes()`
5. Completa el mensaje:

   * Si `fin` es False → añade algo tipo “Te quedan X intentos”.
   * Si `fin` es True → añade algo tipo “Partida terminada. Has conseguido Y puntos.”
6. Llama a la vista para actualizar:

   * `vista.actualizar_puntos(puntos)`
   * `vista.actualizar_intentos(intentos)`
   * `vista.mostrar_mensaje(mensaje)`
   * Si `fin` → `vista.bloquear_celdas()`

## PASO 5 – `main.py`: montar todo

Orden recomendado:

1. Configura CustomTkinter (modo, tema).
2. Crea una clase `Aplicacion` que herede de `CTk`:

   * En el `__init__`:

     * Crea el modelo: `JuegoTopo(...)`.
     * Crea la vista: `VistaJuegoTopo(self)`.
     * Crea el controlador: `ControladorJuegoTopo(modelo, vista)`.
     * Coloca la vista en la ventana (`pack` o `grid`).
     * Llama a `controlador.nueva_partida()` para iniciar el juego.
3. Ejecuta el típico:

```python
if __name__ == "__main__":
    app = Aplicacion()
    app.mainloop()
```

> Observa que:
>
> * La vista **no recibe el controlador** en el constructor.
> * Es el **controlador** el que recibe `modelo` y `vista`, y “registra” sus métodos como callbacks en la vista.

---

## Resumen 
* El **modelo** solo sabe jugar él solo (topo, puntos, intentos), sin ventana.
* La **vista** solo sabe pintar botones y labels, y llamar a **funciones-callback** cuando pasa algo.
* El **controlador** es el que:

  * Recibe los eventos desde la vista (gracias a los callbacks).
  * Llama al modelo.
  * Actualiza la vista.

