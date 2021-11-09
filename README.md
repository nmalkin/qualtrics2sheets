qualtrics2sheets
================
A utility for exporting a survey from Qualtrics and importing the resulting CSV into Google Sheets
(all in one command).
It can also be used for either of those two tasks separately:
only automating export from Qualtrics,
or only uploading (any arbitrary) CSV to Sheets.

Rationale
---------
If you want to work with your Qualtrics data in Google Sheets, you typically have to achieve this manually by:

1. Exporting the data from Qualtrics
2. Downloading the exported file
3. Unzipping the CSV
4. Uploading the CSV to Google Drive
5. Converting the CSV to a Google Sheets spreadsheet

This utility exists to automate these steps.
Specifically, you can:

1. Download the Qualtrics CSV
2. Upload it (or any other CSV!) to Sheets
3. Do it all in one go


Installation
------------

### Prerequisites
To run this program, you need Java 11 or later on your system.
(It should be possible to recompile the program for older versions of Java as well.)

### Obtain the program
Download it from releases or compile it yourself by running `./gradlew shadowJar`.
(The compiled program will be at `qualtrics2sheets/build/libs/qualtrics2sheets-all.jar`.)

If you downloaded or compiled the JAR file, you can execute it as `java -jar qualtrics2sheets.jar`, followed by the command-line arguments (documented below).

The other file in the releases is the same JAR with a launcher script included.
(It will only work on macOS or Linux.)
You can make it executable (`chmod u+x qualtrics2sheets`) and even put it somewhere on your PATH.


Setup
-----
Before you run the program, you'll need to get the proper credentials for the respective APIs.
You'll only have to perform these steps once.

### Qualtrics
You'll need your Qualtrics API token, which you can obtain by following
[this Qualtrics documentation](https://api.qualtrics.com/guides/docs/Instructions/api-key-authentication.md).

Once you have it, you'll pass it to `qualtrics2sheets` using the `--token` flag.

### Google Sheets
You'll need a Google Cloud credentials file. (When this was last updated) the steps to do this are:

1. Create a new project in [Google Cloud Console](https://console.cloud.google.com/)
2. [Enable the Google Sheets API](https://console.cloud.google.com/apis/library/sheets.googleapis.com) for this project
3. [Configure the consent screen](https://developers.google.com/workspace/guides/create-credentials#configure_the_oauth_consent_screen) for an _Internal_ app
4. [Create an OAuth client ID credential](https://developers.google.com/workspace/guides/create-credentials#create_a_oauth_client_id_credential)
5. [Download the JSON file with the newly-created credential](https://developers.google.com/workspace/guides/create-credentials#desktop)
6. Once you have the credentials file, save it somewhere on disk
7. Pass its location to `qualtrics2sheets` using the `--credentials` flag.

The first time you run `qualtrics2sheets`, it will need to use those credentials to obtain an authentication token (which is what's actually used to interact with the API).
To do this, it will open a new browser window, where you'll be asked to authorize the application you created in the Cloud Console with access to Sheets.
The resulting authentication token will be stored in the `tokens` directory in your current working directory. (You can change this location using the `--tokens-directory` flag.)


Usage
-----
**Warning**:
when you run the program, it will overwrite the contents of the target spreadsheet.
Make sure you're not losing any data.

Here is a sample of how to run the program.
(Remember that if you're using the JAR file, you'd instead run `java -jar qualtrics2sheets.jar run ...`)

    qualtrics2sheets run  \
      --datacenter ca1  \
      --token $QUALTRICS_TOKEN  \
      --credentials /path/to/credentials.json  \
      --spreadsheet 1xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx  \
      --survey SV_xxxxxxxxxxxxxxx

- `--datacenter` is your account's Qualtrics datacenter, [see the documentation here](https://api.qualtrics.com/guides/docs/Instructions/base-url-and-datacenter-ids.md)
- `--spreadsheet` is the ID of your spreadsheet (you can get it from its URL)  
  For example, if the spreadsheet's URL is `https://docs.google.com/spreadsheets/d/12345...90/edit#gid=111`, its ID is `12345...90`
- `--survey` is the ID of your Qualtrics survey (you can also get it from its URL)  
  For example, if the survey's URL is `https://www.qualtrics.com/surveys/SV_1234567/`, its ID is `SV_1234567`


You can also run the pipeline steps separately:

    qualtrics2sheets download_qualtrics
    qualtrics2sheets upload_csv

For the latter, the program will upload any CSV to Google Sheets:
it doesn't have to be a Qualtrics export.

If the targeted spreadsheet has multiple sheets (tabs), the contents of the CSV will be written to the first one.

**Reminder**: the content of the sheet will be **overwritten** by the CSV without any warnings.
