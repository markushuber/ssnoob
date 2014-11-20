### **SSNOOB** is a Java tool to create **[Social Snapshots](http://socialsnapshots.nysos.net/)** of Facebook accounts.

## **[>> Download SSNOOB v0.5 (jar)](https://dl.dropboxusercontent.com/u/571550/snoob/ssnoob-0.5.jar)**

# Create a social snapshot of your Facebook account

The precompiled SSNOOB jar file runs on Windows, MacOS, and Linux and requires a [Java runtime environment (Version 1.7 or higher)](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
Start the SSNOOB applications and start the download process:
### Using an existing Facebook browser session (also useful for forensics)
If you are logged into Facebook on Facebook on Internet Explorer, Chrome, or Firefox SSNOOB should automatically detect the running session (MacOS is currently not supported). Select the browser you usually use with Facebook and click Login with cookies.
Click on **"Login with cookies"**.
### Login with username and password
Enter your Facebook username and password and click on **"Login using credentials"**.

**Depending on your Facebook account and your Internet connection the download process might take a while**.

## Detailed settings

1. Download Path
Defines where your social snapshot is being stored.
2. Snapshot Depth
*1* is means only your profile, *2* includes your profiles and your friends, *N* is the roof of a friend, of a friend..*N* times... of your friend
3. Download images
Defines if photos are downloaded as well (increases overall time).
4. Login with cookies
Shows detected active Facebook sessions and login using the detected browser cookies.
5. Login with password
Use SSNOB with your Facebook account's username and password.


![SSNOOB Gui](https://s3-eu-west-1.amazonaws.com/socialsnapshots/ssnoob_gui.jpg)


## Using the text-only mode
**SSNOOB** also supports a text-only mode to be used from the command line:

```none
    $ java -jar ssnoob-<version>-jar-with-dependencies.jar [cookiestring|username password] [options]
      options:
      -help                : shows this help
      -browserName VAL     : change the user agent string of the built-in browser
      -c VAL               : the config file to use
      -d VAL               : the directory where the snapshots will be placed (will be created if not present)
      -downloadThreads N   : download threads per CPU core
      -imageDownload       : boolean value for dis/enabling image download
      -loginDeviceName VAL : the name SSNOOB should use if asked for a device name
      -max N               : the max depth of the snapshot
      -maxImagePriority N  : the priority image download tasks should have
                             (1 is the highest priority)
      -token VAL           : supply an access token directly instead of an username/cookie
```

# Which information do you get with SSNOOB?

## Account content (JSON)
The content of your useraccount is stored in a single JSON file with the following format: `userid_timestamp_socialsnapshot.json` (e.g. `123456789_2014-08-19_07-22-23_socialsnapshot.json`).

## Images (Photos)
SSNOOB creates two different image directories (If download images is actived): `userid-images` and `userid-user-images`. The first directory contains the actual HTTP path to the downloaded pictures, while the second directory lists Photos by Facebook user.

# FAQ

## Are you storing any of my user information?
SSNOOB downloads your account data locally, we do not store or access the API oAuth token. Also the SSNOOB Facebook application is automatically removed from your Facebook account once the download process has finished.

## How do I build SSNOOB from source?

### Requirements
* [Apache Maven](http://maven.apache.org/)
* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

### Clone this git repository.

### Build jar packages
Run `mvn package` within the SSNOOB directory. Maven will create a folder called *target* which contains two jar files: one with all library dependencies and one without libraries.


## Contributors
* The initial project was created at [SBA Research](http://www.sba-research.org) by [Markus Huber](http://www.sba-research.org/team/senior-researcher/markus-huber/) and [Manuel Leithner](http://www.sba-research.org/team/researchers/manuel-leithner/).
* This updated version is based on the [thesis by Maurice Wohlkönig](http://www.mannaz.at/) and a student project at FH Hagenberg called [cookie-forensics](https://code.google.com/p/cookie-forensics/).
* [Stefan Haider](https://github.com/haidelber) contributed by improving the code base and creating version 0.5 of SSNOOB.
