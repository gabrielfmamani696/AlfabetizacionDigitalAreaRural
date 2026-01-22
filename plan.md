lan de Aprendizaje: Alfabetización Digital Rural (Sprint 1)
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