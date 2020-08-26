# Treasure Hunt App

- Programming language: **Java**
- Integrated development environment (IDE): **Android Studio**
- Intended platforms: **Android phones**

## 1. User Guide

This user guide describes the application, its main functionalities and use case.

### 1.1 Description of the Application
The application is a simple virtual treasure hunting game. It uses the phone's location services, sensors, the rear camera of the phone and touchscreen to allow the player to virtually search real places where the treasures are.

### 1.2 Main Features

#### 1.2.1 Tracks
First, the application builds a list of tracks placed on a map, with different milestones.

#### 1.2.2 Questions
When the users click on a milestone in a track, they must answer a question. It can be a single or a multiple choice question with buttons to click, free imputs to fill in or images to click. If they answer correctly, they are redirected to the treasure hunt.

#### 1.2.3 Treasure Hunt
The users have the possibility to search the place in which they can to obtain treasures: they must find one or more hidden objects with the camera of the phone which plays the role of a scan by superimposing virtual elements on the image captured by the camera. The user must click on the treasures that appear on his screen to catch them. To help him, the users can use the position indicators to find them.

#### 1.2.4 Other Considerations
When the users return to the track screen, they can view the treasures they found by clicking on a milestone. 
Also, on the home screen, the users have the option to reset the game.

### 1.3 User Interface
The application offers 4 main screens.
- Home screen
- Question screen
- Track screen
- Hunting screen

### 1.4 Question types
The application has 6 different types of questions:
- single choice question with buttons
- multiple choice question with buttons
- single choice question with free input
- multiple choice question with free inputs
- single choice question with image to click
- multiple choice question with  images to click


## 2. Developer Guide

This guide intended for the developer in charge of maintaining the application presents the architecture software, the classes and interfaces used, the data model handled by the application and a description of the algorithms used.

### 2.1 Activities

#### 2.1.1 MainActivity

This is the entry point of the application. It displays two buttons: one to start hunting treasures, one to reset the game.

#### 2.1.2 TrackActivity

This activity implements the interfaces OnMapReadyCallback, GoogleMap.OnMarkerClickListener and LocationListener. The track database is managed locally on the phone.

#### 2.1.3 QuestionActivity

This activity manages the questions displayed to the user.
The question database is managed locally on the phone.

#### 2.1.3 HuntActivity

The database of objects to search is managed locally on the phone.
The application uses the sensors (accelerometer, magnetometer) to know the orientation of the phone and also identifies actions on the touchscreen to retrieve found items.
Therefore, the activity implements the SensorEventListener and LocationListener interfaces.

### 2.2 Data Model

#### 2.2.1 Track

This class defines the attributes of a Track.

#### 2.2.2 Milestone

This class defines the attributes of a Milestone.

#### 2.2.2 Question

This class defines the attributes of a Question

#### 2.2.2 Treasure

This class defines the attributes of a Treasure


### 2.3 Components

#### 2.3.1. CameraPreview

This view defines the display of the camera view in the HuntActivity activity. It extends SurfaceView and implements SurfaceHolder.Callback.

#### 2.3.2 OverlayView

This view defines the display of treasures that will be superimposed on the CameraView in the activity HuntActivity.
Each treasure has its own OverlayView which disappears when clicked. It extends the View class.

#### 2.3.3. VisualView

This view defines the behavior of the camera view for questions of type "VISUAL" in the QuestionActivity activity. It extends the View class.

### 2.4 Other Considerations

#### 2.4.1 Libraries used

In addition to the classic libraries used in any Android project (Activity, View, Intent, List, Set,
etc.), the app uses other functionalities

##### 2.4.1.1 SharedPreferences

To allow advancement in the game, the following data is recorded in the SharedPreferences:
- SESSION_MILESTONE_ID = Stores the ID of the current Milestone
- SESSION_PASSED_MILESTONE = Stores the list of ids of past Milestones
- SESSION_FOUND_TREASURES = Stores the list of ids of the Treasures found

##### 2.4.1.2 Gson

The open source library developed by Google was used in order to store in a chain of character the data of SharedPreferences. It was also used to store in the SQLite  database  the possible answer choices to the questions.

##### 2.4.1.3 Sensor / Location

The app uses two sensors, the accelerometer and the magnetometer, to pinpoint orientation as well as the location of the phone.

##### 2.4.1.4 GoogleMap

The Google Maps API was chosen for better integration of map management and markers in the app.

##### 2.4.1.5 SQLite

It was used to embed a small database for the game. The DatabaseFiller class creates the database and populates it with pre-recorded data. The DatabaseAccess class allows to make requests to read the contents of the database.

#### 2.4.2 Layouts

Different layouts were defined by type of question. They are called directly by QuestionActivity activity:
- the abstract class AbstractQuestionLayout extends LinearLayout. It initializes the view.
- the ButtonQuestionLayout class extends AbstractQuestionLayout. It adds the buttons. It contains a Listener to select / deselect buttons.
- The InputQuestionLayout class extends AbstractQuestionLayout. It adds a free input and space to display the answers entered. It contains a Listener to add / remove inputs.
- The VisualQuestionLayout class extends AbstractQuestionLayout It calculates the position of the areas that are clickable. It contains a Listener to select / deselect areas.

#### 2.4.3 Resources

In addition to the classic resources such as images, ids or pre-defined character strings, the standard buttons have been redefined via XML files to allow a new behavior, i.e. a button remains pressed when it is pressed (useful for multiple choice questions in particular).