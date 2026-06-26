# TodoApp — Challenge Técnico AranguriApps

App móvil Android de gestión de tareas desarrollada como parte del proceso de selección para Software Engineer Mobile en AranguriApps.

---

## ¿De qué trata el proyecto?

TodoApp permite al usuario crear, visualizar, editar y eliminar tareas personales. Cada tarea tiene un título, descripción, nivel de prioridad (Alta / Media / Baja) y un estado de completado. Las tareas se sincronizan en tiempo real con una base de datos en la nube usando Supabase.

**Pantallas:**
- **Lista de tareas** — muestra todas las tareas con filtros por estado (Todas / Pendientes / Completadas)
- **Detalle de tarea** — muestra la información completa con opciones de editar y eliminar
- **Formulario** — permite crear una nueva tarea o editar una existente

---

## Arquitectura

Se eligió **MVVM (Model-View-ViewModel)** con separación en capas inspirada en Clean Architecture.

```
app/
├── data/
│   ├── model/          # Modelos de datos (Task, CreateTaskRequest, UpdateTaskRequest)
│   ├── remote/         # Cliente Retrofit + interfaz de la API de Supabase
│   └── repository/     # TaskRepository: abstrae el acceso a datos
├── ui/
│   ├── viewmodel/      # TaskViewModel: lógica de negocio y estado de la UI
│   ├── screens/        # Pantallas Compose (List, Detail, Form)
│   └── theme/          # Colores, tipografía y tema Material3
└── navigation/         # AppNavigation con NavHost
```

### ¿Por qué MVVM?

- **Separación clara** entre la vista (Compose) y la lógica de negocio (ViewModel)
- **Testeable**: el ViewModel y el Repository pueden probarse de forma aislada
- **Reactivo**: el estado de la UI se maneja con `StateFlow`, que Compose consume de forma eficiente evitando recomposiciones innecesarias
- Es el patrón **recomendado oficialmente por Google** para apps Android modernas

### Diagrama de arquitectura

```
┌─────────────────────────────────────────────┐
│                   UI Layer                  │
│  TaskListScreen  TaskDetailScreen  TaskForm │
│              (Jetpack Compose)              │
└───────────────────┬─────────────────────────┘
                    │ observa StateFlow
┌───────────────────▼─────────────────────────┐
│              ViewModel Layer                │
│              TaskViewModel                  │
│   UiState: Loading | Success | Error        │
└───────────────────┬─────────────────────────┘
                    │ llama suspend functions
┌───────────────────▼─────────────────────────┐
│              Data Layer                     │
│            TaskRepository                  │
└───────────────────┬─────────────────────────┘
                    │ HTTP via Retrofit
┌───────────────────▼─────────────────────────┐
│           Supabase REST API                 │
│     (PostgreSQL en la nube - BaaS)          │
└─────────────────────────────────────────────┘
```

---

## Stack tecnológico

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Navigation Compose | Navegación entre pantallas |
| ViewModel + StateFlow | Gestión de estado reactivo |
| Retrofit + Gson | Cliente HTTP para consumir la API REST |
| Supabase | Backend as a Service (base de datos PostgreSQL) |
| Coroutines | Llamadas asíncronas a la API |
| Material3 | Guías de diseño y componentes visuales |

---

## Herramientas de IA utilizadas

Este proyecto fue desarrollado orquestando **Claude (Anthropic)** como asistente principal de generación de código.

### ¿Cómo se usó la IA?

- **Generación de estructura**: se le indicó la arquitectura MVVM y la separación en capas; la IA generó el esqueleto del proyecto
- **Integración con Supabase**: se le proporcionó la URL y la API key del proyecto; la IA generó el cliente Retrofit con los headers correctos
- **Pantallas Compose**: se describieron los requisitos de cada pantalla (filtros, navegación, validaciones) y la IA generó los composables
- **Unit tests**: se le pidió que generara tests para la lógica de filtrado y los estados de la UI

### Criterio técnico aplicado

La IA no reemplazó el criterio de ingeniería — se auditó cada archivo generado para verificar:
- Que los tipos de datos coincidan con el esquema de Supabase
- Que el manejo de errores sea correcto en todos los flujos
- Que no haya recomposiciones innecesarias en Compose
- Que la navegación con `NavHost` y los argumentos de tipo `Long` funcionen correctamente

---

## Cómo compilar y correr el proyecto

### Requisitos

- Android Studio Ladybug o superior
- JDK 11+
- Dispositivo Android o emulador con API 26+

### Pasos

1. Cloná el repositorio:
```bash
git clone https://github.com/ramabattini/todo-app-challenge.git
```

2. Abrí el proyecto en Android Studio con **File → Open**

3. Esperá que termine el Gradle Sync automático

4. Conectá un dispositivo o iniciá un emulador

5. Presioná el botón **Run** (▶) o usá `Shift + F10`

### Base de datos

El proyecto usa Supabase como backend. La URL y la API key ya están configuradas en `RetrofitClient.kt`. No se requiere ninguna configuración adicional para correr la app.

---

## Tests

Los tests unitarios se encuentran en `app/src/test/java/com/ramiro/todoapp/TaskViewModelTest.kt`.

Para correrlos desde Android Studio: clic derecho sobre el archivo → **Run Tests**.

Desde terminal:
```bash
./gradlew test
```

---

## Decisiones de diseño

- **Android Nativo en lugar de KMP**: se eligió Android Nativo dado que el entorno de desarrollo es Windows, lo que imposibilita compilar el target iOS de Kotlin Multiplatform (requiere macOS con Xcode).
- **Retrofit sobre Supabase SDK**: se usó Retrofit directamente contra la REST API de Supabase para mantener las dependencias simples y el código más legible y explicable.
- **StateFlow sobre LiveData**: StateFlow es la opción moderna recomendada para Compose, ya que es compatible con coroutines y no requiere un `LifecycleOwner`.
