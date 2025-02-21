# Music Management Application

## Overview
This project is a Java application for managing music data. It allows users to perform various operations such as adding, correcting, removing, and consulting music details, as well as generating personalized playlists based on specified criteria.

## Features
- Add new music entries
- Update existing music details
- Remove music entries
- Consult music details
- Generate personalized playlists based on genre and count

## Project Structure
- `src/main/java/model`: Contains the model classes `Music` and `Playlist`.
- `src/main/java/dao`: Contains DAO classes for database interactions (`MusicDAO` and `PlaylistDAO`).
- `src/main/java/service`: Contains the service layer for music operations (`MusicService`).
- `src/main/java/util`: Contains utility classes for database connection management (`DatabaseConnection`).
- `src/main/java/App.java`: The entry point of the application that presents a menu to the user.
- `src/test/java`: Contains unit tests for the application.

## Setup Instructions
1. Clone the repository.
2. Navigate to the project directory.
3. Use Maven to build the project:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn exec:java -Dexec.mainClass="App"
   ```

## Usage
Follow the on-screen menu to perform operations related to music management.