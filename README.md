# Media Converter

Media Converter is a lightweight and versatile application for converting audio and video files. Built with Kotlin Compose for Desktop, it features a modern UI and supports a variety of file formats with real-time progress tracking and quality customization.

---

## Features

- **Drag & Drop Support**
  - Drag and drop files into the application to quickly select them for conversion.

- **Format Conversion**
  - Supports a range of media formats for conversion:
    - Video: `mp4`, `avi`, `mkv`, `mov`
    - Audio: `mp3`, `wav`

- **Adjustable Quality**
  - Choose output quality with an easy-to-use slider.

- **Output File Management**
  - Select or customize the output directory.

- **Real-Time Progress Tracking**
  - Monitor the conversion progress with a visual progress bar.

- **Operation Control**
  - Start or cancel conversion processes with intuitive controls.

---

## Screenshot

> A screenshot placeholder for the application’s user interface.
![Media Converter UI](#)

---

## Technology Stack

The project leverages the following technologies:

- **Language**: Kotlin
- **Framework**: Jetpack Compose for Desktop
- **UI Libraries**:
  - `androidx.compose.foundation`
  - `androidx.compose.material`
- **Coroutines**: Powered by Kotlin's `kotlinx.coroutines` for asynchronous processing.
- **Drag & Drop**: Java's `DropTarget` for handling drag-and-drop operations.
- **File Selection**: JFileChooser for system file and directory browsing.

---

## How to Run

1. **Prerequisites**
   - Install [JDK 21](https://www.oracle.com/java/technologies/javase-downloads.html) or higher.
   - Use a Kotlin-supported IDE (IntelliJ IDEA Ultimate recommended).

2. **Clone the Repository**

   ```bash
   git clone <repository-url>
   cd <project-directory>
   ```

3. **Run the Application**  
   Use IntelliJ IDEA to execute the `main` function in the project:

   ```kotlin
   fun main() = application { ... }
   ```

4. **Start Converting**  
   Drag and drop media files into the application, customize your output settings, and convert!

---

## Usage

1. **Select Files**
   - Drag files into the application or use