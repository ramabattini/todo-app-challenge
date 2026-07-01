# TodoApp — Challenge Técnico AranguriApps

App de gestión de tareas desarrollada como challenge técnico para AranguriApps.

## ¿De qué trata?

Una app Android para crear, ver, editar y eliminar tareas. Cada tarea tiene título, descripción, prioridad (alta, media o baja) y un estado de completado. Los datos se guardan en Supabase y se sincronizan con la nube.

## Pantallas

- **Lista de tareas** — muestra todas las tareas con filtros por estado
- **Detalle** — información completa de una tarea con opciones de editar y eliminar
- **Formulario** — para crear una tarea nueva o editar una existente

## Arquitectura

Usé MVVM con separación en capas. Elegí este patrón porque separa claramente la lógica de negocio de la interfaz, es testeable y es el patrón recomendado por Google para apps Android modernas.

```
┌─────────────────────────────────────────┐
│              UI Layer                   │
│  TaskListScreen  TaskDetailScreen       │
│  TaskFormScreen  AppNavigation          │
│         ↑ StateFlow (UiState)           │
├─────────────────────────────────────────┤
│           ViewModel Layer               │
│         TaskViewModel                   │
│   StateFlow  ·  viewModelScope          │
│         ↑ TaskRepository (interfaz)     │
├─────────────────────────────────────────┤
│            Data Layer                   │
│   SupabaseTaskRepository                │
│         ↑ Retrofit / OkHttp             │
├─────────────────────────────────────────┤
│              Backend                    │
│         Supabase (PostgreSQL)           │
└─────────────────────────────────────────┘
```

- `data/model` — modelos de datos (Task, CreateTaskRequest, UpdateTaskRequest, Priority)
- `data/network` — cliente HTTP e interfaz de Supabase (Retrofit)
- `data/repository` — interfaz `TaskRepository` + implementación `SupabaseTaskRepository`
- `ui/viewmodel` — `TaskViewModel` con `StateFlow` para el estado de UI
- `ui/screens` — pantallas en Jetpack Compose
- `core` — constantes y configuración

## Flujo de navegación

```
TaskListScreen
    │
    ├──[click tarea]──► TaskDetailScreen
    │                        │
    │                        └──[editar]──► TaskFormScreen (edición)
    │
    └──[+ nueva]──► TaskFormScreen (creación)
```

## Herramientas de IA

Usé Claude de Anthropic como asistente principal durante el desarrollo. Lo usé para:
- Generar el scaffolding inicial de la arquitectura MVVM
- Implementar las pantallas en Jetpack Compose
- Configurar Retrofit para consumir la API REST de Supabase
- Escribir los tests unitarios del ViewModel con FakeRepository
- Revisar y corregir bugs en el código generado

Mi rol fue definir la arquitectura, tomar decisiones técnicas, auditar cada archivo generado, corregir errores de compilación y validar que los flujos de la app funcionaran correctamente.

## Stack

- Kotlin + Jetpack Compose
- Retrofit + Gson
- Supabase (BaaS)
- ViewModel + StateFlow
- Material3
- JUnit + kotlinx-coroutines-test

## Cómo correr el proyecto

1. Clonar el repositorio
2. Abrir en Android Studio
3. Esperar el Gradle Sync
4. Correr en dispositivo o emulador con API 26+

Las credenciales de Supabase se leen desde `local.properties` (no se commitean por seguridad). Copiá `local.properties.example` a `local.properties` y completá los valores con las credenciales del proyecto.

## Tests

Tests unitarios en `app/src/test/java/com/ramiro/todoapp/TaskViewModelTest.kt`.

Cubren los flujos principales del ViewModel:
- Carga exitosa de tareas
- Manejo de error de red
- Creación de tarea con callback de éxito y error
- Toggle de completado sin borrar la lista en caso de fallo
- Limpieza del estado de error

Para correrlos: clic derecho → Run Tests, o desde terminal: `./gradlew testDebugUnitTest`
