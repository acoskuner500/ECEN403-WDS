# ECEN 403 Team 54 - Wildlife Deterrence System
## Overview
There are three main screens as seen in the three bottom navigation destinations: Verify, Log, and Choose.
### 1. Verify
The Verify screen communicates with the WDS server to fetch new deterrences and displays each instance to the user in a card stack format, with the oldest at the top of the stack.
The cards remain on the stack until the user makes a decision to either accept or reject the deterrence attempt.
The user will accept if the animal identified by the machine learning algorithm and deterred by the field unit is in fact what appears in the photo captured.
The user will reject if the animal identified does not match what was captured in the picture (animal or otherwise).
These decisions can be done by swiping the top card to the right or using the "check" button to accept, or by swiping to the left or using the "x" button to reject.
The "undo" button in the center can be used to undo any mistaken decisions made since the last time the decisions were finalized.
Decisions are finalized when the Verify screen is closed (e.g. switching to a different screen or closing the app entirely).
Rejections are discarded and accepted instances are added onto the Log list located on the Log screen.
### 2. Log
The Deterrence Log contains a scrollable list of deterrences previously accepted by the user.
The newest instances appear at the top and the oldest at the bottom.
Internet access is required to load the images within each card for the first time they appear in the list, but can be viewed offline once loaded.
### 3. Choose
The third and final screen contains buttons with an assortment of animals the user can choose from.
The user can select between none to all of the offered animal options.
The selections made will be persisted, and will tell the machine learning algorithm which animals to try to identify and actively deter and ignore anything else.

#### An information pop-up containing simple instructions for each screen is accessible via the "i" button in the top right of the screen.

## Code Breakdown
The app was developed using the programming language Kotlin within Android Studio. Kotlin is designed to work seamlessly with Java but contains much less boilerplate code and is optimized for app development.  

The bulk of the code can be found [here](https://github.com/acoskuner500/ECEN403-WDS/tree/master/app/src/main/java/com/example/wds), and the XML layout files can be found [here](https://github.com/acoskuner500/ECEN403-WDS/tree/master/app/src/main/res/layout).  
+ The [entry](https://github.com/acoskuner500/ECEN403-WDS/tree/master/app/src/main/java/com/example/wds/entry) package contains files that deal with the Room persistence library to create an Entry object, data access object (aka DAO), database, repository, and ViewModel, which all work together to abstract and simplify storing and fetching data relating to one deterrence.  
+ The files in the [fragments](https://github.com/acoskuner500/ECEN403-WDS/tree/master/app/src/main/java/com/example/wds/fragments) package and the [MainActivity.kt](https://github.com/acoskuner500/ECEN403-WDS/blob/master/app/src/main/java/com/example/wds/MainActivity.kt) file work together to handle the UI side of the app, for example using the bottom navigation icons to navigate between different screens and "adapting" the data to the RecyclerView "Views" in the Verify and Log screens.  
+ The XML layout files are the heart of the user interface. They control things like the top app bar and bottom navigation menu, the card stack and scrollable card list, and all the buttons and text seen on any of the screens.

## Dependencies
The major libraries used can be found in [master/app/build.gradle](https://github.com/acoskuner500/ECEN403-WDS/blob/master/app/build.gradle):
+ Room Persistence Library : simplify creation and interaction with SQLite Database (i.e. device storage)
+ RecyclerView : abstracts data and improves UI performance by only loading things on screen and "recycling" off-screen real estate
+ CardStackView : custom extension of RecyclerView created by [Yuya Kaido](https://github.com/yuyakaido/CardStackView) for a Tinder-like swipeable UI
+ CardView : displays "Views" within a clickable, elevated, rounded-rectangular card, used in CardStackView and the Deterrence Log
+ Glide : fetches and loads images from the internet using URL strings
+ Gson : converts Java/Kotlin objects to and from JSON string files, used to store and load "Entry" objects which each contain an ID number, image source URL string, title string, and timestamp string

### Room Persistence Library
The Room Persistence Library simplifies setting up and managing an SQLite database for storing and fetching data to and from device storage. To explain the five files found in the [entry](https://github.com/acoskuner500/ECEN403-WDS/tree/master/app/src/main/java/com/example/wds/entry) package briefly:  
1. The Entry file contains the "Entity" which is a model for a single row in the SQLite database, comprised of fields that store an ID number (for database purposes), a string with an image source URL, a string with the animal type, and a string with the timestamp.  
2. The database file initializes the database the first time it is ever created
3. The data access object (DAO) contains methods which can access the table; in my case I have one method that inserts a single item to the database table, and another to return all items from the database table. The insert method is called to submit accepted deterrences from the Verify screen, and the getter method is called on the Log screen to display the logged instances in a list.  
4. The repository is used to instantiate the DAO  
5. The ViewModel is used to instantiate the ArrayLists required to get, set, and manage the data, making use of Kotlin Coroutines which can run certain processes in background threads to save performance.  
