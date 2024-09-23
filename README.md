# Bookstore JavaFX Application

This is a simple JavaFX-based bookstore application. It contains a login page with hardcoded credentials for quick access during development, as well as buttons to navigate between different pages loaded via FXML files.

## Prerequisites

Before running this application, ensure that you have the following installed:

1. **Java Development Kit (JDK)**: Version 11 or higher.
2. **Apache Maven**: Version 3.6 or higher.
3. **JavaFX**: This project uses JavaFX, so you will need to add JavaFX dependencies to your Maven `pom.xml`.

## How to Run

To run this application using Maven, follow these steps:

1. Open a terminal or command prompt.
2. Navigate to the root directory of the project where the `pom.xml` file is located.
3. Execute the following command to clean any previous builds and run the JavaFX application:
    ```bash
    mvn clean javafx:run
    ```

### Required Maven Dependencies

Ensure that your `pom.xml` includes the necessary JavaFX dependencies. Here is an example of what should be included for running JavaFX with Maven:

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>22</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>22</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <executions>
                <execution>
                    <goals>
                        <goal>run</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

```



## Application Overview

### Main Class
The entry point for the application is the `Main.java` class located in the `bookstore` package. It sets up the login screen and handles switching between different pages of the application.

### Key Features
- **Login Page**: Users can log in using predefined credentials to access different pages.

#### Preloaded Credentials for Development:
- **Username**: admin, **Password**: password (Access to all pages).
- **Username**: page2user, **Password**: password (Access to Page 2 only).
- **Username**: testing, **Password**: password (Access to the Testing page only).

- **FXML Pages**: The application uses FXML files for different pages.
    - `page2.fxml`: The content for Page 2.
    - `testing.fxml`: The content for the Testing page.

### Navigation
Once logged in, the application displays a top bar with buttons to navigate between the pages that the user has access to.

### Authentication
The `authenticateUser` method currently uses hardcoded usernames and passwords. It returns a list of page names that the user has access to, which controls the visibility of the buttons and the pages the user can visit.

### Customization
- **Styles**: UI elements like buttons, text fields, and background colors can be customized through CSS-like inline styles.
    - Button and text field styles are set using constants like `BUTTON_STYLE` and `TEXTFIELD_STYLE`.
    - Background color and text font are also controlled with constants such as `BACKGROUND_STYLE` and `FONT`.

- **Pages**: New pages can be added easily by adding a new FXML file and updating the `pageMap` with the corresponding key-value pair.
  ```java
  pageMap.put("newPage", "newPage.fxml");


### Key points of the `README.md`:
- Instructions on how to run the project with `mvn clean javafx:run`.
- Information about required dependencies for JavaFX.
- A breakdown of the application structure and features.
- Development tips, including the preloaded login credentials.
- Troubleshooting and customizability of the app.

Let me know if you need any adjustments