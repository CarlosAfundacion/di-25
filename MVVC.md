## TEMA: ARQUITECTURA ANDROID (MVVM, LIFECYCLE Y REPOSITORIO)

---

## 1. INTRODUCCIÓN: DE TKINTER A ANDROID

Venimos de trabajar con **Tkinter en Python** usando el patrón MVC (Modelo-Vista-Controlador). En ese entorno, la vida era sencilla: creabas una ventana, declarabas variables y estas seguían vivas hasta que cerrabas el programa.

### 1.1 El "Problema" de Android
Android funciona de manera muy diferente. El sistema operativo gestiona los recursos agresivamente.

**El caso de la rotación de pantalla:**
En Tkinter, si giras el monitor, la ventana sigue igual. En Android, cuando un usuario gira el móvil:
1.  El sistema **DESTRUYE** la Activity (la pantalla actual).
2.  El sistema **RECREA** una nueva Activity desde cero para ajustarse al nuevo ancho/alto.

> **Consecuencia fatal:** Si tenías una variable `int contador = 5;` declarada dentro de tu Activity, al girar la pantalla, la Activity muere y la nueva nace con `contador = 0`. Se han perdido los datos.

### 1.2 La solución: MVVM (Model - View - ViewModel)
Para solucionar esto y organizar el código profesionalmente, Google impone la arquitectura MVVM.

| Concepto | En Tkinter (MVC) | En Android (MVVM) | Responsabilidad |
| :--- | :--- | :--- | :--- |
| **Vista** | Ventana (`Tk()`) | **Activity / Fragment** | **"Tonta"**. Solo muestra XML y captura clics. No guarda datos. Se destruye y recrea a menudo. |
| **Controlador** | Clase Controller | **ViewModel** | **"Cerebro"**. Contiene la lógica y el estado. **Sobrevive** a la rotación de pantalla. |
| **Datos** | Variables/Clases | **Repository** | **"Almacén"**. Decide si los datos vienen de una lista en memoria, BD (Room) o Internet (Retrofit). |
| **Comunicación** | Controller llama a View | View **observa** a VM | **Reactividad**. La View se suscribe a los datos (`LiveData`). Si el dato cambia, la View se actualiza sola. |

---

## 2. ESTRUCTURA DE UN PROYECTO ANDROID

Para este ejemplo, vamos a organizar el código en **paquetes** (carpetas dentro de Java), que es como se trabaja en empresas reales.

Supondremos que tu paquete base es `com.example.mvvm`.
La estructura de carpetas será:

1.  **`com.example.mvvm.data`**: Aquí irá el `StockRepository`.
2.  **`com.example.mvvm.viewmodel`**: Aquí irán `MainViewModel` y su Factory.
3.  **`com.example.mvvm.ui`**: Aquí irá `MainActivity`.

---

## 3. LOS COMPONENTES DE LA ARQUITECTURA 

### 3.1 La Vista: Activity y Fragment
Representan la interfaz.
*   **Activity:** Es el contenedor principal (marco de la ventana).
*   **Fragment:** Es una porción de pantalla reutilizable. Una Activity puede contener varios Fragments.

**Regla de Oro:** La Vista **NUNCA** debe contener lógica de negocio. Si pones lógica aquí, la perderás al girar la pantalla.

### 3.2 El ViewModel
Es una clase diseñada para almacenar y gestionar datos relacionados con la interfaz.
*   **Superpoder:** Sobrevive a los cambios de configuración (rotaciones).
*   **Prohibición:** Un ViewModel **NUNCA** debe tener referencias a objetos de la vista (`Button`, `TextView`, `Context`). Si lo hace, provocará fugas de memoria.

### 3.3 LiveData (El Observador)
Es un contenedor de datos observable.
*   **`MutableLiveData`**: Se usa dentro del ViewModel. Permite leer y escribir (`set`, `get`).
*   **`LiveData`**: Se expone a la Vista. Solo permite leer y observar.

**Funcionamiento:**
1.  La Activity se suscribe (`observe`) al LiveData.
2.  El ViewModel cambia el valor (`setValue`).
3.  Automáticamente, se ejecuta el código en la Activity para actualizar la pantalla.

### 3.4 El Repositorio
Es la única fuente de verdad. El ViewModel le pide datos al Repositorio, y el Repositorio decide si los saca de una lista estática, de una base de datos local o de una API.

---

## GESTOR DE STOCK
Una app para añadir productos a una lista. Debe mantener los datos al girar la pantalla.

---

### 4.1 LA VISTA (XML)
**Archivo:** `res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="20dp"
    android:gravity="center_horizontal">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Control de Stock"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="20dp"/>

    <EditText
        android:id="@+id/etProducto"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Nombre del producto"/>

    <Button
        android:id="@+id/btnGuardar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Guardar Producto"
        android:layout_marginTop="10dp"/>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:text="Inventario Actual:"
        android:textStyle="bold"/>

    <TextView
        android:id="@+id/tvListado"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="10dp"
        android:background="#EFEFEF"
        android:padding="10dp"
        android:gravity="top"/>

</LinearLayout>
```

---

### 4.2 LA CAPA DE DATOS (REPOSITORY)
Simularemos una base de datos.
**Ubicación:** `com.example.mvvm.data`

```java
package com.example.mvvm.data; // 1. Declaramos el paquete

import java.util.ArrayList;
import java.util.List;

public class StockRepository {
    // Esta lista simula nuestra Base de Datos
    private final List<String> database = new ArrayList<>();

    public StockRepository() {
        // Datos iniciales
        database.add("Portátil Gaming");
        database.add("Ratón USB");
    }

    // Método para LEER datos
    public List<String> getAllProducts() {
        return new ArrayList<>(database);
    }

    // Método para ESCRIBIR datos
    public void addProduct(String product) {
        database.add(product);
    }
}
```

---

### 4.3 EL VIEWMODEL (LÓGICA)
Gestiona la comunicación.
**Ubicación:** `com.example.mvvm.viewmodel`

```java
package com.example.mvvm.viewmodel; // 1. Declaramos el paquete

// 2. IMPORTAMOS el repositorio que está en otro paquete
import com.example.mvvm.data.StockRepository;

// 3. Imports de Android
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final StockRepository repository;
    
    // LiveData del estado
    private final MutableLiveData<String> _stockState = new MutableLiveData<>();
    private final MutableLiveData<String> _errorState = new MutableLiveData<>();

    public MainViewModel(StockRepository repository) {
        this.repository = repository;
        refreshData(); 
    }

    public LiveData<String> getStockList() {
        return _stockState;
    }
    
    public LiveData<String> getError() {
        return _errorState;
    }

    public void addNewProduct(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            _errorState.setValue("El nombre no puede estar vacío");
            return;
        }
        
        repository.addProduct(productName);
        refreshData();
    }

    private void refreshData() {
        List<String> products = repository.getAllProducts();
        StringBuilder sb = new StringBuilder();
        for (String p : products) {
            sb.append("• ").append(p).append("\n");
        }
        _stockState.setValue(sb.toString());
    }
}
```

---

### 4.4 LA FACTORY
**Ubicación:** `com.example.mvvm.viewmodel`

Se utiliza ViewModelFactory porque Android no sabe cómo crear un ViewModel que tiene parámetros en el constructor. Este es un caso de bolierplate: código repetitivo, obligatorio y poco expresivo, que no aporta lógica de negocio, pero es necesario por la arquitectura o el framework.

```java
package com.example.mvvm.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.mvvm.data.StockRepository; // Import necesario

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final StockRepository repository;

    public MainViewModelFactory(StockRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
```

---

### 4.5 LA ACTIVITY (LA VISTA)
**Ubicación:** `com.example.mvvm.ui`

```java
package com.example.mvvm.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// IMPORTS DE NUESTRAS CLASES (Porque están en otros paquetes)
import com.example.mvvm.data.StockRepository;
import com.example.mvvm.viewmodel.MainViewModel;
import com.example.mvvm.viewmodel.MainViewModelFactory;
import com.example.mvvm.R; // Importante para encontrar R.layout

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. INICIALIZAR VISTAS ---
        EditText etProducto = findViewById(R.id.etProducto);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        TextView tvListado = findViewById(R.id.tvListado);

        // --- 2. CONFIGURAR ARQUITECTURA ---
        StockRepository repository = new StockRepository();
        MainViewModelFactory factory = new MainViewModelFactory(repository);
        
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        // --- 3. OBSERVAR DATOS (REACTIVIDAD) ---
        viewModel.getStockList().observe(this, textoActualizado -> {
            tvListado.setText(textoActualizado);
        });

        viewModel.getError().observe(this, mensajeError -> {
            Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
        });

        // --- 4. CAPTURAR EVENTOS ---
        btnGuardar.setOnClickListener(v -> {
            String nombre = etProducto.getText().toString();
            viewModel.addNewProduct(nombre);
            etProducto.setText(""); 
        });
    }
}
```

