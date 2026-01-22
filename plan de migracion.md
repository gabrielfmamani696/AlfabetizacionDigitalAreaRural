# Guía de Migración y Configuración del Entorno

Este documento detalla los requisitos necesarios para ejecutar este proyecto en otra computadora sin errores de versionamiento.

## 1. Versión de Java (JDK)

El error más común es la discrepancia entre la versión de Java que usa Gradle para ejecutarse y la versión que el código necesita.

*   **Requisito Crítico:** Necesitas instalar el **JDK 21** (Java Development Kit 21).
    *   *Por qué:* Aunque tu código dice `sourceCompatibility = JavaVersion.VERSION_11` (compila a Java 11), usas **Gradle 9.1.0** y **Android Gradle Plugin 9.0.0**. Estas herramientas modernas requieren una versión reciente de Java para correr (mínimo JDK 17, recomendado JDK 21 en 2026).
*   **Configuración en Android Studio:**
    1.  Ve a `Settings` (o `Preferences` en Mac) > `Build, Execution, Deployment` > `Build Tools` > `Gradle`.
    2.  En "Gradle JDK", asegúrate de seleccionar **Java 21** (o el que corresponda a la instalación del JDK 21). **No uses una versión 1.8 o 11 aquí o el build fallará.**

## 2. Android Studio

Dado que utilizas `compileSdk 36` (Android 16 / Baklava) y AGP 9.0.0:

*   Descarga e instala la **última versión disponible de Android Studio** (versión estable más reciente o Preview si la estable no soporta API 36 aun). Versiones antiguas no reconocerán el plugin de Android 9.0.0.

## 3. Android SDK

Al abrir el proyecto por primera vez, Android Studio intentará descargar lo necesario, pero verifica que tengas instalado:

*   **Android SDK Platform 36** (Baklava).
*   **Android SDK Build-Tools** (versión más reciente).
    *   Puedes ver esto en: `Tools` > `SDK Manager`.

## 4. Verificación Rápida

Si tienes errores al sincronizar (Sync) el proyecto en la nueva máquina:

1.  **Error "Unsupported Java. Your build is currently configured to use Java 11..."**:
    *   *Solución:* Cambia el "Gradle JDK" como se explicó en el punto 1 a JDK 21.

2.  **Error "Plugin [id: 'com.android.application', version: '9.0.0', ...] was not found"**:
    *   *Solución:* Asegúrate de tener conexión a internet y que en `settings.gradle.kts` estén los repositorios `google()` y `mavenCentral()` (ya lo están, pero verifica que la red no los bloquee).

3.  **Error "KSP..."**:
    *   Si cambias la versión de Kotlin en el futuro, recuerda que la versión de KSP (`libs.versions.toml`) debe coincidir exactamente con la de Kotlin. Actualmente tienes:
        *   Kotlin: `2.0.21`
        *   KSP: `2.0.21-1.0.27` (Correcto).

## Resumen de Instalación en Nueva PC

1.  Instalar **JDK 21**.
2.  Instalar **Android Studio (Última versión)**.
3.  Clonar/Copiar el proyecto.
4.  Abrir Android Studio y verificar que apunte al **JDK 21** en la configuración de Gradle.
5.  Dejar que Gradle descargue las dependencias y el SDK 36.

####
####


plan de Aprendizaje: Alfabetización Digital Rural (Sprint 1)
🛠️ Fase 1: Los Cimientos (Configuración)
1. Preparar el Terreno (Configuración Gradle)
   Agregar versiones de Room, Navigation, Coil y KSP en
   gradle/libs.versions.toml
   Configurar plugins y dependencias en
   app/build.gradle.kts
   y
   build.gradle.kts
   (Root)
   Sincronizar y verificar build
   💾 Fase 2: La Memoria (Base de Datos)
2. Crear la Entidad Usuario (UserEntity.kt)
   Crear paquete data.local
   Definir data class con @Entity
3. Crear el DAO (UserDao.kt)
   Definir interface con @Dao
   Métodos @Insert y @Query
4. Conectar la Base de Datos (AppDatabase.kt)
   Clase abstracta RoomDatabase
   Singleton instance
   📦 Fase 3: El Intermediario (Repositorio)
5. Crear el Repositorio (UserRepository.kt)
   Crear paquete data.repository
   Constructor con UserDao
   Funciones suspendidas
   🎨 Fase 4: La Cara (Interfaz de Usuario)
6. Diseñar el Onboarding (ProfileSetupScreen.kt)
   Paquete ui.onboarding
   Input texto (nombre)
   Grid avatares
   Botón "Comenzar"
7. Navegación Principal
   Configurar NavHost en MainActivity