
# TipJar

**Tip Calculator utility app which allows a user to calculate the appropriate tip based on the total amount entered.**

### Features
* [x] User is displayed the tip of specified percentage for specified entered amount
* [x] User enters the total amount of the transaction
* [x] User can select between tip amounts (i.e 10%, 15%, 20%) 
 * Use Radio Group and experiment with Shapes and selectors to have circle RadioButtons
* [x] Upon selecting tip amount, formatted tip value is displayed 
 * EditText
* [x] *Optional:* User changes the total amount and updated tip is reflected automatically
* [x] *Optional:* User can select custom tip percentage if desired 
 * SeekBar and shapes/selectors to design the Progress Bar and the Thumb
* [x] *Optional:* User can select how many ways to split the tip 
 * SeekBar and shapes/selectors to design the Progress Bar and the Thumb
* [x] *Optional:* User can edit preset tip percentages and have them persist across launches 
 * Use Custom Dialog Fragment to edit the tip percentages
 * Use Shared Preferences to persist and load the values across launches
* [x] *Optional:* Experiment with trying input widgets to replace the buttons and/or textviews
 * RadioButtons and SeekBars
* [x] *Optional:* Improve the user interface and experience by using images and/or colors
* [x] *Additional:* Location the city of the current restaurant and find the sales tax of the city. 
 * Tax tables for various cities is stored in res/xml directory in Xmlformat.
 * Persist the preset tip amounts across launches using SharedPrefernces
 * Use GPS and Geocoder to determine the name of the city
* [x] *Additional:* Compute PreTax Amount and Tip on that based on the city
* [ ] *Additional:* Change shape of circles into polygons( explore canvas )
* [ ] *Additional:* Get the name of the restaurant and persist tips/amount and obtain a history for a restaurant
* [ ] *Additional:* Search tip history for a specific restaurant
* [ ] *Additional:* Show tipping etiquette information from itipping.com

### Exploration of Topics
1. Customization of UI  using themes and styles.
2. Creating effects with Shape Drawable, StateListDrawable
3. SeekBar , Radio Button Control
4. TextWatcher
5. Background images - Opacity , Scaling etc
6. GPS related
7. using xml resources in map format.
8. Use Android Lint (Addressed one performance issue - Move background image to styling instead of in the activity - overdrawn issue)
9. Wire framing using FluidUI
