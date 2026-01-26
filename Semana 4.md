

#  SEMANA 4 · LUNES

## Material Design profesional: **Tema importado + aplicación real**

---

## 0. Contexto y objetivo del día

Hasta ahora, *Aula+ Lite*:

* Usa **Material Design**, pero:

  * con colores por defecto,
  * sin identidad visual propia,
  * sin coherencia global controlada.
* Los componentes son Material, pero **el diseño no está centralizado**.

En una app real:

> **La identidad visual NO se decide pantalla a pantalla.
> Se define en un tema global.**

El objetivo de esta sesión es **profesionalizar la interfaz**, sin tocar lógica ni Firebase.

---

## 1. Qué es realmente un tema en Android (idea clara)

Un **tema** define de forma global:

* Colores principales
* Colores secundarios
* Fondo
* Colores de texto
* Apariencia de botones, inputs, diálogos, etc.

Un tema **NO es un layout**
Un tema **NO es un drawable**
Un tema **NO es código Java**

Un tema es un **conjunto de recursos** que Android aplica automáticamente.

---

## 2. Material Design 3 y por qué usamos Theme.Material3.DayNight

En este proyecto usamos **Material Design 3 (Material You)**.

Características clave:

* Componentes modernos
* Mejor accesibilidad
* Soporte nativo de **modo claro / oscuro**
* Colores derivados automáticamente

### Regla DI importante

> Si la app no soporta Dark Mode correctamente,
> **la interfaz no es profesional**, aunque “funcione”.

---

## 3. Generar un tema Material 3 (fuera de Android Studio)

Android Studio **NO es la herramienta adecuada** para diseñar un tema.

Google proporciona una herramienta específica:

 **Material Theme Builder (Material 3)**
[https://m3.material.io/theme-builder](https://m3.material.io/theme-builder)

---

### 3.1 Qué hacemos en el Theme Builder

1. Elegir **Material 3**
2. Definir:

   * Color primario
   * Color secundario
   * (opcional) color terciario
3. Comprobar:

   * Contraste
   * Legibilidad
4. Exportar para **Android (XML)**

> No se evalúa estética, se evalúa **coherencia y legibilidad**.

---

## 4. Archivos que genera un tema Material

Al exportar, el Theme Builder genera varios archivos XML.

Los importantes para nosotros son:

* `colors.xml`
* `themes.xml`
* `themes.xml` (night)

No hace falta entenderlos al 100%, pero **sí saber para qué sirven**.

---

## 5. Importar el tema en el proyecto Android

### 5.1 Dónde van los archivos

Copiar los archivos exportados en:

```
app/src/main/res/values/
app/src/main/res/values-night/
```

Normalmente:

* `values/colors.xml`
* `values/themes.xml`
* `values-night/themes.xml`

 **No mezclar** con otros proyectos
 
 **No renombrar recursos sin saber lo que se hace**

---

## 6. El archivo `themes.xml` (clave del día)

Ejemplo típico (simplificado):

```xml
<resources>

    <style name="Theme.AulaPlus"
        parent="Theme.Material3.DayNight.NoActionBar">

        <!-- Colores principales -->
        <item name="colorPrimary">@color/md_theme_primary</item>
        <item name="colorOnPrimary">@color/md_theme_onPrimary</item>

        <item name="colorSecondary">@color/md_theme_secondary</item>
        <item name="colorOnSecondary">@color/md_theme_onSecondary</item>

        <item name="colorBackground">@color/md_theme_background</item>
        <item name="colorOnBackground">@color/md_theme_onBackground</item>

    </style>

</resources>
```

### Ideas clave

* **parent**: define el comportamiento base (Material3 + DayNight)
* Los colores vienen de `colors.xml`
* Android aplica esto **a toda la app**

---

## 7. Aplicar el tema a la aplicación

Un tema **no hace nada** si no se aplica.

### 7.1 AndroidManifest.xml

En la etiqueta `<application>`:

```xml
<application
    android:theme="@style/Theme.AulaPlus"
    ... >
```

O, si se quiere solo para una Activity:

```xml
<activity
    android:name=".ui.MainActivity"
    android:theme="@style/Theme.AulaPlus" />
```

 En *Aula+ Lite*, lo correcto es aplicarlo **a la aplicación completa**.

---

## 8. Verificación inmediata (obligatoria)

Tras aplicar el tema:

* Botones Material cambian de color
* Inputs (`TextInputLayout`) usan los nuevos colores
* Snackbar respeta el esquema de color
* Fondo y textos son coherentes

Si **no cambia nada**, algo está mal:

* Tema no aplicado
* Colores no referenciados
* Error en nombres

---

## 9. Tema ≠ Layout (error común)

Cambiar el tema:

* **NO rompe MVVM**
* **NO rompe Firebase**
* **NO rompe Navigation**
* **NO obliga a tocar Java**

Esto es **diseño de interfaces**, no lógica.

---

## 10. PRÁCTICA INCREMENTAL

### Objetivo

Dotar a *Aula+ Lite* de una **identidad visual propia**, coherente y profesional.

### Tareas obligatorias

1. Generar un tema Material 3 con Theme Builder
2. Importar los archivos XML al proyecto
3. Crear el estilo `Theme.AulaPlus`
4. Aplicarlo a la aplicación
5. Ejecutar y verificar cambios visibles

---

## 11. Pulir la UI con el tema aplicado (sin añadir lógica)

Con el tema ya activo, revisamos pantallas:

### Login / Register

* `TextInputLayout`:

  * label visible
  * error legible
* Botón principal claramente reconocible
* Colores con contraste suficiente

### Home

* Lista visible sobre fondo correcto
* Texto legible en modo claro
* Snackbar con color coherente

---

## 12. Comprobaciones finales (obligatorias)

El alumno debe comprobar:

* La app **no ha perdido funcionalidad**
* Login y Register siguen funcionando
* Navigation sigue correcta
* Firebase no se ha tocado
* El cambio es **solo visual**


---

## Splash Screen profesional + Dark Mode + cambio de modo desde la app

---

## 0. Contexto del día

*Aula+ Lite* ya dispone de:

* Un **tema Material 3 propio**, importado correctamente.
* Identidad visual coherente y centralizada.
* Componentes Material aplicados sin colores hardcodeados.
* Base preparada para **modo claro / oscuro**.

En esta sesión se trabajan **dos aspectos clave del diseño de interfaces móviles profesionales**:

1. **Pantalla de arranque real (Splash Screen oficial)**
2. **Gestión correcta del modo oscuro**
3. **Control manual del modo Día/Noche desde la aplicación**

Todo lo visto hoy afecta **exclusivamente a la interfaz**, no a la lógica de negocio.

---

## 1. Splash Screen: qué es y qué NO es

### 1.1 Qué NO es un Splash Screen profesional

No se considera profesional:

* Una Activity extra con una imagen
* Un `Handler.postDelayed()`
* Una pantalla falsa que “espera” unos segundos
* Lógica manual para simular una carga

Ese enfoque está **obsoleto** y penaliza UX.

---

### 1.2 Qué es un Splash Screen profesional

Desde Android 12 (API 31), Google define un **sistema oficial**:

 **SplashScreen API**

Características:

* Gestionado por el sistema
* Instantáneo
* Sin Activities extra
* Sin retrasos artificiales
* Integrado con el tema de la aplicación

> El Splash Screen **no es una pantalla**,
> es una **fase visual del arranque de la app**.

---

## 2. Funcionamiento del Splash Screen API

El sistema realiza automáticamente:

1. Arranque de la aplicación
2. Muestra del Splash definido en el tema
3. Carga de la Activity real
4. Transición automática al contenido

Todo esto ocurre **sin lógica compleja ni layouts propios**.

---

## 3. Dependencia necesaria

En `app/build.gradle`:

```gradle
dependencies {
    implementation "androidx.core:core-splashscreen:1.0.1"
}
```

Si ya existe, **no se duplica**.

---

## 4. Tema específico para el Splash Screen

El Splash **no usa el tema general de la app**.
Necesita un **tema exclusivo**, cuyo único propósito es el arranque.

### 4.1 Definición del tema Splash

En `res/values/themes.xml`:

```xml
<style name="Theme.AulaPlus.Splash"
    parent="Theme.SplashScreen">

    <item name="windowSplashScreenBackground">
        @color/md_theme_primary
    </item>

    <item name="windowSplashScreenAnimatedIcon">
        @drawable/ic_launcher_foreground
    </item>

    <item name="postSplashScreenTheme">
        @style/Theme.AulaPlus
    </item>

</style>
```

### Idea clave

* El Splash **no define layouts**
* Todo es **configuración de tema**
* El sistema aplica automáticamente el tema real tras el arranque

---

## 5. Aplicar el tema Splash

En `AndroidManifest.xml`, solo a la Activity inicial:

```xml
<activity
    android:name=".ui.MainActivity"
    android:theme="@style/Theme.AulaPlus.Splash">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

---

## 6. Código mínimo en MainActivity

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    SplashScreen.installSplashScreen(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
}
```

No es necesario añadir más código.

---

## 7. Verificación del Splash Screen

Al ejecutar la app:

* Se muestra el icono
* Fondo con color del tema
* Transición limpia a Login / AuthGate
* Sin pantallas en blanco ni retardos

Si hay parpadeos, la configuración es incorrecta.

---

## 8. Dark Mode: concepto correcto

### 8.1 Qué NO es Dark Mode

No es correcto:

* Cambiar colores manualmente
* Usar `if/else` en Java
* Duplicar layouts

Eso rompe la escalabilidad y la coherencia visual.

---

### 8.2 Dark Mode correcto en Android

Se basa en:

* `Theme.Material3.DayNight`
* Recursos alternativos en `values-night`
* Decisión centralizada por el sistema

---

## 9. Tema base preparado para Dark Mode

En `themes.xml`:

```xml
<style name="Theme.AulaPlus"
    parent="Theme.Material3.DayNight.NoActionBar">
```

Esto es **obligatorio**.

---

## 10. Archivo `values-night/themes.xml`

```xml
<resources>

    <style name="Theme.AulaPlus"
        parent="Theme.Material3.DayNight.NoActionBar">

        <item name="colorPrimary">@color/md_theme_primary</item>
        <item name="colorOnPrimary">@color/md_theme_onPrimary</item>

        <item name="colorBackground">@color/md_theme_background</item>
        <item name="colorOnBackground">@color/md_theme_onBackground</item>

    </style>

</resources>
```

Android aplica automáticamente estos valores cuando el sistema está en modo oscuro.

---

## 11. Comprobación del Dark Mode

Pruebas obligatorias:

* Texto legible
* Fondos correctos
* Botones visibles
* Snackbar con contraste
* Inputs claros

Si algo no se ve bien, **se corrige en el tema**, no en Java.

---

# 12. Cambio manual de modo Día / Noche desde la app

Hasta ahora, el modo oscuro dependía **solo del sistema**.
Ahora añadimos una mejora profesional:

> **El usuario puede elegir el modo desde la aplicación.**

Esto es habitual en apps reales y perfectamente compatible con Material 3.

---

## 13. Cómo se controla el modo en Android

Android expone una API global:

```java
AppCompatDelegate.setDefaultNightMode(...)
```

Valores:

```java
MODE_NIGHT_YES
MODE_NIGHT_NO
MODE_NIGHT_FOLLOW_SYSTEM
```

Características importantes:

* El cambio es **global**
* La Activity se **recrea automáticamente**
* El ViewModel **no pierde estado**

---

## 14. Arquitectura correcta para esta funcionalidad

 No guardar el modo en la Activity
 No cambiar colores manualmente

 Enfoque correcto:

* La UI lanza el evento
* La preferencia se guarda
* Android aplica el tema adecuado

---

## 15. Repositorio de preferencias

`com.example.aula.data.SettingsRepository`

```java
public class SettingsRepository {

    private static final String PREFS_NAME = "settings";
    private static final String KEY_DARK_MODE = "dark_mode";

    private final SharedPreferences prefs;

    public SettingsRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}
```

---

## 16. ViewModel de ajustes

```java
public class SettingsViewModel extends ViewModel {

    private final SettingsRepository repo;
    private final MutableLiveData<Boolean> _darkMode = new MutableLiveData<>();

    public SettingsViewModel(SettingsRepository repo) {
        this.repo = repo;
        _darkMode.setValue(repo.isDarkModeEnabled());
    }

    public LiveData<Boolean> getDarkMode() {
        return _darkMode;
    }

    public void setDarkMode(boolean enabled) {
        repo.setDarkMode(enabled);
        _darkMode.setValue(enabled);
    }
}
```

---

## 17. Switch Material en la UI

Ejemplo:

```xml
<com.google.android.material.materialswitch.MaterialSwitch
    android:id="@+id/switchDarkMode"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Modo oscuro" />
```

---

## 18. Conexión del Switch con el ViewModel

```java
switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
    vm.setDarkMode(isChecked);

    AppCompatDelegate.setDefaultNightMode(
        isChecked
            ? AppCompatDelegate.MODE_NIGHT_YES
            : AppCompatDelegate.MODE_NIGHT_NO
    );
});
```

---

## 19. Aplicar la preferencia al arrancar la app

En `MainActivity`, **antes de `setContentView`**:

```java
SettingsRepository repo = new SettingsRepository(this);

AppCompatDelegate.setDefaultNightMode(
    repo.isDarkModeEnabled()
        ? AppCompatDelegate.MODE_NIGHT_YES
        : AppCompatDelegate.MODE_NIGHT_NO
);
```

---

## 20. Pruebas obligatorias

* Cambiar el switch → la app cambia de modo
* La app se recrea sin perder estado
* Cerrar y abrir → se mantiene el modo elegido
* Firebase y navegación siguen funcionando

---

## 21. PRÁCTICA INCREMENTAL

### Obligatoria

1. Implementar Splash Screen oficial
2. Verificar Dark Mode automático
3. Añadir switch Día/Noche
4. Guardar preferencia del usuario
5. Aplicar el modo al arrancar la app


---
