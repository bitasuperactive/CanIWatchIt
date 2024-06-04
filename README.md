<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/bitasuperactive/CanIWatchIt">
    <img src="doc/images/ic_launcher.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Can-I-Watch-It</h3>

  <p align="center">
    Proyecto escolar (CFGS) para desarrollo de aplicaciones móviles.
    <br />
    View Demo
    ·
    <a href="https://github.com/bitasuperactive/CanIWatchIt/issues/new?labels=bug&template=bug-report.md">Report Bug</a>
    ·
    <a href="https://github.com/bitasuperactive/CanIWatchIt/issues/new?labels=mejora&template=feature-request.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Tabla de contenidos</summary>
  <ol>
    <li>
      <a href="#sobre-el-proyecto">Sobre el proyecto</a>
      <ul>
        <li><a href="#requisitos-de-desarrollo">Requisitos de desarrollo</a></li>
        <li><a href="#herramientas-utilizadas">Herramientas utilizadas</a></li>
      </ul>
    </li>
    <li><a href="#uso">Uso</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contribuir">Contribuir</a></li>
    <li><a href="#licencia">Licencia</a></li>
    <li><a href="#reconocimientos">Reconocimientos</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## Sobre el proyecto
Busca cualquier película o serie para ver si está disponible en tus plataformas contratadas. Limitado a España.
<br />
<br />
<div align="center">
	<a href="https://github.com/bitasuperactive/CanIWatchIt">
		<img src="doc/images/app_showcase/plataformas.png" alt="Logo" width="270" height"555">
		<img src="doc/images/app_showcase/buscar.png" alt="Logo" width="270" height"555">
	</a>
</div>
<br />
<br />
<details>
	<summary><h3>Requisitos de desarrollo</h3></summary>

1. Primera evaluación:
	 - ViewBinding
	 - ConstraintLayout
	 - RecyclerView
	 - Navigation Component y el plugin Safe Args con al menos 3 pantallas.
 2. Segunda evaluación:
	 - ViewModel, LiveData y/o Flow. (2 puntos)
	 - Arquitectura MVVM (2 puntos) con Patrón repositorio (2 puntos) con al menos una fuente de datos de las siguientes:
		 - Base de datos local con Room
		 - API Remota con Retrofit.
   	 - El proyecto debe ir acompañado de un documento con una breve explicación del mismo, las herramientas usadas y su cometido. Con una carilla de un Word se pueden explicar los aspectos fundamentales, pero no os pongo extensión máxima.

Con los puntos anteriores será suficiente para aprobar, y dependiendo de la complejidad del proyecto o de las fuentes de datos usadas, incluso para obtener una nota de partida destacada. Es decir, puntuará mejor si usáis Room + Retrofit que si sólo usáis una de las dos.

#### Valoraciones adicionales

 - Cualquier otra funcionalidad será valorada positivamente (uso de menús para facilitar la navegación, inclusión de contenido multimedia como audio o vídeo, fuentes de datos adicionales, servicios,… ), etc…
 - Se tendrá en cuenta la originalidad del proyecto. Es decir, si hacéis un proyecto que sea un calco de las funcionalidades y pantallas vistas en proyectos hechos en clase, se valorará menos.
 - También se valorarán aspectos como la usablidad, el uso de componentes modernos Material3, la robustez (control de errores y excepciones, validación de entradas de datos, etc…)
 - No se espera algo profesional, o que realmente sea útil y monetizable, pero sí algo que ponga en práctica lo visto en clase, y cualquier cosa que queráis añadir.
</details>

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



### Herramientas utilizadas

This section should list any major frameworks/libraries used to bootstrap your project. Leave any add-ons/plugins for the acknowledgements section. Here are a few examples.

* ViewBinding
* ConstraintLayout
* RecyclerView
* Navigation Component y Safe Args
* ViewModel y LiveData.
* Arquitectura MVVM con Patrón repositorio.
* Base de datos local con Room
* API Remota con Retrofit.
* Navigation menu.
* Otros Plugins: Json to Kotlin data class.

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



<!-- USAGE EXAMPLES -->
## Uso

* Al iniciar la aplicación por primera vez deberás seleccionar las plataformas de streaming que tienes contratadas; podrás cambiar tu elección desde la pestaña esa misma pestaña llamada "Plataformas".
* Desde la pestaña principal "Buscar" que, a partir de ahora te aparecerá directamente, podrás buscar los títulos que quieras ver. Si el contenido resultante está disponible en cualquier de tus plataformas, el logo de esta aparecerá a color, de lo contrario estará teñido de gris.
* Al pinchar en cualquier título de los que busques, se abrirá la web de IMDb correspondiente. Esta vista tiene funcionalidades limitadas y no podrás reproducir el trailer, entre otras limitaciones.

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



<!-- ROADMAP -->
## Roadmap

### Roadmap principal:
- [x] Cumplir los requisitos para el desarrollo
- [ ] Implementar actualizaciones automáticas a través de este repositorio
	- [x] Recuperar la última versión disponible
	- [ ] Gestionar la instalación automáticamente
	- [ ] Diseñar una interfaz básica para la carga del proceso
- [ ] Dar funcionalidad a los iconos de las plataformas para que abran la aplicación correspondiente con el título disponible

### Posibles implementaciones:
- [ ] Cambiar la api a la de JustWatch
- [ ] Implementar cámbio de región dinámico

Vea los [open issues](https://github.com/bitasuperactive/CanIWatchIt/issues) para una lista completa de las características propuestas y bugs conocidos.

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



<!-- CONTRIBUTING -->
## Contribuir

Las contribuciones son lo que hace que la comunidad de código abierto sea un lugar increíble para aprender, inspirarse y crear. Cualquier contribución que hagas será **muy apreciada**.

Si tienes una sugerencia que pueda mejorar este proyecto, por favor haz un fork del repositorio y crea un pull request. También puedes abrir un issue con la etiqueta "enhancement". ¡No olvides darle una estrella al proyecto! ¡Gracias de nuevo!

1.  Haz un fork del proyecto
2.  Crea tu rama de funcionalidades (`git checkout -b feature/AmazingFeature`)
3.  Haz commit de tus cambios (`git commit -m 'Añadir AmazingFeature'`)
4.  Haz push a la rama (`git push origin feature/AmazingFeature`)
5.  Abre un pull request

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



<!-- LICENSE -->
## Licencia

Distribuido bajo la licencia MIT. Vea `LICENSE.txt` para más información.

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Reconocimientos

* [MVVM News App - Philipp Lackner](https://www.youtube.com/watch?v=asuOWE5KuFM&list=PLQkwcJG4YTCRF8XiCRESq1IFFW8COlxYJ)
* [Best-README-Template](https://github.com/othneildrew/Best-README-Template)

<p align="right">(<a href="#readme-top">volver al inicio</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
