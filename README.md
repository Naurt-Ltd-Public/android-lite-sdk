# Android Lite SDK

<br>
Naurt's Android Lite SDK is a lightweight data collection and API wrapper to get up and running with Naurt's Point of Interest (POI) system.

If you're interested in automatically detecting building entrances and parking spots, but not in real-time location tracking, this product is the one for you.

<br>

---
## Quickstart

<br>

Naurt Lite is a silent data collection SDK which accepts user metadata in JSON format, matches it with the current location data, and uploads it to Naurt's servers to process it into building entrance and parking spot data that can be accessed through our POI API. Before you begin, ensure you have a valid API key. If you don't, no worries. You can sign up for a free account on our [dashboard](https://dashboard.naurt.net/).

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
We also have an example application which strings together the above concepts into a full app.

It can be found on [our GitHub here](https://github.com/Naurt-Ltd-Public/naurt-android-sdk-lite).


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
    NaurtValidationStatus.Invalid -> Log.d("Naurt", "API key is invalid. GPS being passed through.")
    NaurtValidationStatus.NotYetValidated -> Log.d("Naurt", "Naurt is currently attempting to validate.")
}
```




---


<br>

## Points Of Interest

<br>

Naurt collects anonymised location data from the SDK which enables us to create automatic Points of Interest, currently in the form of building entrances and parking spots. We achieve this through the collection of "metadata" which helps us link useful data, such as the current delivery address, to events that we detect on the phone, such as the user entering a building.

When you instantiate NaurtLite, you have the ability to provide metadata as an optional **JSONObject**. If you don't have metadata quite yet, that's fine. Later down the line if you wish to add metadata or update the current metadata, you can use the method [updateMetadata](#updatemetadata). This again takes an optional **JSONObject**. If the metadata is null, this will remove any previous metadata and there will be no metadata associated with the subsequent location fixes.

```kotlin
import org.json.JSONObject
import com.naurt.sdk.NaurtLite
import com.naurt.sdk.enums.NaurtEngineType


val originalMeta = JSONObject() 
originalMeta.put("address", "main road")


val naurtLite = NaurtLite(
    "<YOUR NAURT API KEY HERE>",
    applicationContext as Context,
    metadata = originalMeta
)


val updatedMeta = JSONObject() 
updatedMeta.put("address", "london road")


naurtLocationManager.updateMetadata(updatedMeta)
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


Points of interest which have been created are then accessible via [Naurt's POI API](/poi-api) and can be searched via the metadata provided or spatially filtered. 

Naurt Lite offers an easy to use wrapper for this API which will be described in the next section, though it's still worth familiarising yourself with the [POI API documentation](/poi-api) first.


Metadata can be used in many different scenarios and increase the value Naurt brings to your company. If you're still unsure about how metadata could play a part in your use case, contact our [sales team](https://www.naurt.com/#email-form-first-one) to have a chat.

---

## PoiInsert Class

When using Naurt's POI system, it is possible to insert custom POIs. These POIs could be used to keep track of anything; for example, a bench or a lamppost. This data can be sent directly to Naurt's [POI API](/poi-api) via a PUT request, or if you're using an Android SDK, via the PoiInsert class.

To create an instance of this class, it is required that you use its builder.

#### Import

```kotlin
import com.naurt.sdk.poi.PoiInsert
import com.naurt.sdk.poi.PoiInsert.Builder
```


#### Constructor 

The PoiInsert class cannot be directly made with it's constructor. You must build the class via it's Builder. A latitude and longitude must be provided alongside the POI's type (any alphanumerical text) and a valid Naurt key. 


```kotlin
class Builder(
    apiKey: String,
    poiType: String,
    latitude: Double,
    longitude: Double,
)
```

The first basic example demonstrates inserting a restaurant location as a POI. 


```kotlin
val poiInsert = PoiInsert.Builder(
    "<API_KEY_HERE>",
    "restaurant",
    51.0,
    51.0
).build()
```
This next example demonstrates creating a POI which will later be filterable by the type of restaurant.
```kotlin
val poiInsertWithMeta = PoiInsert.Builder(
    "<API_KEY_HERE>",
    "restaurant",
    51.0,
    51.0
).setMetadata(JSONObject(mapOf("type" to "italian"))).build()
```
And now the POI will be filterable by city.
```kotlin
val poiInsertWithDetailedMeta = PoiInsert.Builder(
    "<API_KEY_HERE>",
    "restaurant",
    51.0,
    51.0
).setMetadata(JSONObject(mapOf("type" to "italian", "city" to "London"))).build()
```

This class conforms to the [POI API specification](/poi-api). Please visit the [POI API](/poi-api) docs for a more in-depth guide.


Once built, the request to insert the POI can be carried out using the [`.send()`](#send) method.

---

### send
This method runs a web request and triggers a callback, so ensure it is correctly spawned off to avoid blocking the main thread. 

#### Signature
```kotlin
poiInsert.send(callback: PoiCallback<JSONObject>)
```

#### Parameters

- `callback`: A Naurt [PoiCallback](#poicallback_interface) which will receive the result of the web request. If successful a JSON response 
will be available. If unsuccessful a status code and a JSON containing the error will be present.



#### Returns 

None, but will trigger the callback once the request is done.


#### Throws

Does not throw.


## PoiQuery Class

Once POIs have been generated, you'll be able to query them using powerful location and information based filters.

#### Import

```kotlin
import com.naurt.sdk.poi.PoiQuery
import com.naurt.sdk.poi.PoiQuery.Builder
```


#### Constructor 

The PoiQuery class cannot be directly made with it's constructor. You must build the class via it's Builder. There are two ways to query POI data - with a valid latitude and longitude and/or with the metadata search.

```kotlin

class Builder(
    apiKey: String,
    poiTypes: List<String>,
    latitude: Double,
    longitude: Double
)

class Builder(
    apiKey: String,
    poiTypes: List<String>,
    metadata: JSONObject
)
```

When building a query based around a location, the response will return the closest POIs. When building a query around metadata, the most recently created POIs with an exact match will be returned. For more information please visit the [POI API documentation](/poi-api).
The following example would return the closest 25 restaurants to 10.0 degrees longitude 10.0 degrees latitude that you have inserted into the POI system.

```kotlin
val poiQuery = PoiQuery.Builder(
    "API_KEY_HERE",
    listOf("restaurant"),
    10.0,
    10.0,
).build()
```
You could then build on this by querying the closest italian restaurants. 
```kotlin
val poiQuery = PoiQuery.Builder(
    BuildConfig.API_KEY,
    listOf("restaurant"),
    JSONObject(mapOf("type" to "italian"))
).setDistanceFilter(1e10, 10.23423, 10.23123).build()
```

This class conforms to the [POI API specification](/poi-api) and more details on how to build different queries can be found on the page.


Once built, the request to query the POI can be carried out using the `.send()` method.

---

### send
This method runs a web request and triggers a callback, so ensure it is correctly spawned off to avoid blocking the main thread. 

#### Signature
```kotlin
poiInsert.send(callback: PoiCallback<JSONObject>)
```

#### Parameters

- `callback`: A Naurt [PoiCallback](#poicallback_interface) which will receive the result of the web request. If successful a JSON response 
will be available. If unsuccessful a status code and a JSON containing the error will be present.


#### Returns 

None, but will trigger the callback once the request is done.


#### Throws

Does not throw.

---


## PoiCallback Interface

The interface for creating a callback to receive Naurt POIs. 
#### Import

```kotlin
import com.naurt.sdk.poi.PoiCallback
```
#### Signature
```kotlin
interface PoiCallback<JSONObject> {
    /**
     * Callback for API response
     *
     * This function will be called when a Naurt API has responded.
     *
     */
    fun onComplete(result: NaurtResult<JSONObject>)
}
```
#### Parameters

- `result`: A [NaurtResult](#naurtresult_sealed_class) which can either be a success or a failure depending on the result of the web request. A success will contain a JSON response. A failure will contain a status code and a JSON response.

#### Example
```kotlin
import com.naurt.sdk.poi.PoiCallback
import com.naurt.sdk.enums.NaurtResult

class MyNaurtCallback() : PoiCallback<JSONObject> {
    override fun onComplete(result: NaurtResult<JSONObject>) {
        when (result) {
            is NaurtResult.Success -> {
                println("Success!: ${result.value}")
            }
            is NaurtResult.Failure -> {
                println("Oh no, an Error! Code: ${result.code}, Message: ${result.message}")
            }
        }
    }
}
```





---

## NaurtResult Sealed Class

A sealed class that contains either the JSON result of a successful request, or the error code and response of a failed request.

### Import 
```kotlin
import com.naurt.sdk.enums.NaurtResult
```
### Signature
```kotlin
sealed class NaurtResult<out T>{
    data class Success<out R>(val value: R): NaurtResult<R>()

    data class Failure(val code: Int, val message: JSONObject): NaurtResult<Nothing>()

    fun isSuccess(): Boolean{
        return when(this){
            is Success -> true
            is Failure -> false
        }
    }
}
```

#### Parameters

- `value`: A generic type that is used to return a successful result. This will usually be of type JSONObject.
- `code`: The error code of the failed request.
- `message`: The error message of the failed request.

#### Example
```kotlin
import com.naurt.sdk.poi.PoiCallback
import com.naurt.sdk.enums.NaurtResult

class MyNaurtCallback() : PoiCallback<JSONObject> {
    override fun onComplete(result: NaurtResult<JSONObject>) {
        when (result) {
            is NaurtResult.Success -> {
                println("Success!: ${result.value}")
            }
            is NaurtResult.Failure -> {
                println("Oh no, an Error! Code: ${result.code}, Message: ${result.message}")
            }
        }
    }
}
```

---