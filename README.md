# Reinos del Saber

Juego educativo de estrategia por turnos para 3 participantes, desarrollado en Java con JavaFX. Cada jugador conquista territorio respondiendo preguntas de matemáticas, inglés y vocabulario.

## Documentación

- [Propuesta de juego](<docs/Reinos del Saber propuesta de juego.md>): presentación, alcance y reglas.
- [Guía técnica del equipo](<docs/Reinos del Saber guía técnica del equipo.md>): diseño, pruebas, riesgos y plan.
- [Enunciado del proyecto](<docs/vv2a5mfvXuI28r8c_I03kWeF0-TgDYwjZ-juego-20-arcade-20-desarrollado-20-en-20-java-20-utilizando-20-la-20-plataforma-20-java-fx.pdf>): entregas exigidas por el módulo.

## Requisitos

| Herramienta | Versión | Para qué |
| --- | --- | --- |
| Git | Cualquiera reciente | Descargar el proyecto y compartir cambios |
| JDK | 21 | Compilar y ejecutar el juego |
| Maven | 3.9 o superior | Construir, probar y ejecutar desde la terminal |
| IntelliJ IDEA | Cualquiera reciente | Editar, ejecutar y depurar el código |

Para solo jugar basta con Git, el JDK y Maven. Para desarrollar se recomienda además IntelliJ IDEA; su versión gratuita es suficiente.

Las demás dependencias, como JavaFX y JUnit, las descarga Maven la primera vez que se usa el proyecto, así que se necesita conexión a internet.

## Instalación

### Linux y macOS

Con [Homebrew](https://brew.sh/), que no requiere permisos de administrador si ya está instalado:

```bash
brew install git openjdk@21 maven
```

Maven hace que Homebrew instale también una versión más reciente de Java, así que hay que indicar que el proyecto usa la 21. Agrega estas líneas al final de `~/.bashrc` y abre una terminal nueva:

```bash
export JAVA_HOME="/home/linuxbrew/.linuxbrew/opt/openjdk@21/libexec"
export PATH="$JAVA_HOME/bin:$PATH"
```

En macOS, agrégalas a `~/.zshrc` y usa este valor para `JAVA_HOME`:

```bash
export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
```

También sirve otra forma de instalación, como el gestor de paquetes de la distribución, siempre que deje Git, el JDK 21 y Maven 3.9 o superior.

### Windows

Ejecuta estos comandos en PowerShell y luego abre una terminal nueva:

```powershell
winget install Git.Git
winget install EclipseAdoptium.Temurin.21.JDK

$v = "3.10.0"
$zip = "$env:TEMP\maven.zip"
$destino = "$env:LOCALAPPDATA\Programs\Maven"
Invoke-WebRequest "https://repo1.maven.org/maven2/org/apache/maven/apache-maven/$v/apache-maven-$v-bin.zip" -OutFile $zip
Expand-Archive $zip -DestinationPath $destino -Force
$path = [Environment]::GetEnvironmentVariable("Path", "User")
[Environment]::SetEnvironmentVariable("Path", "$path;$destino\apache-maven-$v\bin", "User")
```

### IntelliJ IDEA

Descárgalo desde la [página de JetBrains](https://www.jetbrains.com/idea/download/) para tu sistema y sigue el instalador. Otra opción es instalar [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/), que instala y actualiza IntelliJ IDEA con un clic.

La versión gratuita incluye todo lo que usa el proyecto: Java, Maven, JUnit y Git. Los estudiantes también pueden pedir la [licencia educativa](https://www.jetbrains.com/community/education/#students), aunque no es necesaria.

### Comprobar la instalación

```bash
git --version
java -version
mvn -version
```

`java -version` debe mostrar la versión 21, y `mvn -version` debe indicar `Java version: 21`.

### Configurar Git

La primera vez, indica el nombre y el correo que aparecerán en tus cambios:

```bash
git config --global user.name "Tu nombre"
git config --global user.email "tu-correo@ejemplo.com"
```

Usa el mismo correo de tu cuenta de GitHub, para que los cambios aparezcan a tu nombre.

## Descargar el proyecto

Abre una terminal en la carpeta donde quieras guardar el proyecto y ejecuta:

```bash
git clone https://github.com/Rajmaninov1/reinos-del-saber.git
cd reinos-del-saber
```

Cualquiera puede descargar el repositorio, pero para subir cambios hay que pedir al dueño que te agregue como colaborador en GitHub y aceptar la invitación que llega por correo.

## Configurar IntelliJ IDEA

1. En la pantalla de bienvenida, elige **Open** y selecciona la carpeta `reinos-del-saber`. Si pregunta, ábrela como proyecto Maven y elige **Trust Project**.
2. Espera a que termine la barra de progreso inferior: IntelliJ lee el `pom.xml` y descarga JavaFX y JUnit.
3. Abre **File > Project Structure > Project** y en **SDK** elige el JDK 21. Si no aparece, usa **Add SDK > JDK** y selecciona la carpeta donde se instaló, o **Download JDK** con la versión 21 de Eclipse Temurin. Deja **Language level** en **SDK default**.
4. Abre **Settings > Build, Execution, Deployment > Build Tools > Maven > Runner** y comprueba que **JRE** sea **Use Project JDK**.
5. Para conectar tu cuenta de GitHub, abre **Settings > Version Control > GitHub**, pulsa **+** y elige **Log In via GitHub**. Así podrás subir cambios desde IntelliJ sin escribir contraseñas.

Si tras estos pasos el código aparece en rojo, abre la ventana **Maven**, en la barra derecha, y pulsa el botón de recargar (**Reload All Maven Projects**).

La carpeta `.idea/`, donde IntelliJ guarda su configuración, está excluida de Git, así que cada integrante hace estos pasos una sola vez en su equipo.

## Ejecutar el juego

Desde una terminal en la carpeta del proyecto:

```bash
mvn javafx:run
```

Desde IntelliJ IDEA, abre la ventana **Maven** y haz doble clic en **reinos-del-saber > Plugins > javafx > javafx:run**. Después de la primera vez, queda en la lista de ejecución de la barra superior y se repite con el botón ▶.

Para depurar con puntos de interrupción, abre `src/main/java/com/reinos/vista/Launcher.java`, pulsa la flecha verde junto a `main` y elige **Debug**. Hay que iniciar desde `Launcher` y no desde `JuegoApp`, porque esta última produce el error `JavaFX runtime components are missing`.

## Otros comandos

| Comando | En IntelliJ IDEA | Qué hace |
| --- | --- | --- |
| `mvn test` | Clic derecho en `src/test/java` > **Run 'All Tests'** | Ejecuta las pruebas |
| `mvn compile` | **Build > Build Project** | Compila sin ejecutar |
| `mvn javadoc:javadoc` | Ventana **Maven** > **Plugins > javadoc > javadoc:javadoc** | Genera la documentación en `target/reports/apidocs/index.html` |
| `mvn clean` | Ventana **Maven** > **Lifecycle > clean** | Borra los archivos generados |

Para ejecutar una sola clase de prueba, ábrela y pulsa la flecha verde junto a su nombre.

## Trabajar en equipo con Git

| Paso | Terminal | En IntelliJ IDEA |
| --- | --- | --- |
| Traer los cambios del grupo antes de empezar | `git pull` | **Git > Pull** |
| Comprobar que todo funciona | `mvn test` | **Run 'All Tests'** |
| Guardar tus cambios con un mensaje | `git add .` y `git commit -m "mensaje"` | **Git > Commit** |
| Subirlos a GitHub | `git push` | **Git > Push** |

Si `git push` falla porque hay cambios nuevos en GitHub, ejecuta `git pull`, vuelve a probar y sube de nuevo. Desde la terminal, la primera vez que subas cambios Git pedirá iniciar sesión en GitHub; en Windows abre una ventana del navegador, y en Linux y macOS lo más sencillo es instalar [GitHub CLI](https://cli.github.com/) y ejecutar `gh auth login`.

## Problemas frecuentes

| Mensaje | Causa | Solución |
| --- | --- | --- |
| `release version 21 not supported` | Maven está usando un JDK anterior | Revisa `mvn -version` y corrige `JAVA_HOME`; en IntelliJ, revisa el SDK del proyecto y el JRE del Runner de Maven |
| El sistema no reconoce `git` o `mvn` | No está en el `PATH` | Abre una terminal nueva o revisa la instalación |
| `JavaFX runtime components are missing` | Se ejecutó `JuegoApp` directamente | Ejecuta `Launcher` o `mvn javafx:run` |
| El código aparece en rojo en IntelliJ | No se cargaron las dependencias | Pulsa **Reload All Maven Projects** en la ventana **Maven** |
| `Permission denied` o `403` al hacer `git push` | La cuenta no es colaboradora o no inició sesión | Pide la invitación al repositorio y conecta tu cuenta de GitHub |
