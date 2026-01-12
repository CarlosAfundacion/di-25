# APUNTES – SEMANA 2 

## PROYECTO BASE: *Aula+ Lite*

*(MVVM + UX/UI + Material Design básico)*

---

## 1. REPASO: De Tkinter a Android: el problema del ciclo de vida

En las aplicaciones de escritorio (Tkinter):

* La ventana vive mientras el programa está abierto.
* Las variables viven mientras la ventana viva.
* El controlador mantiene el estado sin preocuparse del sistema.

En Android esto **NO es así**.

### El problema clave

Android **destruye y recrea** las pantallas (Activities) en situaciones normales:

* Rotación del dispositivo
* Cambio de idioma
* Falta de memoria

 Si el estado estuviera en la pantalla, **se perdería**.

---

## 2. La solución profesional: arquitectura MVVM

Android impone el uso del patrón **MVVM (Model – View – ViewModel)**.

### Responsabilidades claras

| Capa   | En Android | Qué hace                        |
| ------ | ---------- | ------------------------------- |
| Vista  | Activity   | Muestra la UI y captura eventos |
| Lógica | ViewModel  | Contiene estado y reglas        |
| Datos  | Repository | Decide de dónde salen los datos |

 **Idea clave:**
La Vista **no piensa**.
El ViewModel **no dibuja**.

---

## 3. MVVM aplicado a *Aula+ Lite*

### 3.1 Estructura del proyecto

```
com.example.aulamas
 ├─ data        → InMemoryNoticeRepository
 ├─ viewmodel   → NoticeViewModel + Factory
 └─ ui          → MainActivity
```

Esta estructura es **idéntica conceptualmente** al gestor de stock que ya conoces.

---

### 3.2 Repository: la fuente de datos

El repositorio:

* Simula una base de datos en memoria
* No sabe nada de la UI
* No sabe nada del ciclo de vida

Ejemplo:

```java
public List<String> getAll() {
    return new ArrayList<>(database);
}
```

 El repositorio **NO es la pantalla**.

---

### 3.3 ViewModel: estado + lógica

El ViewModel:

* Vive mientras viva la Activity (aunque se recree)
* Contiene el estado observable
* Valida datos
* Emite errores y eventos

Ejemplo de estado observable:

```java
private final MutableLiveData<String> _listado = new MutableLiveData<>();

public LiveData<String> getListado() {
    return _listado;
}
```

La Activity **observa**, no consulta constantemente.

---

### 3.4 Activity: vista “tonta”

La Activity:

* Observa `LiveData`
* Muestra datos
* Captura clics
* NO guarda estado

Ejemplo:

```java
vm.getListado().observe(this, texto -> {
    tvList.setText(texto);
});
```

 Si rotas la pantalla:

* La Activity muere
* El ViewModel sigue vivo
* El estado se conserva

---

## 4. Comprobación práctica del patrón

1. Ejecuta *Aula+ Lite*
2. Añade varios avisos
3. Gira el dispositivo

 Los avisos **no desaparecen**

**Motivo:**
El estado vive en el ViewModel, no en la Activity.

---

## 5. Antes de programar: pensar la interfaz (UX/UI)

Tal y como se explica en los apuntes de layouts, **no se empieza por el código**, sino por el diseño conceptual (wireframe, estructura, jerarquía visual).

Antes de tocar Material Design, analizamos **la UI actual de Aula+ Lite**.

---

## 6. UX/UI en dispositivos móviles: conceptos clave

Las interfaces móviles **no se diseñan igual que las de escritorio**. Un móvil introduce limitaciones físicas y contextuales que afectan directamente a cómo debe diseñarse la interfaz.

Las siguientes imágenes **no son ejemplos estéticos**, sino **esquemas conceptuales** que explican **cómo se usa realmente una aplicación móvil** y **qué espera el usuario al interactuar con ella**.

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2Aemadr9dX6COgnURi_tBPgQ.jpeg)


---

### 6.1 Jerarquía visual en móvil 

Esta imagen representa el concepto de **jerarquía visual** en una pantalla móvil.

En un dispositivo móvil:

* El usuario **no lee**, escanea la pantalla.
* Lo primero que aparece es lo que se considera más importante.
* Debe existir una **acción principal claramente reconocible**.

 En UX móvil, la pregunta clave es:
**“Si el usuario mira la pantalla durante 3 segundos, entiende qué puede hacer?”**

Aplicado a *Aula+ Lite*:

* ¿Está claro que la acción principal es añadir un aviso?
* ¿Destaca esa acción frente al resto?

---

![Image](https://archive.smashing.media/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/496f7bc0-4c6c-4159-b731-ec3adcf91105/thumb-zone-mapping-opt.png)


### 6.2 Zona de alcance del pulgar 

Esta imagen muestra el **mapa de alcance del pulgar** cuando se utiliza el móvil con una sola mano:

* Zona verde → fácil de alcanzar
* Zona amarilla → esfuerzo medio
* Zona roja → incómodo

Esto implica que:

* No todas las zonas de la pantalla son igual de accesibles
* Las acciones frecuentes **no deberían colocarse en zonas incómodas**
* El diseño debe adaptarse al uso real del dispositivo

 Consecuencia directa:

> Una interfaz técnicamente correcta puede ser incómoda de usar.

Este concepto justifica:

* Botones principales en zonas accesibles
* Uso de barras inferiores
* Evitar acciones importantes en esquinas superiores

---
![Image](https://developer.android.com/static/images/design/ui/mobile-promo.png)
### 6.3 Layout usable y claridad visual 

Esta imagen representa un **layout usable**, centrado en la claridad y la simplicidad:

* Espaciado adecuado entre elementos
* Márgenes coherentes
* No saturación visual
* Una acción principal clara

 Idea clave en DI:

> Usabilidad no es meter más cosas, es organizar mejor las que hay.

Errores habituales que se evitan con este enfoque:

* Pantallas sobrecargadas
* Botones demasiado juntos
* Texto poco legible
* Uso incorrecto del espacio

---

### 6.4 Conclusión UX/UI móvil

Diseñar bien una interfaz móvil implica:

* Priorizar acciones
* Guiar al usuario
* Reducir errores
* Facilitar el uso sin explicaciones

 UX/UI **no es estética**, es **funcionalidad bien pensada**.


---

## 7. Análisis UX/UI de Aula+ Lite
<img width="577" height="1203" alt="imagen" src="https://github.com/user-attachments/assets/8810c9e1-28a3-4427-87a0-4864be57288f" />

### 7.1 Preguntas clave de análisis

* ¿Qué es lo más importante en esta pantalla?
* ¿Está claro qué acción debe hacer el usuario?
* ¿La jerarquía visual es correcta?
* ¿El feedback es claro?
* ¿El error aparece donde se produce?

---

 **La app funciona**, pero **no está bien diseñada** todavía.

---
##  PRÁCTICA 1 — ANÁLISIS (sin programar)

**Obligatoria**

1. Detecta **3 problemas UX/UI** en Aula+ Lite.
2. Propón **2 mejoras concretas** (sin escribir código).

Ejemplo:

* Sustituir `EditText` por `TextInputLayout`
* Mejorar el mensaje cuando no hay avisos

---

## 8. Introducción a Material Design

Material Design es el **sistema de diseño oficial de Android**, desarrollado por **Google**.

Material Design **no es una librería**, es un **conjunto de principios, componentes y patrones de comportamiento** que definen cómo debe funcionar una interfaz Android moderna.

### 8.1 Qué aporta Material Design

* Coherencia visual entre aplicaciones
* Accesibilidad integrada
* Componentes estándar
* Menos decisiones arbitrarias
* Mejor experiencia de usuario

 Material Design **no cambia la lógica**, solo la presentación.

---

### 8.2 Documentación oficial (referencia obligatoria)

La fuente oficial es:

[https://m3.material.io/](https://m3.material.io/)

De esta página es importante localizar:

* **Components**

  * Text fields
  * Buttons
  * Snackbars
  * Top app bars
* **Design principles**

  * Jerarquía
  * Feedback
  * Accesibilidad
* **Do / Don’t**

  * Qué se espera de cada componente

Esta documentación **forma parte del trabajo profesional** de un desarrollador de interfaces.

---


## 9. Inputs profesionales: `TextInputLayout`

### 9.1. Problema del `EditText` clásico

El uso de un `EditText` tradicional presenta varios problemas desde el punto de vista de **usabilidad y accesibilidad**:

* El **hint desaparece al escribir**, por lo que el usuario pierde el contexto del dato que está introduciendo.
* El **mensaje de error no está integrado visualmente** en el campo, resultando intrusivo y poco claro.
* Experiencia de usuario pobre en formularios medianos o largos.
* No sigue las recomendaciones actuales de **Material Design**.

Ejemplo clásico (no recomendado):

```xml
<EditText
    android:hint="Título" />
```

---

### 9.2. Solución con Material Design: `TextInputLayout`

Material Design propone el uso del componente **`TextInputLayout`**, que actúa como **contenedor inteligente** del campo de texto.

```xml
<com.google.android.material.textfield.TextInputLayout
    android:hint="Título">

    <com.google.android.material.textfield.TextInputEditText />
</com.google.android.material.textfield.TextInputLayout>
```

#### Reparto de responsabilidades

* **`TextInputLayout`**: gestiona el label, animaciones, errores y ayudas.
* **`TextInputEditText`**: recoge la entrada de texto del usuario.

 El `hint` se define en el **`TextInputLayout`**, no en el `EditText`.

---

### 9.3. Funcionamiento del label persistente

* Cuando el campo está vacío, el texto aparece dentro del input.
* Al comenzar a escribir, el texto **flota hacia arriba**.
* El label **nunca desaparece**, manteniendo siempre el contexto.

Este comportamiento se conoce como **label flotante** y es un estándar en interfaces modernas.

---

### 9.4. Gestión correcta de errores

Con `TextInputLayout`, los errores se muestran **integrados en el propio campo**, sin romper el diseño:

```java
tilTitle.setError("El título es obligatorio");
```

Para limpiar el error:

```java
tilTitle.setError(null);
```

✔ Mensaje claro
✔ Integrado visualmente
✔ Compatible con accesibilidad

---

### 9.5. Ejemplo completo recomendado

```xml
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/tilTitle"
    style="?attr/textInputOutlinedStyle"
    android:hint="Título"
    app:helperText="Campo obligatorio">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/etTitle"
        android:inputType="textCapSentences" />
</com.google.android.material.textfield.TextInputLayout>
```

---

### 9.6. Ventajas del uso de `TextInputLayout`

* **Label persistente**: el usuario no pierde el contexto.
* **Error visible en el propio campo**, integrado con Material Design.
* **Mejor accesibilidad**, compatible con lectores de pantalla.
* **Mejor experiencia de usuario (UX)**.
* Interfaz coherente con el resto de componentes Material.

---

### 9.7. Idea clave para el alumnado

> Usar `EditText` solo **funciona**,
> pero usar `TextInputLayout` es **hacer interfaces profesionales**.

En **Desarrollo de Interfaces**, no se evalúa solo que la aplicación funcione,
sino que **sea usable, clara y acorde a los estándares actuales**.

---

Si quieres, en el siguiente apartado puedo ayudarte a enlazar esto con:

* validación de formularios,
* Material Design 2 vs Material 3,
* o una **rúbrica de evaluación específica** para inputs.

---

## 10. Botones Material

Sustituimos `Button` por `MaterialButton`:

```xml
<com.google.android.material.button.MaterialButton
    android:text="Añadir aviso"/>
```

Material define jerarquía visual:

### Botón principal (Filled / Contained Button)

Es el botón con **más peso visual**. Debe usarse **una sola vez por pantalla**.

**Características**

* Fondo sólido (color primario)
* Texto en color contrastado
* Llama claramente la atención

**Configuración**

```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Guardar"
    style="?attr/materialButtonFilledStyle"/>
```

---

### Botón secundario (Outlined Button)

Acción importante, pero **no principal**.

**Características**

* Sin fondo
* Borde visible
* Menor peso visual que el principal

**Configuración**

```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Cancelar"
    style="?attr/materialButtonOutlinedStyle"/>
```

---

### Botón de texto (Text Button)

Acciones auxiliares o poco críticas.

**Características**

* Sin fondo
* Sin borde
* Solo texto

**Configuración**

```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Más información"
    style="?attr/materialButtonTextStyle"/>
```

 No todos los botones deben llamar la atención igual.

---

## 11. Feedback al usuario: **Toast vs Snackbar**

En una interfaz gráfica, **el feedback es obligatorio**: el usuario necesita saber si una acción se ha realizado correctamente, si ha fallado o si requiere atención.

En Android existen dos mecanismos habituales para mostrar mensajes breves:

* `Toast`
* `Snackbar`

Aunque ambos muestran texto temporal, **no cumplen el mismo papel en términos de UX**.

---

### 11.1. `Toast`

![Image](https://developer.android.com/static/images/toast.png)

#### Características

* Flota sobre la pantalla
* No pertenece al layout de la app
* Aparece en una posición genérica
* No está asociado visualmente a ninguna acción concreta

```java
Toast.makeText(this, "Aviso añadido", Toast.LENGTH_SHORT).show();
```

#### Problemas desde el punto de vista de DI

*  **Se pierde el contexto**: el mensaje no indica claramente qué acción lo ha provocado.
*  Puede pasar desapercibido.
*  No respeta el estilo visual de la aplicación.
*  No permite interacción (deshacer, repetir, etc.).
*  Experiencia de usuario pobre en apps modernas.

 Hoy en día, `Toast` se considera **feedback genérico**, no integrado.

---

### 11.2. `Snackbar` (recomendado)

![Image](https://media.geeksforgeeks.org/wp-content/uploads/20201102165255/GFGbackframe.gif)

#### Características

* Está **anclado a la interfaz**
* Aparece en la parte inferior de la pantalla
* Está **relacionado con la acción del usuario**
* Sigue las guías de **Material Design**
* Puede incluir acciones adicionales (UNDO, REINTENTAR…)

Ejemplo básico:

```java
Snackbar.make(
    findViewById(android.R.id.content),
    "Aviso añadido",
    Snackbar.LENGTH_SHORT
).show();
```

#### Ventajas desde el punto de vista de UX

*  El mensaje **forma parte de la UI**
*  El usuario entiende **qué ha pasado y por qué**
*  No rompe la experiencia visual
*  Es consistente con el resto de componentes Material
*  Mejora accesibilidad y claridad

---

### 11.3. Snackbar con acción (valor añadido)

Una de las grandes ventajas del `Snackbar` es que **permite deshacer acciones**:

```java
Snackbar.make(view, "Aviso eliminado", Snackbar.LENGTH_LONG)
        .setAction("DESHACER", v -> restaurarAviso())
        .show();
```

Esto:

* Reduce errores del usuario
* Aumenta la confianza
* Mejora notablemente la experiencia de uso

---

### 11.4. Comparativa directa

| Aspecto                | Toast   | Snackbar |
| ---------------------- | ------- | -------- |
| Integración en la UI   |  No    |  Sí     |
| Relación con la acción |  Débil |  Clara  |
| Estilo Material        |  No    |  Sí     |
| Posibilidad de acción  |  No    |  Sí     |
| UX recomendada         |  No    |  Sí     |

---

### 11.5. Cuándo usar cada uno

#### Uso aceptable de `Toast`

* Mensajes **muy genéricos**
* Debug rápido
* Prototipos o prácticas iniciales

#### Uso correcto de `Snackbar`

* Confirmación de acciones del usuario
* Avisos de éxito o error
* Feedback tras botones, formularios, listas, etc.

---

### 11.6. Regla clara para Desarrollo de Interfaces

> **Acciones de la UI → Snackbar**

Si el usuario:

* pulsa un botón
* guarda datos
* borra un elemento
* envía un formulario

el feedback **debe mostrarse con Snackbar**, no con Toast.

---

### 11.7. Idea clave

> `Toast` **funciona**,
> pero `Snackbar` **se integra**.

En **Desarrollo de Interfaces**, se valora:

* claridad,
* coherencia visual,
* y experiencia de usuario,

no solo que “salga un mensaje”.

---

##  PRÁCTICA 2 — MEJORA DE UI (con código)

**Obligatoria**

1. Sustituir `EditText` por `TextInputLayout`
2. Cambiar botones a `MaterialButton`
3. Usar Snackbar
4. Mejorar el mensaje de “sin avisos”
5. Comprobar:

   * El Snackbar **no se repite** al rotar
   * El estado se mantiene

