# Android Lite SDK

<br>
Naurt's Android Lite SDK is a lightweight data collection and API wrapper to get up and running with Naurt's Point of Interest (POI) system.

If you're interested in automatically detecting building entrances and parking spots, but not in real-time location tracking, this product is the one for you.

<br>

---
## Quickstart

<br>

Naurt Lite is a silent data collection SDK which accepts user data about a destination in JSON format, matches it with the current location data, and uploads it to Naurt's servers to process it into building entrance and parking spot locations that can be accessed through our POI API. Before you begin, ensure you have a valid API key. If you don't, no worries. You can sign up for a free account and key on our [dashboard](https://dashboard.naurt.net/).

<br>


### Project Configuration
Naurt's minimum supported API level is 16 which is Android 4.1 (Jelly Bean). Your project's build.gradle file should contain a "minSdkVersion" of 16 or above.
```groovy
android {
    defaultConfig {
        minSdkVersion 16
    }
}
```



<br>

While you're in the build.gradle file, Naurt also needs to be added as a dependency. First add mavenCentral() to your repositories 

```groovy
repositories {
    mavenCentral()
}
```
And then the Naurt Lite can be added as a dependency.

```groovy
dependencies {
    implementation "com.naurt.sdk:lite:0.0.1"
}
```

To view change logs or manually include Naurt Lite in your project, visit our [Github](https://github.com/Naurt-Ltd-Public).

<br>

### App permissions

As Naurt accesses the phone's Network and GPS location services, you'll need to add the corresponding permissions to your AndroidManifest.xml file to ensure Naurt Lite works as expected.

```xml
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
```

**You will also need the user to grant location permissions before starting Naurt.** If you do not already have this logic in your application, you can use the following example to check for the granted permission and only initialise Naurt if it has been granted.

```
private val LOCATION_PERMISSION_REQUEST_CODE = 1001

if (!this.hasLocationPermission()) {
    this.requestLocationPermission()
} else{
    // Start Naurt!
}


// Check if the app has permission to access location
private fun hasLocationPermission(): Boolean {
    val permissionStatus = ContextCompat.checkSelfPermission(
        this,
        ACCESS_FINE_LOCATION
    )
    return permissionStatus == PackageManager.PERMISSION_GRANTED
}

// Request location permission from the user
private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(
        this, arrayOf(ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE
    )
}

// Handle the result of the permission request
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("Naurt", "I have the permissions!")
            // Start Naurt
        } else {
            Log.d("Naurt", "I haven't got the permissions!")
            // Don't start Naurt
        }
    }
}
```

<br>

### Instantiating Naurt

First, begin by importing the NaurtLite class

```kotlin
import com.naurt.sdk.NaurtLite
```

Naurt Lite then requires a valid Naurt API key and the application's context.
```kotlin
val naurtLite = NaurtLite("<API_KEY_HERE>", applicationContext as Context)
```

Instantiation should be carried out within the onCreate() method of the app, though it's important the user has granted location permissions first otherwise Naurt Lite will throw an exception.

Once Naurt has been created, validation will be attempted and, once successful, Naurt will begin collecting the location data required to log building entrance and parking locations. Please see [Points Of Interest](#points_of_interest) for more information.

<br>

### Disabling Naurt

When you're finished using Naurt Lite and want to close the app, call the onDestroy method. This will clean up any internal processes and listeners created by Naurt. We recommend doing this within the onDestroy method of your app.

```kotlin
naurtLite.onDestroy()
```

<br>




---
<br>

## Example Application
We also have an example application which combines the above concepts into a full app.

It can be found on [our GitHub here](https://github.com/Naurt-Ltd-Public/android-lite-sdk).


---
<br>

## Background tracking

By default, Naurt Lite does not automatically track when its parent app is in the background. To do this, Naurt Lite should be included within a foreground service, or the background tracking permission should be engaged and enabled.

---
<br>

## API key validation

<br>

When the Location Manager is initialised, it will attempt to validate the API key. If a key cannot be successfully validated, data will not be uploaded to Naurt's servers and building entrances and parking spots will not be logged.

The easiest way to check whether Naurt is validated yet is by using the [getValidated](#getisvalidated) method.


```kotlin
import com.naurt.sdk.enums.NaurtValidationStatus


val isMyApiKeyValidated = naurtLite.getIsValidated()

when(isMyApiKeyValidated) {
    NaurtValidationStatus.Valid -> Log.d("Naurt", "API key is valid.")
    NaurtValidationStatus.ValidNoDataTransfer -> Log.d("Naurt", "API key is valid, but no data is to be uploaded.")
    NaurtValidationStatus.Invalid -> Log.d("Naurt", "API key is invalid. Naurt Will not create parking and door POIs.")
    NaurtValidationStatus.NotYetValidated -> Log.d("Naurt", "Naurt is currently attempting to validate.")
}
```




---


<br>

## Points Of Interest

<br>

Naurt collects anonymised location data from the SDK which enables us to create automatic Points of Interest, currently in the form of building entrances and parking spots. We achieve this through the collection of data about the destination helping us link useful data, such as the current delivery address, to events that we detect on the phone, such as the user entering a building.

When you instantiate NaurtLite, you have the ability to provide data about your destination as an optional **JSONObject**. If you don't have any data about the destination quite yet, that's fine. Later down the line if you wish to add data or update the current data, you can use the method [newDestination](#newDestination). This again takes an optional **JSONObject**. If it is null, this will remove any previous data and there will be no data associated with the subsequent location fixes and destination.

```kotlin
import org.json.JSONObject
import com.naurt.sdk.NaurtLite
import com.naurt.sdk.enums.NaurtEngineType


val originalDestination = JSONObject() 
originalDestination.put("address", "main road")


val naurtLite = NaurtLite(
    "<YOUR NAURT API KEY HERE>",
    applicationContext as Context,
    destinationData = originalDestination
)


val updatedDestination = JSONObject() 
updatedDestination.put("address", "london road")


naurtLite.newDestination(updatedDestination)
```


<div class="callout-block callout-block-danger">
    <div class="content">
        <h4 class="callout-title">
            <span class="callout-icon-holder me-1">
                <i class="fas fa-info-circle"></i>
            </span>
            <!--//icon-holder-->
            Important
        </h4>
        <p>Please do not send any personal information or data that directly identifies the user.
        </p>
    </div>
    <!--//content-->
</div>


Points of interest which have been created are then accessible via [Naurt's POI API](/poi-api) and can be searched via the data provided or spatially filtered. 

Naurt Lite offers an easy to use wrapper for this API which will be described in the next section, though it's still worth familiarising yourself with the [POI API documentation](/poi-api) first.

Naurt's POI system can be used in many different scenarios and increase the value Naurt brings to your company. If you're still unsure about how the POI system could play a part in your use case, contact our [sales team](https://www.naurt.com/contact-us) to have a chat.

---

For full documentation please visit [our documentation](https://docs.naurt.net)